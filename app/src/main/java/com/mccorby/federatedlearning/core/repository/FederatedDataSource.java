package com.mccorby.federatedlearning.core.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;

public interface FederatedDataSource {

    FederatedDataSet getTrainingData();
    FederatedDataSet getTestData();
    FederatedDataSet getCrossValidationData();
}
