package com.mccorby.federatedlearning.app.configuration;

import android.content.Context;

import com.mccorby.federatedlearning.core.domain.model.FederatedModel;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.features.diabetes.datasource.DiabetesFileDataSource;
import com.mccorby.federatedlearning.features.diabetes.model.DiabetesModel;

import org.deeplearning4j.optimize.api.IterationListener;

import java.io.InputStream;

public class DiabetesConfiguration extends ModelConfiguration {
    private FederatedModel mModel;
    private FederatedDataSource mDataSource;

    public DiabetesConfiguration(Context context) {
        super(context);
    }

    @Override
    public FederatedModel getModel() {
        return mModel;
    }

    @Override
    public FederatedDataSource getDataSource() {
        return mDataSource;
    }

    @Override
    public ModelConfiguration invoke(int modelNumber, IterationListener iterationListener) {
        int numInputs = 11;
        int numOutputs = 1;

        mModel = new DiabetesModel("Diabetes" + modelNumber, numInputs, numOutputs, iterationListener);

        InputStream dataFile = getDataFile("diabetes.csv");
        mDataSource = new DiabetesFileDataSource(dataFile, (modelNumber - 1) % 3);
        return this;
    }
}