package machinelearning;
import java.text.DecimalFormat;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

import linearalgebra.MatrixFeatures;

public class LogisticRegression {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final double P = 0.5;  // default cut off point

	public static void main(String... args) {
		// TODO Auto-generated method stub
		// Unlikely Linear Regression, Logistic Regression is for multiple independent 
		// inputs and one binary output (Yes or No). The binary output must be range
		// between 0 and 1. This is why Linear Regression would not work because its output
		// are usually beyond the range of 0 and 1.
		// To achieve the range of 0 and 1. The sigmoid function is used to generated the 
		// result between 0 and 1.
		// For classifying data by multiple labels, then multiple classifiers could 
		// be employed for the same dataset.
		// For example: Dogs, Cats, Fishes, Elephants
		// First Logistic Regression classifier: Dogs or Not Dogs
		// Second Logistic Regression classifier: Cats or Not Cats
		// Third Logistic Regression classifier: Fishes or Not Fishes
		// Fourth Logistic Regression classifier: Elephants or Not Elephants
		
		// The advantages of using Logistic Regression over knn search (although knn search is 
		// simpler to implement), The classification takes time to setup, but it is extremely 
		// fast and efficient because it is just a evaluation of a math formula.
		
		Equation eq = new Equation();
		eq.process("TRAININGS = [" +
				//   x0,  x1,  x2,  x3,  x4         <-- features
					" 1, 5.1, 3.5, 1.4, 0.2;" +    // training data #1
					" 1, 4.9, 3.0, 1.4, 0.2;" +    // training data #2
					" 1, 7.7, 2.8, 6.7, 2.0;" +	   // training data #3
					" 1, 6.3, 2.7, 4.9, 1.8 " +	   // training data #4
					"]");

		
		eq.process("RESULTS = [ 1; 1; 0; 0 ]");     // 1 - approved, 0 - not approved
		
		eq.process("WEIGHTS = [ 1; 1; 1; 1; 1 ]");  // initial weights for each feature
		
		DMatrixRMaj trainings = eq.lookupDDRM("TRAININGS");
		DMatrixRMaj results = eq.lookupDDRM("RESULTS");
		DMatrixRMaj weights = eq.lookupDDRM("WEIGHTS");
			
		weights = training(trainings, weights, results, 0.01, 0.0001, 10000, P);
		System.out.println("OPTIMAL WEIGHTS: " + weights);
		System.out.println("x0(0.800) has positive & stongest influence of the outcome");
		System.out.println("both x1(-0.397) and x4(-0.156) have negative(or reverse) influence of the outcome");
		System.out.println("both x2(0.450) and x5(0.621) has positive influnce of the outcome, x5(0.621) is stronger than x2(0.450), but not as strong as x0(0.800)");
		System.out.println("x1(-0.397) has three times the reverse effect comparing to x4(-0.156)");
		System.out.println("================================");
		
		eq.process("TESTS = [" +
			//   x0,  x1,  x2,  x3,  x4         <-- features
				" 1, 7.2, 2.5, 1.0, 0.3;" +    // test data #1
				" 1, 5.2, 1.9, 5.2, 1.2;" +    // test data #2
				" 1, 4.3, 2.2, 3.8, 0.9;" +	   // test data #3
				" 1, 6.2, 2.7, 4.8, 0.8;" +	   // test data #4
				" 1, 4.5, 3.3, 4.5, 1.0;" +    // test data #5
				" 1, 6.6, 1.8, 5.0, 0.2;" +    // test data #6
				" 1, 5.2, 2.3, 3.9, 0.5;" +    // test data #7
				" 1, 7.0, 3.3, 2.5, 2.4;" +    // test data #8
				" 1, 4.9, 1.8, 3.2, 1.2;" +    // test data #9
				" 1, 5.1, 2.5, 3.0, 0.7;" +    // test data #10
				" 1, 4.5, 3.1, 4.4, 0.1;" +    // test data #11
				" 1, 5.3, 1.8, 4.1, 2.1;" +    // test data #12
				" 1, 7.0, 2.9, 3.2, 1.9;" +    // test data #13
				" 1, 6.4, 2.4, 3.9, 1.0;" +    // test data #14
				" 1, 6.8, 3.3, 2.5, 1.5;" +    // test data #15
				" 1, 7.0, 3.1, 2.1, 0.7;" +    // test data #16
				" 1, 4.2, 2.3, 3.6, 1.8;" +    // test data #17
				" 1, 6.6, 1.8, 4.8, 0.9;" +    // test data #18
				" 1, 6.1, 1.2, 5.0, 1.1;" +    // test data #19
				" 1, 4.7, 3.7, 1.8, 1.6 " +    // test data #20
				"]");
		
		DMatrixRMaj tests = eq.lookupDDRM("TESTS");
		
		DMatrixRMaj actual = classify(tests, weights, P);
		
		eq.process("TESTS_EXPECTED_RESULTS = [ 0; 1; 1; 1; 0; 1; 0; 1; 1; 1; 0; 0; 1; 1; 1; 0; 0; 1; 1; 1 ]");
		
		DMatrixRMaj expected = eq.lookupDDRM("TESTS_EXPECTED_RESULTS");
		
		DMatrixRMaj comparing = new DMatrixRMaj(tests.numRows, 2);
		for (int i = 0; i < tests.numRows; i++) {
			comparing.set(i, 0, actual.get(i, 0));
			comparing.set(i, 1, expected.get(i, 0));
		}
		
		System.out.println("ACTUAL, EXPECTED  " + comparing);
		
		
		
		System.out.println("Accuracy: " + ff.format(accuracy(expected, actual)));
		
	
		// The cut off point (default 0.5) should be adjusted.
		// Try the same dataset and same testset with various cut off point and attempt
		// to get the optimal accuracy, sensitivity, specificity, positive predictive value
		// and negative predictive value.
		
		
		//      				  true positive 
		// sensitivity = ---------------------------------
		//                true positive + false negative
		System.out.println("Sensitivity: " + ff.format(sensitivity(expected, actual)));
		
		//      				  true negative
		// specificity = ---------------------------------
		//                true negative + false positive
		System.out.println("Specificity: " + ff.format(specificity(expected, actual)));
		
		//      								true positive
		// positive predictive value = ---------------------------------
		//                              true positive + false positive
		System.out.println("Predictive Positive: " + ff.format(predictivePositive(expected, actual)));
		
		//                                       true negative
		// negative predictive value = ---------------------------------
		//                              true negative + false negative
		System.out.println("Predictive Negative: " + ff.format(predictiveNegative(expected, actual)));
		
		
		
		// 	      LL(overall probability) - LL(fit)
		// R^2 = ---------------------------------
		//            LL(overall probability)
		
		// overall probability = number of 1's / total number of results
		// overall probability = 7 / 10;
		CommonOps_DDRM.fill(actual, 7.0 / 10.0);
		
		actual = sigmoid(actual);
		actual.set(0, 0, 1.0 - actual.get(0, 0));  // first result: 0 - not approved
		actual.set(7, 0, 1.0 - actual.get(7, 0));  // seventh result: 0 - not approved
		
		CommonOps_DDRM.elementLog(actual, actual);
		
		double overall = CommonOps_DDRM.elementSum(actual);
		
		
		
		
		// best fit probability = ln(sigmoid(P0)) + ln(sigmoid(P1)) + .... ln(sigmoid(Pn))
		CommonOps_DDRM.mult(tests, weights, actual);
		
		actual = sigmoid(actual);
		actual.set(0, 0, 1.0 - actual.get(0, 0));  // first result: 0 - not approved
		actual.set(7, 0, 1.0 - actual.get(7, 0));  // seventh result: 0 - not approved
		
		CommonOps_DDRM.elementLog(actual, actual);
		
		double fitted = CommonOps_DDRM.elementSum(actual);
		
		double r_squared = (overall - fitted) / overall;
		
		System.out.println("R^2 of the optimal weights: " + ff.format(r_squared));
		
		
		
		
		// 2 ( LL(best fitted) - LL(overall probability) )
		// = Chi squared values of the degrees of freedom equal to the difference in the
		//   number of parameters in the two models.
		//   LL(best fitted) has two degree of freedom - actual results (y axis) and  the slope
		//   LL(overall probability) has one degree of freedom - actual results(y axis)
		//   Degrees of freedom = 2 - 1
		
		System.out.println("Chi Squared with one degree of freedom: " + ff.format((2 * (fitted - overall))));
		
	}
	
