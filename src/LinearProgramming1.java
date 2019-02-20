import java.text.DecimalFormat;
import java.util.Arrays;

import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixFormat;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

public class LinearProgramming1 {
	
	private static final int MAX_TRY = 5;
	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");
	private static final RealMatrixFormat f1 = new RealMatrixFormat("", "", "[ ", " ]", "\n", ", ", ff);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// 2x^2 - 3x + 4y^2 + 2y + 3z^2 -z = 3
		// with known equation, find x, y, z
		
		final double expectedResult = 3;

		MultivariateJacobianFunction knownEquationApprox = new MultivariateJacobianFunction() {
			public Pair<RealVector, RealMatrix> value(final RealVector point) {
				
				Vector3D test = new Vector3D(point.getEntry(0), point.getEntry(1), point.getEntry(2));

				RealVector value = new ArrayRealVector(MAX_TRY);
				RealMatrix jacobian = new Array2DRowRealMatrix(MAX_TRY, 3);
					
				double testResult = f(test.getX(), test.getY(), test.getZ());
					
				for (int i = 0; i < MAX_TRY; i++) {
					
					value.setEntry(i, testResult);
					
					// df/dx = 4x - 3
					jacobian.setEntry(i, 0, 4 * test.getX() - 3);
					// df/dy = 8y + 2
					jacobian.setEntry(i, 1, 8 * test.getY() + 2);
					// df/dz = 6z - 1
					jacobian.setEntry(i, 2, 6 * test.getZ() - 1);
				}

				return new Pair<RealVector, RealMatrix>(value, jacobian);

			}
		};

		double[] predictedResult = new double[MAX_TRY];
		Arrays.fill(predictedResult, expectedResult);

		LeastSquaresProblem problem1 = new LeastSquaresBuilder()
				.start(new double[] { 0, 0, 0 })
				.model(knownEquationApprox)
				.target(predictedResult)
				.lazyEvaluation(false)
				.maxEvaluations(1000)
				.maxIterations(1000).build();

		LeastSquaresOptimizer optimizer = new LevenbergMarquardtOptimizer().
									withCostRelativeTolerance(1.0e-12).
                                    withParameterRelativeTolerance(1.0e-12);
	//	LeastSquaresOptimizer optimizer = new GaussNewtonOptimizer().
	//				withDecomposition(GaussNewtonOptimizer.Decomposition.QR);
		
		LeastSquaresOptimizer.Optimum optimum = optimizer.optimize(problem1);

		Vector3D fittedballoon = new Vector3D(optimum.getPoint().getEntry(0), 
											optimum.getPoint().getEntry(1),
											optimum.getPoint().getEntry(2));
		System.out.println("fitted balloon: [" + ff.format(fittedballoon.getX()) + ", " + 
												ff.format(fittedballoon.getY()) + ", " + 
												ff.format(fittedballoon.getZ()) + "]");
		System.out.println("RMS: " + optimum.getRMS());
		System.out.println("evaluations: " + optimum.getEvaluations());
		System.out.println("iterations: " + optimum.getIterations());
		System.out.println("Cost: " + optimum.getCost());
		System.out.println("Jacobian:\n" + f1.format(optimum.getJacobian()));

	}
	
	private static double f(double x, double y, double z) {
		// 2x^2 - 3x + 4y^2 + 2y + 3z^2 -z 
		return 2 * (x * x) - 3 * x + 4 * y * y + 2 * y + 3 * z * z - z;
	}
}
