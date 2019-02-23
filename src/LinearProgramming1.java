import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

public class LinearProgramming1 {
	
	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");
	
	private static final Vector2D [] POINTS = {
			new Vector2D(1, 34.234064369),
			new Vector2D(2, 68.2681162306),
			new Vector2D(3, 118.6158990846),
			new Vector2D(4, 184.1381972386),
			new Vector2D(5, 266.5998779163),
			new Vector2D(6, 364.1477352516),
			new Vector2D(7, 478.0192260919),
			new Vector2D(8, 608.1409492707),
			new Vector2D(9, 754.5988686671),
			new Vector2D(10, 916.1288180859)
			
	};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// f(x) = a x^2 + b x + c
		QuadraticProblem model = new QuadraticProblem();

		
		final double[] targets = new double[POINTS.length];
		
		for (int i = 0; i < POINTS.length; i++) {
			model.addPoint(POINTS[i]);
			targets[i] = POINTS[i].getY();
		}
		

		LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
		
		LeastSquaresProblem problem = new LeastSquaresBuilder()
	            .start(new double[] {1, 1, 1})
	            .model(model)
	            .target(targets)
	            .lazyEvaluation(false)
	            .maxEvaluations(1000)
	            .maxIterations(1000)
	            .build();

		LeastSquaresOptimizer.Optimum optimum = optimizer.optimize(problem);

		RealVector point = optimum.getPoint();

		System.out.println("Point: [" + ff.format(point.getEntry(0)) + ", " +
										ff.format(point.getEntry(1)) + ", " +
										ff.format(point.getEntry(2)) + "]");
	}

	public static class QuadraticProblem implements MultivariateJacobianFunction {

		private List<Vector2D> points;
		
		public QuadraticProblem() {
			points = new ArrayList<Vector2D>();
		}

		public void addPoint(Vector2D point) {
			this.points.add(point);
		}
		
		@Override
		public Pair<RealVector, RealMatrix> value(RealVector point) {
			// TODO Auto-generated method stub
			double a = point.getEntry(0);
            double b = point.getEntry(1);
            double c = point.getEntry(2);
           
            RealVector value = new ArrayRealVector(points.size());
            RealMatrix jacobian = new Array2DRowRealMatrix(points.size(), 3);
            
            
            for (int i = 0; i < points.size(); i++) {
            	
            	DerivativeStructure res = f(points.get(i).getX(), a, b, c);
            	
            	
            	value.setEntry(i, res.getValue());
            	
            	
            	// df/da = x^2
                jacobian.setEntry(i, 0, res.getPartialDerivative(1, 0, 0));
            	// df/db = x
                jacobian.setEntry(i, 1, res.getPartialDerivative(0, 1, 0));
            	// df/dc = 1
                jacobian.setEntry(i, 2, res.getPartialDerivative(0, 0, 1));
            }
            
            
            return new Pair<RealVector, RealMatrix>(value, jacobian);
		}
		
		public DerivativeStructure f(double _x, double a, double b, double c) {
			
			DerivativeStructure _a = new DerivativeStructure(3, 1, 0, a);
			DerivativeStructure _b = new DerivativeStructure(3, 1, 1, b);
			DerivativeStructure _c = new DerivativeStructure(3, 1, 2, c);
			
			DerivativeStructure _k = _a.multiply(_x).multiply(_x);
			DerivativeStructure _j = _b.multiply(_x);
			
			
			return _k.add(_j).add(_c);
		}

	}

}
