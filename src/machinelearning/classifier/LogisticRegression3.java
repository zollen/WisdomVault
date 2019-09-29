package machinelearning.classifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class LogisticRegression3 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final double LEARNING_RATE = 0.3;
	private static final double DECISION_LINE = 0.5;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DMatrixRMaj data = generateTrainingData("data/iris.arff.txt");
		
		
		double c0 = 0.0;
		double c1 = 0.0;
		double c2 = 0.0;
		double c3 = 0.0;
		double c4 = 0.0;
		
		for (int epoch = 0; epoch < 15; epoch++) {

			for (int row = 0; row < data.numRows; row++) {

				double expected = data.get(row, 4);
				double actual = sigmoid(data, row, c0, c1, c2, c3, c4);

				c0 = c0 + LEARNING_RATE * (expected - actual) * actual * (1.0 - actual) * 1.0;
				c1 = c1 + LEARNING_RATE * (expected - actual) * actual * (1.0 - actual) * data.get(row, 0);
				c2 = c2 + LEARNING_RATE * (expected - actual) * actual * (1.0 - actual) * data.get(row, 1);
				c3 = c3 + LEARNING_RATE * (expected - actual) * actual * (1.0 - actual) * data.get(row, 2);
				c4 = c4 + LEARNING_RATE * (expected - actual) * actual * (1.0 - actual) * data.get(row, 3);
			}

			System.out.println("c0 = " + ff.format(c0) + ", c1 = " + 
							ff.format(c1) + ", c2 = " + ff.format(c2) +
							", c3 = " + ff.format(c3) + ", c4 = " + ff.format(c4));
		}
		
		
		Equation eq = new Equation();
		eq.process("TEST = [" +
						" 4.6, 3.4, 1.4, 0.3, 0.0;" +
						" 5.5, 2.4, 3.7, 1.0, 1.0;" +
						" 2.6, 3.1, 1.2, 0.4, 0.0;" +
						" 5.2, 3.0, 4.9, 1.7, 1.0 " +
					"]");
		
		DMatrixRMaj TEST = eq.lookupDDRM("TEST");
		for (int row = 0; row < TEST.numRows; row++) {
			
			DMatrixRMaj rec = CommonOps_DDRM.extractRow(TEST, row, null);
			
			double res = sigmoid(TEST, row, c0, c1, c2, c3, c4);
			
			System.out.println(print(rec) + " <=> " + ff.format(res) + " ==> " + 
									ff.format(res < DECISION_LINE ? 0.0 : 1.0));
		}
						
	}
	
	private static String print(DMatrixRMaj rec) {
		
		StringBuilder builder = new StringBuilder();
		
		for (int col = 0; col < 5; col++) {
			if (col > 0)
				builder.append(", ");
			
			builder.append(ff.format(rec.get(0, col)));
		}
		
		return builder.toString();
	}
	
	private static double sigmoid(DMatrixRMaj data, int row, 
			double c0, double c1, double c2, double c3, double c4) {
		
		Sigmoid sigmoid = new Sigmoid();
		
		return sigmoid.value(formula(data, row, c0, c1, c2, c3, c4));
		
	}
	 
	private static double formula(DMatrixRMaj data, int row, 
			double c0, double c1, double c2, double c3, double c4) {
		return c0 + c1 * data.get(row, 0) + c2 * data.get(row, 1) +
				c3 * data.get(row, 2) + c4 * data.get(row, 3);
	}
	
	private static DMatrixRMaj generateTrainingData(String fileName) {

		Instances training = null;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			training = arff.getData();
			training.setClassIndex(training.numAttributes() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DMatrixRMaj data = new DMatrixRMaj(100, 5);
		
		for (int row = 0; row < 100; row++) {
			
			Instance rec = training.get(row);
			
			data.set(row, 0, rec.value(training.attribute(0)));
			data.set(row, 1, rec.value(training.attribute(1)));
			data.set(row, 2, rec.value(training.attribute(2)));
			data.set(row, 3, rec.value(training.attribute(3)));
			data.set(row, 4, rec.stringValue(training.attribute(4)).equals("Iris-setosa") ? 0.0 : 1.0);		
		}

		return data;
	}

}
