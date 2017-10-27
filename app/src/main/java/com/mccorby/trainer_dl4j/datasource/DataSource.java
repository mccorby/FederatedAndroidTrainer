package com.mccorby.trainer_dl4j.datasource;

import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.util.Random;

public interface DataSource {

    DataSetIterator getTrainingData(int batchSize, Random rand);
}
