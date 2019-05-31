package machinelearning.neuralnetwork.autoencoder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import javax.swing.JFrame;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.math.plot.FrameView;
import org.math.plot.Plot2DPanel;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.jujutsu.tsne.PrincipalComponentAnalysis;

import gui.ColoredScatterPlot;
import smile.clustering.GMeans;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class AutoEncoder2 {
	
	// AutoEncoder vs PCA for for Dimensional reductions
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub	
		Instances training = generateTrainingData("data/iris.arff.txt");
		double[][] data = data(training);
		String[] labels = labels(training);
		
		
		pca(data, labels);
		pca_gmeans(data);
		nn(data, labels);
				
	}
	
	// G-means clustering (self-determine total number of clusters)
	// ============================================================
	// We fit a set of k gaussians clusters to the data. And we estimate gaussian 
	// distribution parameters such as mean and Variance for each cluster and weight 
	// of a cluster. After learning the parameters for each data point we can calculate 
	// the probabilities of it belonging to each of the clusters.
	
	// Unlike K-means where we map each data point to the closest cluster (requirement: number of 
	// clusters must be known)
	// G-means imposes number of clusters(from 1 to max) into all data points and compute 
	// the probabilities of each data points that belongs to each cluster. As long as data 
	// points in existing clusters meet the gaussian, G-means continues to add a new cluster 
	// until any clusters failed to meet the gaussian constraint.
	
	// gaussian distribution has the following probability density function
	// P(x) = 1 / ( σ * sqrt( 2 π ) )  *  e^( -(x - μ)^2 / (2 σ^2) )
	public static void pca_gmeans(double[][] data) {

		String[] labels = new String[data.length];
		
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		double[][] Y = pca.pca(data, 2);

		GMeans gmeans = new GMeans(Y, 16);

		for (int i = 0; i < data.length; i++) {
			labels[i] = "cluster_" + String.valueOf(gmeans.getClusterLabel()[i]);
		}
		
		Plot2DPanel plot = new Plot2DPanel();
		ColoredScatterPlot setosaPlot = new ColoredScatterPlot("iris", Y, labels);
		plot.plotCanvas.setNotable(true);
		plot.plotCanvas.setNoteCoords(true);
		plot.plotCanvas.addPlot(setosaPlot);

		FrameView plotframe = new FrameView("PCA with G-Means Clustering", plot);
		plotframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		plotframe.setVisible(true);
	}
	
	public static void nn(double [][] data, String [] labels) {
		
		System.out.println("Starting Neural Network - AutoEncoder");
		
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.weightInit(WeightInit.XAVIER)
				.updater(new Sgd(0.01))
				.activation(Activation.LEAKYRELU)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.l2(0.0001)
				.list()
				.layer(0, new AutoEncoder.Builder().nIn(4).nOut(2).build())
				.layer(1, new OutputLayer.Builder().nIn(2).nOut(2)
								.lossFunction(LossFunctions.LossFunction.MSE).build())
				.build();
		
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(1));
		
		for (int nEpochs = 0; nEpochs < 30; nEpochs++) {
			network.fit(new DataSet(convert(data), Nd4j.zeros(data.length, 2)));
		}	
	
		
		List<INDArray> result = network.feedForward(true);
		
		// index 0 - input INDArray
		// index 1 - output INDArray
		double [][] Y = convert(result.get(1));	
		
		Plot2DPanel plot = new Plot2DPanel();
		ColoredScatterPlot setosaPlot = new ColoredScatterPlot("iris", Y, labels);
		plot.plotCanvas.setNotable(true);
		plot.plotCanvas.setNoteCoords(true);
		plot.plotCanvas.addPlot(setosaPlot);

		FrameView plotframe = new FrameView("Neural Network - AutoEncoder", plot);
		plotframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		plotframe.setVisible(true);
	
	}
	
	public static void pca(double[][] data, String[] labels) {
		
		System.out.println("Starting PCA");

		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
		double[][] Y = pca.pca(data, 2);
		
		Plot2DPanel plot = new Plot2DPanel();
		ColoredScatterPlot setosaPlot = new ColoredScatterPlot("iris", Y, labels);
		plot.plotCanvas.setNotable(true);
		plot.plotCanvas.setNoteCoords(true);
		plot.plotCanvas.addPlot(setosaPlot);

		FrameView plotframe = new FrameView("PCA", plot);
		plotframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		plotframe.setVisible(true);
	}

	public static double[][] data(Instances instances) {

		int total = instances.size();
		int attrs = instances.numAttributes() - 1;

		double[][] data = new double[total][attrs];

		for (int record = 0; record < total; record++) {

			Instance instance = instances.get(record);
			double[] vals = instance.toDoubleArray();

			for (int attr = 0; attr < attrs; attr++) {
				data[record][attr] = vals[attr];
			}
		}

		return data;
	}

	public static String[] labels(Instances instances) {

		int total = instances.size();
		Attribute cls = instances.classAttribute();

		String[] data = new String[total];

		for (int record = 0; record < total; record++) {
			Instance instance = instances.get(record);
			data[record] = instance.stringValue(cls);
		}

		return data;
	}

	public static Instances generateTrainingData(String fileName) {

		Instances training = null;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			training = arff.getData();
			training.setClassIndex(training.numAttributes() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return training;
	}
	
	public static INDArray convert(double [][] data) {
		
		INDArray arr = Nd4j.zeros(data.length, data[0].length);
		
		for (int row = 0; row < data.length; row++) {
			for (int col = 0; col < data[0].length; col++) {
				arr.putScalar(row, col, data[row][col]);
			}
		}
		
		return arr;
	}
	
	public static double [][] convert(INDArray arr) {
		
		double [][] data = new double[(int) arr.size(0)][(int) arr.size(1)];
		
		int numRecs = (int) arr.size(0);
		int numAttrs = (int) arr.size(1);
		
		for (int row = 0; row < numRecs; row++) {
			for (int col = 0; col < numAttrs; col++) {
				data[row][col] = arr.getDouble(row, col);
			}
		}
		
		return data;
	}

}
