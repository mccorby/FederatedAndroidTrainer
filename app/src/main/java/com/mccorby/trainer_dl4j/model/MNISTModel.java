package com.mccorby.trainer_dl4j.model;

import android.util.Log;

import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class MNISTModel implements FederatedModel {

    private static final String TAG = MNISTModel.class.getSimpleName();
    private String mId;
    private IterationListener mIterationListener;
    private MultiLayerNetwork model;

    public MNISTModel(String id, IterationListener iterationListener) {
        mId = id;

        mIterationListener = iterationListener;
    }

    @Override
    public void buildModel() {
        //number of rows and columns in the input pictures
        final int numRows = 28;
        final int numColumns = 28;
        int outputNum = 10; // number of output classes
        int batchSize = 64; // batch size for each epoch
        int rngSeed = 123; // random number seed for reproducibility
        int numEpochs = 15; // number of epochs to perform
        double rate = 0.0015; // learning rate

        Log.d(TAG, "Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(rngSeed) //include a random seed for reproducibility
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT) // use stochastic gradient descent as an optimization algorithm
                .iterations(1)
                .activation(Activation.RELU)
                .weightInit(WeightInit.XAVIER)
                .learningRate(rate) //specify the learning rate
                .updater(Updater.NESTEROVS)
                .regularization(true).l2(rate * 0.005) // regularize learning model
                .list()
                .layer(0, new DenseLayer.Builder() //create the first input layer.
                        .nIn(numRows * numColumns)
                        .nOut(500)
                        .build())
                .layer(1, new DenseLayer.Builder() //create the second input layer
                        .nIn(500)
                        .nOut(100)
                        .build())
                .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
                        .activation(Activation.SOFTMAX)
                        .nIn(100)
                        .nOut(outputNum)
                        .build())
                .pretrain(false).backprop(true) //use backpropagation to adjust weights
                .build();

        model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(mIterationListener);  //print the score with every iteration

        Log.d(TAG, "****************Example finished********************");
    }

    public void evaluate(int outputNum, DataSetIterator mnistTest) {
        Log.d(TAG, "Evaluate model....");
        Evaluation eval = new Evaluation(outputNum); //create an evaluation object with 10 possible classes
        while (mnistTest.hasNext()) {
            DataSet next = mnistTest.next();
            INDArray output = model.output(next.getFeatureMatrix()); //get the networks prediction
            eval.eval(next.getLabels(), output); //check the prediction against the true class
        }

        Log.d(TAG, eval.stats());
    }

    public void train(int numEpochs, DataSetIterator mnistTrain) {
        Log.d(TAG, "Train model....");
        for (int i = 0; i < numEpochs; i++) {
            Log.d(TAG, "Epoch " + i);
            model.fit(mnistTrain);
        }
    }

    @Override
    public String getId() {
        return mId;
    }

    @Override
    public void updateWeights(INDArray remoteGradient) {

    }

    @Override
    public INDArray getGradientAsArray() {
        return null;
    }

    @Override
    public void updateWeights(Gradient averageGradient) {

    }

    @Override
    public Gradient getGradient() {
        return model.gradient();
    }
}
