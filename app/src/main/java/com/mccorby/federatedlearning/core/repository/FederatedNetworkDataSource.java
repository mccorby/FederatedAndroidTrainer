package com.mccorby.federatedlearning.core.repository;

import io.reactivex.Observable;


public interface FederatedNetworkDataSource {

    Observable<Boolean> sendGradient(byte[] gradient);

    Observable<byte[]> retrieveGradient();

    Observable<Integer> registerModel();
}
