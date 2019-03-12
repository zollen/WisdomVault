package optimization;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class LinearProgramming3 {

	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// unkown high degree polynomial with single unknown: x
		// 3 + 5 x + 4 x^2 - 3 x^3 + 2 x^4
		
		// we have a collection of random samples (i.e. points)
		// we want to guess the coefficient of the polynomial equation
		
		final RealDistribution rng = new UniformRealDistribution(-100, 100);
        rng.reseedRandomGenerator(System.nanoTime());
		
		
		final double[] coeff = { 3, 5, 4, -3, 2 }; // 3 + 5 x + 4 x^2 - 3 x^3 + 2 x^4
        final PolynomialFunction f = new PolynomialFunction(coeff); // or we have a collection of observed points

        // Collect data from a known polynomial.
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        for (int i = 0; i < 100; i++) {
            final double x = rng.sample();
            obs.add(x, f.value(x));  // It could be replaced with observed (x, y)
        }

        // Start fit from initial guesses that are far from the optimal values.
        final PolynomialCurveFitter fitter
            = PolynomialCurveFitter
            		.create(4)
            		.withStartPoint(new double[] { 1, 5, 10, 100, 500  });
        
        // other one variable CurveFitter:
        // 1. SimpleCurveFitter
        // 2. HarmonicCurveFitter
        // 3. GaussianCurveFitter (for curves of normal distribution)
        
        double[] best = fitter.fit(obs.toList());
		
       
        System.out.println(Arrays.stream(coeff).mapToObj(p -> ff.format(p)).collect(Collectors.joining(", ")));
 
        System.out.println("Calculating the best fit should be equal to coeff!!");
        
        System.out.println(Arrays.stream(best).mapToObj(p -> ff.format(p)).collect(Collectors.joining(", ")));

	}

}
