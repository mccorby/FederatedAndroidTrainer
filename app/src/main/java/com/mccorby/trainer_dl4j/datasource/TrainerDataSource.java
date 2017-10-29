package com.mccorby.trainer_dl4j.datasource;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.Random;

public interface TrainerDataSource {

    DataSetIterator getTrainingData(int batchSize, Random rand);
    DataSet getTestData(int batchSize, Random rand);
}
