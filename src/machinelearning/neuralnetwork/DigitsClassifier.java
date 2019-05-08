package machinelearning.neuralnetwork;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;

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
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import graphics.ImageUtils;

public class DigitsClassifier {
	
	private static DecimalFormat ff = new DecimalFormat("0.000");

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.weightInit(WeightInit.XAVIER)
				.biasInit(0.0)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new AdaDelta())
				.list()
				.layer(0, new DenseLayer.Builder()
						.nIn(784)
						.nOut(250)
						.activation(Activation.TANH)	
						.build())
				.layer(1, new DenseLayer.Builder()
						.nIn(250)
						.nOut(250)
						.activation(Activation.LEAKYRELU)	
						.build())
				.layer(2,
						new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
						.nIn(250)
						.nOut(10)
						.activation(Activation.SOFTMAX)
						.build())
				.build();

		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.setListeners(new ScoreIterationListener(1)); 
		
		System.out.println(network.summary());
		
		
		DataSet data = getData("img/digits");
		
		double accuracy = 0.0;
		double recall = 0.0;
		double precision = 0.0;
		double f1 = 0.0;
		double numOfTimes = 15.0;
		
		for (int i = 0; i < numOfTimes; i++) {
			
			MultiLayerNetwork model = new MultiLayerNetwork(conf.clone());
			model.init();
			
			data.shuffle();
			SplitTestAndTrain samples = data.splitTestAndTrain(0.8);
		
			DataSet training = samples.getTrain();
			DataSet testing = samples.getTest();
			
	
			for (int nEpochs = 0; nEpochs < 30; nEpochs++) {
				network.fit(training);
			}
			
			Evaluation eval = new Evaluation();
			eval.eval(testing.getLabels(), network.output(testing.getFeatures()));
	
			accuracy += eval.accuracy();
			precision += eval.precision();
			recall += eval.recall();
			f1 += eval.f1();
			
			System.out.println("Step " + (i + 1) + " Accuracy: [" + ff.format(eval.accuracy()) +
					"], Precision: [" + ff.format(eval.precision()) + 
					"], Recall: [" + ff.format(eval.recall()) +
					"], F1: [" + ff.format(eval.f1())+ "]");
		}
		
		System.out.println();
		System.out.println("Cross-Validation After " + (int) numOfTimes + " iterations");
		System.out.println("======================================");
		System.out.println("Accuracy: " + ff.format(accuracy / numOfTimes));
		System.out.println("Precision: " + ff.format(precision / numOfTimes));
		System.out.println("Recall: " + ff.format(recall / numOfTimes));
		System.out.println("F1: " + ff.format(f1 / numOfTimes));
	}
	
	public static DataSet getData(String path) throws Exception {
		
		File dir = new File(path);
		File [] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.toLowerCase().endsWith(".png");
			}			
		});
		
		double [][] data = new double[files.length][];
		double [][] labels = new double[files.length][10];
		
		int index = 0;
		for (File file : files) {
			
			data[index] = ImageUtils.array(ImageUtils.image(file.getAbsolutePath()));
			int result = Integer.valueOf(file.getName().charAt(0) - '0');

			
			for (int i = 0; i < 10; i++) {
				if (result == i)
					labels[index][i] = 1.0;
				else
					labels[index][i] = 0.0;
			}
			
			index++;
		}
		
		return new DataSet(Nd4j.create(data), Nd4j.create(labels));
	}

}
