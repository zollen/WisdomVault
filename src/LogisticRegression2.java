import org.apache.commons.math3.analysis.function.Sigmoid;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class LogisticRegression2 {

	public static void main(String... args) {
		// TODO Auto-generated method stub
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
			
		System.out.println(training(trainings, weights, results, 0.01, 0.0001, 10000));
	}
	
	public static DMatrixRMaj training(DMatrixRMaj trainings, DMatrixRMaj weights, 
			DMatrixRMaj results, double learningRate, double tolerance, int maxIterations) {
		
		int numOfTrainingData = trainings.numRows;
		int numOfFeatures = trainings.numCols;
		
		for (int i = 0; i < maxIterations; i++) {
			
			DMatrixRMaj res = new DMatrixRMaj(numOfTrainingData, 1);
			CommonOps_DDRM.mult(trainings, weights, res);
		
			res = sigmoid(res);
		
			CommonOps_DDRM.subtract(res, results, res);
		
			DMatrixRMaj gradients = new DMatrixRMaj(numOfFeatures, 1);
			CommonOps_DDRM.multTransA(1.0 / numOfTrainingData, trainings, res, gradients);
		
			DMatrixRMaj wweights = new DMatrixRMaj(numOfFeatures, 1);
			CommonOps_DDRM.scale(learningRate, gradients);
			CommonOps_DDRM.subtract(weights, gradients, wweights);
		
			if (MatrixFeatures.isEquals(weights, wweights, tolerance)) {
				break;
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

}