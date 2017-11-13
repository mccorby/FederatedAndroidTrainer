package com.mccorby.federatedlearning.di;

import android.content.Context;

import com.google.gson.Gson;
import com.mccorby.federatedlearning.app.FederatedParams;
import com.mccorby.federatedlearning.app.configuration.ModelConfiguration;
import com.mccorby.federatedlearning.app.configuration.ModelConfigurationFactory;
import com.mccorby.federatedlearning.app.executor.DefaultUseCaseExecutor;
import com.mccorby.federatedlearning.app.executor.DefaultUseCaseThreadExecutor;
import com.mccorby.federatedlearning.app.presentation.TrainerView;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.executor.UseCaseExecutor;
import com.mccorby.federatedlearning.core.executor.UseCaseThreadExecutor;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.core.repository.FederatedNetworkDataSource;
import com.mccorby.federatedlearning.core.repository.FederatedRepositoryImpl;
import com.mccorby.federatedlearning.datasource.network.ServerDataSource;
import com.mccorby.federatedlearning.datasource.network.ServerService;
import com.mccorby.federatedlearning.datasource.network.mapper.NetworkMapper;

import org.deeplearning4j.optimize.api.IterationListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class MainModule {

    private TrainerView view;
    private Context context;
    private IterationListener iterationListener;

    public MainModule(TrainerView view, Context context, IterationListener IterationListener) {
        this.view = view;

        this.context = context;
        iterationListener = IterationListener;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    public UseCaseExecutor provideUseCaseExecutor() {
        return new DefaultUseCaseExecutor(Executors.newSingleThreadExecutor());
    }

    @Provides
    public FederatedParams provideFederatedParams(Context context) {
        // TODO Better to have a ParamLoader object doing this work. Having a possible exception in
        // the injection module is not the best thing
        Gson gson = new Gson();
        return gson.fromJson(loadJSONFromAsset(context, "config.json"), FederatedParams.class);

    }

    @Provides
    public IterationListener provideIterationListener() {
        return iterationListener;
    }

    @Provides
    public ModelConfigurationFactory provideModelConfigurationFactory(Context context,
                                                                      FederatedParams params,
                                                                      IterationListener iterationListener) {
        return new ModelConfigurationFactory(context, iterationListener, params.getBatchSize());

    }

    @Provides
    public ModelConfiguration provideModelConfiguration(ModelConfigurationFactory factory, FederatedParams params) {
        return factory.getConfiguration(params.getModel()).invoke();
    }

    @Provides
    public FederatedDataSource provideFederatedDataSource(ModelConfiguration modelConfiguration) {
        return modelConfiguration.getDataSource();
    }

    @Provides
    public NetworkMapper provideNetworkMapper() {
        return new NetworkMapper();
    }

    @Provides
    public ServerService provideServerService(FederatedParams params) {
        return new Retrofit.Builder()
                .baseUrl(params.getServerUrl())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ServerService.class);
    }

    @Provides
    public FederatedNetworkDataSource provideFederatedNetworkDataSource(ServerService service, NetworkMapper networkMapper) {
        return new ServerDataSource(service, networkMapper);
    }

    @Provides
    public FederatedRepository provideFederatedRepository(FederatedDataSource dataSource, FederatedNetworkDataSource networkDataSource) {
        return new FederatedRepositoryImpl(dataSource, networkDataSource);
    }

    @Provides
    public UseCaseThreadExecutor provideUseCaseThreadExecutor() {
        return new DefaultUseCaseThreadExecutor();
    }

    @Provides
    public TrainerView provideTrainerView() {
        return view;
    }

    @Provides
    @Named("dataset_splits")
    public Integer provideDatasetSplits(FederatedParams params) {
        return params.getMaxClients();
    }

    private String loadJSONFromAsset(Context context, String filename) {
        String json;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