	public static DMatrixRMaj training(DMatrixRMaj trainings, DMatrixRMaj weights, 
			DMatrixRMaj results, double learningRate, double tolerance, int maxIterations, double p) {
		
		int numOfTrainingData = trainings.numRows;
		int numOfFeatures = trainings.numCols;
		
		for (int i = 0; i < maxIterations; i++) {
			
			DMatrixRMaj res = new DMatrixRMaj(numOfTrainingData, 1);
			CommonOps_DDRM.mult(trainings, weights, res);
		
			res = classify(res, p);
		
			CommonOps_DDRM.subtract(res, results, res);
		
			DMatrixRMaj gradients = new DMatrixRMaj(numOfFeatures, 1);
			CommonOps_DDRM.multTransA(1.0 / numOfTrainingData, trainings, res, gradients);
		
			DMatrixRMaj wweights = new DMatrixRMaj(numOfFeatures, 1);
			CommonOps_DDRM.scale(learningRate, gradients);
			CommonOps_DDRM.subtract(weights, gradients, wweights);
		
			if (MatrixFeatures.isEquals(weights, wweights, tolerance)) {
				i = maxIterations;
			}
		
			weights = wweights;
		}
		
		return weights;
	}
	
	public static DMatrixRMaj sigmoid(DMatrixRMaj inputs) {
		
		Sigmoid sigmoid = new Sigmoid();
		DMatrixRMaj output = new DMatrixRMaj(inputs.numRows, inputs.numCols);
		
		for (int i = 0; i < inputs.numRows; i++) {
			output.set(i, 0, sigmoid.value(inputs.get(i, 0)));
		}
		
		return output;
	}
	
