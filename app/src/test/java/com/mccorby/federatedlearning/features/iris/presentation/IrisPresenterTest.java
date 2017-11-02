package com.mccorby.federatedlearning.features.iris.presentation;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.features.iris.usecase.GetIrisTrainingData;

import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class IrisPresenterTest {

    @Test
    public void testGetTrainingDataIsPassedToTheView() {
        // Given
        int batchSize = 64;
        UseCaseExecutor executor = mock(UseCaseExecutor.class);
        FederatedDataSource dataSource = mock(FederatedDataSource.class);
        FederatedRepository repository = mock(FederatedRepository.class);
        IrisView view = mock(IrisView.class);
        FederatedModel model = mock(FederatedModel.class);

        // When
        IrisPresenter cut = new IrisPresenter(view, model, repository, executor, batchSize);
        cut.startProcess();

        // Then
        verify(executor).execute(any(GetIrisTrainingData.class));
    }

}