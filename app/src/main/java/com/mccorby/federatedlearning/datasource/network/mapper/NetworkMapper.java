package com.mccorby.federatedlearning.datasource.network.mapper;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.datasource.network.model.NetworkModel;

import org.nd4j.linalg.factory.Nd4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NetworkMapper {

    public NetworkModel toNetworkModel(FederatedModel model) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Nd4j.write(outputStream, model.getGradient().gradient());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new NetworkModel(outputStream.toByteArray());
    }
}
