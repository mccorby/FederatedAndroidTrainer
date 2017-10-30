package com.mccorby.federatedlearning.features.iris.presentation;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.usecase.UseCase;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseError;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.datasource.FederatedDataSource;
import com.mccorby.federatedlearning.features.iris.usecase.GetIrisTrainingData;
import com.mccorby.federatedlearning.features.iris.usecase.TrainIrisModel;

public class IrisPresenter implements UseCaseCallback<FederatedDataSet>{


    private final FederatedModel model;
    private FederatedDataSource dataSource;
    private final UseCaseExecutor executor;
    private int batchSize;

    public IrisPresenter(FederatedModel model, FederatedDataSource dataSource, UseCaseExecutor executor, int batchSize) {
        this.model = model;
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
        UseCase useCase = new TrainIrisModel(model, result, new UseCaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {

            }

            @Override
            public void onError(UseCaseError error) {

            }
        });
        executor.execute(useCase);
    }

    @Override
    public void onError(UseCaseError error) {

    }
}
