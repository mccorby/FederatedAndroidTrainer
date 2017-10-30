package com.mccorby.federatedlearning.features.iris.presentation;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.usecase.UseCase;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseError;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.datasource.FederatedDataSource;
import com.mccorby.federatedlearning.features.iris.usecase.GetIrisTrainingData;

public class IrisPresenter implements UseCaseCallback<FederatedDataSet> {


    private FederatedDataSource dataSource;
    private final UseCaseExecutor executor;
    private int batchSize;

    public IrisPresenter(FederatedDataSource dataSource, UseCaseExecutor executor, int batchSize) {
        this.dataSource = dataSource;

        this.executor = executor;
        this.batchSize = batchSize;
    }

    public void retrieveTrainingData() {
        UseCase useCase = new GetIrisTrainingData(this, dataSource, batchSize);
        executor.execute(useCase);
    }

    @Override
    public void onSuccess(FederatedDataSet result) {

    }

    @Override
    public void onError(UseCaseError error) {

    }
}
