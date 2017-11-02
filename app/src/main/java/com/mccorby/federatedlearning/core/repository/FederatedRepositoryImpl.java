package com.mccorby.federatedlearning.core.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
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
    public FederatedDataSet getTrainingData(int batchSize) {
        return dataSource.getTrainingData(batchSize);
    }

    @Override
    public FederatedDataSet getTestData(int batchSize) {
        return dataSource.getTestData(batchSize);
    }

    @Override
    public FederatedDataSet getCrossValidationData(int batchSize) {
        return dataSource.getCrossValidationData(batchSize);
    }

    @Override
    public Observable<Boolean> uploadGradient(FederatedModel model) {
        return networkDataSource.sendGradient(model);
    }
}
