package machinelearning.classifier;

import java.io.BufferedReader;
import java.io.FileReader;

import javax.swing.JFrame;

import org.math.plot.FrameView;
import org.math.plot.Plot2DPanel;
import org.math.plot.plots.ColoredScatterPlot;

import com.jujutsu.tsne.FastTSne;
import com.jujutsu.tsne.PrincipalComponentAnalysis;
import com.jujutsu.tsne.TSne;
import com.jujutsu.tsne.TSneConfiguration;
import com.jujutsu.utils.TSneUtils;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class T_SNE_PCA_Demo {

	private static final double perplexity = 50.0;
	
	private static int initial_dims;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Instances training = generateTrainingData("data/iris.arff.txt");
		double[][] data = data(training);
		String[] labels = labels(training);
		
		initial_dims = training.numAttributes();

		pca(data, labels);
		tsne(data, labels);
	}

	public static void tsne(double[][] data, String[] labels) {

		TSneConfiguration config = TSneUtils.buildConfig(data, 2, initial_dims, perplexity, 300);
		TSne tsne = new FastTSne();
		double[][] Y = tsne.tsne(config);

		Plot2DPanel plot = new Plot2DPanel();

		ColoredScatterPlot setosaPlot = new ColoredScatterPlot("iris", Y, labels);
		plot.plotCanvas.setNotable(true);
		plot.plotCanvas.setNoteCoords(true);
		plot.plotCanvas.addPlot(setosaPlot);

		FrameView plotframe = new FrameView("t-SNE", plot);
		plotframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		plotframe.setVisible(true);
	}

	public static void pca(double[][] data, String[] labels) {

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
		int attrs = instances.numAttributes();

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

}
