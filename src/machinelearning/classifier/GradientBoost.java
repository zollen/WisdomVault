package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

import linearalgebra.MatrixFeatures;
import weka.classifiers.trees.RandomTree;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class GradientBoost {
	
	/**
	 * This is a GradientBoosting Regression example!
	 */
	
	private static final DecimalFormat ff = new DecimalFormat("0.00");
	private static final double THRESHOLD = 0.01;
	private static final int TOTAL_TREES = 1000;
	private static final double LEARNING_RATE = 0.1;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [" +
			//	     Height, Color, Gender, Weight
					"   1.6,     0,      0,   88;" +
					"   1.6,     1,      1,   76;" +
					"   1.5,     0,      1,   56;" +
					"   1.8,     2,      0,   73;" +
					"   1.5,     1,      0,   77;" +
					"   1.4,     0,      1,   57 " +
					"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		// Let's get the average weight as our first prediction
		// The first prediction suggest all predicted weigths are 71.166667
		DMatrixRMaj W = CommonOps_DDRM.extractColumn(A, 3, null);
		
		/*** The averaging step is actually the derivative calcuation  ***/
		/*** MSE: Σ 1/2 (Prediction - Actual)^2  ***/
		double avg = CommonOps_DDRM.sumCols(W, null).get(0, 0) / W.numRows;
		
		DMatrixRMaj TARGET = new DMatrixRMaj(W.numRows, 1);
		CommonOps_DDRM.fill(TARGET, 0.0);
		
		// First residual;
		DMatrixRMaj R = new DMatrixRMaj(W.numRows, 1);
		DMatrixRMaj AVG = new DMatrixRMaj(W.numRows, 1);
		CommonOps_DDRM.fill(AVG, avg);
		CommonOps_DDRM.subtract(W, AVG, R);
		
		DMatrixRMaj r = new DMatrixRMaj(R.numRows, 1);
		CommonOps_DDRM.fill(r, 0.0);
		
		
		
		List<RandomTree> trees = new ArrayList<RandomTree>();
		
		// Let's start building trees
		for (int i = 0; i < TOTAL_TREES; i++) {
					
			RandomTree tree = new RandomTree();	
			tree.setMaxDepth(1);   // we have a small toy data, this tree depth would be 3 to 6 
					
			Instances data = toInstances(A, R);
			
			tree.buildClassifier(data);
			
			/*** The averaging step is actually the derivative calcuation  ***/
			/*** MSE: Σ 1/2 (Prediction - Actual)^2  ***/
			DMatrixRMaj C = toMatrix(tree, data);
			
			trees.add(tree);

			
			CommonOps_DDRM.add(r.copy(), LEARNING_RATE, C, r);
			DMatrixRMaj tmp = new DMatrixRMaj(W.numRows, 1);
			CommonOps_DDRM.add(AVG, r, tmp);
			CommonOps_DDRM.subtract(W, tmp, R);
			
			if (MatrixFeatures.isEquals(R, TARGET, THRESHOLD))
				break;			
		}
		
		System.out.println("GraidentBoosting Regression Example");
		System.out.println("Total Trees: " + trees.size());
		
		for (int row = 0; row < A.numRows; row++) {
			
			Instances tests = data(A.get(row, 0), (int) A.get(row, 1), (int) A.get(row, 2), A.get(row, 3));
			for (Instance test : tests)
				System.out.println(test + "   ==>    " + ff.format(classify(trees, avg, test)));
		}		
	}
	
	private static Instances data(double height, int color, int gender, double weight) {
		
		ArrayList<Attribute> attrs = setup();

		Instances testing = new Instances("TESTING", attrs, 1);
		Instance data = new DenseInstance(attrs.size());	
		
		data.setValue(attrs.get(0), height);
		data.setValue(attrs.get(1), attrs.get(1).value(color));
		data.setValue(attrs.get(2), attrs.get(2).value(gender));
		data.setValue(attrs.get(3), weight);
		
		testing.add(data);
		
		testing.setClassIndex(testing.numAttributes() - 1);
	
		return testing;		
	}
	
	private static double classify(List<RandomTree> trees, double avg, Instance data) throws Exception {
		
		double sum = avg;
		for (RandomTree tree : trees) {
			sum += tree.classifyInstance(data) * LEARNING_RATE;
		}
				
		return sum;
	}
	
	private static DMatrixRMaj toMatrix(RandomTree tree, Instances data) throws Exception {
		
		double [][] res = tree.distributionsForInstances(data);
		
		DMatrixRMaj mat = new DMatrixRMaj(res);
		
		return mat;
	}
	
	private static Instances toInstances(DMatrixRMaj A, DMatrixRMaj R) {
		
		ArrayList<Attribute> attrs = setup();
		
		Instances training = new Instances("TRAINING", attrs, A.numRows);
		
		for (int row = 0; row < A.numRows; row++) {
			
			Instance data = new DenseInstance(attrs.size());	
			
			for (int col = 0; col < A.numCols; col++) {
				
				switch(col) {
				case 0:
					data.setValue(attrs.get(col), A.get(row, col));
				break;
				case 1:
					data.setValue(attrs.get(col), attrs.get(col).value((int) A.get(row, col)));
				break;
				case 2:
					data.setValue(attrs.get(col), attrs.get(col).value((int) A.get(row, col)));
				break;
				default:
					data.setValue(attrs.get(col), R.get(row, 0));
				}
			}
			
			training.add(data);
			
		}
		
		training.setClassIndex(training.numAttributes() - 1);
		
		return training;
	}
	
	private static ArrayList<Attribute> setup() {
		
		final ArrayList<String> colorVals = new ArrayList<String>();
		colorVals.add("Blue");
		colorVals.add("Green");
		colorVals.add("Red");
		
		final ArrayList<String> genderVals = new ArrayList<String>();
		genderVals.add("Male");
		genderVals.add("Female");
		
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("Height");
		Attribute attr2 = new Attribute("Color", colorVals);
		Attribute attr3 = new Attribute("Gender", genderVals);
		Attribute attr4 = new Attribute("Residual");
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		attrs.add(attr4);
		
		return attrs;
		
	}
	
	
}
