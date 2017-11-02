package com.mccorby.federatedlearning.core.domain.usecase;

import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;

import io.reactivex.Observer;
import io.reactivex.Scheduler;

public class SendGradient implements UseCaseRx<Boolean> {

    private FederatedRepository repository;
    private byte[] gradient;
    private final Scheduler originScheduler;
    private final Scheduler postScheduler;

    public SendGradient(FederatedRepository repository, byte[] gradient,
                        Scheduler originScheduler,
                        Scheduler postScheduler) {

        this.repository = repository;
        this.gradient = gradient;
        this.originScheduler = originScheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public void execute(Observer<Boolean> observer) {
        repository.uploadGradient(gradient)
                .subscribeOn(originScheduler)
                .observeOn(postScheduler)
                .subscribeWith(observer);
    }
}
