package com.mccorby.federatedlearning.app.configuration;

import android.content.Context;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;

import org.deeplearning4j.optimize.api.IterationListener;

import java.io.IOException;
import java.io.InputStream;

public abstract class ModelConfiguration {

    private Context context;

    public abstract FederatedModel getModel();
    public abstract FederatedDataSource getDataSource();
    public abstract ModelConfiguration invoke(int modelNumber, IterationListener iterationListener);

    public ModelConfiguration(Context context) {

        this.context = context;
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
