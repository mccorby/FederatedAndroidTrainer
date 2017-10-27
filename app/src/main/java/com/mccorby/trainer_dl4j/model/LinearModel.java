package com.mccorby.trainer_dl4j.model;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;


public class LinearModel {

    //Number of ITERATIONS per minibatch
    private static final int ITERATIONS = 1;
    //Number of epochs (full passes of the data)
    private static final int N_EPOCHS = 200;
    //Network learning rate
    private static final double LEARNING_RATE = 0.01;

    private IterationListener mIterationListener;
    private int mSeed;
    private MultiLayerNetwork mNetwork;

    public LinearModel(IterationListener iterationListener, int seed) {
        mIterationListener = iterationListener;
        mSeed = seed;
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
                        .build())
                .layer(1, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                        .activation(Activation.IDENTITY)
                        .nIn(nHidden).nOut(numOutputs).build())
                .pretrain(false).backprop(true).build()
        );
        mNetwork.init();
        mNetwork.setListeners(mIterationListener);
    }

    public void train(DataSetIterator iterator) {
        //Train the network on the full data set, and evaluate in periodically
        for (int i = 0; i < N_EPOCHS; i++) {
            iterator.reset();
            mNetwork.fit(iterator);
        }
    }

    public INDArray predict(final INDArray input) {
        return mNetwork.output(input, false);
    }
}
