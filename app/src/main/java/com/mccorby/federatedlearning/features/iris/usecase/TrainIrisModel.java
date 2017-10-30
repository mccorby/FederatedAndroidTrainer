package com.mccorby.federatedlearning.features.iris.usecase;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.usecase.UseCase;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;
import com.mccorby.federatedlearning.datasource.FederatedDataSource;

public class TrainIrisModel implements UseCase {

    private final FederatedModel model;
    private final FederatedDataSource dataSource;
    private final UseCaseCallback<Boolean> callback;

    public TrainIrisModel(FederatedModel model, FederatedDataSource dataSource,
                          UseCaseCallback<Boolean> callback) {

        this.model = model;
        this.dataSource = dataSource;
        this.callback = callback;
    }

    @Override
    public void execute() {
        model.buildModel();
        model.train(dataSource);
        callback.onSuccess(Boolean.TRUE);
    }
}
