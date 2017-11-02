package com.mccorby.federatedlearning.core.domain.model;

import org.nd4j.linalg.api.ndarray.INDArray;

// TODO This model is still dependant in DL4J. Refactor until there is no dependency with the framework
public interface FederatedModel {

    String getId();

    void updateWeights(INDArray remoteGradient);

    INDArray getGradient();

    void buildModel();

    void train(FederatedDataSet federatedDataSet);

    String evaluate(FederatedDataSet federatedDataSet);

}
