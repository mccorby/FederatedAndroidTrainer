package com.mccorby.federatedlearning.app;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mccorby.federatedlearning.BuildConfig;
import com.mccorby.federatedlearning.R;
import com.mccorby.federatedlearning.app.executor.DefaultUseCaseExecutor;
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
import com.mccorby.federatedlearning.features.diabetes.datasource.DiabetesFileDataSource;
import com.mccorby.federatedlearning.features.diabetes.model.DiabetesModel;
import com.mccorby.federatedlearning.app.presentation.TrainerPresenter;
import com.mccorby.federatedlearning.app.presentation.TrainerView;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements TrainerView {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int BATCH_SIZE = 64;

    private TextView loggingArea;
    private TextView predictTxt;
    private Button predictBtn;

    private int nModels;
    private List<FederatedModel> models;
    private FederatedDataSource dataSource;
    private FederatedModel currentModel;

    private IterationListener iterationListener =  new IterationListener() {

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
    private FederatedDataSet testDataSet;

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
                presenter.getUpdatedGradient();
            }
        });

        predictTxt = (TextView) findViewById(R.id.predict_txt);

        injectMembers();
    }

    private void injectMembers() {
        models = new ArrayList<>();
        executor = new DefaultUseCaseExecutor(Executors.newSingleThreadExecutor());
    }

    // TODO This to injectMembers
    private TrainerPresenter createPresenter() {
        // TODO Please move this somewhere else inmmediately after testing it works!
        // For diabetes
        int numInputs = 11;
        int numOutputs = 1;
        // For iris
//        int numInputs = 4;
//        int numOutputs = 3;

        FederatedModel model = new DiabetesModel("Diabetes" + nModels++, numInputs, numOutputs, iterationListener);
//        FederatedModel model = new IrisModel("Iris" + nModels++, numInputs, numOutputs, iterationListener);

        dataSource = new DiabetesFileDataSource(getIrisFile(), (nModels - 1) % 3);
        String baseUrl = BuildConfig.API_URL;
        ServerService networkClient = RetrofitServerService.getNetworkClient(baseUrl);
        NetworkMapper networkMapper = new NetworkMapper();
        FederatedNetworkDataSource networkDataSource = new ServerDataSource(networkClient, networkMapper);
        FederatedRepository repository = new FederatedRepositoryImpl(dataSource, networkDataSource);
        return new TrainerPresenter(this, model, repository, executor, BATCH_SIZE);
    }

    private InputStream getIrisFile() {
        AssetManager am = getAssets();
        try {
            return  am.open("diabetes.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void predict() {
        // Show the current model evaluation
        predictTxt.setText(currentModel.evaluate(testDataSet));

        for (FederatedModel model: models) {
            String score = model.evaluate(testDataSet);
            String message = "\nScore for " + model.getId() + " => " + score;
            Log.d(TAG, message);
            loggingArea.append(message);
        }
    }

    private void train() {
        predictBtn.setEnabled(false);
        presenter = createPresenter();
        presenter.startProcess();
    }

    private void sendGradientToServer() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Nd4j.write(outputStream, currentModel.getGradient());
        } catch (IOException e) {
            e.printStackTrace();
        }
        presenter.sendGradient(outputStream.toByteArray());
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
        sendGradientToServer();
    }

    @Override
    public void onDataReady(FederatedRepository result) {
        // TODO Probably not necessary. Check the tests
        if (testDataSet == null) {
            testDataSet = result.getTestData(BATCH_SIZE);
        }

    }

    @Override
    public void onError(String errorMessage) {

    }

    @Override
    public void onGradientSent(Boolean aBoolean) {
        loggingArea.append("\n\nGradient sent to server " + aBoolean);
    }

    @Override
    public void onGradientReceived(byte[] gradient) {
        loggingArea.append("\n\nGradient received from server " + (gradient != null ? gradient.length : "null"));
        try {
            INDArray remoteGradient = Nd4j.fromByteArray(gradient);
            for (FederatedModel model: models) {
                model.updateWeights(remoteGradient);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
