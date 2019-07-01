package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

// AdaBoost or its varations are best for classification problems. Boosting NEVER OVERFITTED!!
public class AdaBoostBasic {
	
	private static final double THRESHOLD = 0.00001;
	private static final int X = 0;
	private static final int Y = 1;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
						//		x,    y,  cls,  weight
						//  ---------------------------
							" 1.0,  1.0,   1,   1./10;" +
							" 1.5,  1.5,   1,   1./10;" +
							" 1.0,  2.0,   1,   1./10;" + 
							" 3.0,  1.0,   1,   1./10;" +
							" 2.0,  2.0,   1,   1./10;" +
							
							" 7.0,  8.0,  -1,   1./10;" +
							" 8.0,  9.0,  -1,   1./10;" +
							" 8.0,  8.0,  -1,   1./10;" +
							" 7.5,  8.5,  -1,   1./10;" +
							" 7.0, 10.0,  -1,   1./10 " +
						"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		Classifiers classifiers =  new Classifiers(
				
				new Classifier(X, 2.5),
				new Classifier(Y, 2.0),
				new Classifier(Y, 8.5),
				new Classifier(X, 8.0)	
		);
		
		
		classifiers.fit(A, 10);
		
		eq.process("T = [ " + 
						//		 x,    y,  expected,  actual
						//  ---------------------------------
							" -1.0,  -1.0,        1,      0;" +
							"  3.2,   1.5,        1,      0;" +
							"  6.6,   7.8,       -1,      0;" +
							"  9.2,   8.8,       -1,      0 " + 
							"]");
		
		DMatrixRMaj T = eq.lookupDDRM("T");
		
		System.out.println(classifiers.classify(T));
	}
	
	
	private static class Classifiers {
		
		private static Random rand = new Random(83);
		
		private List<Classifier> classifiers = new ArrayList<Classifier>();
		Map<Double, Classifier> says = new LinkedHashMap<Double, Classifier>();
		
		public Classifiers(Classifier ...clsifier) {
			
			for (int i = 0; i < clsifier.length; i++) {
				classifiers.add(clsifier[i]);
			}
		}
		
		public Result classify(DMatrixRMaj data) {
			
			double errors = 0;
			Set<Integer> index = new HashSet<Integer>();
			
			for (int row = 0; row < data.numRows; row++) {
			
				data.set(row, 3, classify(data.get(row, 0), data.get(row, 1)));
			}
			
			return new Result(says, data, errors, index);
		}
		
		public int classify(double x, double y) {
			
			double sum = says.entrySet().stream().mapToDouble(say -> {
				
				return say.getKey() * say.getValue().classify(x, y);
			}).sum();
					
			if (sum >= 0)
				return 1;
			
			return -1;
		}
		
		public void fit(DMatrixRMaj data, int epochs) {
			
			List<Classifier> clsifiers = new ArrayList<Classifier>(classifiers);
			
			if (clsifiers.size() < epochs)
				epochs = clsifiers.size();
			
			
			for (int i = 0; i < epochs; i++) {
				
				double min = Double.MAX_VALUE;
				Result target = null;
				
				
				// Not necessary to go through all classifiers
				// In each epoch, only a few classifiers actual matter
				for (Classifier classifier : clsifiers) {
				
					Result result = classifier.classify(data);
					
					if (result.getErrorRate() < min) {
						min = result.getErrorRate();
						target = result;
					}
					
					if (result.getErrorRate() <= THRESHOLD || result.getErrorRate() >= 1.0) 
						break;
				}
				
				clsifiers.remove(target.getClassifier());
				
				data = update4(data, target);
				
				says.put(alpha(min), target.getClassifier());	
				
				System.out.println("EPOCH: " + (i + 1) + "   " + target);
				
				if (target.getErrorRate() <= 0 || target.getErrorRate() >= 0.9999)
					break;
			}		
		}
		
		// My improvement#2 based on update2
		private DMatrixRMaj update4(DMatrixRMaj data, Result result) {
			
			double sum = 0.0;
			
			for (int row = 0; row < data.numRows; row++) {
				
				double w = data.get(row, 3);
	
				if (result.getErrorIndex().contains(row)) {				
					w += w * (double) 1.0 / result.getErrorIndex().size();
				}
				
				data.set(row, 3, w);
				
				sum += w;
			}
			
			for (int row = 0; row < data.numRows; row++) {
				data.set(row, 3, data.get(row, 3) / sum);
			}
			
			return data;	
		}
		
		// My improvement based on update2
		@SuppressWarnings("unused")
		private DMatrixRMaj update3(DMatrixRMaj data, Result result) {
			
			DMatrixRMaj data2 = data.copy();
			
			for (int row = 0; row < data2.numRows; row++) {
				
				double w = 0.0;
	
				if (result.getErrorIndex().contains(row)) {				
					w = 3.0 / 4.0 * (double) 1.0 / result.getErrorIndex().size();
				}
				else {
					w = 1.0 / 4.0 * (double) 1.0 / (data.numRows - result.getErrorIndex().size());
				}
				
				data2.set(row, 3, w);
			}
			
			DMatrixRMaj data3 = new DMatrixRMaj(data.numRows, data.numCols);
			
			for (int row = 0; row < data3.numRows; row++) {
				
				int index = random(data2);
				
				data3.set(row, 0, data.get(index, 0));
				data3.set(row, 1, data.get(index, 1));
				data3.set(row, 2, data.get(index, 2));
				data3.set(row, 3, 1.0 / data.numRows);		
			}
				
			return data3;	
		}
		
		// MIT professor discovered better method for calculating weight
		@SuppressWarnings("unused")
		private DMatrixRMaj update2(DMatrixRMaj data, Result result) {
			
			DMatrixRMaj data2 = data.copy();
			
			for (int row = 0; row < data2.numRows; row++) {
				
				double w = 0.0;
	
				if (result.getErrorIndex().contains(row)) {				
					w = 0.5 * (double) 1.0 / result.getErrorIndex().size();
				}
				else {
					w = 0.5 * (double) 1.0 / (data.numRows - result.getErrorIndex().size());
				}
				
				data2.set(row, 3, w);
			}
			
			DMatrixRMaj data3 = new DMatrixRMaj(data.numRows, data.numCols);
			
			for (int row = 0; row < data3.numRows; row++) {
				
				int index = random(data2);
				
				data3.set(row, 0, data.get(index, 0));
				data3.set(row, 1, data.get(index, 1));
				data3.set(row, 2, data.get(index, 2));
				data3.set(row, 3, 1.0 / data.numRows);		
			}
				
			return data3;	
		}
		
		// standard Adaboost weight calculation
		@SuppressWarnings("unused")
		private DMatrixRMaj update(DMatrixRMaj data, Result result) {
			
			DMatrixRMaj data2 = data.copy();
			
			double sum = 0.0;
			for (int row = 0; row < data2.numRows; row++) {
				
				double w = data2.get(row, 3);
	
				if (result.getErrorIndex().contains(row)) {
					
					w = adjust(w, result.getErrorRate());
				
					data2.set(row, 3, w);
				}
				
				sum += w;
			}
			
			for (int row = 0; row < data2.numRows; row++) {
				data2.set(row, 3, data2.get(row, 3) / sum);
			}
			
		
			DMatrixRMaj data3 = new DMatrixRMaj(data.numRows, data.numCols);
			
			for (int row = 0; row < data3.numRows; row++) {
				
				int index = random(data2);
				
				data3.set(row, 0, data.get(index, 0));
				data3.set(row, 1, data.get(index, 1));
				data3.set(row, 2, data.get(index, 2));
				data3.set(row, 3, 1.0 / data.numRows);		
			}
				
			return data3;
		}
		
		private static int random(DMatrixRMaj data) {
			
			double prob = rand.nextDouble();
			
			double sum = data.get(0, 3);
			
			if (prob <= sum)
				return 0;
			
			for (int row = 1; row < data.numRows; row++) {
				
				if (prob > sum && prob <= sum + data.get(row, 3))
					return row;
				
				sum += data.get(row, 3);
			}
			
			return data.numRows - 1;
		}
		
		private static double alpha(double error) {		
			if (error <= 0)
				error = THRESHOLD;
			
			return 0.5 * Math.log((1.0 - error) / error);
		}
		
		private static double adjust(double weight, double errors) {
			return weight * Math.pow(Math.E, alpha(errors));
		}
	}
	
	private static class Result {
		
		private static final DecimalFormat ff = new DecimalFormat("0.000");
		
		private Classifier classifier;
		private Map<Double, Classifier> classifiers;
		private DMatrixRMaj inputs;
		private double errorRate;
		private Set<Integer> errorIndex;
		
		public Result(Map<Double, Classifier> classifiers, DMatrixRMaj inputs, double errorRate, Set<Integer> errorIndex) {
			this.classifiers = classifiers;
			this.inputs = inputs;
			this.errorRate = errorRate < 1.0 ? errorRate : 0.999999;
			this.errorIndex = errorIndex;		
		}
		
		public Result(Classifier classifier, DMatrixRMaj inputs, double errorRate, Set<Integer> errorIndex) {
			this.classifier = classifier;
			this.inputs = inputs;
			this.errorRate = errorRate < 1.0 ? errorRate : 0.999999;
			this.errorIndex = errorIndex;
		}
		
		public Classifier getClassifier() {
			return classifier;
		}
		
		@SuppressWarnings("unused")
		public DMatrixRMaj getInputs() {
			return inputs;
		}
		
		public double getErrorRate() {
			return errorRate;
		}

		public Set<Integer> getErrorIndex() {
			return errorIndex;
		}
		
		public String toString() {
			StringBuilder builder = new StringBuilder();
			
			if (classifier != null) {
				builder.append(classifier);
				builder.append(" ==> ");
				builder.append("e: " + ff.format(errorRate));	
				builder.append(" : ");
				builder.append("a: " + ff.format(Classifiers.alpha(errorRate)));
				builder.append(" : ");
				builder.append(errorIndex.stream().map(p -> p.toString()).collect(Collectors.joining(", ")));
				builder.append("\n");
			}
			else
			if (classifiers != null) {
				classifiers.entrySet().stream().forEach(entry -> {
					
					if (builder.length() <= 0)
						builder.append(" = ");
					else
					if (builder.length() > 0)
						builder.append(" + ");
					
					builder.append(ff.format(entry.getKey()));
					builder.append(" * ");
					builder.append(entry.getValue());
					builder.append("\n");
					
				});
			}
				
			builder.append(inputs);
			builder.append("\n\n");
			
			return builder.toString();
		}
	}
	
	// Dummy Weak Classifier
	private static class Classifier {
		
		private double seed;
		private int attribute;
		
		public Classifier(int attr, double seed) {
			this.attribute = attr;
			this.seed = seed;
		}
		
		public Result classify(DMatrixRMaj data) {
			
			double errors = 0;
			Set<Integer> index = new HashSet<Integer>();
			
			for (int row = 0; row < data.numRows; row++) {
			
				if (classify(data.get(row, 0), data.get(row, 1)) != data.get(row, 2)) {
					errors += data.get(row, 3);
					index.add(row);
				}
			}
			
			return new Result(this, data, errors, index);
		}
		
		public int classify(double x, double y) {	
			
			if (attribute == 0) {
				if (seed > x)
					return 1;
				else
					return -1;
			}
			else {
				if (seed > y)
					return 1;
				else
					return -1;
			}
		}
		
		@Override
		public String toString() {
			return "classifier(" + (attribute == 0 ? "X" : "Y") + ", " + seed + ")";
		}
	}

}
