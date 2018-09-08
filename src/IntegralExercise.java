import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;

public class IntegralExercise {

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
	    System.out.println("Simpson integral : " + i);        
	    System.out.println("Trapezoid integral : " + j);  
	    System.out.println("Romberg integral : " + k);
	}

}
