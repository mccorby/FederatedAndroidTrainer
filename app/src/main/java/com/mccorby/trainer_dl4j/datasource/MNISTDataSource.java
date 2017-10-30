package com.mccorby.trainer_dl4j.datasource;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;

import java.io.IOException;

public class MNISTDataSource implements TrainerDataSource {

    @Override
    public DataSet getTrainingData(int batchSize, int seed) {
        MnistDataSetIterator trainingData = null;
        try {
            trainingData = new MnistDataSetIterator(batchSize, true, seed);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public DataSet getTestData(int batchSize, int seed) {
        return null;
    }
}
