package optimization;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

public class LinearProgramming2 {
	
	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");
	
	// secret formula: 2 x^2 + 3 x + 2 y^2 + 4 y + 2 z^2 - z = 3
	// points that touch the surface of the sphere
	private static final Vector3D [] POINTS = {
			new Vector3D(-0.3, -1.77, 1.78),
			new Vector3D(-1.77, -1.93, 1.35),
			new Vector3D(0.75, -1.57, 0.99),
			new Vector3D(-2.5, -0.8, 0.4),
			new Vector3D(0.09, 0.53, 0.54),
			new Vector3D(-0.66, 0, 1.71),
			new Vector3D(0.53, -1, -0.97),
			new Vector3D(-0.89, -2.28, -0.96),
			new Vector3D(-0.99, -0.03, -1.21),
			new Vector3D(-1.81, -1.59, -1.03),
			new Vector3D(0.1, -2.51, 0.61)		
	};

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// f(x) = a x^2  + b x + c y^2 + d y + e z^2 + f z
		// let test the following model
		PolynominalModel model = new PolynominalModel();

		
		final double[] targets = new double[POINTS.length];
		
		for (int i = 0; i < POINTS.length; i++) {
			model.addPoint(POINTS[i]);
			targets[i] = 3;
		}
		

		LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();
		
		LeastSquaresProblem problem = new LeastSquaresBuilder()
	            .start(new double[] {1, 1, 1, 1, 1, 1 })
	            .model(model)
	            .target(targets)
	            .lazyEvaluation(false)
	            .maxEvaluations(1000)
	            .maxIterations(1000)
	            .build();

		LeastSquaresOptimizer.Optimum optimum = optimizer.optimize(problem);

		RealVector point = optimum.getPoint();

		System.out.println("Cofficients: [" + ff.format(point.getEntry(0)) + ", " +
										ff.format(point.getEntry(1)) + ", " +
										ff.format(point.getEntry(2)) + ", " +
										ff.format(point.getEntry(3)) + ", " +
										ff.format(point.getEntry(4)) + ", " +
										ff.format(point.getEntry(5)) + "]");
	}

	public static class PolynominalModel implements MultivariateJacobianFunction {

		private List<Vector3D> points;
		
		public PolynominalModel() {
			points = new ArrayList<Vector3D>();
		}

		public void addPoint(Vector3D point) {
			this.points.add(point);
		}
		
		@Override
		public Pair<RealVector, RealMatrix> value(RealVector point) {
			// TODO Auto-generated method stub
			double a = point.getEntry(0);
            double b = point.getEntry(1);
            double c = point.getEntry(2);
            double d = point.getEntry(3);
            double e = point.getEntry(4);
            double f = point.getEntry(5);
         
           
            RealVector value = new ArrayRealVector(points.size());
            RealMatrix jacobian = new Array2DRowRealMatrix(points.size(), 6);
            
            
            for (int i = 0; i < points.size(); i++) {
            	
            	DerivativeStructure res = f(points.get(i), a, b, c, d, e, f);
            	
            	
            	value.setEntry(i, res.getValue());
            	
            	
            	// df/da = x^2
                jacobian.setEntry(i, 0, res.getPartialDerivative(1, 0, 0, 0, 0, 0));
            	// df/db = x
                jacobian.setEntry(i, 1, res.getPartialDerivative(0, 1, 0, 0, 0, 0));
            	// df/dc = y^2
                jacobian.setEntry(i, 2, res.getPartialDerivative(0, 0, 1, 0, 0, 0));
                // df/dd = y
                jacobian.setEntry(i, 3, res.getPartialDerivative(0, 0, 0, 1, 0, 0));
                // df/de = z^2
                jacobian.setEntry(i, 4, res.getPartialDerivative(0, 0, 0, 0, 1, 0));
                // df/df = z
                jacobian.setEntry(i, 5, res.getPartialDerivative(0, 0, 0, 0, 0, 1));     
            }
            
            
            return new Pair<RealVector, RealMatrix>(value, jacobian);
		}
		
		public DerivativeStructure f(Vector3D pt, double a, double b, double c,
					double d, double e, double f) {
			
			DerivativeStructure _a = new DerivativeStructure(6, 1, 0, a);
			DerivativeStructure _b = new DerivativeStructure(6, 1, 1, b);
			DerivativeStructure _c = new DerivativeStructure(6, 1, 2, c);
			DerivativeStructure _d = new DerivativeStructure(6, 1, 3, d);
			DerivativeStructure _e = new DerivativeStructure(6, 1, 4, e);
			DerivativeStructure _f = new DerivativeStructure(6, 1, 5, f);
			
			DerivativeStructure _k = _a.multiply(pt.getX()).multiply(pt.getX());
			DerivativeStructure _j = _b.multiply(pt.getX());
			DerivativeStructure _l = _c.multiply(pt.getY()).multiply(pt.getY());
			DerivativeStructure _m = _d.multiply(pt.getY());
			DerivativeStructure _o = _e.multiply(pt.getZ()).multiply(pt.getZ());
			DerivativeStructure _p = _f.multiply(pt.getZ());
			
			
			return _k.add(_j).add(_l).add(_m).add(_o).add(_p);
		}

	}

}
