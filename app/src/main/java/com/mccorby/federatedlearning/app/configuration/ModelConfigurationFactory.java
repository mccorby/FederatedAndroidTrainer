package com.mccorby.federatedlearning.app.configuration;

import android.content.Context;

import org.deeplearning4j.optimize.api.IterationListener;

public class ModelConfigurationFactory {

    private static final String DIABETES = "diabetes";
    private static final String IRIS = "iris";


    private Context context;
    private IterationListener iterationListener;

    public ModelConfigurationFactory(Context context, IterationListener iterationListener) {
        this.context = context;
        this.iterationListener = iterationListener;
    }

    public ModelConfiguration getConfiguration(String model) {
        switch (model.toLowerCase()) {
            case DIABETES:
                return new DiabetesConfiguration(context, iterationListener);
            case IRIS:
                return new IrisConfiguration(context, iterationListener);
            default:
                return new IrisConfiguration(context, iterationListener);
        }
    }
}
