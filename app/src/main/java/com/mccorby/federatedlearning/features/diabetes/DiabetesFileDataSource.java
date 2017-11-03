package com.mccorby.federatedlearning.features.diabetes;

import com.mccorby.federatedlearning.core.domain.model.FederatedDataSet;
import com.mccorby.federatedlearning.core.repository.FederatedDataSource;
import com.mccorby.federatedlearning.datasource.FederatedDataSetImpl;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.InputStreamInputSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;

import java.io.IOException;
import java.io.InputStream;

public class DiabetesFileDataSource implements FederatedDataSource {

    private static final String TAG = DiabetesFileDataSource.class.getSimpleName();
    private InputStream mDataFile;
    private int mDistributedOrder;
    private DataSet trainingData;
    private DataSet testData;
    private int batchSize;

    public DiabetesFileDataSource(InputStream dataFile, int distributedOrder) {
        mDataFile = dataFile;
        mDistributedOrder = distributedOrder;
    }

    private void createDataSource() throws IOException, InterruptedException {
        //First: get the dataset using the record reader. CSVRecordReader handles loading/parsing
        int numLinesToSkip = 0;
        String delimiter = ",";
        RecordReader recordReader = new CSVRecordReader(numLinesToSkip, delimiter);
        recordReader.initialize(new InputStreamInputSplit(mDataFile));

        //Second: the RecordReaderDataSetIterator handles conversion to DataSet objects, ready for use in neural network
        int labelIndex = 11;
        batchSize = 300;

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, labelIndex, true);
        DataSet allData = iterator.next();
        org.nd4j.linalg.dataset.api.DataSet result;
        switch (mDistributedOrder) {
            case 0:
                result = allData.getRange(0, 100);
                break;
            case 1:
                result = allData.getRange(101, 200);
                break;
            case 2:
                result = allData.getRange(201, 300);
                break;
            default:
                result = allData;
                break;
        }

        SplitTestAndTrain testAndTrain = result.splitTestAndTrain(0.80);  //Use 80% of data for training

        trainingData = testAndTrain.getTrain();
        testData = testAndTrain.getTest();

        //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform(trainingData);     //Apply normalization to the training data
        normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set
    }

    @Override
    public FederatedDataSet getTrainingData(int batchSize) {
        if (trainingData == null) {
            try {
                createDataSource();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new FederatedDataSetImpl(trainingData);
    }

    @Override
    public FederatedDataSet getTestData(int batchSize) {
        if (testData == null) {
            try {
                createDataSource();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new FederatedDataSetImpl(testData);
    }

    @Override
    public FederatedDataSet getCrossValidationData(int batchSize) {
        return null;
    }
}
