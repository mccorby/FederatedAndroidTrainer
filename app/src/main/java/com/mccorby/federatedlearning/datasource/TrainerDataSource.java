package com.mccorby.federatedlearning.datasource;

import org.nd4j.linalg.dataset.DataSet;

public interface TrainerDataSource {

    DataSet getTrainingData(int batchSize);
    DataSet getTestData(int batchSize);
}
