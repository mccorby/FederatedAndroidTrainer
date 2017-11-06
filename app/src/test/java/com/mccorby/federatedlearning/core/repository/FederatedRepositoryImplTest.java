package com.mccorby.federatedlearning.core.repository;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
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
        given(dataSource.getTrainingData()).willReturn(dataSet);

        // When
        FederatedDataSet trainingData = cut.getTrainingData();

        // Then
        assertNotNull(trainingData);
    }

    @Test
    public void testGetTestData() {
        // Given
        given(dataSource.getTestData()).willReturn(dataSet);

        // When
        FederatedDataSet testData = cut.getTestData();

        // Then
        assertNotNull(testData);
    }

    @Test
    public void testGetCrossValidationData() {
        // Given
        given(dataSource.getCrossValidationData()).willReturn(dataSet);

        // When
        FederatedDataSet crossValidationData = cut.getCrossValidationData();

        // Then
        assertNotNull(crossValidationData);
    }

    @Test
    public void testGradientIsSentToServer() {
        // Given
        byte[] gradient = new byte[]{};

        // When
        cut.uploadGradient(gradient);

        // Then
        verify(networkDataSource).sendGradient(gradient);
    }
}