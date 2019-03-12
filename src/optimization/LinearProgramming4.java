package optimization;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

import linearalgebra.RrefGaussJordanRowPivot;

public class LinearProgramming4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Unlike Linear Regression where y are approximated.
		// Splines are common known as cubic splines go through all exact y points and 
		// smooth out the curve between points. With n points, there are n - 1 splines.
		
        // Using cubic splines to go through all points) and interpolate *between*
		// x = 1 and x = 2 (without any actual measurements)
		// we have three points (1, 2), (2, 3) and (3, 5)
		
		// General cubic spline formula: a0 + b0(x - 1) + c0(x - 1)^2 + d0(x - 1)^3
		// we have two splines 
		// 1. between (1, 2) and (2, 3)
		// 2. between (2, 3) and (3, 5)
		
		// (1, 2) : S0(x) = a0 + b0(x - 1) + c0(x - 1)^2 + d0(x - 1)^3 
		// (2, 3) : S0(x) = a0 + b0(x - 1) + c0(x - 1)^2 + d0(x - 1)^3 
		// (2, 3) : S1(x) = a1 + b1(x - 2) + c1(x - 2)^2 + d1(x - 2)^3 
		// (3, 5) : S1(x) = a1 + b1(x - 2) + c1(x - 2)^2 + d1(x - 2)^3
		
		// There are 8 unknowns: a0, b0, c0, d0, a1, b1, c1, d1
		
		// Si(xi)   = Si+1(xi)
		// Si'(xi)  = Si+1'(xi)
		// Si''(xi) = Si+1''(xi)
		
		// According to the measurements
		// S0(1) = 2
		// S0(2) = 3
		// S1(2) = 3
		// S1(3) = 5
		
		// (1, 2) : S0(1) = a0 + b0(0) + c0(0) + d0(0) = 2
		// Therefore a0 = 2                                  <--- a0 solved!
		// (2, 3) : S0(2) = 2 + b0(2 - 1) + c0(2 - 1) + d0(2 - 1) = 3
		//		  : S0(2) = 2 + 2b0 - b0 + 2c0 - c0 + 2d0 - d0 = 3
		//		  : S0(2) = 2 + b0 +c0 + d0 = 3
		//		  : S0(2) = b0 + c0 + d0 = 1                 <--- Equation #1
		// (2, 3) : S1(2) = a1 + b1(0) + c1(0) + d1(0) = 3
		// Therefore a1 = 3                                  <--- a1 solved!
		// (3, 5) : S1(5) = 3 + b1(3 - 2) + c1(3 - 2) + d1(3 - 2) = 5
		//        : S1(5) = 3 + b1 + c1 + d1 = 5            
		//	 	  : S1(5) = b1 + c1 + d1 = 2                 <--- Equation #2
		
		// First Derivative of both S0 and S1
		// S0'(x) = b0 + 2c0(x - 1)  + 3d0(x - 1)^2
		// S1'(x) = b1 + 2c1(x - 2)  + 3d1(x - 2)^2
		// S0'(x) = S1'(x) 
		
		// S0'(2) = b0 + 2c0 + 3d0
		// S1'(2) = b1 + 2c1(0) + 3d1(0) = b1
		// Therefore S0'(2) = S1'(2) ==> b0 + 2c0 + 3d0 = b1  <--- Equation #3
		
		// Second Derivative of both S0 and S1
		// S0''(x) = 2c0 + 6d0(x - 1)
		// S1''(x) = 2c1 + 6d1(x - 2)
		// S0''(2) = 2c0 + 6d0
		// S1''(2) = 2c1 + 6d1(0) = 2c1                       
		// Therefore S0''(2) = S1''(2) ==> 2c0 + 6d0 = 2c1    <--- Equation #4
		
		// Applying the Boundary Condition(BC): Each end must be equal to 0
		// Therefore..
		// S0''(1) = 0
		// S1''(3) = 0
		// S0''(1) = 2c0 + 6d0(0) = 2c0 = 0 ==> c0 = 0        <--- c0 solved
		// S1''(3) = 2c1 + 6d1(1) = 2c1 + 6d1 = 0             <--- Equation #5
		
		// Summary
		//=============================
		// b0 + d0 = 1
		// b1 + c1 + d1 = 2
		// b0 + 3d0 = b1
		// 6d0 = 2c1
		// 2c1 + 6d1 = 0
		// a0 = 2, a1 = 3, c0 = 0
		Equation eq = new Equation();
		eq.process("A = [" +
				  /* b0, d0, b1, c1, d1 */
					" 1,  1,  0,  0,  0,    1;" +
					" 0,  0,  1,  1,  1,    2;" +
					" 1,  3, -1,  0,  0,    0;" +
					" 0,  6,  0, -2,  0,    0;" +
					" 0,  0,  0,  2,  6,    0 " +
					"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		RrefGaussJordanRowPivot processor = new RrefGaussJordanRowPivot();
		processor.reduce(A, A.numCols - 1);
		System.out.println(A);
		
		// We Got all of the unknowns!!!
		// a0 = 2, a1 = 3, c0 = 0
		// b0 = 0.75, d0 = 2.5, b1 = 1.5, c1 = 0.75, d1 = -0.25 
		
		// S0(x) = 2 + 0.75(x - 1) + 0.25(x - 1)^3  , for any x between 1 and 2
		// S1(x) = 3 + 1.5(x - 2) + 0.75(x - 2)^2 - 0.25(x - 2)^3  , for any x between 2 and 3
		
		
	}

}
