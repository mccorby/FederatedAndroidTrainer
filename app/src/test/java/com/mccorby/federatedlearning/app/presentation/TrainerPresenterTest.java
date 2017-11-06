package com.mccorby.federatedlearning.app.presentation;

import com.mccorby.federatedlearning.app.configuration.ModelConfiguration;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.domain.usecase.GetTrainingData;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;

import org.junit.Test;
import org.mockito.Mockito;

import io.reactivex.Scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class TrainerPresenterTest {

    @Test
    public void testGetTrainingDataIsPassedToTheView() {
        // Given
        int batchSize = 64;
        int dataSetSplits = 3;
        UseCaseExecutor executor = mock(UseCaseExecutor.class);
        FederatedRepository repository = mock(FederatedRepository.class);
        TrainerView view = mock(TrainerView.class);
        Scheduler origin = mock(Scheduler.class);
        Scheduler post = mock(Scheduler.class);
        ModelConfiguration modelConfiguration = mock(ModelConfiguration.class);

        // When
        TrainerPresenter cut = new TrainerPresenter(view,
                modelConfiguration,
                repository,
                executor,
                origin,
                post,
                batchSize,
                dataSetSplits);
        cut.retrieveData();

        // Then
        Mockito.verify(executor).execute(any(GetTrainingData.class));
    }
}