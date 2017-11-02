package com.mccorby.federatedlearning.core.domain.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;

import io.reactivex.Observable;

public interface FederatedRepository {

    FederatedDataSet getTrainingData(int batchSize);
    FederatedDataSet getTestData(int batchSize);
    FederatedDataSet getCrossValidationData(int batchSize);

    // TODO Breaking Demeter's law here until deciding how to represent the gradients in the domain
    // TODO This method should only know about the gradient itself
    Observable<Boolean> uploadGradient(FederatedModel model);
}
