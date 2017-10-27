package com.mccorby.federatedlearning.model;

import com.mccorby.federatedlearning.datasource.TrainerDataSource;

import org.deeplearning4j.nn.gradient.Gradient;
import org.nd4j.linalg.api.ndarray.INDArray;

public interface FederatedModel {

    String getId();

    void updateWeights(INDArray remoteGradient);

    INDArray getGradientAsArray();

    void updateWeights(Gradient averageGradient);

    Gradient getGradient();

    void buildModel();

    void train(TrainerDataSource trainerDataSource);

    String evaluate(TrainerDataSource trainerDataSource);

}
