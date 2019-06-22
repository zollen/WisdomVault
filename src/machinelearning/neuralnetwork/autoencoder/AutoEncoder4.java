package machinelearning.neuralnetwork.autoencoder;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.CenterLossOutputLayer;
import org.deeplearning4j.nn.conf.layers.variational.GaussianReconstructionDistribution;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
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
		
		MultiLayerNetwork ae = ae();
		MultiLayerNetwork vae = vae();
		
		ae.init();
		vae.init();
		
		ae.setListeners(new ScoreIterationListener(1000));
		vae.setListeners(new ScoreIterationListener(1000));
		
		System.out.println(ae.summary());
		System.out.println(vae.summary());
		
		System.out.println("Let's overfitting both networks!!!!!!!!!!!!");
		for (int i = 0; i < 10000; i++) {
			ae.fit(expected, expected);
			vae.fit(expected, expected);
		}

		INDArray actual_ae = ae.output(expected);
		INDArray actual_vae = vae.output(expected);
		
		System.out.println("=========== EXPECTED ============");
		System.out.println(expected);
		System.out.println("============ AE OUTPUT ===========");
		System.out.println(actual_ae);
		System.out.println("=========== VAE OUTPUT ===========");
		System.out.println(actual_vae);
	}
	
	public static MultiLayerNetwork ae() {
		
		System.out.println("Building AE....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(83)
				.cacheMode(CacheMode.HOST)
				.weightInit(WeightInit.XAVIER)
				.updater(new Nadam())
				.activation(Activation.TANH)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.l2(0.0001)
				.list()
				.layer(0, new AutoEncoder.Builder().nIn(4).nOut(2).corruptionLevel(0.3).build())
				.layer(1, new AutoEncoder.Builder().nIn(2).nOut(2).corruptionLevel(0.3).build())
				.layer(2, new CenterLossOutputLayer.Builder().nIn(2).nOut(4)
						// Alpha can be thought of as the learning rate for the centers for 
						// each class
						.alpha(0.01)
						// Lambda defines the relative strength of the center loss component.
				        // lambda = 0.0 is equivalent to training with standard softmax only
						.lambda(0.0)
						.weightInit(WeightInit.NORMAL)
						.activation(Activation.SIGMOID)
						.lossFunction(LossFunctions.LossFunction.XENT).build())
				.build();
		
		return new MultiLayerNetwork(conf);
	}
	
	public static MultiLayerNetwork vae() {
		
		/**
		 * In the variational autoencoder, pp is specified as a standard Normal distribution 
		 * with mean zero and variance one, or p(z) = Normal(0,1)p(z)=Normal(0,1). If the 
		 * encoder outputs representations z (hidden layer) that are different than those from a 
		 * standard normal distribution, it will receive a penalty in the loss. This regularizer 
		 * term means ‘keep the representations z (hidden layer) of each digit sufficiently diverse’. 
		 * If we didn’t include the regularizer, the encoder could learn to cheat and give each 
		 * datapoint a representation in a different region of Euclidean space. This is bad, 
		 * because then two images of the same number (say a 2 written by different people, 
		 * 2 by {alice} and 2 by {bob}) could end up with very different representations 
		 * z_{alice}, z_{bob}. We want the representation space of z to be meaningful, so we 
		 * penalize this behavior. This has the effect of keeping similar inputs representations 
		 * close together. 
		 */
		
		System.out.println("Building VAE....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(83)
				.cacheMode(CacheMode.HOST)
				.weightInit(WeightInit.XAVIER)
				.updater(new Nadam())
				.activation(Activation.TANH)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.l2(0.0001)
				.list()
				.layer(0, new VariationalAutoencoder.Builder()
						.nIn(4).nOut(2)
						.encoderLayerSizes(256, 256)            
		                .decoderLayerSizes(256, 256)   
						.activation(Activation.TANH)
						.pzxActivationFunction(Activation.IDENTITY)
						.reconstructionDistribution(new GaussianReconstructionDistribution(Activation.SIGMOID))
						.lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						.build())
				.layer(1, new CenterLossOutputLayer.Builder().nIn(2).nOut(4)
						// Alpha can be thought of as the learning rate for the centers for 
						// each class
						.alpha(0.01)
						// Lambda defines the relative strength of the center loss component.
				        // lambda = 0.0 is equivalent to training with standard softmax only
						.lambda(0.0)
						.weightInit(WeightInit.NORMAL)
						.activation(Activation.SIGMOID)
						.lossFunction(LossFunctions.LossFunction.XENT).build())
				.build();
		
		return new MultiLayerNetwork(conf);
	}
	
	
}
