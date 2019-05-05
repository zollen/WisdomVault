package machinelearning.neuralnetwork;


import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
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
import org.nd4j.linalg.learning.config.Nesterovs;

public class LogisticRegression {
	
	// Logistic Regression using DL4j
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Preparing data");
		DataSet training = getTrainingData();
		
			
		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Nesterovs(0.9))
				.list()
				.layer(0, new DenseLayer.Builder()
						.nIn(2)
						.nOut(2)
						.weightInit(WeightInit.XAVIER)
						.biasInit(0.0)
						.activation(Activation.SIGMOID).build())
				.layer(1, new OutputLayer.Builder()
						.nIn(2)
						.nOut(2)
						.weightInit(WeightInit.XAVIER)
						.biasInit(0.0)
						.activation(Activation.SOFTMAX).build())
				.build();
		
		
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(100));
		
		System.out.println("Training model....");
		for (int i = 0; i < 1000; i++) {
			network.fit(training);
		}
		
		
		System.out.println("Testing model....");
		DataSet test = getTestData();
		Evaluation eval = new Evaluation(2);
		INDArray output = network.output(test.getFeatures());	
		eval.eval(test.getLabels(), output);
		System.out.println(eval.stats());
		
	}
	
	private static DataSet getTestData() {
		
		INDArray inputs = Nd4j.zeros(4, 2);
		INDArray labels = Nd4j.zeros(4, 2);
				
		inputs.putScalar(0, 0, 7.5210836);
		inputs.putScalar(0, 1, 2.5801);
		labels.putScalar(0, 1, 1.0);
		
		inputs.putScalar(1, 0, 3.1739);
		inputs.putScalar(1, 1, 2.04125);
		labels.putScalar(1, 0, 1.0);
		
		inputs.putScalar(2, 0, 1.859);
		inputs.putScalar(2, 1, 2.851);
		labels.putScalar(2, 0, 1.0);
		
		inputs.putScalar(0, 0, 5.803);
		inputs.putScalar(0, 1, 1.965);
		labels.putScalar(0, 1, 1.0);
		
		return new DataSet(inputs, labels);
	}
	
	private static DataSet getTrainingData() {
		
		
		INDArray inputs = Nd4j.zeros(10, 2);
		INDArray labels = Nd4j.zeros(10, 2);
		
		inputs.putScalar(0, 0, 2.7810836);
		inputs.putScalar(0, 1, 2.550537003);
		labels.putScalar(0, 0, 1.0);
		
		inputs.putScalar(1, 0, 1.465489372);
		inputs.putScalar(1, 1, 2.362125076);
		labels.putScalar(1, 0, 1.0);
		
		inputs.putScalar(2, 0, 3.396561688);
		inputs.putScalar(2, 1, 4.400293529);
		labels.putScalar(2, 0, 1.0);
		
		inputs.putScalar(3, 0, 1.38807019);
		inputs.putScalar(3, 1, 1.850220317);
		labels.putScalar(3, 0, 1.0);
		
		inputs.putScalar(4, 0, 3.06407232);
		inputs.putScalar(4, 1, 3.005305973);
		labels.putScalar(4, 0, 1.0);
		
		inputs.putScalar(5, 0, 7.627531214);
		inputs.putScalar(5, 1, 2.759262235);
		labels.putScalar(5, 1, 1.0);
		
		inputs.putScalar(6, 0, 5.332441248);
		inputs.putScalar(6, 1, 2.088626775);
		labels.putScalar(6, 1, 1.0);
		
		inputs.putScalar(7, 0, 6.922596716);
		inputs.putScalar(7, 1, 1.77106367);
		labels.putScalar(7, 1, 1.0);
		
		inputs.putScalar(8, 0, 8.675418651);
		inputs.putScalar(8, 1, -0.2420686549);
		labels.putScalar(8, 1, 1.0);
		
		inputs.putScalar(9, 0, 7.673756466);
		inputs.putScalar(9, 1, 3.508563011);
		labels.putScalar(9, 1, 1.0);
		
		return new DataSet(inputs, labels);
	}

}
