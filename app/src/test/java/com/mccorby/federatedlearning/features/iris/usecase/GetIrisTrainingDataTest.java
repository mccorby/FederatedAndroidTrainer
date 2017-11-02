package com.mccorby.federatedlearning.features.iris.usecase;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class GetIrisTrainingDataTest {

    @Mock
    private FederatedDataSet dataSet;
    @Mock
    private UseCaseCallback<FederatedDataSet> useCaseCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetTrainingData() {
        // Given
        int batchSize = 64;
        FederatedDataSource dataSource = mock(FederatedDataSource.class);
        FederatedRepository repository = mock(FederatedRepository.class);

        given(dataSource.getTrainingData(batchSize)).willReturn(dataSet);

        // When
        GetIrisTrainingData cut = new GetIrisTrainingData(useCaseCallback, repository, batchSize);
        cut.execute();

        // Then
        verify(dataSource).getTrainingData(batchSize);
        verify(useCaseCallback).onSuccess(dataSet);
    }
}