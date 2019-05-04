package machinelearning.neuralnetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

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
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class NeuralNetwork3 {
	
	private static final String VALUE_FLOWER_SETOSA = "Iris-setosa";
	private static final String VALUE_QUALITY_VERCOLOR = "Iris-versicolor";
	private static final String VALUE_QUALITY_VIGRINICA = "Iris-virginica";
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Preparing data");
		DataSet data = getData("data/iris.arff.txt");
		
		data.shuffle();
		SplitTestAndTrain samples = data.splitTestAndTrain(0.85);
		
		DataSet training = samples.getTrain();
		DataSet test = samples.getTest();
		
		// Normalize data so one attribute with much larger range 
		// would not dominate the other attributes
		NormalizerStandardize normalizer = new NormalizerStandardize();
		normalizer.fit(training);
		normalizer.transform(training);
		normalizer.transform(test);
		
		
		
		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.weightInit(WeightInit.XAVIER)
				.biasInit(0.0)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Sgd(0.1))
				.l2(0.0001)
				.list()
				.layer(0, new DenseLayer.Builder().nIn(4).nOut(3).activation(Activation.TANH).build())
				.layer(1, new DenseLayer.Builder().nIn(3).nOut(3).activation(Activation.LEAKYRELU).build())
				.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
								.nIn(3)
								.nOut(3)
								.activation(Activation.SOFTMAX).build())
				.build();
		
		
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(100));
		
		System.out.println("Training model....");
		for (int i = 0; i < 1000; i++) {
			network.fit(training);
		}
		
		
		System.out.println("Cross-Validating model....");
		Evaluation eval = new Evaluation(3);
		INDArray output = network.output(test.getFeatures());
		eval.eval(test.getLabels(), output);
		System.out.println(eval.stats());
		
	}
	
	private static DataSet getData(String fileName) throws Exception {
		
		BufferedReader reader = null;
		
		Map<String,Integer> map = new HashMap<String, Integer>();
		map.put(VALUE_FLOWER_SETOSA, 0);
		map.put(VALUE_QUALITY_VERCOLOR, 1);
		map.put(VALUE_QUALITY_VIGRINICA, 2);
		
		try {
			reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			Instances instances = arff.getData();
			instances.setClassIndex(instances.numAttributes() - 1);
			
			INDArray inputs = Nd4j.zeros(instances.numInstances(), instances.numAttributes() - 1);
			INDArray labels = Nd4j.zeros(instances.numInstances(), 3);

			for (int row = 0; row < instances.numInstances(); row++) {
				
				Instance instance = instances.get(row);
				
				for (int col = 0; col < instances.numAttributes() - 1; col++) {
					inputs.putScalar(row, col, instance.value(col));
				}
				
				int position = map.get(instance.stringValue(instances.numAttributes() - 1));
				
				labels.putScalar(row, position, 1);
			}
			
			return new DataSet(inputs, labels);
		} 
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception ex) {}
		}
	}

}
