package com.mccorby.federatedlearning.datasource;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;

import org.nd4j.linalg.dataset.api.DataSet;

public class FederatedDataSetImpl implements FederatedDataSet<DataSet> {

    private DataSet nativeDataSet;

    public FederatedDataSetImpl(DataSet nativeDataSet) {

        this.nativeDataSet = nativeDataSet;
    }

    @Override
    public DataSet getNativeDataSet() {
        return nativeDataSet;
    }

    @Override
    public FederatedDataSet<DataSet> getSubSet(int from, int to) {
        return new FederatedDataSetImpl(nativeDataSet.getRange(from, to));
    }

    @Override
    public int size() {
        return nativeDataSet.numExamples();
    }
}
