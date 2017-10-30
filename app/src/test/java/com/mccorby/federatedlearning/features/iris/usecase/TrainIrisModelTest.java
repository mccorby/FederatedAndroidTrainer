package com.mccorby.federatedlearning.features.iris.usecase;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TrainIrisModelTest {

    @Mock
    private UseCaseCallback<Boolean> useCaseCallback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testModelIsTrained() {
        // Given
        FederatedModel model = mock(FederatedModel.class);
        FederatedDataSet dataSource = mock(FederatedDataSet.class);

        // When
        TrainIrisModel cut = new TrainIrisModel(model, dataSource, useCaseCallback);
        cut.execute();

        // Then
        verify(model).buildModel();
        verify(model).train(dataSource);
        verify(useCaseCallback).onSuccess(Boolean.TRUE);
    }
}