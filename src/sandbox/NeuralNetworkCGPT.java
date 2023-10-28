package sandbox;

import java.util.Random;

public class NeuralNetworkCGPT {
    private int inputSize;
    private int outputSize;
    private int[] hiddenLayerSizes;
    private double[][][] weights;
    private double[][] biases;

    public NeuralNetworkCGPT(int inputSize, int[] hiddenLayerSizes, int outputSize) {
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.hiddenLayerSizes = hiddenLayerSizes;
        int numLayers = 2 + hiddenLayerSizes.length; // Input, hidden, output

        System.out.println(numLayers);
        // Initialize weights and biases with random values
        Random rand = new Random();
        weights = new double[numLayers][][];
        biases = new double[numLayers][];

        for (int i = 1; i < numLayers; i++) {
            weights[i] = new double[hiddenLayerSizes[i - 1]][hiddenLayerSizes[i]];
            biases[i] = new double[hiddenLayerSizes[i]];

            for (int j = 0; j < hiddenLayerSizes[i - 1]; j++) {
                for (int k = 0; k < hiddenLayerSizes[i]; k++) {
                    weights[i][j][k] = rand.nextDouble();
                }
            }
        }
    }

    public double[] feedForward(double[] input) {
        double[] layerOutput = input;
        input = applySigmoid(input); //
        
        for (int layer = 1; layer < weights.length; layer++) {
            double[] layerInput = matrixVectorProduct(weights[layer], layerOutput);
            layerInput = addBiases(layerInput, biases[layer]);
            layerOutput = applySigmoid(layerInput);
        }

        return layerOutput;
    }

    public NeuralNetworkCGPT deepClone() {
        NeuralNetworkCGPT clone = new NeuralNetworkCGPT(inputSize, hiddenLayerSizes, outputSize);

        for (int layer = 1; layer < weights.length; layer++) {
            for (int i = 0; i < hiddenLayerSizes[layer - 1]; i++) {
                System.arraycopy(this.weights[layer][i], 0, clone.weights[layer][i], 0, hiddenLayerSizes[layer]);
            }
            System.arraycopy(this.biases[layer], 0, clone.biases[layer], 0, hiddenLayerSizes[layer]);
        }

        return clone;
    }

    private double[] matrixVectorProduct(double[][] matrix, double[] vector) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] result = new double[rows];

        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += matrix[i][j] * vector[j];
            }
            result[i] = sum;
        }

        return result;
    }

    private double[] addBiases(double[] vector, double[] bias) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] += bias[i];
        }
        return vector;
    }

    private double[] applySigmoid(double[] vector) {
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = 1.0 / (1.0 + Math.exp(-vector[i]));
        }
        return result;
    }

    // Training methods, backpropagation, and error calculations can be added for learning purposes.
}