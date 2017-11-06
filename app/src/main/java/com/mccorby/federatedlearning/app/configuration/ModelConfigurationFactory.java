package com.mccorby.federatedlearning.app.configuration;

import android.content.Context;

import org.deeplearning4j.optimize.api.IterationListener;

public class ModelConfigurationFactory {

    private static final String DIABETES = "diabetes";
    private static final String IRIS = "iris";


    private Context context;
    private IterationListener iterationListener;
    private int batchSize;

    public ModelConfigurationFactory(Context context, IterationListener iterationListener, int batchSize) {
        this.context = context;
        this.iterationListener = iterationListener;
        this.batchSize = batchSize;
    }

    public ModelConfiguration getConfiguration(String model) {
        switch (model.toLowerCase()) {
            case DIABETES:
                return new DiabetesConfiguration(context, iterationListener, batchSize);
            case IRIS:
                return new IrisConfiguration(context, iterationListener, batchSize);
            default:
                return new IrisConfiguration(context, iterationListener, batchSize);
        }
    }
}
