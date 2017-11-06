package com.mccorby.federatedlearning.core.domain.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;

import io.reactivex.Observable;

public interface FederatedRepository {

    FederatedDataSet getTrainingData();
    FederatedDataSet getTestData();
    FederatedDataSet getCrossValidationData();

    Observable<Boolean> uploadGradient(byte[] gradient);
    Observable<byte[]> retrieveGradient();
}
