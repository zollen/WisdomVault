package machinelearning.neuralnetwork;

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

	private static final int SUNNY = 0;
	private static final int RAINY = 1;

	private static final int PIZZA = 2;
	private static final int SALAD = 3;
	private static final int HOTDOG = 4;

	private static final String[] LABELS = { "Sunny", "Rainy", "Pizza", "Salad", "Hotdog" };

	private static final int[][] activities = { { SUNNY, PIZZA }, { SUNNY, PIZZA }, { RAINY, SALAD }, { RAINY, HOTDOG },
			{ SUNNY, HOTDOG }, { RAINY, PIZZA }, { RAINY, SALAD }, { SUNNY, SALAD }, { SUNNY, SALAD },
			{ RAINY, HOTDOG }, { SUNNY, HOTDOG }, { RAINY, PIZZA }, { SUNNY, PIZZA }, { RAINY, SALAD },
			{ RAINY, HOTDOG }, { SUNNY, HOTDOG }, { RAINY, PIZZA }, { SUNNY, PIZZA }, { RAINY, SALAD },
			{ SUNNY, SALAD }, { RAINY, HOTDOG }, { SUNNY, HOTDOG }, { RAINY, PIZZA }, { RAINY, SALAD },
			{ SUNNY, SALAD }, { RAINY, HOTDOG }, { SUNNY, HOTDOG }, { RAINY, PIZZA }, { RAINY, SALAD },
			{ RAINY, HOTDOG }, { SUNNY, HOTDOG }, { RAINY, PIZZA }, { RAINY, SALAD }, { SUNNY, SALAD } };

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

		INDArray inputs = Nd4j.zeros(1, 5, activities.length);
		INDArray labels = Nd4j.zeros(1, 3, activities.length);

		for (int day = 1; day < activities.length; day++) {

			int[] yesterday = activities[day - 1];
			int[] today = activities[day];

			inputs.putScalar(new int[] { 0, today[0], day }, 1);
			inputs.putScalar(new int[] { 0, yesterday[1], day }, 1);

			labels.putScalar(new int[] { 0, today[1] - PIZZA, day }, 1);

		}

		System.out.println("Training model....");
		for (int i = 0; i < 30; i++) {
			network.fit(inputs, labels);
		}

		INDArray test = Nd4j.zeros(1, 5, 1);
		test.putScalar(new int[] { 0, SUNNY, 0 }, 1);
		test.putScalar(new int[] { 0, SALAD, 0 }, 1);

		INDArray output = network.output(test);

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 5; i++) {

			if (test.getInt(new int[] { 0, i, 0 }) == 1) {
				if (builder.length() > 0)
					builder.append(", ");

				String date = "Today: ";
				if (i >= PIZZA)
					date = "Yesterday: ";

				builder.append(date + LABELS[i]);
			}
		}

		System.err.println(output);

		int idx = Nd4j.getExecutioner().exec(new IMax(output, 1)).getInt(0) + PIZZA;
		System.out.println(builder.toString() + " =======> " + LABELS[idx]);
	}

}
