package com.mccorby.federatedlearning.datasource;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;

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
