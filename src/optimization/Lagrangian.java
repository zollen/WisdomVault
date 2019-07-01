package optimization;

import java.text.DecimalFormat;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

public class Lagrangian {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	/**
	 *  f(a, b) = a^2 + a * b + 3
	 *	c(x) = x^2 + b^2 = 1
     *
	 *	L(a, b, l) = a^2 + a * b + 3 + l * (a^2 + b^2 - 1)
     *
	 *	f(a, b) = a^2 + a * b + 3
	 *	c(x) = x^2 + b^2 <= 2
     *
	 *	L(a, b, l) = a^2 + a * b + 3 + l * (a^2 + b^2 -2)
     *
	 *	With Constraints of inequalities <= or >=
	 *  -----------------------------------------
	 *	We must perform another set of optimizations; systematically swap out each inequality 
	 *  constraint and see if the result is highest/lowest and still satisfies all constraints. 
	 */

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Using Lagrangian function and gradient decent for solving 
		// quadratic programming problems with constraints
		final double LEARNING_RATE = 0.01;
		final double THRESHOLD = 0.0001;
		
		double a = 1;
		double b = 1;
		double c = 1;
		double lambda = 0.5;
		
		for (int iter = 1; iter <= 100000; iter++) {
			
			DerivativeStructure f = formula(a, b, c);
			
	//		f = constraint1(f, lambda, a, b, c);  // a + b + c = 3.0
	//		f = constraint2(f, lambda, a, b, c);  // a^2 + b^2 <= 3
			
			double gradA = f.getPartialDerivative(1, 0, 0, 0);
			double gradB = f.getPartialDerivative(0, 1, 0, 0);
			double gradC = f.getPartialDerivative(0, 0, 1, 0);
			double gradL = f.getPartialDerivative(0, 0, 0, 1);
		
			System.out.println("Iteration: " + iter + 
					" L(" + ff.format(a) + ", " +
					ff.format(b) + ", " +
					ff.format(c) + ", " +
					ff.format(lambda) +
					"): " + ff.format(f.getValue()) +  
					" ∂L/∂λ: " + ff.format(gradL) +
					" ∂L/∂a: " + ff.format(gradA) +
					" ∂L/∂b: " + ff.format(gradB) +
					" ∂L/∂c: " + ff.format(gradC));
			
			lambda -= gradL * LEARNING_RATE;
			a -= gradA * LEARNING_RATE;
			b -= gradB * LEARNING_RATE;
			c -= gradC * LEARNING_RATE;
			
			if (Math.abs(gradL) < THRESHOLD &&
					Math.abs(gradA) < THRESHOLD && 
					Math.abs(gradB) < THRESHOLD && 
					Math.abs(gradC) < THRESHOLD)
				break;
		}
	}
	
	private static DerivativeStructure constraint1(DerivativeStructure f, double lamda, double a, double b, double c) {
		// a + b + c = 4.5
		DerivativeStructure aa = new DerivativeStructure(4, 1, 0, a);
		DerivativeStructure bb = new DerivativeStructure(4, 1, 1, b);
		DerivativeStructure cc = new DerivativeStructure(4, 1, 2, c);
		DerivativeStructure ll = new DerivativeStructure(4, 1, 3, lamda);
		
		return f.add(ll.multiply(aa.add(bb).add(cc).subtract(3.0)));
	}
	
	private static DerivativeStructure constraint2(DerivativeStructure f, double lamda, double a, double b, double c) {
		return f;
	}
	
	private static DerivativeStructure formula(double a, double b, double c) {
		// f(a, b, c) = 3 * cos(a)^4 + 4 * cos(b)^3 + 2 sin(c)^2 * cos(c)^2 + 5	
		DerivativeStructure aa = new DerivativeStructure(4, 1, 0, a);
		DerivativeStructure bb = new DerivativeStructure(4, 1, 1, b);
		DerivativeStructure cc = new DerivativeStructure(4, 1, 2, c);

		return new DerivativeStructure(
						3.0, aa.cos().pow(4.0), 
						4.0, bb.cos().pow(3.0), 
						2.0, cc.sin().pow(2.0).multiply(cc.cos().pow(2.0)))
							.add(5.0);
		
	}

}
