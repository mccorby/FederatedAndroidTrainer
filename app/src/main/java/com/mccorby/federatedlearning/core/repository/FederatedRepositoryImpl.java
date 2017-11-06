package com.mccorby.federatedlearning.core.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;

import io.reactivex.Observable;


public class FederatedRepositoryImpl implements FederatedRepository {

    private FederatedDataSource dataSource;
    private FederatedNetworkDataSource networkDataSource;

    public FederatedRepositoryImpl(FederatedDataSource dataSource, FederatedNetworkDataSource networkDataSource) {

        this.dataSource = dataSource;
        this.networkDataSource = networkDataSource;
    }

    @Override
    public FederatedDataSet getTrainingData() {
        return dataSource.getTrainingData();
    }

    @Override
    public FederatedDataSet getTestData() {
        return dataSource.getTestData();
    }

    @Override
    public FederatedDataSet getCrossValidationData() {
        return dataSource.getCrossValidationData();
    }

    @Override
    public Observable<Boolean> uploadGradient(byte[] gradient) {
        return networkDataSource.sendGradient(gradient);
    }

    @Override
    public Observable<byte[]> retrieveGradient() {
        return networkDataSource.retrieveGradient();

    }
}
