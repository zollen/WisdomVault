package machinelearning.neuralnetwork;

import java.util.Random;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class NeuralNetwork5 {
	
	private static final Random rand = new Random(0);
	
	// Autoencoder
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub	
		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.weightInit(WeightInit.XAVIER)
				.updater(new AdaGrad(0.05))
				.activation(Activation.RELU)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.l2(0.0001)
				.list()
				.layer(0, new DenseLayer.Builder().nIn(784).nOut(250).build())
				.layer(1, new DenseLayer.Builder().nIn(250).nOut(10).build())
				.layer(2, new DenseLayer.Builder().nIn(10).nOut(250).build())
				.layer(3, new OutputLayer.Builder().nIn(250).nOut(784)
								.lossFunction(LossFunctions.LossFunction.MSE).build())
				.build();
		
		
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(1));
		
		DataSetIterator itr = new MnistDataSetIterator(100, 500000, false);
		
		INDArray featuresTrain = Nd4j.zeros();
		INDArray featuresTest  = Nd4j.zeros();
		INDArray labelsTest  = null;
		
		while (itr.hasNext()) {
			
			DataSet data = itr.next();
			
			SplitTestAndTrain samples = data.splitTestAndTrain(80, rand);
			
			DataSet training = samples.getTrain();
			DataSet testing = samples.getTest();
			
			featuresTrain.add(training.getFeatures());
			featuresTest.add(testing.getFeatures());
			INDArray ind = Nd4j.argMax(testing.getLabels(), 1);
			labelsTest.add(ind);
		}
		
	}
	
	private static DataSet getData() throws Exception {
		
		return null;
	}

}
