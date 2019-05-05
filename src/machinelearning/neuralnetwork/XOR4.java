package machinelearning.neuralnetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffLoader.ArffReader;

public class XOR4 {

	private static DecimalFormat ff = new DecimalFormat("0.000");

	private static final String backPropOptions = " -L " + 0.1 // learning rate
			+ " -M " + 0 // momentum
			+ " -N " + 10000 // epoch
			+ " -V " + 0 // validation
			+ " -S " + 0 // seed
			+ " -E " + 0 // error
			+ " -H " + 3; // hidden nodes.

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Instances training = generateTrainingData("data/xor.arff.txt");
	
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		mlp.setOptions(Utils.splitOptions(backPropOptions));
		mlp.buildClassifier(training);

		System.out.println(mlp.globalInfo());
		System.out.println(mlp);

		System.out.println("Predictions");
		for (int i = 0; i < training.numInstances(); i++) {
			
			Instance instance = training.instance(i);

			double actual = instance.classValue();
			double prediction = mlp.distributionForInstance(instance)[0];
			System.out.println(instance + " ---> Actual: [" + ff.format(actual) + 
					"]  Prediction: [" + ff.format(prediction) + "]");
		}
		
		System.out.println("Success Metrics");
		Evaluation eval = new Evaluation(training);
		eval.evaluateModel(mlp, training);
		 
		//display metrics
		System.out.println("Correlation: " + eval.correlationCoefficient());
		System.out.println("MAE: " + ff.format(eval.meanAbsoluteError()));
		System.out.println("RMSE: " + ff.format(eval.rootMeanSquaredError()));
		System.out.println("RAE: "+ ff.format(eval.relativeAbsoluteError()) + "%");
		System.out.println("RRSE: " + ff.format(eval.rootRelativeSquaredError()) + "%");
		System.out.println("Instances: " + eval.numInstances());
		 
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
