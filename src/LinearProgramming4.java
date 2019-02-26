import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

public class LinearProgramming4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
        // The following polynomial function is for verify the interpolated result.
		final double[] coeff = { 3, 5, 4, -3, 2 }; // 3 + 5 x + 4 x^2 - 3 x^3 + 2 x^4
        final PolynomialFunction P = new PolynomialFunction(coeff); // or we have a collection of observed points

        double [] x = new double[100];
        double [] y = new double[100];
        for (int i = 0; i < 100; i++) {
            x[i] = i;
            y[i] = P.value(x[i]);  // It could be replaced with any observed (x, y)
        }
        
        SplineInterpolator interpolator = new SplineInterpolator();
        
        UnivariateFunction  f = interpolator.interpolate(x, y);
        
        System.out.println("P(5) = 3 + 5(5) + 4(5)^2 - 3(5)^3 + 2(5)^4 = 1003");
        System.out.println("f(5): " + f.value(5.5));
        
        System.out.println("\nLet's predict an unknown point between measurements");
        // Result between points x = 5 and x = 6
        System.out.println("P(5.5) = 3 + 5(5.5) + 4(5.5)^2 - 3(5.5)^3 + 2(5.5)^4 = 1482.5");
        System.out.println("f(5.5): " + f.value(5.5));
 
	}

}
