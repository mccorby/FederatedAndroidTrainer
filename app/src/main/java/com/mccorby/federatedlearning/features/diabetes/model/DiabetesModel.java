package com.mccorby.federatedlearning.features.diabetes.model;

import android.util.Log;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;

import org.deeplearning4j.eval.RegressionEvaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import static android.content.ContentValues.TAG;

public class DiabetesModel implements FederatedModel {

    private MultiLayerNetwork model;
    private String id;
    private final int numInputs;
    private final int numClasses;
    private IterationListener iterationListener;

    public DiabetesModel(String id, int numInputs, int numClasses, IterationListener iterationListener) {
        this.id = id;
        this.numInputs = numInputs;
        this.numClasses = numClasses;
        this.iterationListener = iterationListener;
    }

    public void buildModel() {
        if (model == null) {
            int iterations = 1000;
            long seed = 6;

            MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                    .seed(seed)
                    .iterations(iterations)
                    .activation(Activation.TANH)
                    .weightInit(WeightInit.XAVIER)
                    .learningRate(0.1)
                    .regularization(true).l2(1e-4)
                    .list()
                    .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(3)
                            .build())
                    .layer(1, new DenseLayer.Builder().nIn(3).nOut(3)
                            .build())
                    .layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.MEAN_SQUARED_LOGARITHMIC_ERROR)
                            .activation(Activation.SOFTMAX)
                            .nIn(3).nOut(numClasses).build())
                    .backprop(true).pretrain(false)
                    .build();

            //run the model
            model = new MultiLayerNetwork(conf);
            model.init();
            model.setListeners(iterationListener);
        }
    }

    @Override
    public void train(FederatedDataSet trainingData) {
        model.fit((DataSet) trainingData.getNativeDataSet());
    }

    @Override
    public String evaluate(FederatedDataSet federatedDataSet) {
        //evaluate the model on the test set
        DataSet testData = (DataSet) federatedDataSet.getNativeDataSet();
        RegressionEvaluation eval = new RegressionEvaluation(12);
        INDArray output = model.output(testData.getFeatureMatrix());
        eval.eval(testData.getLabels(), output);
        return "MSE: " + eval.meanSquaredError(11);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void updateWeights(INDArray remoteGradient) {
        Log.d(TAG, "Updating weights with INDArray object");
        INDArray params = model.params(true);
        params.addi(remoteGradient);
    }

    @Override
    public INDArray getGradient() {
        return model.gradient().gradient();
    }
}
