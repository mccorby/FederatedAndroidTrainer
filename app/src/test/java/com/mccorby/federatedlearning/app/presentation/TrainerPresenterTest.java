package com.mccorby.federatedlearning.app.presentation;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.core.domain.usecase.GetTrainingData;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import io.reactivex.Scheduler;

import static org.mockito.Mockito.mock;

public class TrainerPresenterTest {

    @Test
    public void testGetTrainingDataIsPassedToTheView() {
        // Given
        int batchSize = 64;
        UseCaseExecutor executor = mock(UseCaseExecutor.class);
        FederatedRepository repository = mock(FederatedRepository.class);
        TrainerView view = mock(TrainerView.class);
        FederatedModel model = mock(FederatedModel.class);
        Scheduler origin = mock(Scheduler.class);
        Scheduler post = mock(Scheduler.class);

        // When
        TrainerPresenter cut = new TrainerPresenter(view, model, repository, executor, origin, post, batchSize);
        cut.startProcess();

        // Then
        Mockito.verify(executor).execute(ArgumentMatchers.any(GetTrainingData.class));
    }

}