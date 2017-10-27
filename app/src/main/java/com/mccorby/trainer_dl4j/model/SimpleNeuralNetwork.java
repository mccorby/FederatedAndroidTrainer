package com.mccorby.trainer_dl4j.model;

import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;

public class SimpleNeuralNetwork {

    private static final int NUM_SAMPLES = 4;

    private MultiLayerNetwork multiLayerNetwork;
    private OutputLayer outputLayer;
    private DenseLayer inputLayer;

    public void build() {
        inputLayer = new DenseLayer.Builder()
                .nIn(2)
                .nOut(3)
                .name("Input")
                .build();

        DenseLayer hiddenLayer = new DenseLayer.Builder()
                .nIn(3)
                .nOut(2)
                .name("Hidden")
                .build();

        outputLayer = new OutputLayer.Builder()
                .nIn(2)
                .nOut(1)
                .name("Output")
                .build();

        NeuralNetConfiguration.Builder nncBuilder = new NeuralNetConfiguration.Builder();
        nncBuilder.iterations(10000);
        nncBuilder.learningRate(0.01);

        NeuralNetConfiguration.ListBuilder listBuilder = nncBuilder.list();
        listBuilder.layer(0, inputLayer);
        listBuilder.layer(1, hiddenLayer);
        listBuilder.layer(2, outputLayer);

        listBuilder.backprop(true);

        multiLayerNetwork = new MultiLayerNetwork(listBuilder.build());
    }

    public void init() {
        multiLayerNetwork.init();
    }

    public DataSet addTrainingData() {
        INDArray inputs = Nd4j.zeros(NUM_SAMPLES, inputLayer.getNIn());
        INDArray outputs = Nd4j.zeros(NUM_SAMPLES, outputLayer.getNOut());

        // If 0,0 show 0
        inputs.putScalar(new int[]{0, 0}, 0);
        inputs.putScalar(new int[]{0, 1}, 0);
        outputs.putScalar(new int[]{0, 0}, 0);

        // If 0,1 show 1
        inputs.putScalar(new int[]{1, 0}, 0);
        inputs.putScalar(new int[]{1, 1}, 1);
        outputs.putScalar(new int[]{1, 0}, 1);

        // If 1,0 show 1
        inputs.putScalar(new int[]{2, 0}, 1);
        inputs.putScalar(new int[]{2, 1}, 0);
        outputs.putScalar(new int[]{2, 0}, 1);

        // If 1,1 show 0
        inputs.putScalar(new int[]{3, 0}, 1);
        inputs.putScalar(new int[]{3, 1}, 1);
        outputs.putScalar(new int[]{3, 0}, 0);

        return new DataSet(inputs, outputs);
    }


    public void train(DataSet dataSet) {
        multiLayerNetwork.fit(dataSet);
    }

    public int[] predict(int[][] values) {
        INDArray input = Nd4j.create();
//        input.putScalar(values[0]);
        return multiLayerNetwork.predict(input);
    }
}
