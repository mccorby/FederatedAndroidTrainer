package com.mccorby.federatedlearning.datasource.network;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.datasource.network.mapper.NetworkMapper;
import com.mccorby.federatedlearning.datasource.network.model.NetworkModel;

import org.junit.Test;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServerDataSourceTest {

    @Test
    public void testSendGradient() {
        // Given
        FederatedModel model = mock(FederatedModel.class);
        NetworkModel networkModel = mock(NetworkModel.class);
        NetworkMapper mapper = mock(NetworkMapper.class);
        given(mapper.toNetworkModel(model)).willReturn(networkModel);

        byte[] gradient = new byte[]{};
        given(networkModel.getGradient()).willReturn(gradient);

        ServerService networkService = mock(ServerService.class);

        // When
        ServerDataSource dataSource = new ServerDataSource(networkService, mapper);
        dataSource.sendGradient(model);

        // Then
        verify(mapper).toNetworkModel(model);
        verify(networkService).uploadGradient(gradient);
    }
}