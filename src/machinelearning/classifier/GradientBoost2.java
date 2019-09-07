package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

import linearalgebra.MatrixFeatures;

public class GradientBoost2 {
	
	/**
	 * This is a GradientBoosting Classification example!
	 */
	
	private static final DecimalFormat ff = new DecimalFormat("0.00");
	private static final Random rand = new Random(83);
	private static final double THRESHOLD = 0.0001;
	private static final int TOTAL_TREES = 1000;
	private static final double LEARNING_RATE = 0.1;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [" +
		 //   Likes Popcorn,   Age,  Color,   Like Movies
					"     1,    12,      0,    1;" +
					"     1,    87,      1,    1;" +
					"     0,    44,      0,    0;" +
					"     1,    19,      2,    0;" +
					"     0,    32,      1,    1;" +
					"     0,    14,      0,    1 " +
					"]");
		
		
		DMatrixRMaj A = eq.lookupDDRM("A");

		
		// Like Moves column
		DMatrixRMaj W = CommonOps_DDRM.extractColumn(A, 3, null);
	
		/*** We use log odd for our probabilty calculation  ***/
		/** There are 4 people who like movies, but 2 people do not.  **/
		/** Instead of averging it like the regression example, 
		/** we use toProbabilty to 'average' **/
		double avg = toProbability(4.0/2.0);
		
		DMatrixRMaj PREV = new DMatrixRMaj(W.numRows, 1);
		CommonOps_DDRM.fill(PREV, 0.0);
		
		// First residual;
		DMatrixRMaj R = new DMatrixRMaj(W.numRows, 1);
		DMatrixRMaj AVG = new DMatrixRMaj(W.numRows, 1);
		CommonOps_DDRM.fill(AVG, avg);
		
		DMatrixRMaj PROB = AVG.copy();
		
		CommonOps_DDRM.subtract(W, PROB, R);
		
		
		DMatrixRMaj r = new DMatrixRMaj(R.numRows, 1);
		CommonOps_DDRM.fill(r, 0.0);
		
		
		
		List<WeakTree> trees = new ArrayList<WeakTree>();
		
		DMatrixRMaj ACC = new DMatrixRMaj(R.numRows, 1);
		CommonOps_DDRM.fill(ACC, 0.0);
		
		// Let's start building trees
		for (int i = 0; i < TOTAL_TREES; i++) {
					
			WeakTree tree = new WeakTree(rand, PROB);	
					
			DMatrixRMaj data = toMatrix(A, R);
			
			tree.fit(data);
			
			trees.add(tree);
						
			CommonOps_DDRM.add(ACC.copy(), LEARNING_RATE, tree.toMatrix(), ACC);
			
			DMatrixRMaj logOdd = new DMatrixRMaj(ACC.numRows, 1);
			CommonOps_DDRM.add(AVG, ACC, logOdd);
			
			PROB = toProbability(logOdd);
						
			CommonOps_DDRM.subtract(W, PROB, R);
			
			
			if (MatrixFeatures.isEquals(R, PREV, THRESHOLD))
				break;	
			
			PREV = R.copy();
		}
		
