package com.mccorby.trainer_dl4j.datasource;

import org.deeplearning4j.datasets.iterator.impl.ListDataSetIterator;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SumDataSource implements TrainerDataSource {

    //Number of data points
    private static final int N_SAMPLES = 1000;

    // The range of the sample data, data in range (0-1 is sensitive for NN, you can try other
    // ranges and see how it effects the results
    // also try changing the range along with changing the activation function
    private static final int MIN_RANGE = 0;
    private static final int MAX_RANGE = 3;

    @Override
    public DataSetIterator getTrainingData(int batchSize, Random rand) {
        double[] sum = new double[N_SAMPLES];
        double[] input1 = new double[N_SAMPLES];
        double[] input2 = new double[N_SAMPLES];
        for (int i = 0; i < N_SAMPLES; i++) {
            input1[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
            input2[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
            sum[i] = input1[i] + input2[i];
        }
        INDArray inputNDArray1 = Nd4j.create(input1, new int[]{N_SAMPLES, 1});
        INDArray inputNDArray2 = Nd4j.create(input2, new int[]{N_SAMPLES, 1});
        INDArray inputNDArray = Nd4j.hstack(inputNDArray1, inputNDArray2);
        INDArray outPut = Nd4j.create(sum, new int[]{N_SAMPLES, 1});
        DataSet dataSet = new DataSet(inputNDArray, outPut);
        List<DataSet> listDs = dataSet.asList();
        Collections.shuffle(listDs, rand);
        return new ListDataSetIterator(listDs, batchSize);
    }

    @Override
    public DataSet getTestData(int batchSize, Random rand) {
        int numSamples = N_SAMPLES/10;
        double[] sum = new double[numSamples];
        double[] input1 = new double[numSamples];
        double[] input2 = new double[numSamples];
        for (int i = 0; i < numSamples; i++) {
            input1[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
            input2[i] = MIN_RANGE + (MAX_RANGE - MIN_RANGE) * rand.nextDouble();
            sum[i] = input1[i] + input2[i];
        }
        INDArray inputNDArray1 = Nd4j.create(input1, new int[]{numSamples, 1});
        INDArray inputNDArray2 = Nd4j.create(input2, new int[]{numSamples, 1});
        INDArray inputNDArray = Nd4j.hstack(inputNDArray1, inputNDArray2);
        INDArray outPut = Nd4j.create(sum, new int[]{numSamples, 1});
        return new DataSet(inputNDArray, outPut);
    }
}
