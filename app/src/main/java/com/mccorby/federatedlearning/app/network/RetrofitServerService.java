package com.mccorby.federatedlearning.app.network;

import com.mccorby.federatedlearning.datasource.network.ServerService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServerService {

    private static ServerService sInstance;

    // TODO This might be handled by DI
    public static ServerService getNetworkClient(String baseUrl) {
        if (sInstance == null) {
            sInstance = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ServerService.class);
        }
        return sInstance;
    }
}
