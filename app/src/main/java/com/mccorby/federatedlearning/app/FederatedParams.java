package com.mccorby.federatedlearning.app;

import com.google.gson.annotations.SerializedName;

public class FederatedParams {

    @SerializedName("model")
    private String model;
    @SerializedName("server_url")
    private String serverUrl;
    @SerializedName("max_clients")
    private int maxClients;
    @SerializedName("batch_size")
    private int batchSize;

    public String getModel() {
        return model;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public int getMaxClients() {
        return maxClients;
    }

    public int getBatchSize() {
        return batchSize;
    }
}