		System.out.println("GraidentBoosting Classification Example");
		System.out.println("Total Trees: " + trees.size());
		
	
		classify(trees, avg, A);	
	}
	

	private static double toProbability(double val) {
		
		if (val <= 0)
			val = Double.MIN_VALUE;
		
		return Math.pow(Math.E, Math.log(val)) / 
				(1.0 + Math.pow(Math.E, Math.log(val)));
	}
	
	public static DMatrixRMaj toProbability(DMatrixRMaj O) {
		
		DMatrixRMaj r = new DMatrixRMaj(O.numRows, O.numCols);
		
		for (int row = 0; row < r.numRows; row++) {				
			r.set(row, 0, toProbability(O.get(row, 0)));
		}
		
		return r;
	}
	
	private static void classify(List<WeakTree> trees, double avg, DMatrixRMaj data) throws Exception {
		
		for (int row = 0; row < data.numRows; row++) {
			
			double sum = avg;
			
			for (WeakTree tree : trees) {
				sum += tree.classify(row) * LEARNING_RATE;
			}
			
			System.out.println(data.get(row, 0) + ", " + data.get(row, 1) + ", " +
						data.get(row, 2) + ", " + data.get(row, 3) + " ===> " + 
					ff.format(toProbability(sum)));
		}
	}
	
	private static DMatrixRMaj toMatrix(DMatrixRMaj A, DMatrixRMaj R) {
		
		DMatrixRMaj mat = A.copy();
		
		for (int row = 0; row < A.numRows; row++) {
			
			mat.set(row, A.numCols - 1, R.get(row, 0));
		}
		
		return mat;
	}
	

	
	private static class WeakTree {
		
		private static final int MAX_LEAVES = 3;
		
		private Map<Double, Set<Pair<Integer, Double>>> map = new HashMap<Double, Set<Pair<Integer, Double>>>();
		private Random rand;
		private int chosen;
		private DMatrixRMaj prob;
		
		@SuppressWarnings("unused")
		public WeakTree(int chosen, DMatrixRMaj prob) {
			this.chosen = chosen;
			this.prob = prob;
		}
		
		public WeakTree(Random rand, DMatrixRMaj prob) {
			this.rand = rand;
			this.prob = prob;
		}
		
		public void fit(DMatrixRMaj A) {
			
			if (rand != null)
				this.chosen = rand.nextInt(3);
					
			switch(this.chosen) {
			case 0:
				classifyB(A, 0, A.numCols - 1);
			break;
			case 1:
				classifyN(A, 1, A.numCols - 1);
			break;
			default:
				classifyB(A, 2, A.numCols - 1);
			}
			
			average();
		}
		
		public double classify(int rowIndex) {
			
			DoubleAdder val = new DoubleAdder();
			
			map.entrySet().forEach(p -> {
				
				p.getValue().stream().forEach(k -> {
					
					if (k.getFirst() == rowIndex)
						val.add(k.getSecond());
				});
			});
			
			return val.doubleValue();
		}
		
		private void classifyN(DMatrixRMaj A, int col, int cls) {
			
			Map<Integer, Integer> m = new TreeMap<Integer, Integer>();
			
			for (int row = 0; row < A.numRows; row++) {
				m.put((int) A.get(row, col), row);
			}
			
			int size = Math.floorDiv(m.size(), MAX_LEAVES);
			AtomicInteger key = new AtomicInteger(0);
			
			m.entrySet().stream().forEach(p -> {
				
				Set<Pair<Integer, Double>> set = map.get(key.doubleValue());
				if (set == null) {
					set = new HashSet<Pair<Integer, Double>>();
					map.put(key.doubleValue(), set);
				}
				else {
					if (set.size() >= size) {
						key.incrementAndGet();
						set = new HashSet<Pair<Integer, Double>>();
						map.put(key.doubleValue(), set);
					}
				}
				
				set.add(new Pair<>(p.getValue(), A.get(p.getValue(), cls)));
			});
		}
		
		private void classifyB(DMatrixRMaj A, int col, int cls) {
			
			for (int row = 0; row < A.numRows; row++ ) {
				
				double key = A.get(row, col);
				
				Set<Pair<Integer, Double> > set = map.get(key);
				if (set == null) {
					set = new HashSet<Pair<Integer, Double>>();
					map.put(key, set);
				}
				
				set.add(new Pair<>(row, A.get(row, cls)));				
			}
		}	
		
		public void average() {
			
			/*** This following step is actually the derivative calcuation  ***/
	        // Σ ( residual / Σ (Previous Probabilty * (1 - Previous Probability)) ) 
			
			map.entrySet().stream().forEach(p -> {
				
				DoubleAdder nomins = new DoubleAdder();
				DoubleAdder denomins = new DoubleAdder();
				
				p.getValue().stream().forEach(k -> {
					
					nomins.add(k.getSecond());
					denomins.add(prob.get(k.getFirst(), 0) * (1.0 - prob.get(k.getFirst(), 0)));
					
				});
				
				Set<Pair<Integer, Double>> set = new HashSet<Pair<Integer, Double>>();
				
				p.getValue().stream().forEach(k -> {
					
					set.add(new Pair<>(k.getFirst(), nomins.doubleValue() / denomins.doubleValue()));
				});
				
				map.put(p.getKey(), set);
			});
		}
		
		public DMatrixRMaj toMatrix() {
			
			DMatrixRMaj mat = new DMatrixRMaj(prob.numRows, 1);
			
			map.entrySet().stream().forEach(p -> {
				
				p.getValue().stream().forEach(k -> {
					
					mat.set(k.getFirst(), 0, k.getSecond());
				});
				
			});
					
			return mat;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			
			map.entrySet().stream().forEach(p -> {
				
				builder.append(p.getKey() + "\n");
				
				p.getValue().stream().forEach(k -> {
					
					builder.append("   row: " + k.getFirst() + " ==> " + k.getSecond() + "\n");
				});
			});
			
			return builder.toString();
		}
	}
	
}
