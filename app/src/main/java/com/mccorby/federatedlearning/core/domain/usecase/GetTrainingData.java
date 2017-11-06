package com.mccorby.federatedlearning.core.domain.usecase;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;


public class GetTrainingData implements UseCase {
    private final FederatedRepository repository;
    private UseCaseCallback<FederatedRepository> useCaseCallback;

    public GetTrainingData(UseCaseCallback<FederatedRepository> useCaseCallback,
                           FederatedRepository repository) {
        this.useCaseCallback = useCaseCallback;

        this.repository = repository;
    }

    @Override
    public void execute() {
        FederatedDataSet trainingData = repository.getTrainingData();
        if (trainingData != null) {
            useCaseCallback.onSuccess(repository);
        } else {
            useCaseCallback.onError(new UseCaseError());
        }
    }
}
