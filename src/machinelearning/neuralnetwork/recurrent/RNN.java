package machinelearning.neuralnetwork.recurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.BackpropType;
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
import org.nd4j.linalg.primitives.Pair;

public class RNN {
	
	private static final Random rand = new Random(System.currentTimeMillis());
	
	private static final int SEQLENGTH = 40;

	private static final int SUNNY = 0;
	
	private static final int PIZZA = 2;

	private static final String[] LABELS = { "Sunny", "Rainy", "Pizza", "Salad", "Hotdog" };


	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.weightInit(WeightInit.XAVIER)
				.biasInit(0.0)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Nesterovs())
				.list()
				.layer(0, new LSTM.Builder()
						.nIn(5)
						.nOut(3)
						.activation(Activation.TANH).build())
				.layer(1, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
						.nIn(3)
						.nOut(3)
						.activation(Activation.SOFTMAX).build())
				.backpropType(BackpropType.TruncatedBPTT)
	            .tBPTTForwardLength(10)
	            .tBPTTBackwardLength(10)
				.build();

		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(1));

		System.out.println(network.summary());
		
		int total = 0;
		int correct = 0;

		for (int numEpochs = 0; numEpochs < 30; numEpochs++) {
			
			System.out.println("Training model....");
			List<Pair<INDArray, INDArray>> training = generateData(1000, SEQLENGTH);
			
			for (Pair<INDArray, INDArray> data : training) {
				
				INDArray inputs = data.getFirst();
				INDArray labels = data.getSecond();
				
				network.fit(inputs, labels);
			}
			
			network.rnnClearPreviousState();
			
			
			System.out.println("Testing model....");
			List<Pair<INDArray, INDArray>> testing = generateData(1, SEQLENGTH);
			
			
			for (Pair<INDArray, INDArray> data : testing) {
				
				INDArray inputs = data.getFirst();
				INDArray labels = data.getSecond();
				

				for (int day = 0; day < SEQLENGTH; day++) {
				
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
	
	public static List<Pair<INDArray, INDArray>> generateData(int numOfRecords, int seqLength) {
		
		List<Pair<INDArray, INDArray>> ds = new ArrayList<Pair<INDArray, INDArray>>();
		
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
			
			Pair<INDArray, INDArray> combo = new Pair<INDArray, INDArray>();
			combo.setFirst(input);
			combo.setSecond(label);
			
			ds.add(combo);
		}
				
		return ds;
		
	}

}
