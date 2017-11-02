package com.mccorby.federatedlearning.datasource.network;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServerService {

    @POST("/service/federatedservice/gradient")
    Observable<Boolean> uploadGradient(@Body byte[] gradient);

}
