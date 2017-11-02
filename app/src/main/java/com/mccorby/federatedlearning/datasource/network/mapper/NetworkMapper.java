package com.mccorby.federatedlearning.datasource.network.mapper;

import com.mccorby.federatedlearning.datasource.network.model.NetworkModel;

public class NetworkMapper {

    public NetworkModel toNetworkModel(byte[] gradient) {
        return new NetworkModel(gradient);
    }
}
