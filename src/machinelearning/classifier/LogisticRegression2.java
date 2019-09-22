package machinelearning.classifier;

import java.text.DecimalFormat;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class LogisticRegression2 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final double LEARNING_RATE = 0.3;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("DATA = [" +
				///		       X1,           X2,   Y
					"   2.7810836,  2.550537003,   0;" +
					" 1.465489372,  2.362125076,   0;" +
					" 3.396561688,  4.400293529,   0;" +
					"  1.38807019,  1.850220317,   0;" +
					" 7.627531214,  2.759262235,   1;" +
					" 5.332441248,  2.088626775,   1;" +
					" 6.922596716,   1.77106367,   1;" +
					" 8.675418651, -0.2420686549,  1;" +
					" 7.673756466,  3.508563011,   1 " +
					"]");
		
		DMatrixRMaj data = eq.lookupDDRM("DATA");
		
		double c0 = 0.0;
		double c1 = 0.0;
		double c2 = 0.0;
		
		for (int epoch = 0; epoch < 10; epoch++) {

			for (int row = 0; row < data.numRows; row++) {

				double expected = data.get(row, 2);
				double actual = sigmoid(data, row, c0, c1, c2);

				c0 = c0 + LEARNING_RATE * (expected - actual) * actual * (1.0 - actual) * 1.0;
				c1 = c1 + LEARNING_RATE * (expected - actual) * actual * (1.0 - actual) * data.get(row, 0);
				c2 = c2 + LEARNING_RATE * (expected - actual) * actual * (1.0 - actual) * data.get(row, 1);
			}

			System.out.println("c0 = " + ff.format(c0) + ", c1 = " + 
							ff.format(c1) + ", c2 = " + ff.format(c2));
		}
	}
	
	private static double sigmoid(DMatrixRMaj data, int row, double c0, double c1, double c2) {
		
		Sigmoid sigmoid = new Sigmoid();
		
		return sigmoid.value(formula(data, row, c0, c1, c2));
		
	}
	 
	private static double formula(DMatrixRMaj data, int row, double c0, double c1, double c2) {
		return c0 + c1 * data.get(row, 0) + c2 * data.get(row, 1);
	}

}
