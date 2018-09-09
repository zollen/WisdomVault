import java.text.DecimalFormat;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

public class IntegralExercise {
	
	private static final DecimalFormat formatter = new DecimalFormat("0.00000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpsonIntegrator simpson = new SimpsonIntegrator();
	    TrapezoidIntegrator trapezoid = new TrapezoidIntegrator();
	    RombergIntegrator romberg = new RombergIntegrator();
	    double[] vector = new double[2];
	    vector[0] = 0;
	    vector[1] = 1;
	   

	    PolynomialFunction f = new PolynomialFunction(vector);
	    UnivariateFunction uf = (UnivariateFunction)new PolynomialFunction(vector);
	    System.out.println("To String " + uf.toString());
	    System.out.println("Degree: " + f.degree());

	    double i = simpson.integrate(10, uf, 0, 1);
	    double j = trapezoid.integrate(10, uf, 0, 1);
	    double k = romberg.integrate(10, uf, 0, 1);
	    System.out.println("Simpson integral : " + formatter.format(i));        
	    System.out.println("Trapezoid integral : " + formatter.format(j));  
	    System.out.println("Romberg integral : " + formatter.format(k));
	    
	    UnivariateFunction function = x -> Math.cos(x) * Math.cos(x);
	    UnivariateIntegrator integrator = new SimpsonIntegrator();
	    double l = integrator.integrate(1000, function, -Math.PI, Math.PI);
	      
	    System.out.println("Simpson integral: " + formatter.format(l));
	    
	    final SimpsonIntegrator si = new SimpsonIntegrator();
	    final double result = si.integrate(50, x -> 2*x, 0, 10);
	    System.out.println(formatter.format(result) + " should be 100");
	
	}

}
