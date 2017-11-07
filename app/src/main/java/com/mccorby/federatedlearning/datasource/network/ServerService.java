package com.mccorby.federatedlearning.datasource.network;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ServerService {

    @POST("/service/federatedservice/gradient")
    Observable<Boolean> uploadGradient(@Body RequestBody gradient);

    @GET("/service/federatedservice/gradient")
    Observable<ResponseBody> getGradient();

    @GET("/service/federatedservice/register")
    Observable<Integer> register();
}
