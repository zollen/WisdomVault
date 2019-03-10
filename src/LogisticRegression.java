import org.apache.commons.math3.analysis.function.Sigmoid;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class LogisticRegression {

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
			
		weights = training(trainings, weights, results, 0.01, 0.0001, 10000);
		System.out.println("OPTIMAL WEIGHTS: " + weights);
		
		eq.process("TESTS = [" +
			//   x0,  x1,  x2,  x3,  x4         <-- features
				" 1, 7.2, 2.5, 1.0, 1.3;" +    // test data #1
				" 1, 5.2, 1.9, 5.2, 2.2;" +    // test data #2
				" 1, 4.3, 2.2, 3.8, 1.9;" +	   // test data #3
				" 1, 6.2, 2.7, 4.8, 2.8;" +	   // test data #4
				" 1, 4.5, 3.3, 4.5, 3.0;" +    // test data #5
				" 1, 6.6, 1.8, 5.0, 3.2;" +    // test data #6
				" 1, 5.2, 2.3, 3.9, 2.5;" +    // test data #7
				" 1, 7.0, 3.3, 2.5, 1.4;" +    // test data #8
				" 1, 4.9, 1.8, 3.2, 3.2;" +    // test data #9
				" 1, 5.1, 2.5, 3.0, 2.7 " +    // test data #10
				"]");
		
		DMatrixRMaj tests = eq.lookupDDRM("TESTS");
		
		DMatrixRMaj actual = classify(tests, weights);
	
		System.out.println("TESTS_ACTUAL_RESULTS: " + actual);
		
		eq.process("TESTS_EXPECTED_RESULTS = [ 0; 1; 1; 1; 0; 1; 0; 1; 1; 1 ]");
		
		DMatrixRMaj expected = eq.lookupDDRM("TESTS_EXPECTED_RESULTS");
		
		
		
		DMatrixRMaj test = new DMatrixRMaj(tests.numRows, 1);
		
		// accuracy = number of correct predictions / number of predictions
		CommonOps_DDRM.subtract(expected, actual, test);
		System.out.println("NOT CORRECTS: " + MatrixFeatures.countNonZero(test));
		System.out.println("CORRECTS: " + (tests.numRows - MatrixFeatures.countNonZero(test)));
		System.out.println("Accuracy: " + 7.0 / 10.0);
		
	
		
		
		
		//      				  true positive 
		// sensitivity = ---------------------------------
		//                true positive + false negative
		double sensitivity = 6.0 / (6.0 + 1.0);
		
		//      				  true negative
		// specificity = ---------------------------------
		//                true negative + false positive
		double specificity = 1.0 / (1.0 + 2.0);
		
		//      								true positive
		// positive predictive value = ---------------------------------
		//                              true positive + false positive
		double predictive_positive = 6.0 / (6.0 + 2.0);
		
		//                                       true negative
		// negative predictive value = ---------------------------------
		//                              true negative + false negative
		double predictive_negative = 1.0 / (1.0 + 1.0);
		
		System.out.println("Sensitivity: " + sensitivity);
		System.out.println("Specificity: " + specificity);
		System.out.println("Predictive Positive: " + predictive_positive);
		System.out.println("Predictive Negative: " + predictive_negative);
		
		
		
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
		
		System.out.println("R^2 of the optimal weights: " + r_squared);
		
		
		
		
		// 2 ( LL(best fitted) - LL(overall probability) )
		// = Chi squared values of the degrees of freedom equal to the difference in the
		//   number of parameters in the two models.
		//   LL(best fitted) has two degree of freedom - actual results (y axis) and  the slope
		//   LL(overall probability) has one degree of freedom - actual results(y axis)
		//   Degrees of freedom = 2 - 1
		
		System.out.println("Chi Squared with one degree of freedom: " + (2 * (fitted - overall)));
		
	}
	
	public static DMatrixRMaj training(DMatrixRMaj trainings, DMatrixRMaj weights, 
			DMatrixRMaj results, double learningRate, double tolerance, int maxIterations) {
		
		int numOfTrainingData = trainings.numRows;
		int numOfFeatures = trainings.numCols;
		
		for (int i = 0; i < maxIterations; i++) {
			
			DMatrixRMaj res = new DMatrixRMaj(numOfTrainingData, 1);
			CommonOps_DDRM.mult(trainings, weights, res);
		
			res = classify(res);
		
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
	
	public static DMatrixRMaj classify(DMatrixRMaj tests) {
		
		DMatrixRMaj res = sigmoid(tests);
		
		for (int i = 0; i < tests.numRows; i++) {
			res.set(i, 0, res.get(i, 0) > 0.5 ? 1 : 0);
		}
		
		return res;
	}
	
	public static DMatrixRMaj classify(DMatrixRMaj tests, DMatrixRMaj weights) {
		
		DMatrixRMaj res = new DMatrixRMaj(tests.numRows, 1);
		
		CommonOps_DDRM.mult(tests, weights, res);
		
		return classify(res);
		
	}

}