package com.mccorby.federatedlearning.app.presentation;

import com.mccorby.federatedlearning.app.configuration.ModelConfiguration;
import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.domain.usecase.GetTrainingData;
import com.mccorby.federatedlearning.core.domain.usecase.RetrieveGradient;
import com.mccorby.federatedlearning.core.domain.usecase.SendGradient;
import com.mccorby.federatedlearning.core.domain.usecase.TrainModel;
import com.mccorby.federatedlearning.core.domain.usecase.UseCase;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseCallback;
import com.mccorby.federatedlearning.core.domain.usecase.UseCaseError;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

// TODO Reaching callback hell very soon. Think moving to RxJava
// TODO Should FederatedModel be passed as a dependency or not?
public class TrainerPresenter implements UseCaseCallback<FederatedRepository>{

    private final TrainerView view;
    private final Scheduler postScheduler;
    private final Scheduler originScheduler;
    private final FederatedRepository repository;
    private ModelConfiguration modelConfiguration;
    private final UseCaseExecutor executor;
    private int batchSize;

    private List<FederatedModel> models;
    private FederatedDataSet testDataSet;

    public TrainerPresenter(TrainerView view,
                            ModelConfiguration modelConfiguration,
                            FederatedRepository repository,
                            UseCaseExecutor executor,
                            Scheduler originScheduler,
                            Scheduler postScheduler,
                            int batchSize) {
        this.view = view;
        this.modelConfiguration = modelConfiguration;
        this.executor = executor;
        this.repository = repository;
        this.batchSize = batchSize;
        this.originScheduler = originScheduler;
        this.postScheduler = postScheduler;

        models = new ArrayList<>();
    }

    public void retrieveData() {
        UseCase useCase = new GetTrainingData(this, repository, batchSize);
        executor.execute(useCase);
    }

    @Override
    public void onSuccess(FederatedRepository result) {
        view.onDataReady(result);
        // Keeping the same test dataset for all models trained in this client
        if (testDataSet == null) {
            testDataSet = result.getTestData(batchSize);
        }
    }

    @Override
    public void onError(UseCaseError error) {
        view.onError("Error retrieving data");
    }

    // TODO This method does not correspond to this object
    public void sendGradient() {
        // TODO Having a Nd4j in this presenter does not look good.
        FederatedModel currentModel = models.get(models.size() - 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Nd4j.write(outputStream, currentModel.getGradient());
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] gradient = outputStream.toByteArray();
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
            public void onNext(@NonNull byte[] gradient) {
                try {
                    INDArray remoteGradient = Nd4j.fromByteArray(gradient);
                    for (FederatedModel model: models) {
                        model.updateWeights(remoteGradient);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                view.onGradientReceived(gradient);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void train() {
        FederatedModel model = modelConfiguration.getNewModel(models.size() + 1);
        models.add(model);
        UseCase useCase = new TrainModel(model, repository.getTrainingData(batchSize), new UseCaseCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result != null && result) {
                    sendGradient();
                    view.onTrainingDone();
                }
            }

            @Override
            public void onError(UseCaseError error) {
                view.onError("Error training");
            }
        });
        executor.execute(useCase);
    }

    public void predict() {
        // Show the current model evaluation
        for (FederatedModel model: models) {
            String score = model.evaluate(testDataSet);

            String message = "\nScore for " + model.getId() + " => " + score + "\n";
            view.onPrediction(message);
        }
    }
}
