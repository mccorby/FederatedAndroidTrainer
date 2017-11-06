package com.mccorby.federatedlearning.core.domain.model;

public interface FederatedDataSet<T> {

    T getNativeDataSet();

    FederatedDataSet<T> getSubSet(int from, int to);

    int size();
}
