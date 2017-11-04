package com.mccorby.federatedlearning.app.configuration;

import android.content.Context;

public class ModelConfigurationFactory {

    private static final String DIABETES = "diabetes";
    private static final String IRIS = "iris";


    private Context context;

    public ModelConfigurationFactory(Context context) {
        this.context = context;
    }

    public ModelConfiguration getConfiguration(String model) {
        switch (model.toLowerCase()) {
            case DIABETES:
                return new DiabetesConfiguration(context);
            case IRIS:
                return new IrisConfiguration(context);
            default:
                return new IrisConfiguration(context);
        }
    }
}
