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

public class AdaBoostBasic {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
						//		x,    y,  cls,  weight
						//  ---------------------------
							" 1.0,  1.0,   1,   1./8;" +
							" 1.5,  1.5,   1,   1./8;" +
							" 1.0,  2.0,   1,   1./8;" + 
							" 3.0,  1.0,   1,   1./8;" +
							" 2.0,  2.0,   1,   1./8;" +
							
							" 7.0,  8.0,  -1,   1./8;" +
							" 8.0,  9.0,  -1,   1./8;" +
							" 8.0,  8.0,  -1,   1./8;" +
							" 7.5,  8.5,  -1,   1./8;" +
							" 7.0, 10.0,  -1,   1./8 " +
						"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		Classifiers classifiers =  new Classifiers(
				new Classifier(13),
				new Classifier(29),
				new Classifier(83),
				new Classifier(89)	
		);
		
		
		classifiers.fit(A, 10);
			
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
		
		public void fit(DMatrixRMaj data, int epochs) {
			
			double min = Double.MAX_VALUE;
			Result target = null;
			
			
			for (int i = 0; i < epochs; i++) {
				
				for (Classifier classifier : classifiers) {
				
					Result result = classifier.classify(data);
					
					if (result.getErrorRate() < min) {
						min = result.getErrorRate();
						target = result;
					}
				}
			
				says.put(alpha(min), target.getClassifier());	
				
				System.out.println("EPOCH: " + (i + 1) + "   " + target);
				
				data = update(data, target);
			}		
		}
		
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
				data3.set(row, 3, 1.0 / 8.0);		
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
			return 0.5 * Math.log((1.0 - error) / error);
		}
		
		private static double adjust(double weight, double errors) {
			return weight * Math.pow(Math.E, alpha(errors));
		}
	}
	
	private static class Result {
		
		private static final DecimalFormat ff = new DecimalFormat("0.000");
		
		private Classifier classifier;
		private DMatrixRMaj inputs;
		private double errorRate;
		private Set<Integer> errorIndex;
		
		public Result(Classifier classifier, DMatrixRMaj inputs, double errorRate, Set<Integer> errorIndex) {
			this.classifier = classifier;
			this.inputs = inputs;
			this.errorRate = errorRate;
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
			
			builder.append(classifier);
			builder.append(" ==> ");
			builder.append(ff.format(errorRate));	
			builder.append(" : ");
			builder.append(errorIndex.stream().map(p -> p.toString()).collect(Collectors.joining(", ")));
			builder.append("\n");
			builder.append(inputs);
			builder.append("\n\n");
			
			return builder.toString();
		}
	}
	
	
	private static class Classifier {
		
		private int seed;
		
		public Classifier(int seed) {
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
			double val = (double) Math.abs(Double.valueOf(x * 20 + y).hashCode()) % seed / seed;
			return val >= 0.5 ? 1 : -1;
		}
		
		@Override
		public String toString() {
			return "classifier(" + seed + ")";
		}
	}

}
