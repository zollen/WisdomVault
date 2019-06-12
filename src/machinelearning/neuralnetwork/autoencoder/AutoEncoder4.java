package machinelearning.neuralnetwork.autoencoder;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.CacheMode;
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
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class AutoEncoder4 {
	
	public static void main(String [] args) throws Exception {
		
		INDArray expected = Nd4j.zeros(new int[] { 5, 4 });
		
		for (int i = 0; i < 5; i++) {
			
			for (int j = 0; j < 4; j++) {
				expected.putScalar(new int[] { i, j }, i + j);
			}
		}
		
		DataSet data = new DataSet(expected, expected);
		
		NormalizerMinMaxScaler preprocessor = new NormalizerMinMaxScaler();
		preprocessor.fit(data);
		preprocessor.preProcess(data);
		
        System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.cacheMode(CacheMode.HOST)
				.weightInit(WeightInit.XAVIER)
				.updater(new Nadam())
				.activation(Activation.TANH)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.l2(0.0001)
				.list()
				.layer(0, new AutoEncoder.Builder().nIn(4).nOut(2).build())
				.layer(1, new AutoEncoder.Builder().nIn(2).nOut(2).build())
				.layer(2, new OutputLayer.Builder().nIn(2).nOut(4)
						.activation(Activation.SIGMOID)
						.lossFunction(LossFunctions.LossFunction.XENT).build())
				.build();
		
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		
		
		network.setListeners(new ScoreIterationListener(1));
		
		System.out.println(network.summary());
		
		for (int i = 0; i < 10000; i++) {
			network.fit(expected, expected);
		}

		INDArray actual = network.output(expected);
		
		System.out.println(expected);
		System.out.println("===============================");
		System.out.println(actual);
	}
	
	
}
