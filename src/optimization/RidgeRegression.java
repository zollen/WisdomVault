package optimization;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.stat.regression.RegressionResults;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.linsol.LinearSolverDense;

public class RidgeRegression {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	public static final Vector2D [] points = {
							new Vector2D(1.0, 1.0),
							new Vector2D(2.0, 3.0),
							new Vector2D(4.0, 2.0),
							new Vector2D(5.0, 3.0),
							new Vector2D(6.0, 6.0),
							new Vector2D(8.0, 9.0)
							};
	
	public static final double [] LAMBDAS = {
							0.2, 0.4, 0.5, 0.7, 1.0, 1.2, 5.0, 10.0
							};

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		// Ridge Regression introduces penalty to regression for preventing small training 
		// data from overfitting. For example with two training data points - a straight line 
		// would have zero squared sum residuals, zero bias but large variance when test with
		// other 'unseen' testing data points. Ridge regression ensure the straight line does
		// not matched the two training points, introduce small bias in exchange of significant
		// reduction of variances from testing with new data points.
		
		{
			// If all training data points are available. Easy - least square approximate
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
							" 1, 8 " +
							"]");
			
			eq.process("b = [ 1; 3; 2; 3; 6; 9 ]"); 
			
			DMatrixRMaj A = eq.lookupDDRM("A");
			DMatrixRMaj x = new DMatrixRMaj(A.numCols, 1);
	        DMatrixRMaj y = eq.lookupDDRM("b");
			
			// x = [ C; D ]
			// Ax = b is not solvable!!!!!!!!!
	        
	        LinearSolverDense<DMatrixRMaj> x2 =  LinearSolverFactory_DDRM.leastSquares(A.numRows, A.numCols);
			x2.setA(A);
			
			CommonOps_DDRM.fill(x, 0);
			x2.solve(y, x);
			
			System.out.println(x);
			
			eq.alias(x, "x");
			
		}
		
		
		{
		
			SimpleRegression regression = new SimpleRegression();
		
			regression.addData(points[0].getX(), points[0].getY());
			regression.addData(points[1].getX(), points[1].getY());
			regression.addData(points[2].getX(), points[2].getY());
			regression.addData(points[3].getX(), points[3].getY());
			regression.addData(points[4].getX(), points[4].getY());
			regression.addData(points[5].getX(), points[5].getY());
			
			RegressionResults result = regression.regress();
			
			System.out.println("Sum of Squared Errors: " + result.getErrorSumSquares());
			System.out.println("R^2: " + result.getRSquared());
			
			double [] pts = result.getParameterEstimates();
		
			System.out.println(Arrays.stream(pts).mapToObj( p -> ff.format(p) ).collect(
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
			// 0 + 1 * 2^2 = 4 (let's assume lambda is 1)
			// using the above least squared answer (-0.420, 1.020)
			// y = -0.42 + 1.02x
			// -0.42 + 1.02(1) = 0.6     (1.0, 0.6)
			// -0.42 + 1.02(2) = 1.62    (2.0, 1.62)
			// (1.0 - 0.6)^2 + (3 - 1.62)^2 + 1 * 1.02^2 = 2.14 
			// 2.14 is obviously smaller bias than 4 (perfect fitted)
			
			// lambda = 10-folds cross validation 
			
			// Ridge Regression helps reduce variance by shrinking the parameters and make
			// our prediction less sensitive to them. In general, the ridge regression
			// penalty includes all of the parameters *EXCEPT* the y-intercept (b).
			
			// performing k-folds cross validation to estimate the lambda
			// double lambda = cv(points);
			
			double lambda = cv(points);
		
		}
		
	}
	
	public static double cv(Vector2D [] points) throws Exception {
		// working on it....
		
		for (Vector2D point1 : points) {
			
			SimpleRegression regression = new SimpleRegression();
			
			for (Vector2D point2 : points) {
				
				if (point1 != point2) {					
					regression.addData(point2.getX(), point2.getY());
				}
			}
			
			
			RegressionResults result = regression.regress(); 
			double [] pts = result.getParameterEstimates();
				
			double yIntercept = pts[0];
			double slope = pts[1];
			double yy = yIntercept + slope * point1.getX();
			
			AtomicInteger index = new AtomicInteger();
			List<Double> values = new ArrayList<Double>();
			Arrays.stream(LAMBDAS).forEach(p -> {				
				values.add(Math.pow(yy - point1.getY(), 2) + LAMBDAS[index.getAndIncrement()] * Math.pow(slope, 2));
			});
			
			String vals = values.stream().map(p -> ff.format(p)).collect(Collectors.joining(", "));
			
			System.out.println("y: " + ff.format(yIntercept) + ", slope: " + ff.format(slope) + "  ridge regression: " + vals);
		}
		
		return 0.0;
	}

}
