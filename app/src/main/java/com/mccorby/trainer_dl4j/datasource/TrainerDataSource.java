package com.mccorby.trainer_dl4j.datasource;

import org.nd4j.linalg.dataset.DataSet;

public interface TrainerDataSource {

    DataSet getTrainingData(int batchSize, int seed);
    DataSet getTestData(int batchSize, int seed);
}
