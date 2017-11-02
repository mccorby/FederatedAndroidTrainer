package com.mccorby.federatedlearning.core.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;

import io.reactivex.Observable;


public interface FederatedNetworkDataSource {

    Observable<Boolean> sendGradient(FederatedModel model);
}
