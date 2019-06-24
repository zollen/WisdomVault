package optimization;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunctionLagrangeForm;

public class LagrangePolynomial {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("p(x) = 1.5x - 4");
		// p(x) = 1.5x - 4
		// p(0) = -4.0
		// p(3.0) = 0.5
		lagrangeForm(new double[] { 0.0, 3.0 }, new double[] { -4.0, 0.5 }, 2.0, 4.5, 6.0);
		
		System.out.println();
	
		System.out.println("p(x) = 2x^2 + 5x - 3");
		
		// p(x) = 2x^2 + 5x - 3 = (2x - 1)(x + 3)
		// p(0.0) = -3.0
		// p(-1.0) = -6.0
		// p(0.5) = 0.0
		lagrangeForm(new double[] { 0.0, -1.0, 0.5 }, new double[] { -3.0, -6.0, 0.0 }, 1.0, 2.5, -2.0);
		
		System.out.println();
		
		// p(x) = 6x^4 + 3x^2 + 8x + 5
		double [] x = { 0.0, -1.0, -1.5, 2.0, -3.5 };
		double [] y = new double[5];
	
		for (int i = 0; i < 5; i++) {
			y[i] = func1(x[i]);
		}
		
		lagrangeForm(x, y, 1.0, 3.0, 5.0);
		
	}
	
	private static double func1(double x) {
		return 6 * Math.pow(x, 4) + 3 * Math.pow(x, 2) + 8 * x + 5;
	}
	
	public static void lagrangeForm(double [] x, double [] y, double ... tests) {
		
		// input array x of length n returns polynomial equation with n - 1 order.
		PolynomialFunctionLagrangeForm p = new PolynomialFunctionLagrangeForm(x, y);

		for (int i = 0; i < tests.length; i++)
			System.out.println("p(" + tests[i] + ") = " + p.value(tests[i]));


		System.out.println(p.degree());

		double [] c = p.getCoefficients();
		System.out.println("coeffs: " + Arrays.stream(c).mapToObj(element -> ff.format(element)).collect(Collectors.joining(", ")));
	
	}

}
