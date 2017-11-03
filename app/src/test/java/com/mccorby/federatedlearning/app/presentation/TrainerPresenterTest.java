package com.mccorby.federatedlearning.app.presentation;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.core.domain.usecase.GetTrainingData;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public class TrainerPresenterTest {

    @Test
    public void testGetTrainingDataIsPassedToTheView() {
        // Given
        int batchSize = 64;
        UseCaseExecutor executor = Mockito.mock(UseCaseExecutor.class);
        FederatedRepository repository = Mockito.mock(FederatedRepository.class);
        TrainerView view = Mockito.mock(TrainerView.class);
        FederatedModel model = Mockito.mock(FederatedModel.class);

        // When
        TrainerPresenter cut = new TrainerPresenter(view, model, repository, executor, batchSize);
        cut.startProcess();

        // Then
        Mockito.verify(executor).execute(ArgumentMatchers.any(GetTrainingData.class));
    }

}