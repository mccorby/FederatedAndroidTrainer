package com.mccorby.federatedlearning.core.domain.usecase;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;

public class TrainModel implements UseCase {

    private final FederatedModel model;
    private final FederatedDataSet dataSource;
    private final UseCaseCallback<Boolean> callback;

    public TrainModel(FederatedModel model, FederatedDataSet dataSource,
                      UseCaseCallback<Boolean> callback) {

        this.model = model;
        this.dataSource = dataSource;
        this.callback = callback;
    }

    @Override
    public void execute() {
        if (model == null) {
            callback.onError(new UseCaseError());
        } else {
            model.buildModel();
            model.train(dataSource);
            callback.onSuccess(Boolean.TRUE);
        }
    }
}
