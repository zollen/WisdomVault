import java.text.DecimalFormat;
import java.util.Arrays;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
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
	
	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");
	private static final RealMatrixFormat f1 = new RealMatrixFormat("", "", "[ ", " ]", "\n", ", ", ff);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// 2x^2 - 3x + 4y^2 + 2y + 3z^2 - z = 3
		// with known equation, find x, y, z
		
		final double expectedResult = 3;
		
		final Vector3D [] points = {
				
				new Vector3D(1.01, 0.01, 1.33),
				new Vector3D(1.7, 0.42, 0.71),
				new Vector3D(0.48, 0.81, 0),
				new Vector3D(-0.42, 0.2, 0.72),
				new Vector3D(0.67,-1.21, 0.67),
				new Vector3D(1.25, -1.05, -0.51),
				new Vector3D(0.21, -0.42, -0.95),
				new Vector3D(0.77, 0.56, -0.61),
				new Vector3D(1.6, -0.21, -0.83)
		};

		MultivariateJacobianFunction knownEquationApprox = new MultivariateJacobianFunction() {
			public Pair<RealVector, RealMatrix> value(final RealVector point) {
				
				Vector3D test = new Vector3D(point.getEntry(0), point.getEntry(1), point.getEntry(2));

				RealVector value = new ArrayRealVector(points.length);
				RealMatrix jacobian = new Array2DRowRealMatrix(points.length, 3);
						
				for (int i = 0; i < points.length; i++) {
					
			//		Vector3D experiment = points[i];
					
					DerivativeStructure eq = f(test.getX(), test.getY(), test.getZ());
					
					value.setEntry(i, eq.getValue());
					
					// df/dx = 4x - 3
					jacobian.setEntry(i, 0, eq.getPartialDerivative(1, 0, 0));
					// df/dy = 8y + 2
					jacobian.setEntry(i, 1, eq.getPartialDerivative(0, 1, 0));
					// df/dz = 6z - 1
					jacobian.setEntry(i, 2, eq.getPartialDerivative(0, 0, 1));
				}

				return new Pair<RealVector, RealMatrix>(value, jacobian);

			}
		};

		double[] predictedResult = new double[points.length];
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

		Vector3D fitted = new Vector3D(optimum.getPoint().getEntry(0), 
											optimum.getPoint().getEntry(1),
											optimum.getPoint().getEntry(2));
		System.out.println("fitted point: [" + ff.format(fitted.getX()) + ", " + 
												ff.format(fitted.getY()) + ", " + 
												ff.format(fitted.getZ()) + "]");
		System.out.println("RMS: " + ff.format(optimum.getRMS()));
		System.out.println("evaluations: " + optimum.getEvaluations());
		System.out.println("iterations: " + optimum.getIterations());
		System.out.println("Cost: " + ff.format(optimum.getCost()));
		System.out.println("Jacobian:\n" + f1.format(optimum.getJacobian()));

	}
	
	private static DerivativeStructure f(double x, double y, double z) {
		// 2x^2 - 3x + 4y^2 + 2y + 3z^2 -z 
		DerivativeStructure xVar = new DerivativeStructure(3, 1, 0, x); // latitude is 0-th variable of 2 for derivatives up to 1
		DerivativeStructure yVar = new DerivativeStructure(3, 1, 1, y); // longitude is 1-st variable of 2 for derivatives up to 1
		DerivativeStructure zVar = new DerivativeStructure(3, 1, 2, z); // longitude is 2-st variable of 2 for derivatives up to 1
		DerivativeStructure c1 = xVar.pow(2);
		DerivativeStructure c2 = yVar.pow(2);
		DerivativeStructure c3 = zVar.pow(2);
		
		DerivativeStructure c4 = new DerivativeStructure(2, c1, 4, c2, 3, c3);
		DerivativeStructure c5 = new DerivativeStructure(-3, xVar, 2, yVar, -1, zVar);
		
		return new DerivativeStructure(1, c4, 1, c5);
	}
}
