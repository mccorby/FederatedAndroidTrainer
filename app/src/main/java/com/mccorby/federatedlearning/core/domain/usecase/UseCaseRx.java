package com.mccorby.federatedlearning.core.domain.usecase;

import io.reactivex.Observer;

public interface UseCaseRx<T> {

    void execute(Observer<T> observer);
}