	public static DMatrixRMaj classify(DMatrixRMaj tests, double p) {
		
		DMatrixRMaj res = sigmoid(tests);
		
		// cut off point: 0.5
		for (int i = 0; i < tests.numRows; i++) {
			res.set(i, 0, res.get(i, 0) > p ? 1 : 0);
		}
		
		return res;
	}
	
	public static DMatrixRMaj classify(DMatrixRMaj tests, DMatrixRMaj weights, double p) {
		
		DMatrixRMaj res = new DMatrixRMaj(tests.numRows, 1);
		
		CommonOps_DDRM.mult(tests, weights, res);
		
		return classify(res, p);
		
	}
	
	public static double accuracy(DMatrixRMaj expected, DMatrixRMaj actual) {
		
		// accuracy = number of correct predictions / number of predictions	
		DMatrixRMaj test = new DMatrixRMaj(expected.numRows, 1);
		
		CommonOps_DDRM.subtract(expected, actual, test);
		double notcorrects = MatrixFeatures.countNonZero(test);
		double corrects = expected.numRows - notcorrects;
		
		return corrects / expected.numRows;
	}
	
	public static double sensitivity(DMatrixRMaj expected, DMatrixRMaj actual) {
		
		//	  					 true positive 
		// sensitivity = ---------------------------------
		//                true positive + false negative
		
		double truePositive = 0.0;
		double falseNegative = 0.0;
		for (int i = 0; i < expected.numRows; i++) {
			
			if (actual.get(i, 0) == 1 && expected.get(i, 0) == 1)
				truePositive++;
			
			if (actual.get(i, 0) == 0 && expected.get(i, 0) == 1)
				falseNegative++;
		}
		
		if (truePositive + falseNegative == 0)
			return 0;
		
		return truePositive / (truePositive + falseNegative);
		
	}
	
	public static double specificity(DMatrixRMaj expected, DMatrixRMaj actual) {
		
		//	 					  true negative
		// specificity = ---------------------------------	
		//                true negative + false positive
		
		double trueNegative = 0.0;
		double falsePositive = 0.0;
		for (int i = 0; i < expected.numRows; i++) {
			
			if (actual.get(i, 0) == 0 && expected.get(i, 0) == 0)
				trueNegative++;
			
			if (actual.get(i, 0) == 1 && expected.get(i, 0) == 0)
				falsePositive++;
		}
	
		if (trueNegative + falsePositive == 0)
			return 0;
		
		return trueNegative / (trueNegative + falsePositive);
	}
	
	public static double predictivePositive(DMatrixRMaj expected, DMatrixRMaj actual) {
		
		//										true positive
		// positive predictive value = ---------------------------------
		//                              true positive + false positive	
		
		double truePositive = 0.0;
		double falsePositive = 0.0;
		for (int i = 0; i < expected.numRows; i++) {
			
			if (actual.get(i, 0) == 1 && expected.get(i, 0) == 1)
				truePositive++;
			
			if (actual.get(i, 0) == 1 && expected.get(i, 0) == 0)
				falsePositive++;
		}
		
		if (truePositive + falsePositive == 0)
			return 0;
		
		return truePositive / (truePositive + falsePositive);
	}

	public static double predictiveNegative(DMatrixRMaj expected, DMatrixRMaj actual) {
		
		//      								true negative
		// negative predictive value = ---------------------------------
		//                              true negative + false negative
		
		double trueNegative = 0.0;
		double falseNegative = 0.0;
		for (int i = 0; i < expected.numRows; i++) {
			
			if (actual.get(i, 0) == 0 && expected.get(i, 0) == 0)
				trueNegative++;
			
			if (actual.get(i, 0) == 0 && expected.get(i, 0) == 1)
				falseNegative++;
		}
		
		if (trueNegative + falseNegative == 0)
			return 0;
		
		return trueNegative / (trueNegative + falseNegative);
	}
}