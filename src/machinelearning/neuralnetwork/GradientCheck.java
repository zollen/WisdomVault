package machinelearning.neuralnetwork;

import java.util.Random;

import org.deeplearning4j.gradientcheck.GradientCheckUtil;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.learning.config.NoOp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class GradientCheck {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		{
			DataSet testData = getData(23, 200);
		
			MultiLayerNetwork network = build(Activation.SIGMOID, 
				LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD, new NoOp(), 0.0);
		
			network.setListeners(new ScoreIterationListener(100), 
					new PerformanceListener.Builder()
						.reportSample(true)
						.reportScore(true)
						.reportTime(true)
						.reportETL(true)
						.reportBatch(true)
						.reportIteration(true)
						.build());
		
			System.out.println(network.summary());
		
			DataTypeUtil.setDTypeForContext(DataType.DOUBLE);
		
			boolean check = GradientCheckUtil.checkGradients(network, 1e-6, 1e-5, 1e-10, 
				true, false, testData.getFeatures(), testData.getLabels()); 
		
			System.err.println("Grandient Test with : " + Activation.SIGMOID + ", " + Activation.SOFTMAX + ", " +
	        		LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD + " ===> " + check);
		}
		
		{
			DataSet trainData = getData(83, 500);
			
			MultiLayerNetwork network = build(Activation.SIGMOID, 
				LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD, new Nesterovs(), 0.0001);
		
			network.setListeners(new ScoreIterationListener(1000), 
				new PerformanceListener.Builder()
					.reportSample(true)
					.reportScore(true)
					.reportTime(true)
					.reportETL(true)
					.reportBatch(true)
					.reportIteration(true)
					.setFrequency(1000).build());
		
			for (int i = 0; i < 150; i++)
				network.fit(trainData);
		
		
			DataSet testData = getData(117, 20);
			Evaluation eval = new Evaluation(2);
			INDArray output = network.output(testData.getFeatures());	
			eval.eval(testData.getLabels(), output);
			System.out.println(eval.stats());
		}
	}
	
	private static DataSet getData(int seed, int size) {
		
		Random rand = new Random(seed);
		
		INDArray input = Nd4j.zeros(new int [] { size, 4 });
		INDArray label = Nd4j.zeros(new int [] { size, 2 });
		
		for (int i = 0; i < size; i++) {
			
			int sum = 0;
			for (int j = 0; j < 4; j++) {
				int val = rand.nextInt(100);
				sum += val;
				input.putScalar(new int[] { i, j }, val);
			}
			
			label.putScalar(new int[] { i, sum < 200 ? 0 : 1 }, 1.0); 
		}
		
		DataSet data = new DataSet(input, label);
		
		NormalizerMinMaxScaler processor = new NormalizerMinMaxScaler();
		processor.fit(data);
		processor.transform(data);
		
		return new DataSet(input, label);		
	}
	
	public static MultiLayerNetwork build(Activation actFn, LossFunctions.LossFunction lossFn,
										IUpdater updater, double l2) {
		
		
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(83)
				.cacheMode(CacheMode.HOST)
				.weightInit(WeightInit.XAVIER)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(updater)
				.activation(actFn)
				.l2(l2)
				.cudnnAlgoMode(ConvolutionLayer.AlgoMode.NO_WORKSPACE)
				.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
				.trainingWorkspaceMode(WorkspaceMode.ENABLED)
				.dataType(DataType.DOUBLE)
				.list()
				
				.layer(0, new DenseLayer.Builder()
								.nIn(4)
								.nOut(10).activation(actFn).build())
				.layer(1, new DenseLayer.Builder()
								.nIn(10).nOut(5).activation(actFn).build())
				.layer(2, new OutputLayer.Builder(lossFn)
								.activation(Activation.SOFTMAX)        
								.nIn(5).nOut(2).build())
				.build();
		
		return new MultiLayerNetwork(conf);
	}

}