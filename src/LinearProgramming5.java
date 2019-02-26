import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class LinearProgramming5 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
        // The following polynomial function is for verify the interpolated result.
		final double[] coeff = { 3, 5, 4 }; // 3 + 5 x + 4 x^2
        final PolynomialFunction P = new PolynomialFunction(coeff); // or we have a collection of observed points

        double [] x = new double[10];
        double [] y = new double[10];
        for (int i = 0; i < 10; i++) {
            x[i] = i;
            y[i] = P.value(x[i]);  // It could be replaced with any observed (x, y)
        }
        
        SplineInterpolator interpolator = new SplineInterpolator();
        
        PolynomialSplineFunction  f = interpolator.interpolate(x, y);
        
        PolynomialFunction polynomials[] = f.getPolynomials();
        
       for (int i = 0; i < polynomials.length; i++) {
    	   System.out.println(i + " => " + (i + 1) + " : " + polynomials[i] + " = " + polynomials[i].value(5));  
       }
 
	}

}
