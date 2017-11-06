package com.mccorby.federatedlearning.app.configuration;

import android.content.Context;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;

import org.deeplearning4j.optimize.api.IterationListener;

import java.io.IOException;
import java.io.InputStream;

public abstract class ModelConfiguration {

    protected IterationListener iterationListener;
    protected int batchSize;

    private Context context;

    public abstract FederatedModel getNewModel(int modelNumber);
    public abstract FederatedDataSource getDataSource();
    public abstract ModelConfiguration invoke();

    public ModelConfiguration(Context context, IterationListener iterationListener, int batchSize) {

        this.context = context;
        this.iterationListener = iterationListener;
        this.batchSize = batchSize;
    }

    protected final InputStream getDataFile(String filename) {
        try {
            return  context.getAssets().open(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
