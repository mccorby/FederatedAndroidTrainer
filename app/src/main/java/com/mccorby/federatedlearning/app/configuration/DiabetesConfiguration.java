package com.mccorby.federatedlearning.app.configuration;

import android.content.Context;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.features.diabetes.datasource.DiabetesFileDataSource;
import com.mccorby.federatedlearning.features.diabetes.model.DiabetesModel;

import org.deeplearning4j.optimize.api.IterationListener;

import java.io.InputStream;

public class DiabetesConfiguration extends ModelConfiguration {
    private FederatedDataSource mDataSource;
    private   static final int NUM_INPUTS = 11;
    private static final int NUM_OUTPUTS = 1;

    public DiabetesConfiguration(Context context, IterationListener iterationListener, int batchSize) {
        super(context, iterationListener, batchSize);
    }

    @Override
    public FederatedModel getNewModel(int modelNumber) {
        return new DiabetesModel("Diabetes" + modelNumber, NUM_INPUTS, NUM_OUTPUTS, iterationListener);
    }

    @Override
    public FederatedDataSource getDataSource() {
        return mDataSource;
    }

    @Override
    public ModelConfiguration invoke() {

        InputStream dataFile = getDataFile("diabetes.csv");
        mDataSource = new DiabetesFileDataSource(dataFile, batchSize);
        return this;
    }
}