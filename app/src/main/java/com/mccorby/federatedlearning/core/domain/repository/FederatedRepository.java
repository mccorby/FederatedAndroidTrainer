package com.mccorby.federatedlearning.core.domain.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;

public interface FederatedRepository {

    FederatedDataSet getTrainingData(int batchSize);
    FederatedDataSet getTestData(int batchSize);
    FederatedDataSet getCrossValidationData(int batchSize);
}
