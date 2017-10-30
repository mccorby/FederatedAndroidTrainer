package com.mccorby.federatedlearning.core.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.datasource.FederatedDataSource;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class FederatedRepositoryImplTest {

    @Test
    public void testGetTrainingData() {
        // Given
        int batchSize = 64;
        FederatedDataSource dataSource = mock(FederatedDataSource.class);
        FederatedDataSet dataSet = mock(FederatedDataSet.class);
        given(dataSource.getTrainingData(batchSize)).willReturn(dataSet);

        // When
        FederatedRepository cut = new FederatedRepositoryImpl(dataSource);
        FederatedDataSet trainingData = cut.getTrainingData(batchSize);

        // Then
        assertNotNull(trainingData);
    }

    @Test
    public void testGetTestData() {
        // Given
        int batchSize = 64;
        FederatedDataSource dataSource = mock(FederatedDataSource.class);
        FederatedDataSet dataSet = mock(FederatedDataSet.class);
        given(dataSource.getTestData(batchSize)).willReturn(dataSet);

        // When
        FederatedRepository cut = new FederatedRepositoryImpl(dataSource);
        FederatedDataSet testData = cut.getTestData(batchSize);

        // Then
        assertNotNull(testData);
    }

    @Test
    public void testGetCrossValidationData() {
        // Given
        int batchSize = 64;
        FederatedDataSource dataSource = mock(FederatedDataSource.class);
        FederatedDataSet dataSet = mock(FederatedDataSet.class);
        given(dataSource.getCrossValidationData(batchSize)).willReturn(dataSet);

        // When
        FederatedRepository cut = new FederatedRepositoryImpl(dataSource);
        FederatedDataSet crossValidationData = cut.getCrossValidationData(batchSize);

        // Then
        assertNotNull(crossValidationData);
    }
}