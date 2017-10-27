package com.mccorby.federatedlearning.datasource;

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

public class IrisDataSource implements TrainerDataSource {

    private static final String TAG = IrisDataSource.class.getSimpleName();
    private InputStream mDataFile;
    private int mDistributedOrder;
    private DataSet trainingData;
    private DataSet testData;
    private int batchSize;

    public IrisDataSource(InputStream dataFile, int distributedOrder) {
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
        int labelIndex = 4;     //5 values in each row of the iris.txt CSV: 4 input features followed by an integer label (class) index. Labels are the 5th value (index 4) in each row
        int numClasses = 3;     //3 classes (types of iris flowers) in the iris data set. Classes have integer values 0, 1 or 2
        batchSize = 150;    //Iris data set: 150 examples total. We are loading all of them into one DataSet (not recommended for large data sets)

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader, batchSize, labelIndex, numClasses);
        DataSet allData = iterator.next();

        allData.shuffle();
        org.nd4j.linalg.dataset.api.DataSet result;
        switch (mDistributedOrder) {
            case 0:
                result = allData.getRange(0, 49);
                break;
            case 1:
                result = allData.getRange(50, 99);
                break;
            case 2:
                result = allData.getRange(100, 149);
                break;
            default:
                result = allData;
                break;
        }

        SplitTestAndTrain testAndTrain = result.splitTestAndTrain(0.65);  //Use 65% of data for training

        trainingData = testAndTrain.getTrain();
        testData = testAndTrain.getTest();

        //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(trainingData);           //Collect the statistics (mean/stdev) from the training data. This does not modify the input data
        normalizer.transform(trainingData);     //Apply normalization to the training data
        normalizer.transform(testData);         //Apply normalization to the test data. This is using statistics calculated from the *training* set
    }

    @Override
    public DataSet getTrainingData(int batchSize) {
        if (trainingData == null) {
            try {
                createDataSource();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return trainingData;
    }

    @Override
    public DataSet getTestData(int batchSize) {
        if (testData == null) {
            try {
                createDataSource();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return testData;
    }
}
