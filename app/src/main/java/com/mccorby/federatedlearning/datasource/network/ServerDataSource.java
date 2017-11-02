package com.mccorby.federatedlearning.datasource.network;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.repository.FederatedNetworkDataSource;
import com.mccorby.federatedlearning.datasource.network.mapper.NetworkMapper;
import com.mccorby.federatedlearning.datasource.network.model.NetworkModel;

import io.reactivex.Observable;


public class ServerDataSource implements FederatedNetworkDataSource {

    private ServerService networkService;
    private NetworkMapper mapper;

    public ServerDataSource(ServerService networkService, NetworkMapper mapper) {
        this.networkService = networkService;

        this.mapper = mapper;
    }

    @Override
    public Observable<Boolean> sendGradient(FederatedModel model) {
        NetworkModel networkModel = mapper.toNetworkModel(model);
        return networkService.uploadGradient(networkModel.getGradient());
    }
}
