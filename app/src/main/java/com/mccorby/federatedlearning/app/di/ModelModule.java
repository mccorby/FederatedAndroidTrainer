package com.mccorby.federatedlearning.app.di;

import android.content.Context;
import android.content.res.AssetManager;

import com.mccorby.federatedlearning.app.executor.DefaultUseCaseExecutor;
import com.mccorby.federatedlearning.app.presentation.TrainerPresenter;
import com.mccorby.federatedlearning.app.presentation.TrainerView;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.core.repository.FederatedNetworkDataSource;
import com.mccorby.federatedlearning.core.repository.FederatedRepositoryImpl;
import com.mccorby.federatedlearning.datasource.network.ServerDataSource;
import com.mccorby.federatedlearning.datasource.network.ServerService;
import com.mccorby.federatedlearning.datasource.network.mapper.NetworkMapper;
import com.mccorby.federatedlearning.features.diabetes.datasource.DiabetesFileDataSource;
import com.mccorby.federatedlearning.features.diabetes.model.DiabetesModel;
import com.mccorby.federatedlearning.features.iris.datasource.IrisFileDataSource;
import com.mccorby.federatedlearning.features.iris.model.IrisModel;

import org.deeplearning4j.optimize.api.IterationListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

// TODO It would be better to have injections per feature
@Module
public class ModelModule {

    private final Context context;
    private TrainerView view;
    private final IterationListener iterationListener;

    public ModelModule(Context context, TrainerView view, IterationListener iterationListener) {
        this.context = context;
        this.view = view;
        this.iterationListener = iterationListener;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    public IterationListener provideIterationListener() {
        return iterationListener;
    }

    @Provides
    public TrainerView provideTrainerView() {
        return view;
    }

    @Provides
    @Named("iris")
    public FederatedModel provideIrisModel(IterationListener iterationListener) {
        return new IrisModel("Iris", 4, 3, iterationListener);
    }

    @Provides
    @Named("iris")
    public FederatedDataSource provideIrisDataSource(Context context) {
        return new IrisFileDataSource(getFile(context, "iris.csv"), 3);
    }

    @Provides
    @Named("diabetes")
    public FederatedModel provideDiabetesModel(IterationListener iterationListener) {
        return new DiabetesModel("Diabetes", 11, 1, iterationListener);
    }

    @Provides
    @Named("diabetes")
    public FederatedDataSource provideDiabetesDataSource(Context context) {
        return new DiabetesFileDataSource(getFile(context, "diabetes.csv"), 3);
    }

    @Provides
    public NetworkMapper provideNetworkMapper() {
        return new NetworkMapper();
    }

    @Provides
    public FederatedNetworkDataSource provideNetworkDataSource(ServerService serverService,
                                                               NetworkMapper mapper) {
        return new ServerDataSource(serverService, mapper);
    }

    @Provides
    public FederatedRepository provideRepository(FederatedDataSource dataSource,
                                                 FederatedNetworkDataSource networkDataSource) {
        return new FederatedRepositoryImpl(dataSource, networkDataSource);
    }

    @Provides
    public Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    public UseCaseExecutor provideUseCaseExecutor(Executor executor) {
        return new DefaultUseCaseExecutor(executor);
    }

    @Provides
    public TrainerPresenter provideTrainerPresenter(TrainerView view,
                                                    FederatedModel model,
                                                    FederatedRepository repository,
                                                    UseCaseExecutor executor) {
        return new TrainerPresenter(view, model, repository, executor, 64);
    }

    private InputStream getFile(Context context, String fileName) {
        AssetManager am = context.getAssets();
        try {
            return  am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
