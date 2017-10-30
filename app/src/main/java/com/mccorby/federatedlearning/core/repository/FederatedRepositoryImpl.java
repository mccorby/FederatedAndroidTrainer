package com.mccorby.federatedlearning.core.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.datasource.FederatedDataSource;


public class FederatedRepositoryImpl implements FederatedRepository {

    private FederatedDataSource dataSource;

    public FederatedRepositoryImpl(FederatedDataSource dataSource) {

        this.dataSource = dataSource;
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
}
