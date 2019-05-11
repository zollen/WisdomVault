package machinelearning.neuralnetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class RNN {
	
	private static final Random rand = new Random(System.currentTimeMillis());

	private static final int SUNNY = 0;
	
	private static final int PIZZA = 2;

	private static final String[] LABELS = { "Sunny", "Rainy", "Pizza", "Salad", "Hotdog" };


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(0).weightInit(WeightInit.XAVIER)
				.biasInit(0.0)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Nesterovs())
				.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)  //Not always required, but helps with this data set
			    .gradientNormalizationThreshold(0.7)
				.list()
				.layer(0, new LSTM.Builder()
						.nIn(5)
						.nOut(3)
						.activation(Activation.TANH).build())
				.layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
						.nIn(3)
						.nOut(3)
						.activation(Activation.SOFTMAX).build())
				.build();

		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(1));

		System.out.println(network.summary());
		
		int total = 0;
		int correct = 0;

		for (int numEpochs = 0; numEpochs < 30; numEpochs++) {
			
			System.out.println("Training model....");
			List<List<INDArray>> training = generateData(1000, 10);
			
			for (List<INDArray> data : training) {
				
				INDArray inputs = data.get(0);
				INDArray labels = data.get(1);
				
				network.fit(inputs, labels);
			}
			
			network.rnnClearPreviousState();
			
			
			System.out.println("Testing model....");
			List<List<INDArray>> testing = generateData(1, 10);
			
			
			for (List<INDArray> data : testing) {
				
				INDArray inputs = data.get(0);
				INDArray labels = data.get(1);
				

				for (int day = 0; day < 10; day++) {
				
					INDArray output = network.rnnTimeStep(inputs);
					
					StringBuilder builder = new StringBuilder();
					builder.append("Day [" + (day + 1) + "] ");
					
					for (int i = 0; i < 2; i++) {
						if (inputs.getInt(new int[] { 0, i, day }) == 1) {
							builder.append("Today: [" + LABELS[i] + "], ");
							break;
						}
					}
					
					for (int i = 2; i < 5; i++) {

						if (inputs.getInt(new int[] { 0, i, day }) == 1) {
							builder.append("Yesterday: [" + LABELS[i] + "]");
							break;
						}
					}
					
					int expected = Nd4j.getExecutioner().exec(new IMax(labels, 1)).getInt(day) + PIZZA;
					int actual = Nd4j.getExecutioner().exec(new IMax(output, 1)).getInt(day) + PIZZA;
					
					if (expected == actual) {
						correct++;
						System.out.println(builder.toString() + " ===> " + LABELS[expected] + " : " + LABELS[actual]);
					}
					else {
						System.err.println(builder.toString() + " ===> " + LABELS[expected] + " : " + LABELS[actual]);
					}
					
					total++;
				}
			}
		}
		
		System.out.println("TOTAL: " + total + " -> ACCURACY: " + (double) correct / total);
	}
	
	public static List<List<INDArray>> generateData(int numOfRecords, int seqLength) {
		
		List<List<INDArray>> ds = new ArrayList<List<INDArray>>();
		
		for (int record = 0; record < numOfRecords; record++) {
			
			INDArray input = Nd4j.zeros(1, 5, seqLength);
			INDArray label = Nd4j.zeros(1, 3, seqLength);
			
			int food = rand.nextInt(3);
			
			for (int day = 0; day < seqLength; day++) {
				
				int weather = rand.nextInt(10) <= 5 ? 0 : 1;
				
				input.putScalar(new int[] { record, weather, day }, 1);
				input.putScalar(new int[] { record, food + PIZZA, day }, 1);
				
				if (weather != SUNNY)
					food = (food + 1) % 3;
				
				label.putScalar(new int[] { record, food, day }, 1);
			}
			
			List<INDArray> combo = new ArrayList<INDArray>();
			combo.add(input);
			combo.add(label);
			
			ds.add(combo);
		}
				
		return ds;
		
	}

}
