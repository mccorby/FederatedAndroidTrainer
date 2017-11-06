package com.mccorby.federatedlearning.app.configuration;

import android.content.Context;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.features.iris.datasource.IrisFileDataSource;
import com.mccorby.federatedlearning.features.iris.model.IrisModel;

import org.deeplearning4j.optimize.api.IterationListener;

import java.io.InputStream;

public class IrisConfiguration extends ModelConfiguration {

    private static final int NUM_INPUTS = 4;
    private static final int NUM_OUTPUTS = 3;

    private FederatedDataSource dataSource;

    public IrisConfiguration(Context context, IterationListener iterationListener, int batchSize) {
        super(context, iterationListener, batchSize);
    }

    @Override
    public FederatedModel getNewModel(int modelNumber) {

        return new IrisModel("Iris" + modelNumber, NUM_INPUTS, NUM_OUTPUTS, iterationListener);
    }

    @Override
    public FederatedDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public ModelConfiguration invoke() {
        InputStream dataFile = getDataFile("iris.csv");
        dataSource = new IrisFileDataSource(dataFile, batchSize);
        return this;
    }
}
