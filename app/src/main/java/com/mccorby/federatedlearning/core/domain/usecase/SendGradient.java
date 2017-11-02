package com.mccorby.federatedlearning.core.domain.usecase;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;

import io.reactivex.Observer;
import io.reactivex.Scheduler;

public class SendGradient implements UseCaseRx<Boolean> {

    private FederatedRepository repository;
    private FederatedModel model;
    private final Scheduler originScheduler;
    private final Scheduler postScheduler;

    public SendGradient(FederatedRepository repository, FederatedModel model,
                        Scheduler originScheduler,
                        Scheduler postScheduler) {

        this.repository = repository;
        this.model = model;
        this.originScheduler = originScheduler;
        this.postScheduler = postScheduler;
    }

    @Override
    public void execute(Observer<Boolean> observer) {
        repository.uploadGradient(model)
                .subscribeOn(originScheduler)
                .observeOn(postScheduler)
                .subscribe();
    }
}
