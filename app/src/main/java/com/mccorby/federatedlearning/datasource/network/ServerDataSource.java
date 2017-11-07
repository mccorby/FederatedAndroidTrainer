package com.mccorby.federatedlearning.datasource.network;

import com.mccorby.federatedlearning.core.repository.FederatedNetworkDataSource;
import com.mccorby.federatedlearning.datasource.network.mapper.NetworkMapper;
import com.mccorby.federatedlearning.datasource.network.model.NetworkModel;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class ServerDataSource implements FederatedNetworkDataSource {

    private ServerService networkService;
    private NetworkMapper mapper;

    public ServerDataSource(ServerService networkService, NetworkMapper mapper) {
        this.networkService = networkService;

        this.mapper = mapper;
    }

    @Override
    public Observable<Boolean> sendGradient(byte[] gradient) {
        NetworkModel networkModel = mapper.toNetworkModel(gradient);
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/octet-stream"), networkModel.getGradient());
        return networkService.uploadGradient(requestBody);
    }

    @Override
    public Observable<byte[]> retrieveGradient() {
        return networkService.getGradient().map(new Function<ResponseBody, byte[]>() {
            @Override
            public byte[] apply(@NonNull ResponseBody response) throws Exception {
                return response.bytes();
            }
        });
    }

    @Override
    public Observable<Integer> registerModel() {
        return networkService.register();
    }
}
