package com.mccorby.trainer_dl4j;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mccorby.trainer_dl4j.datasource.DataSource;
import com.mccorby.trainer_dl4j.datasource.SumDataSource;
import com.mccorby.trainer_dl4j.model.LinearModel;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.optimize.api.IterationListener;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    //Random number generator SEED, for reproducability
    private static final int SEED = 12345;
    //Batch size: i.e., each epoch has nSamples/BATCH_SIZE parameter updates
    private static final int BATCH_SIZE = 100;



    private static final String TAG = MainActivity.class.getSimpleName();
    private ExecutorService executor;
    private TextView loggingArea;
    private LinearModel linearModel;
    private TextView predictTxt;
    private Button predictBtn;

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
        predictTxt = (TextView) findViewById(R.id.predict_txt);
        executor = Executors.newSingleThreadExecutor();
    }

    private void predict() {
        final INDArray input = Nd4j.create(new double[]{0.111111, 0.3333333333333}, new int[]{1, 2});
        INDArray predict = linearModel.predict(input);
        String message = "PREDICTION for " + input + " => " + predict;
        Log.d(TAG, message);
        predictTxt.setText(message);
    }

    private void train() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                IterationListener iterationListener = new IterationListener() {
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
                                String message = "Score at iteration " + iterCount + " is " + result;
                                Log.d("LinearModel", message);

                                loggingArea.append(message);
                                iterCount++;
                            }
                        });
                    }
                };

                linearModel = new LinearModel(iterationListener, SEED);
                Log.d(TAG, "Starting training");
                linearModel.buildModel();
                DataSource dataSource = new SumDataSource();
                linearModel.train(dataSource.getTrainingData(BATCH_SIZE, new Random(SEED)));
                Log.d(TAG, "Train finished");
                predictBtn.setEnabled(true);
            }
        });
    }

}
