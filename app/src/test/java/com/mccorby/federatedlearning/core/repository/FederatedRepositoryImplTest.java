package com.mccorby.federatedlearning.core.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class FederatedRepositoryImplTest {

    @Mock
    private FederatedDataSource dataSource;
    @Mock
    private FederatedNetworkDataSource networkDataSource;
    @Mock
    private FederatedDataSet dataSet;

    private FederatedRepository cut;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cut = new FederatedRepositoryImpl(dataSource, networkDataSource);
    }

    @Test
    public void testGetTrainingData() {
        // Given
        int batchSize = 64;
        given(dataSource.getTrainingData(batchSize)).willReturn(dataSet);

        // When
        FederatedDataSet trainingData = cut.getTrainingData(batchSize);

        // Then
        assertNotNull(trainingData);
    }

    @Test
    public void testGetTestData() {
        // Given
        int batchSize = 64;
        given(dataSource.getTestData(batchSize)).willReturn(dataSet);

        // When
        FederatedDataSet testData = cut.getTestData(batchSize);

        // Then
        assertNotNull(testData);
    }

    @Test
    public void testGetCrossValidationData() {
        // Given
        int batchSize = 64;
        given(dataSource.getCrossValidationData(batchSize)).willReturn(dataSet);

        // When
        FederatedDataSet crossValidationData = cut.getCrossValidationData(batchSize);

        // Then
        assertNotNull(crossValidationData);
    }

    @Test
    public void testGradientIsSentToServer() {
        // Given
        FederatedModel model = mock(FederatedModel.class);

        // When
        cut.uploadGradient(model);

        // Then
        verify(networkDataSource).sendGradient(model);
    }
}