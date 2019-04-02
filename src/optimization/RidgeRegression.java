package optimization;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class RidgeRegression {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final Random rand = new Random(0);
	
	public static final Vector2D [] points = {
							new Vector2D(1.0, 1.0),
							new Vector2D(2.0, 3.0),
							new Vector2D(4.0, 2.0),
							new Vector2D(5.0, 3.0),
							new Vector2D(6.0, 6.0),
							new Vector2D(8.0, 5.0),
							new Vector2D(8.0, 9.0),
							new Vector2D(10.0, 8.0),
							new Vector2D(11.0, 10.0)
						};

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Ridge Regression introduces penalty to regression for preventing small training 
		// data from over-fitting. For example with two training data points - a straight line 
		// would have zero squared sum residuals, zero bias but large variance when test with
		// other 'unseen' testing data points. Ridge regression ensure the straight line does
		// not matched the two training points, introduce small bias in exchange of significant
		// reduction of variances from testing with new data points.
		
		{
			// OLS (x, y) using commons math library
			SimpleRegression regression = new SimpleRegression();
		
			regression.addData(points[0].getX(), points[0].getY());
			regression.addData(points[1].getX(), points[1].getY());
			regression.addData(points[2].getX(), points[2].getY());
			regression.addData(points[3].getX(), points[3].getY());
			regression.addData(points[4].getX(), points[4].getY());
			regression.addData(points[5].getX(), points[5].getY());
			regression.addData(points[6].getX(), points[6].getY());
			regression.addData(points[7].getX(), points[7].getY());
			regression.addData(points[8].getX(), points[8].getY());
			
			RegressionResults result = regression.regress();
			
			System.out.println("Sum of Squared Errors: " + ff.format(result.getErrorSumSquares()));
			System.out.println("R^2: " + ff.format(result.getRSquared()));
			
			double [] pts = result.getParameterEstimates();
		
			System.out.println("OLS: " + Arrays.stream(pts).mapToObj( p -> ff.format(p) ).collect(
							Collectors.joining(", ")));
		}
		
		{
			// Now let's assume we have only two training data points
			// (1.0, 1.0) and (2.0, 3.0)
			
			// Least Squared Approx
			// --------------------
			// y = b + mx, where mx = slope * weight, where weight is the minimized of
			// the sum of squared residuals
			
			
			// With (1.0, 1.0) and (2.0, 3.0): y = -1 + 2x would perfectly fitted the two points.
			// Zero sums of residuals^2 (bias) but very high variance because it would have 
			// large errors to the remaining 'unseen' points.
			
			// Ridge Regression
			// ----------------
			// Ridge Regression introduces penalty or bias 
			// y = b + mx, where mx = slope * weight, where weight is the minimized of
			// 0 + 1 * 2^2 = 4 (let's assume λ is 1)
			// using the above least squared answer (-0.420, 1.020)
			// y = -0.42 + 1.02x
			// -0.42 + 1.02(1) = 0.6     (1.0, 0.6)
			// -0.42 + 1.02(2) = 1.62    (2.0, 1.62)
			// (1.0 - 0.6)^2 + (3 - 1.62)^2 + 1 * 1.02^2 = 2.14 
			// 2.14 is obviously smaller bias than 4 (perfect fitted)
			
			// λ = 10-folds cross validation 
			
			// Ridge Regression helps reduce variance by shrinking the parameters and make
			// our prediction less sensitive to them. In general, the ridge regression
			// penalty includes all of the parameters *EXCEPT* the y-intercept (b).
			
			// performing k-folds cross validation to estimate the lambda
			
			// General formula of least squared (OLS) : inv(A'A) A'y
			// (1.0, 1.0), (2.0, 3.0), (4.0, 2.0), (5.0, 3.0), (6.0, 6.0), (8.0, 9.0)
			// C + Dx = y
			// ==========
			// C + 1D = 1
			// C + 2D = 3
			// C + 4D = 2
			// C + 5D = 3
			// C + 6D = 6
			// C + 8D = 9
			Equation eq = new Equation();
			eq.process("A = [" +
							" 1, 1;" +
							" 1, 2;" +
							" 1, 4;" +
							" 1, 5;" +
							" 1, 6;" +
							" 1, 8;" +
							" 1, 8;" +
							" 1, 10;" +
							" 1, 11 " +
							"]");
			
			eq.process("b = [ 1; 3; 2; 3; 6; 5; 9; 8; 10 ]"); 
			
			eq.process("K = inv(A' * A) * A' * b");
			
			System.out.println("OLS: " + eq.lookupDDRM("K"));
			
			DMatrixRMaj A = eq.lookupDDRM("A");
			DMatrixRMaj b = eq.lookupDDRM("b");
			
			// General formula of ridge regression : inv(A'A + λI) A'y
			// as λ increase, slope decrease
			// as λ getting very large, A'A no longer matter, then
			// new_y = inv(λI) A'y = 1/λ A'y
			
			Map<Double, Double> map = new TreeMap<Double, Double>();
			
			
			for (double lambda = 0.1; lambda < 20; lambda += 0.1) {
				
				List<Double> sumSqs = new ArrayList<Double>();
				double sum = 0.0;
				int k = 3;
				
				// k-folds cross validation (in this case: omit k number of data for testing)
				for (int fold = 0; fold < 100; fold++) {
					
					Set<Integer> omitted = random(A.numRows, k);
					
					// training with k-data
					DMatrixRMaj C = generate(A, omitted);
					DMatrixRMaj d = generate(b, omitted);
					
					eq.alias(C, "C");
					eq.alias(d, "d");
					
					eq.alias(lambda, "LAMBDA");
					eq.process("LINE = inv(C' * C + LAMBDA * eye(2)) * C' * d");
					
					DMatrixRMaj line = eq.lookupDDRM("LINE");
					
					// validate with the test data
					for (Integer omit : omitted) {
						double y = points[omit].getY();
						double yy = line.get(0, 0) + line.get(1, 0) * points[omit].getX();
						sum += Math.pow(y - yy, 2);
					}
					
					sumSqs.add(sum);
				}
				
				// average all k-folds sum errors squared with one fix lambda
				double avg = StatUtils.mean(sumSqs.stream().mapToDouble(p -> p.doubleValue()).toArray());
		
				Double ll = map.get(avg);
				
				if (ll == null) {
					map.put(avg, lambda);
				}
				else {
					if (ll > lambda)
						map.put(avg, lambda);
				}
			
			}
			
	//		map.entrySet().stream().forEach(p -> System.out.println("SumSqs: " + ff.format(p.getKey()) + ", Lambda: " + ff.format(p.getValue())));
			
			// pick a lambda with the lowest sum error squared
			double lambda = map.entrySet().stream().map(p -> p.getValue()).findFirst().get();
			eq.alias(lambda, "LAMBDA");
			eq.process("LINE = inv(A' * A + LAMBDA * eye(2)) * A' * b");
			System.out.println("LAMBDA: " + ff.format(lambda) + " " + eq.lookupDDRM("LINE"));
		}
		
	}
	
	private static Set<Integer> random(int size, int k) {
		
		int [] arr = new int[size];
		
		for (int i = 0; i < size; i++)
			arr[i] = i;
		
		for (int i = 0; i < size; i++) {
			int from = rand.nextInt(size);
			int to = rand.nextInt(size);
			
			int tmp = arr[to];
			arr[to] = arr[from];
			arr[from] = tmp;
		}
		
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < k; i++)
			set.add(arr[i]);
		
		return set;
	}
	
	private static DMatrixRMaj generate(DMatrixRMaj mat, Set<Integer> omitted) {
		
		DMatrixRMaj A = new DMatrixRMaj(mat.numRows - omitted.size(), mat.numCols);
		
		for (int row = 0, i = 0; row < mat.numRows; row++) {
			
			if (!omitted.contains(row)) {
				
				for (int col = 0; col < mat.numCols; col++) {
					A.set(i, col, mat.get(row, col));
				}
				
				i++;
			}
		}
			
		return A;
		
	}
	
}
