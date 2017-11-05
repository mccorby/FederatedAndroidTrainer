package com.mccorby.federatedlearning.app.presentation;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.domain.usecase.GetTrainingData;
import com.mccorby.federatedlearning.core.domain.usecase.Register;
import com.mccorby.federatedlearning.core.domain.usecase.RetrieveGradient;
import com.mccorby.federatedlearning.core.domain.usecase.SendGradient;
import com.mccorby.federatedlearning.core.domain.usecase.TrainModel;
import com.mccorby.federatedlearning.core.domain.usecase.UseCase;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseError;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

// TODO Reaching callback hell very soon. Think moving to RxJava
// TODO Should FederatedModel be passed as a dependency or not?
public class TrainerPresenter implements UseCaseCallback<FederatedRepository>{

    private final TrainerView view;
    private final Scheduler postScheduler;
    private final Scheduler originScheduler;
    private FederatedModel model;
    private final FederatedRepository repository;
    private final UseCaseExecutor executor;
    private int batchSize;

    public TrainerPresenter(TrainerView view,
                            FederatedModel model,
                            FederatedRepository repository,
                            UseCaseExecutor executor,
                            Scheduler originScheduler,
                            Scheduler postScheduler,
                            int batchSize) {
        this.view = view;
        this.model = model;
        this.executor = executor;
        this.repository = repository;
        this.batchSize = batchSize;
        this.originScheduler = originScheduler;
        this.postScheduler = postScheduler;

    }

    public void startProcess() {
        UseCase useCase = new GetTrainingData(this, repository, batchSize);
        executor.execute(useCase);
    }

    @Override
    public void onSuccess(FederatedRepository result) {
        view.onDataReady(result);
        UseCase useCase = new TrainModel(model, result.getTrainingData(batchSize), new UseCaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result != null && result) {
                    view.onTrainingDone(model);
                }
            }

            @Override
            public void onError(UseCaseError error) {
                view.onError("Error training");
            }
        });
        executor.execute(useCase);
    }

    @Override
    public void onError(UseCaseError error) {
        view.onError("Error retrieving data");
    }

    public void setModel(FederatedModel model) {
        this.model = model;
    }

    // TODO This method does not correspond to this object
    public void sendGradient(byte[] gradient) {
        SendGradient sendGradient = new SendGradient(repository, gradient, originScheduler, postScheduler);
        sendGradient.execute(new DisposableObserver<Boolean>() {
            @Override
            public void onNext(@NonNull Boolean aBoolean) {
                view.onGradientSent(aBoolean);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    // TODO This method does not correspond to this object
    public void getUpdatedGradient() {
        RetrieveGradient retrieveGradient = new RetrieveGradient(repository, originScheduler, postScheduler);
        retrieveGradient.execute(new DisposableObserver<byte[]>() {
            @Override
            public void onNext(@NonNull byte[] bytes) {
                view.onGradientReceived(bytes);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
