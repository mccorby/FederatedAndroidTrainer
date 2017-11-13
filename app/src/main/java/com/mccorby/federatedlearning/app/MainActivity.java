package com.mccorby.federatedlearning.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mccorby.federatedlearning.R;
import com.mccorby.federatedlearning.app.presentation.TrainerPresenter;
import com.mccorby.federatedlearning.app.presentation.TrainerView;
import com.mccorby.federatedlearning.core.domain.repository.FederatedRepository;
import com.mccorby.federatedlearning.di.DaggerMainComponent;
import com.mccorby.federatedlearning.di.MainComponent;
import com.mccorby.federatedlearning.di.MainModule;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.IterationListener;

import javax.inject.Inject;

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

    @Inject
    FederatedParams params;
    @Inject
    TrainerPresenter presenter;

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
        MainComponent component = DaggerMainComponent
                .builder()
                .mainModule(new MainModule(this, this, iterationListener))
                .build();
        component.inject(this);

        modelNameTxt.setText(params.getModel());
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

    @Override
    public void onTrainingStart(int modelNumber, int size) {
        loggingArea.append("\nStarting training of model " + modelNumber + " with " + size + " samples");
    }

    @Override
    public void onRegisterStart() {

    }

    @Override
    public void onRegisterDone() {

    }
}
