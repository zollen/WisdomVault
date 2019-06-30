package optimization;

import java.text.DecimalFormat;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.linsol.LinearSolverDense;

public class GradientDecent {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// y = mx + b
		
		// Points: (1.0, 1.0), (3.0, 1.0), (2.0, 3.0), (4.0, 2.0), (3.5, 3.5)
		
		{
			// Gradient Decent
			final double LEARNING_RATE = 0.01;
			final double THRESHOLD = 0.00001;
			
			double intercept = 0.1;
			double slope = 0.1;
			
			for (int iter = 1; iter <= 1000; iter++) {
				
				// Stochastic Gradient Descent
				// ===========================
				// Imagine a very large data of 1,000,000 samples, it would take too much
				// calculation for each iteration.
				// Instead of using all samples of x, SGD randomly picks a single sample of
				// x or a subset of sample of x for each iteration

				
				DerivativeStructure f = formula(intercept, slope, new double[] {1.0, 3.0, 2.0, 4.0, 3.5}, new double[] {1.0, 1.0, 3.0, 2.0, 3.5});
					
				double pI = f.getPartialDerivative(1, 0);
				double pS = f.getPartialDerivative(0, 1);
				
				System.out.println("Iteration: " + iter + 
						" intercept: " + ff.format(intercept) +
						" slope: " + ff.format(slope) +
						" f: " + ff.format(f.getValue()) +  
						" ∂L/∂i: " + ff.format(pI) +
						" ∂L/∂s: " + ff.format(pS));
				
			
				intercept -= pI * LEARNING_RATE;
				slope -= pS * LEARNING_RATE;		
				
				if (Math.abs(pI) < THRESHOLD && Math.abs(pS) < THRESHOLD)
					break;
			}
		}
		
		{
			// Least square regression for verification
			
			// (1, 1), (3, 1), (2, 3), (4, 2), (3.5, 3.5)
			// C + Dx = y
			// ==========
			// C + 1D = 1
			// C + 3D = 1
			// C + 2D = 3
			// C + 4D = 2
			// C + 3.5D = 3.5
			
			Equation eq = new Equation();
			eq.process("A = [" +
							" 1, 1;" +
							" 1, 3;" +
							" 1, 2;" +
							" 1, 4;" +
							" 1, 3.5 " +
						"]");
			
			eq.process("b = [ 1.0; 1.0; 3.0; 2.0; 3.5 ]"); 
			
			DMatrixRMaj A = eq.lookupDDRM("A");
			DMatrixRMaj x = new DMatrixRMaj(A.numCols, 1);
	        DMatrixRMaj y = eq.lookupDDRM("b");
	        
	        LinearSolverDense<DMatrixRMaj> x2 =  LinearSolverFactory_DDRM.leastSquares(A.numRows, A.numCols);
			x2.setA(A);
			
			CommonOps_DDRM.fill(x, 0);
			x2.solve(y, x);
			
			System.out.println("===== Least Square Verification =====");
			System.out.println("Intercept: " + ff.format(x.get(0, 0)));
			System.out.println("Slope: " + ff.format(x.get(1, 0)));
		}
		
	}
	
	
	private static DerivativeStructure formula(double intercept, double slope, double [] x, double [] y) {
		// L = (1.0 - (intercept + slope * 1.0))^2 +
		//		(1.0 - (intercept + slope * 3.0))^2 +
		//		(3.0 - (intercept + slope * 2.0))^2 +
		//		(2.0 - (intercept + slope * 4.0))^2 +
		//		(3.5 - (intercept + slope * 3.5))^2
		
		DerivativeStructure i = new DerivativeStructure(2, 2, 0, intercept);
		DerivativeStructure s = new DerivativeStructure(2, 2, 1, slope);
		
		DerivativeStructure t1 = i.add(s.multiply(x[0])).multiply(-1).add(y[0]).pow(2);
		DerivativeStructure t2 = i.add(s.multiply(x[1])).multiply(-1).add(y[1]).pow(2);
		DerivativeStructure t3 = i.add(s.multiply(x[2])).multiply(-1).add(y[2]).pow(2);
		DerivativeStructure t4 = i.add(s.multiply(x[3])).multiply(-1).add(y[3]).pow(2);
		DerivativeStructure t5 = i.add(s.multiply(x[4])).multiply(-1).add(y[4]).pow(2);
		
		return t1.add(t2).add(t3).add(t4).add(t5);
	}
	
	
}
