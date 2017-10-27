package com.mccorby.federatedlearning;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mccorby.federatedlearning.datasource.IrisDataSource;
import com.mccorby.federatedlearning.datasource.TrainerDataSource;
import com.mccorby.federatedlearning.model.FederatedModel;
import com.mccorby.federatedlearning.model.IrisModel;
import com.mccorby.federatedlearning.server.FederatedServer;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.gradient.Gradient;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ExecutorService executor;
    private TextView loggingArea;
    private TextView predictTxt;
    private Button predictBtn;

    private FederatedServer federatedServer;
    private int nModels;
    private List<FederatedModel> models;
    private TrainerDataSource trainerDataSource;
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
        executor = Executors.newSingleThreadExecutor();

        federatedServer = new FederatedServer();
        models = new ArrayList<>();
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
        predictTxt.setText(currentModel.evaluate(trainerDataSource));

        for (FederatedModel model: models) {
            String score = model.evaluate(trainerDataSource);
            Log.d(TAG, "Score for " + model.getId() + " => " + score);
        }
    }

    private void train() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Gradient averageGradient = federatedServer.getAverageGradient();
                currentModel = new IrisModel("Model" + nModels++, iterationListener);
                federatedServer.registerModel(currentModel);
                models.add(currentModel);
                Log.d(TAG, "Starting training");
                try {
                    currentModel.buildModel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "Model built");
                // TODO Train should start with any gradients already in the server?
                trainerDataSource = new IrisDataSource(getIrisFile(), (nModels - 1) % 3);
                currentModel.train(trainerDataSource);
                Log.d(TAG, "Train finished");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        predictBtn.setEnabled(true);
                    }
                });

                sendGradientToServer(currentModel.getGradient());
            }
        });
    }

    private void sendGradientToServer(Gradient gradient) {
        federatedServer.pushGradient(gradient);
    }

    private void sendGradientToServer(INDArray gradient) throws IOException {
        byte[] gradientBytes = Nd4j.toByteArray(gradient);
        // Send the bytes to the server
        federatedServer.pushGradient(gradientBytes);
    }

}
