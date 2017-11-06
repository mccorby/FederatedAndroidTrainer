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
        FederatedDataSource dataSource = Mockito.mock(FederatedDataSource.class);
        FederatedRepository repository = Mockito.mock(FederatedRepository.class);

        given(dataSource.getTrainingData()).willReturn(dataSet);
        given(repository.getTrainingData()).willReturn(dataSet);

        // When
        GetTrainingData cut = new GetTrainingData(useCaseCallback, repository);
        cut.execute();

        // Then
        Mockito.verify(repository).getTrainingData();
        Mockito.verify(useCaseCallback).onSuccess(repository);
    }
}