package com.mccorby.federatedlearning.features.sumoperation.model;

import android.util.Log;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.List;
import java.util.Map;


public class LinearModel implements FederatedModel {

    //Number of ITERATIONS per minibatch
    private static final int ITERATIONS = 1;
    //Number of epochs (full passes of the data)
    private static final int N_EPOCHS = 200;
    //Network learning rate
    private static final double LEARNING_RATE = 0.01;

    private static final String TAG = LinearModel.class.getSimpleName();
    private static final int BATCH_SIZE = 150;

    private IterationListener mIterationListener;
    private final String mId;
    private int mSeed;
    private Gradient mServerGradient;

    private MultiLayerNetwork mNetwork;

    public LinearModel(String id, IterationListener iterationListener, int seed, Gradient serverGradient) {
        mId = id;
        mIterationListener = iterationListener;
        mSeed = seed;
        mServerGradient = serverGradient;
    }

    public void buildModel() {
        //Create the network
        int numInput = 2;
        int numOutputs = 1;
        int nHidden = 10;
        mNetwork = new MultiLayerNetwork(new NeuralNetConfiguration.Builder()
                .seed(mSeed)
                .iterations(ITERATIONS)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(LEARNING_RATE)
                .weightInit(WeightInit.XAVIER)
                .updater(Updater.NESTEROVS)
                .list()
                .layer(0, new DenseLayer.Builder().nIn(numInput).nOut(nHidden)
                        .activation(Activation.TANH)
                        .name("input")
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .name("output")
                        .nIn(nHidden).nOut(numOutputs).build())
                .pretrain(false)
                .backprop(true)
                .build()
        );
        mNetwork.init();
        mNetwork.setListeners(mIterationListener);
    }

    @Override
    public void train(FederatedDataSet dataSource) {
        DataSet trainingData = (DataSet) dataSource.getNativeDataSet();
        List<DataSet> listDs = trainingData.asList();
        DataSetIterator iterator = new ListDataSetIterator(listDs, BATCH_SIZE);

        //Train the network on the full data set, and evaluate in periodically
        for (int i = 0; i < N_EPOCHS; i++) {
            iterator.reset();
            mNetwork.fit(iterator);
        }
    }

    @Override
    public String evaluate(FederatedDataSet federatedDataSet) {
        DataSet testData = (DataSet) federatedDataSet.getNativeDataSet();
        List<DataSet> listDs = testData.asList();
        DataSetIterator iterator = new ListDataSetIterator(listDs, BATCH_SIZE);

        return mNetwork.evaluate(iterator).stats();
    }

    public INDArray predict(final INDArray input) {
        return mNetwork.output(input, false);
    }

    public double score(DataSet trainerDataSource) {
        return mNetwork.score(trainerDataSource);
    }

    @Override
    public INDArray getGradientAsArray() {
        return mNetwork.gradient().gradient();
    }


    public Gradient getGradient() {
        return mNetwork.gradient();
    }

    @Override
    public void updateWeights(Gradient remoteGradient) {
        Log.d(TAG, "Remote Gradient " + remoteGradient);
        mNetwork.update(remoteGradient);
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void updateWeights(INDArray remoteGradient) {
        Log.d(TAG, "Remote Gradient " + remoteGradient);
        Gradient gradient = new DefaultGradient(remoteGradient);
        Log.d(TAG, "Updating weights from server with gradient " + gradient.gradient().toString());
        // TODO Transform the remoteGradient flattened array into the map required by the network?
        Map<String, INDArray> netGradients = mNetwork.gradient().gradientForVariable();
        for (Map.Entry<String, INDArray> entry : netGradients.entrySet()) {
            Log.d(TAG, entry.getKey());
            for (int i : entry.getValue().shape()) {
                Log.d(TAG, "Shape " + i);
            }
            for (int i = 0; i < entry.getValue().shape().length; i++) {
                Log.d(TAG, "Size (" + i + ")" + entry.getValue().size(i));
            }
        }

        /*
 0_W
 Shape 2
 Shape 10
 0_b
 Shape 1
 Shape 10
 1_W
 Shape 10
 Shape 1
 1_b
 Shape 1
 Shape 1
 Weights updated
         */

        mNetwork.update(gradient);
        Log.d(TAG, "Weights updated");
    }
}
