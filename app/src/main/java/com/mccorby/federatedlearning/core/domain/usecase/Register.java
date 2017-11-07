package com.mccorby.federatedlearning.core.domain.usecase;

import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;

import io.reactivex.Observer;
import io.reactivex.Scheduler;

public class Register implements UseCaseRx<Integer> {

    private final FederatedRepository repository;
    private final Scheduler originScheduler;
    private final Scheduler postScheduler;

    public Register(FederatedRepository repository, Scheduler originScheduler, Scheduler postScheduler) {
        this.repository  =repository;
        this.originScheduler = originScheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public void execute(Observer<Integer> observer) {
        repository.registerModel()
                .subscribeOn(originScheduler)
                .observeOn(postScheduler)
                .subscribeWith(observer);
    }
}
