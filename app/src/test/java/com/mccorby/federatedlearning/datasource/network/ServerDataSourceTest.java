package com.mccorby.federatedlearning.datasource.network;

import com.mccorby.federatedlearning.datasource.network.mapper.NetworkMapper;
import com.mccorby.federatedlearning.datasource.network.model.NetworkModel;

import org.junit.Test;

import okhttp3.RequestBody;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ServerDataSourceTest {

    @Test
    public void testSendGradient() {
        // Given
        byte[] gradient = new byte[]{};
        NetworkModel networkModel = mock(NetworkModel.class);
        NetworkMapper mapper = mock(NetworkMapper.class);
        given(mapper.toNetworkModel(gradient)).willReturn(networkModel);

        given(networkModel.getGradient()).willReturn(gradient);

        ServerService networkService = mock(ServerService.class);

        // When
        ServerDataSource dataSource = new ServerDataSource(networkService, mapper);
        dataSource.sendGradient(gradient);

        // Then
        verify(mapper).toNetworkModel(gradient);
        verify(networkService).uploadGradient(any(RequestBody.class));
    }
}