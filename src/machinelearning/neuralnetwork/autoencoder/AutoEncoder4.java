package machinelearning.neuralnetwork.autoencoder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class AutoEncoder4 {
	
	public static void main(String [] args) throws Exception {
		
        DataSet data = generateTrainingData("data/iris.arff.txt");
	//	DataSet data = getData("data/iris.arff.txt");
        
   
        System.out.println("NUM OF INPUTS: " + data.numInputs());
        
               
        System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.weightInit(WeightInit.XAVIER)
				.updater(new Nadam())
				.activation(Activation.TANH)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.l2(0.0001)
				.list()
				.layer(0, new AutoEncoder.Builder().nIn(4).nOut(2).build())
				.layer(1, new AutoEncoder.Builder().nIn(2).nOut(2).build())
				.layer(2, new OutputLayer.Builder().nIn(2).nOut(4)
								.lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD).build())
				.build();
		
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		
		
		network.setListeners(new ScoreIterationListener(1));
		
		System.out.println(network.summary());
		
		for (int i = 0; i < 30; i++)
			network.fit(data);
		
		Evaluation eval = new Evaluation(4);
		INDArray output = network.output(data.getFeatures());	
		eval.eval(data.getLabels(), output);
		System.out.println(eval.stats());   
	}
	
	public static DataSet generateTrainingData(String fileName) {

		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			Instances training = arff.getData();
			
			INDArray data = Nd4j.zeros(new int[] { training.size(), 4 });
			
			for (int i = 0; i < training.size(); i++) {		
				
				for (int j = 0; j < 4; j++) {					
					data.putScalar(new int[] { i, j }, training.get(i).value(j));
				}
			}
			
			return new DataSet(data, data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private static DataSet getData(String fileName) throws Exception {
		
		BufferedReader reader = null;
		
		Map<String,Integer> map = new HashMap<String, Integer>();
		map.put("Iris-setosa", 0);
		map.put("Iris-versicolor", 1);
		map.put("Iris-virginica", 2);
		
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
