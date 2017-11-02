package com.mccorby.federatedlearning.features.mnist.datasource;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;

public class MNISTDataSource implements FederatedDataSource {

    private int seed;

    public MNISTDataSource(int seed) {

        this.seed = seed;
    }

    @Override
    public FederatedDataSet getTrainingData(int batchSize) {
        return null;
    }

    @Override
    public FederatedDataSet getTestData(int batchSize) {
        return null;
    }

    @Override
    public FederatedDataSet getCrossValidationData(int batchSize) {
        return null;
    }
}
