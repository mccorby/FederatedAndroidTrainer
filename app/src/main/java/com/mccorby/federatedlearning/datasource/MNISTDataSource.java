package com.mccorby.federatedlearning.datasource;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;

import java.io.IOException;

public class MNISTDataSource implements TrainerDataSource {

    private int seed;

    public MNISTDataSource(int seed) {

        this.seed = seed;
    }

    @Override
    public DataSet getTrainingData(int batchSize) {
        MnistDataSetIterator trainingData = null;
        try {
            trainingData = new MnistDataSetIterator(batchSize, true, seed);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DataSet getTestData(int batchSize) {
        return null;
    }
}
