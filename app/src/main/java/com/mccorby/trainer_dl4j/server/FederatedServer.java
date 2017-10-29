package com.mccorby.trainer_dl4j.server;

import android.util.Log;

import com.mccorby.trainer_dl4j.model.FederatedModel;

import org.deeplearning4j.nn.gradient.DefaultGradient;
import org.deeplearning4j.nn.gradient.Gradient;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This object mocks what an actual server would do in a complete system
 * In real life, the server would send a notification to the clients indicating a new
 * average gradient is available. It would be then responsibility of the client to decide
 * when to download it and process it
 */
public class FederatedServer {

    private static final String TAG = FederatedServer.class.getSimpleName();
    private List<FederatedModel> registeredModels;
    private INDArray averageFlattenGradient;
    private Gradient averageGradient;
    private List<Gradient> gradients;
    private DefaultGradient aggr;

    public void registerModel(FederatedModel model) {
        if (registeredModels == null) {
            registeredModels = new ArrayList<>();
        }
        registeredModels.add(model);
    }

    public void pushGradient(INDArray gradient) {
        // Doing a very simple and not correct average
        // In real life, we would keep a map with the gradients sent by each model
        // This way we could remove outliers
        if (averageFlattenGradient == null) {
            averageFlattenGradient = gradient;
        } else {
            if (Arrays.equals(averageFlattenGradient.shape(), gradient.shape())) {
                Log.d(TAG, "Updating average gradient");
                averageFlattenGradient = averageFlattenGradient.add(gradient).div(2);
            } else {
                Log.d(TAG, "Gradients had different shapes");
            }
        }
        Log.d(TAG, "Average Gradient " + averageFlattenGradient);
    }

    public void sendUpdatedGradient() {
        for (FederatedModel model: registeredModels) {
//            model.updateWeights(averageFlattenGradient);
            model.updateWeights(averageGradient);
        }
    }

    public void pushGradient(byte[] gradientBytes) {
        try {
            INDArray gradient = Nd4j.fromByteArray(gradientBytes);
            pushGradient(gradient);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pushGradient(Gradient gradient) {
        if (averageGradient == null) {
            averageGradient = gradient;
        }
        if (gradients == null) {
            gradients = new ArrayList<>();
        }
        processGradient(gradient);
    }

    // TODO A valid gradient set should improve a model the server keeps. Do some research on this
    // TODO Different strategies could be applied by whoever defines the model:
    // 1. It could validate the model with every new gradients
    // 2. It could validate a batch of gradients (by averaging them) with the model the server keeps
    private void processGradient(Gradient gradient) {
        aggr = new DefaultGradient();
        for (Map.Entry<String, INDArray> entry: gradient.gradientForVariable().entrySet()) {
            INDArray result = entry.getValue();
            for (Gradient prev: gradients) {
                INDArray gradientFor = prev.getGradientFor(entry.getKey());
                result.add(gradientFor);
            }
            aggr.setGradientFor(entry.getKey(), result.div(gradients.size() + 1));
        }
        Log.d(TAG, "Average Gradient after processing one gradient " + averageGradient.toString());
        gradients.add(gradient);
    }

    public Gradient getAverageGradient() {
        return aggr;
    }
}
