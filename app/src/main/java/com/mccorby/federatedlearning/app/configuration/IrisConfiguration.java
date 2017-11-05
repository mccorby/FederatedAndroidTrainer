package com.mccorby.federatedlearning.app.configuration;

import android.content.Context;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.features.iris.datasource.IrisFileDataSource;
import com.mccorby.federatedlearning.features.iris.model.IrisModel;

import org.deeplearning4j.optimize.api.IterationListener;

import java.io.InputStream;

public class IrisConfiguration extends ModelConfiguration {
    private FederatedModel model;
    private FederatedDataSource dataSource;

    public IrisConfiguration(Context context) {
        super(context);
    }

    @Override
    public FederatedModel getModel() {
        return model;
    }

    @Override
    public FederatedDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public ModelConfiguration invoke(int modelNumber, IterationListener iterationListener) {
        int numInputs = 4;
        int numOutputs = 3;

        model = new IrisModel("Iris" + modelNumber, numInputs, numOutputs, iterationListener);

        InputStream dataFile = getDataFile("iris.csv");
        dataSource = new IrisFileDataSource(dataFile, (modelNumber - 1) % 3);
        return this;
    }
}
