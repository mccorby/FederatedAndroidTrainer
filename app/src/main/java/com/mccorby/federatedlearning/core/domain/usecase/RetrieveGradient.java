package com.mccorby.federatedlearning.core.domain.usecase;

import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;

import io.reactivex.Observer;
import io.reactivex.Scheduler;

public class RetrieveGradient implements UseCaseRx<byte[]> {

    private final FederatedRepository repository;
    private final Scheduler originScheduler;
    private final Scheduler postScheduler;

    public RetrieveGradient(FederatedRepository repository,
                            Scheduler originScheduler,
                            Scheduler postScheduler) {

        this.repository = repository;
        this.originScheduler = originScheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public void execute(Observer<byte[]> observer) {
        repository.retrieveGradient()
                .subscribeOn(originScheduler)
                .observeOn(postScheduler)
                .subscribeWith(observer);
    }
}
