package neural;

public class NeuralNetworkSimple implements Cloneable{

	// Simple neural net with a input and an output layer
	
	int inputLayerSize;
	int outputLayerSize;
	public double[] weights;
	
	public NeuralNetworkSimple(int inputLayerSize,int outputLayerSize) {
		this.inputLayerSize = inputLayerSize;
		this.outputLayerSize = outputLayerSize;
		weights = new double[inputLayerSize* outputLayerSize];
	}
	
	
	public double[] calculate(double[] input) {
	
		double[] output = new double[outputLayerSize];
		int weightIndex = 0;
		for (int j = 0;j < outputLayerSize; j++) {
			
			for (int i = 0; i < inputLayerSize; i++) {
				output[j] += input[i]*weights[weightIndex];
				
				weightIndex++;
			}
		}
		return output;
	}
	
	public NeuralNetworkSimple clone() {
		NeuralNetworkSimple clone = new NeuralNetworkSimple(inputLayerSize, outputLayerSize);
		for (int i = 0;i < clone.weights.length;i++) {
			clone.weights[i] = weights[i];
		}
		return clone;
	}
	
}
