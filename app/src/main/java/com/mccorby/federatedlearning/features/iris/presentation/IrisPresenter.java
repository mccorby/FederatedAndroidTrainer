package com.mccorby.federatedlearning.features.iris.presentation;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.domain.usecase.RetrieveGradient;
import com.mccorby.federatedlearning.core.domain.usecase.SendGradient;
import com.mccorby.federatedlearning.core.domain.usecase.UseCase;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseError;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.features.iris.usecase.GetIrisTrainingData;
import com.mccorby.federatedlearning.features.iris.usecase.TrainIrisModel;

import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

// TODO Reaching callback hell very soon. Think moving to RxJava
// TODO Should FederatedModel be passed as a dependency or not?
public class IrisPresenter implements UseCaseCallback<FederatedRepository>{

    private final IrisView view;
    private FederatedModel model;
    private final FederatedRepository repository;
    private final UseCaseExecutor executor;
    private int batchSize;

    public IrisPresenter(IrisView view,
                         FederatedModel model,
                         FederatedRepository repository,
                         UseCaseExecutor executor,
                         int batchSize) {
        this.view = view;
        this.model = model;
        this.executor = executor;
        this.repository = repository;
        this.batchSize = batchSize;
    }

    public void startProcess() {
        UseCase useCase = new GetIrisTrainingData(this, repository, batchSize);
        executor.execute(useCase);
    }

    @Override
    public void onSuccess(FederatedRepository result) {
        view.onDataReady(result);
        UseCase useCase = new TrainIrisModel(model, result.getTrainingData(batchSize), new UseCaseCallback<Boolean>() {
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
        Scheduler origin = Schedulers.from(Executors.newSingleThreadExecutor());
        Scheduler postScheduler = AndroidSchedulers.mainThread();
        SendGradient sendGradient = new SendGradient(repository, gradient, origin, postScheduler);
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
        Scheduler origin = Schedulers.from(Executors.newSingleThreadExecutor());
        Scheduler postScheduler = AndroidSchedulers.mainThread();
        RetrieveGradient retrieveGradient = new RetrieveGradient(repository, origin, postScheduler);
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
