package machinelearning.neuralnetwork.autoencoder;

import java.io.BufferedReader;
import java.io.FileReader;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class AutoEncoder4 {
	
	public static void main(String [] args) throws Exception {
		
		DataSet data = generateData("data/iris.arff.txt");
		
		INDArray expected = data.getFeatures();
		
		
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
		
		for (int i = 0; i < 30; i++) {
			network.fit(data);
		}

		INDArray actual = network.output(expected);
		
		if (actual.equalsWithEps(expected, 0.001))
			System.out.println("INPUTS AND OUTPUTS ARE IDENTIAL!!!");
		else
			System.err.println("INPUTS AND OUTPUTS ARE NOT THE SAME!!!");	
		
		System.out.println(expected.getRow(0));
		System.out.println(actual.getRow(0));
	}
	
	public static DataSet generateData(String fileName) {
	
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
}
