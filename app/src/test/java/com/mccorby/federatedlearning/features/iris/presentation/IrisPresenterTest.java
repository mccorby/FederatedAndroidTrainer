package com.mccorby.federatedlearning.features.iris.presentation;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.datasource.FederatedDataSource;
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

        // When
        IrisPresenter cut = new IrisPresenter(dataSource, executor, batchSize);
        cut.retrieveTrainingData();

        // Then
        verify(executor).execute(any(GetIrisTrainingData.class));
    }

}