package com.mccorby.federatedlearning.features.iris.usecase;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.usecase.UseCase;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseError;
import com.mccorby.federatedlearning.datasource.FederatedDataSource;


public class GetIrisTrainingData implements UseCase {
    private UseCaseCallback<FederatedDataSet> useCaseCallback;
    private FederatedDataSource dataSource;
    private int batchSize;

    public GetIrisTrainingData(UseCaseCallback<FederatedDataSet> useCaseCallback, FederatedDataSource dataSource, int batchSize) {
        this.useCaseCallback = useCaseCallback;

        this.dataSource = dataSource;
        this.batchSize = batchSize;
    }

    @Override
    public void execute() {
        FederatedDataSet trainingData = dataSource.getTrainingData(batchSize);
        if (trainingData != null) {
            useCaseCallback.onSuccess(trainingData);
        } else {
            useCaseCallback.onError(new UseCaseError());
        }
    }
}
