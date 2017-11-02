package com.mccorby.federatedlearning.features.iris.usecase;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.domain.usecase.UseCase;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseError;


public class GetIrisTrainingData implements UseCase {
    private final FederatedRepository repository;
    private UseCaseCallback<FederatedDataSet> useCaseCallback;
    private int batchSize;

    public GetIrisTrainingData(UseCaseCallback<FederatedDataSet> useCaseCallback,
                               FederatedRepository repository,
                               int batchSize) {
        this.useCaseCallback = useCaseCallback;

        this.repository = repository;
        this.batchSize = batchSize;
    }

    @Override
    public void execute() {
        FederatedDataSet trainingData = repository.getTrainingData(batchSize);
        if (trainingData != null) {
            useCaseCallback.onSuccess(trainingData);
        } else {
            useCaseCallback.onError(new UseCaseError());
        }
    }
}
