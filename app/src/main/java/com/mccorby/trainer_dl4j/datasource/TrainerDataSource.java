package com.mccorby.trainer_dl4j.datasource;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

public interface TrainerDataSource {

    DataSetIterator getTrainingData(int batchSize, int seed);
    DataSet getTestData(int batchSize, int seed);
}
