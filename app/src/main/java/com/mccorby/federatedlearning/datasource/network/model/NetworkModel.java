package com.mccorby.federatedlearning.datasource.network.model;

public class NetworkModel {

    private byte[] gradient;

    public NetworkModel(byte[] gradient) {

        this.gradient = gradient;
    }

    public byte[] getGradient() {
        return gradient;
    }
}
