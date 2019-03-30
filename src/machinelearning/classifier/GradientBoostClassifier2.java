package machinelearning.classifier;

import java.io.File;
import java.text.DecimalFormat;

import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.regression.GradientTreeBoost;

public class GradientBoostClassifier2 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ArffParser arffParser = new ArffParser();
		arffParser.setResponseIndex(3);
		AttributeDataset species = arffParser.parse(new File("data/species.txt"));
		double[][] x = species.toArray(new double[species.size()][]);
		double[] y = species.toArray(new double[species.size()]);
		
		System.out.println("SIZE: " + species.size());
		
		
		GradientTreeBoost trees = new GradientTreeBoost(x, y, 100);
		
		System.out.println("SIZE: " + species.size());
	
		double [] k = { 1.7, 0, 1 };
	 	System.out.println(trees.predict(k));
	}

}
