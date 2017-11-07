package com.mccorby.federatedlearning.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mccorby.federatedlearning.R;
import com.mccorby.federatedlearning.app.configuration.ModelConfiguration;
import com.mccorby.federatedlearning.app.configuration.ModelConfigurationFactory;
import com.mccorby.federatedlearning.app.executor.DefaultUseCaseExecutor;
import com.mccorby.federatedlearning.app.network.RetrofitServerService;
import com.mccorby.federatedlearning.app.presentation.TrainerPresenter;
import com.mccorby.federatedlearning.app.presentation.TrainerView;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.core.repository.FederatedNetworkDataSource;
import com.mccorby.federatedlearning.core.repository.FederatedRepositoryImpl;
import com.mccorby.federatedlearning.datasource.network.ServerDataSource;
import com.mccorby.federatedlearning.datasource.network.ServerService;
import com.mccorby.federatedlearning.datasource.network.mapper.NetworkMapper;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.IterationListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements TrainerView {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView loggingArea;
    private Button predictBtn;

    private IterationListener iterationListener = new IterationListener() {

        @Override
        public boolean invoked() {
            return false;
        }

        @Override
        public void invoke() {

        }

        @Override
        public void iterationDone(final Model model, final int iteration) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (iteration % 100 == 0) {
                        double result = model.score();
                        String message = "\nScore at iteration " + iteration + " is " + result;
                        Log.d(TAG, message);

                        loggingArea.append(message);
                    }
                }
            });
        }
    };

    private DefaultUseCaseExecutor executor;
    private TrainerPresenter presenter;
    private ModelConfiguration modelConfiguration;
    private Button trainBtn;
    private TextView modelNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loggingArea = (TextView) findViewById(R.id.logging_area);
        trainBtn = (Button) findViewById(R.id.train_btn);
        trainBtn.setEnabled(false);
        trainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                train();
            }
        });
        predictBtn = (Button) findViewById(R.id.predict_btn);
        predictBtn.setEnabled(false);
        predictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predict();
            }
        });

        Button updateModelsBtn = (Button) findViewById(R.id.update_models_btn);
        updateModelsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getUpdatedGradient();
            }
        });

        modelNameTxt = (TextView) findViewById(R.id.model_txt);

        injectMembers();
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.retrieveData();
    }

    private void injectMembers() {
        Gson gson = new Gson();
        FederatedParams params = gson.fromJson(loadJSONFromAsset("config.json"), FederatedParams.class);

        modelNameTxt.setText(params.getModel());

        executor = new DefaultUseCaseExecutor(Executors.newSingleThreadExecutor());
        ModelConfigurationFactory modelConfigurationFactory = new ModelConfigurationFactory(this,
                iterationListener,
                params.getBatchSize());
        modelConfiguration = modelConfigurationFactory
                .getConfiguration(params.getModel())
                .invoke();

        presenter = createPresenter(params);
    }

    // TODO This to injectMembers
    private TrainerPresenter createPresenter(FederatedParams params) {
        Scheduler origin = Schedulers.from(Executors.newSingleThreadExecutor());
        Scheduler postScheduler = AndroidSchedulers.mainThread();

        FederatedDataSource dataSource = modelConfiguration.getDataSource();

        String baseUrl = params.getServerUrl();
        ServerService networkClient = RetrofitServerService.getNetworkClient(baseUrl);
        NetworkMapper networkMapper = new NetworkMapper();
        FederatedNetworkDataSource networkDataSource = new ServerDataSource(networkClient, networkMapper);
        FederatedRepository repository = new FederatedRepositoryImpl(dataSource, networkDataSource);
        return new TrainerPresenter(this,
                modelConfiguration,
                repository,
                executor,
                origin,
                postScheduler,
                params.getMaxClients());
    }

    private void predict() {
        presenter.predict();
    }

    private void train() {
        predictBtn.setEnabled(false);
        presenter.trainNewModel();
    }

    @Override
    public void onTrainingDone() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                predictBtn.setEnabled(true);
                predict();
            }
        });
    }

    @Override
    public void onDataReady(FederatedRepository result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                trainBtn.setEnabled(true);
            }
        });
    }

    @Override
    public void onError(String errorMessage) {
        loggingArea.append("Something went wrong: " + errorMessage);
    }

    @Override
    public void onGradientSent(Boolean aBoolean) {
        loggingArea.append("\n\nGradient sent to server? " + aBoolean);
    }

    @Override
    public void onGradientReceived(byte[] gradient) {
        loggingArea.append("\n\nGradient received from server " + (gradient != null ? gradient.length : "null") + "\n");
    }

    @Override
    public void onPrediction(String message) {
        Log.d(TAG, message);
        loggingArea.append(message);
    }

    private String loadJSONFromAsset(String filename) {
        String json;
        try {
            InputStream is = getAssets().open(filename);
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
