package com.mccorby.federatedlearning.datasource;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;

import org.nd4j.linalg.dataset.DataSet;

public class FederatedDataSetImpl implements FederatedDataSet<DataSet> {

    private DataSet nativeDataSet;

    public FederatedDataSetImpl(DataSet nativeDataSet) {

        this.nativeDataSet = nativeDataSet;
    }

    @Override
    public DataSet getNativeDataSet() {
        return nativeDataSet;
    }
}
