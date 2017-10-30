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

// TODO Reaching callback hell very soon. Think moving to RxJava
// TODO Should FederatedModel be passed as a dependency or not?
public class IrisPresenter implements UseCaseCallback<FederatedDataSet>{

    private final IrisView view;
    private FederatedModel model;
    private final FederatedDataSource dataSource;
    private final UseCaseExecutor executor;
    private int batchSize;

    public IrisPresenter(IrisView view, FederatedModel model, FederatedDataSource dataSource, UseCaseExecutor executor, int batchSize) {
        this.view = view;
        this.model = model;
        this.dataSource = dataSource;
        this.executor = executor;
        this.batchSize = batchSize;
    }

    public void startProcess() {
        UseCase useCase = new GetIrisTrainingData(this, dataSource, batchSize);
        executor.execute(useCase);
    }

    @Override
    public void onSuccess(FederatedDataSet result) {
        view.onDataReady(result);
        UseCase useCase = new TrainIrisModel(model, result, new UseCaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result != null && result) {
                    view.onTrainingDone(model);
                }
            }

            @Override
            public void onError(UseCaseError error) {
                view.onError("Error training");
            }
        });
        executor.execute(useCase);
    }

    @Override
    public void onError(UseCaseError error) {
        view.onError("Error retrieving data");
    }

    public void setModel(FederatedModel model) {
        this.model = model;
    }
}
