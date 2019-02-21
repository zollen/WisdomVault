import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

public class LinearProgramming4 {
	
	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// latitude/longitude/age in milliseconds/signal strength
	    final PositionInfo[] data = new PositionInfo[]{
	            new PositionInfo(43.48932915, 1.66561772, 1000, -20),
	            new PositionInfo(43.48849093, 1.6648176, 2000, -10),
	            new PositionInfo(43.48818612, 1.66615113, 3000, -50)
	    };


	    double[] target = new double[data.length];
	    Arrays.fill(target, 0.0);

	    double[] start = new double[2];

	    for (PositionInfo row : data) {
	        start[0] += row.latitude;
	        start[1] += row.longitude;
	    }
	    start[0] /= data.length;
	    start[1] /= data.length;

	    MultivariateJacobianFunction distancesModel = new MultivariateJacobianFunction() {
	        @Override
	        public Pair<RealVector, RealMatrix> value(final RealVector point) {
	            double tgtLat = point.getEntry(0);
	            double tgtLong = point.getEntry(1);

	            RealVector value = new ArrayRealVector(data.length);
	            RealMatrix jacobian = new Array2DRowRealMatrix(data.length, 2);
	            for (int i = 0; i < data.length; i++) {
	                DerivativeStructure distance = f(tgtLat, tgtLong, data[i]);
	               
	                value.setEntry(i, distance.getValue());
	                jacobian.setEntry(i, 0, distance.getPartialDerivative(1, 0));
	                jacobian.setEntry(i, 1, distance.getPartialDerivative(0, 1));
	            }

	            return new Pair<RealVector, RealMatrix>(value, jacobian);
	        }
	    };


	    LeastSquaresProblem problem = new LeastSquaresBuilder()
	            .start(start)
	            .model(distancesModel)
	            .target(target)
	            .lazyEvaluation(false)
	            .maxEvaluations(1000)
	            .maxIterations(1000)
	            .build();

	    LeastSquaresOptimizer optimizer = new LevenbergMarquardtOptimizer().
	            withCostRelativeTolerance(1.0e-12).
	            withParameterRelativeTolerance(1.0e-12);

	    LeastSquaresOptimizer.Optimum optimum = optimizer.optimize(problem);
	    RealVector point = optimum.getPoint();
	    System.out.println("Start = [" + Arrays.stream(start).mapToObj(p -> ff.format(p)).collect(Collectors.joining(", ")) + "]");
	    System.out.println("Solve = " + point);
		
		
	}
	
	public static class PositionInfo {
	    public final double latitude;
	    public final double longitude;
	    public final int ageMs;
	    public final int strength;

	    public PositionInfo(double latitude, double longitude, int ageMs, int strength) {
	        this.latitude = latitude;
	        this.longitude = longitude;
	        this.ageMs = ageMs;
	        this.strength = strength;
	    }

	    public double getWeight() {
	        return Math.min(1.0, Math.sqrt(2000.0 / ageMs)) / (strength * strength);
	    }
	}
	
	public static DerivativeStructure f(double tgtLat, double tgtLong, PositionInfo knownPos) {
	    DerivativeStructure varLat = new DerivativeStructure(2, 1, 0, tgtLat); // latitude is 0-th variable of 2 for derivatives up to 1
	    DerivativeStructure varLong = new DerivativeStructure(2, 1, 1, tgtLong); // longitude is 1-st variable of 2 for derivatives up to 1
	    DerivativeStructure latDif = varLat.subtract(knownPos.latitude);
	    DerivativeStructure longDif = varLong.subtract(knownPos.longitude);
	    DerivativeStructure latDif2 = latDif.pow(2);
	    DerivativeStructure longDif2 = longDif.pow(2);
	    DerivativeStructure dist2 = latDif2.add(longDif2);
	    DerivativeStructure dist = dist2.sqrt();
	    return dist.multiply(knownPos.getWeight());
	}
}
