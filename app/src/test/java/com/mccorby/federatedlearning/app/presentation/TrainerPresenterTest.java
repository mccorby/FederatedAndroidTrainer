package com.mccorby.federatedlearning.app.presentation;

import com.mccorby.federatedlearning.app.configuration.ModelConfiguration;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.domain.usecase.GetTrainingData;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.core.executor.UseCaseThreadExecutor;

import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class TrainerPresenterTest {

    @Test
    public void testGetTrainingDataIsPassedToTheView() {
        // Given
        int numberClients = 3;
        UseCaseExecutor executor = mock(UseCaseExecutor.class);
        FederatedRepository repository = mock(FederatedRepository.class);
        TrainerView view = mock(TrainerView.class);
        ModelConfiguration modelConfiguration = mock(ModelConfiguration.class);
        UseCaseThreadExecutor threadExecutor = mock(UseCaseThreadExecutor.class);

        // When
        TrainerPresenter cut = new TrainerPresenter(view,
                modelConfiguration,
                repository,
                executor,
                threadExecutor,
                numberClients);
        cut.retrieveData();

        // Then
        Mockito.verify(executor).execute(any(GetTrainingData.class));
    }
}