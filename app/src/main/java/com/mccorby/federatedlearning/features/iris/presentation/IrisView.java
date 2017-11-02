package com.mccorby.federatedlearning.features.iris.presentation;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;

public interface IrisView {

    void onTrainingDone(FederatedModel model);

    void onDataReady(FederatedDataSet result);

    void onError(String errorMessage);

    void onGradientSent(Boolean aBoolean);

    void onGradientReceived(byte[] gradient);
}
