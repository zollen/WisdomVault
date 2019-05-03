package machinelearning.classifier;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

/**
 * This basic example shows how to manually create a DataSet and train it to an basic Network.
 * <p>
 * The network consists in 2 input-neurons, 1 hidden-layer with 4 hidden-neurons, and 2 output-neurons.
 * <p>
 * I choose 2 output neurons, (the first fires for false, the second fires for
 * true) because the Evaluation class needs one neuron per classification.
 * <p>
 * +---------+---------+---------------+--------------+
 * | Input 1 | Input 2 | Label 1(XNOR) | Label 2(XOR) |
 * +---------+---------+---------------+--------------+
 * |    0    |    0    |       1       |       0      |
 * +---------+---------+---------------+--------------+
 * |    1    |    0    |       0       |       1      |
 * +---------+---------+---------------+--------------+
 * |    0    |    1    |       0       |       1      |
 * +---------+---------+---------------+--------------+
 * |    1    |    1    |       1       |       0      |
 * +---------+---------+---------------+--------------+
 *
 */
public class NeuralNetwork2 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		double rate = 0.05; // learning rate
		int numEpochs = 10000; // number of epochs to perform

		System.out.println("Preparing data");
		DataSet ds = getData();

		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.weightInit(WeightInit.XAVIER)
				.biasInit(0.0)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Sgd(rate))
				.list()
				.layer(0, new DenseLayer.Builder().nIn(2).nOut(4).activation(Activation.SIGMOID)
						// random initialize weights with values between 0 and 1
						.weightInit(WeightInit.DISTRIBUTION).dist(new UniformDistribution(0, 1)).build())
				.layer(1,
						new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
								.nOut(2)
								.activation(Activation.SOFTMAX)
								.weightInit(WeightInit.DISTRIBUTION)
								.dist(new UniformDistribution(0, 1)).build())
				.build();

		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(100)); // print the score with every iteration

		System.out.println(network.summary());

		System.out.println("Training model....");
		for (int i = 0; i < numEpochs; i++) {
			network.fit(ds);
		}

		INDArray output = network.output(ds.getFeatures());
		System.out.println(output);

		Evaluation eval = new Evaluation();
		eval.eval(ds.getLabels(), output);
		System.out.println(eval.stats());

	}

	private static DataSet getData() {

		INDArray inputs = Nd4j.zeros(4, 2);
		INDArray labels = Nd4j.zeros(4, 2);

		// create first dataset
		// when first input=0 and second input=0
		inputs.putScalar(new int[] { 0, 0 }, 0);
		inputs.putScalar(new int[] { 0, 1 }, 0);
		// then the first output fires for false, and the second is 0 (see class
		// comment)
		labels.putScalar(new int[] { 0, 0 }, 1);
		labels.putScalar(new int[] { 0, 1 }, 0);

		// when first input=1 and second input=0
		inputs.putScalar(new int[] { 1, 0 }, 1);
		inputs.putScalar(new int[] { 1, 1 }, 0);
		// then xor is true, therefore the second output neuron fires
		labels.putScalar(new int[] { 1, 0 }, 0);
		labels.putScalar(new int[] { 1, 1 }, 1);

		// same as above
		inputs.putScalar(new int[] { 2, 0 }, 0);
		inputs.putScalar(new int[] { 2, 1 }, 1);
		labels.putScalar(new int[] { 2, 0 }, 0);
		labels.putScalar(new int[] { 2, 1 }, 1);

		// when both inputs fire, xor is false again - the first output should fire
		inputs.putScalar(new int[] { 3, 0 }, 1);
		inputs.putScalar(new int[] { 3, 1 }, 1);
		labels.putScalar(new int[] { 3, 0 }, 1);
		labels.putScalar(new int[] { 3, 1 }, 0);

		return new DataSet(inputs, labels);
	}

}
