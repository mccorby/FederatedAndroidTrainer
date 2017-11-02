package com.mccorby.federatedlearning.app;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mccorby.federatedlearning.R;
import com.mccorby.federatedlearning.app.network.RetrofitServerService;
import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.core.repository.FederatedNetworkDataSource;
import com.mccorby.federatedlearning.core.repository.FederatedRepositoryImpl;
import com.mccorby.federatedlearning.datasource.network.ServerDataSource;
import com.mccorby.federatedlearning.datasource.network.ServerService;
import com.mccorby.federatedlearning.datasource.network.mapper.NetworkMapper;
import com.mccorby.federatedlearning.features.iris.datasource.IrisFileDataSource;
import com.mccorby.federatedlearning.features.iris.model.IrisModel;
import com.mccorby.federatedlearning.features.iris.presentation.IrisPresenter;
import com.mccorby.federatedlearning.features.iris.presentation.IrisView;
import com.mccorby.federatedlearning.app.executor.DefaultUseCaseExecutor;
import com.mccorby.federatedlearning.server.FederatedServer;
import com.mccorby.federatedlearning.server.Logger;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements IrisView {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView loggingArea;
    private TextView predictTxt;
    private Button predictBtn;

    private FederatedServer federatedServer;
    private int nModels;
    private List<FederatedModel> models;
    private FederatedDataSource dataSource;
    private FederatedModel currentModel;

    private IterationListener iterationListener =  new IterationListener() {
        int iterCount;

        @Override
        public boolean invoked() {
            return false;
        }

        @Override
        public void invoke() {

        }

        @Override
        public void iterationDone(final Model model, int iteration) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    double result = model.score();
                    String message = "\nScore at iteration " + iterCount + " is " + result;
                    Log.d(TAG, message);

                    loggingArea.append(message);
                    iterCount++;
                }
            });
        }
    };
    private DefaultUseCaseExecutor executor;
    private IrisPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loggingArea = (TextView) findViewById(R.id.logging_area);
        final Button trainBtn = (Button) findViewById(R.id.train_btn);
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
                federatedServer.sendUpdatedGradient();
            }
        });

        predictTxt = (TextView) findViewById(R.id.predict_txt);

        injectMembers();
    }

    private void injectMembers() {
        federatedServer = new FederatedServer(new Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        models = new ArrayList<>();
        executor = new DefaultUseCaseExecutor(Executors.newSingleThreadExecutor());
    }

    private IrisPresenter createPresenter() {
        FederatedModel model = new IrisModel("Iris" + nModels++, iterationListener);
        federatedServer.registerModel(model);

        dataSource = new IrisFileDataSource(getIrisFile(), (nModels - 1) % 3);
        String baseUrl = "http://192.168.0.33:9999/";
        ServerService networkClient = RetrofitServerService.getNetworkClient(baseUrl);
        NetworkMapper networkMapper = new NetworkMapper();
        FederatedNetworkDataSource networkDataSource = new ServerDataSource(networkClient, networkMapper);
        FederatedRepository repository = new FederatedRepositoryImpl(dataSource, networkDataSource);
        return new IrisPresenter(this, model, repository, executor, 64);
    }

    private InputStream getIrisFile() {
        AssetManager am = getAssets();
        try {
            return  am.open("iris.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void predict() {
        // Show the current model evaluation
        predictTxt.setText(currentModel.evaluate(dataSource.getTestData(64)));

        for (FederatedModel model: models) {
            String score = model.evaluate(dataSource.getTestData(64));
            Log.d(TAG, "Score for " + model.getId() + " => " + score);
        }
    }

    private void train() {
        predictBtn.setEnabled(false);
        presenter = createPresenter();
        presenter.startProcess();
    }

    private void sendGradientToServer(Gradient gradient) {

        presenter.sendGradient(gradient);
    }

    private void sendGradientToServer(INDArray gradient) throws IOException {
        byte[] gradientBytes = Nd4j.toByteArray(gradient);
        // Send the bytes to the server
        federatedServer.pushGradient(gradientBytes);
    }

    @Override
    public void onTrainingDone(FederatedModel model) {
        currentModel = model;
        models.add(model);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                predictBtn.setEnabled(true);
                predict();
            }
        });

        // TODO This should be done by someone else, not by the view
        sendGradientToServer(model.getGradient());
    }

    @Override
    public void onDataReady(FederatedDataSet result) {
        // TODO Probably not necessary. Check the tests
    }

    @Override
    public void onError(String errorMessage) {

    }

    @Override
    public void onGradientSent(Boolean aBoolean) {
        loggingArea.append("Gradient sent to server " + aBoolean);
    }
}
