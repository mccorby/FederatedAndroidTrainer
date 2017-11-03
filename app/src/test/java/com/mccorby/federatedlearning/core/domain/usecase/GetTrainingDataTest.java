package com.mccorby.federatedlearning.core.domain.usecase;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;

public class GetTrainingDataTest {

    @Mock
    private FederatedDataSet dataSet;
    @Mock
    private UseCaseCallback<FederatedRepository> useCaseCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetTrainingData() {
        // Given
        int batchSize = 64;
        FederatedDataSource dataSource = Mockito.mock(FederatedDataSource.class);
        FederatedRepository repository = Mockito.mock(FederatedRepository.class);

        given(dataSource.getTrainingData(batchSize)).willReturn(dataSet);
        given(repository.getTrainingData(batchSize)).willReturn(dataSet);

        // When
        GetTrainingData cut = new GetTrainingData(useCaseCallback, repository, batchSize);
        cut.execute();

        // Then
        Mockito.verify(repository).getTrainingData(batchSize);
        Mockito.verify(useCaseCallback).onSuccess(repository);
    }
}