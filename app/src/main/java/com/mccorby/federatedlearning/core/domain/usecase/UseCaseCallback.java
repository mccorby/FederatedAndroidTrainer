package com.mccorby.federatedlearning.core.domain.usecase;

public interface UseCaseCallback<T> {

    void onSuccess(T result);
    void onError(UseCaseError error);
}
