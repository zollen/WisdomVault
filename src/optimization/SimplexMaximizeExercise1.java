package optimization;
import org.ejml.equation.Equation;

public class SimplexMaximizeExercise1 {
	
	// For minimization problem:
	// i.e. Z = 3x + 9y
	// Subject to:
	//     2x + y >= 8
	//      x + 2y >= 8
	//      x, y >= 0
	//  We need to turn the minimize problem into maximum problem.
	//  Using matrix transpose to do that
	//  [ 2, 1, 8 ]
	//  [ 1, 2, 8 ]
	//  [ 3, 9, C ]
	//  After transpose
	//  [ 2, 1, 3 ]
	//  [ 1, 2, 9 ]
	//  [ 8, 8, C ]
	//  Now we have the new formula
	//  Maximum 8a + 8b = C
	//  Subject to:
	//      2a + b <= 3
	//       a +2b <= 9
	//      a, b >= 0
	// Adding Slack variables
	//      2a + b + x = 3
	//       a + 2b + y = 9
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Maximize: Z = 8x + 10y + 7z
		// Subject to: 
		// 	  x + 3y + 2z <= 10
		//    x + 5y + z <= 8
		//    x, y, z >= 0

		
		// Step 1: Move The objective function to the Z (right side)
		// Z - 8x - 10y -7z = 0
		
		// Step 2: add a slack variable in each constraints to equalize the formula
		//    x + 3y + 2z + s1 = 10
		//    x + 5y + z + s2 = 8
		
		// Step 3: Calculate the ratio of each pivot row by using the largest negative 
		// indicator of the objective function: -10
		//    row #1:  10/3
		//    row #2:   8/5
		//    The smallest non-negative value of the *positive* two is 8/5, so R2 is our pivot row
		//    we want the columns of non-pivot rows to be 0
		
		// Step 4: Row reductions
		//     1/5 * R2      -> R2
		//      -3 * R2 + R1 -> R1
		//      10 * R2 + R3 -> R3
		
		Equation eq = new Equation();
		eq.process("A = [ " + 
		                // x,   y,  z, s1, s2,  Z
						"  1,   3,  2,  1,  0,  0, 10;" +
						"  1,   5,  1,  0,  1,  0,  8;" +
						" -8, -10, -7,  0,  0,  1,  0 " +
					"]");
		
		// Step 5: Row reductions
		//     1/5 * R2      -> R2
		//      -3 * R2 + R1 -> R1
		//      10 * R2 + R3 -> R3
		
		eq.process("A = [" +			
					 //   x,   y,    z,  s1,   s2,  Z
					 " 2./5,   0, 7./5,   1, 3./5,  0, 26./5;" +
					 " 1./5,   1, 1./5,   0, 1./5,  0,  8./5;" +
					 "   -6,   0,   -5,   0,    2,  1,    16 " +
					"]");
		
		// Row 3 and Column 2 is now 0
		// But we still have negative at the last row, now we reduce the next most negative
		// indicator in the row: -6 so R2 again is our pivot row
		// we want the columns of non-pivot rows to be 0
		
		// Step 6: Calculate the ratio of each pivot row by using the next largest negative 
		// indicator of the objective function: -6
		//	    row #1:  26/5 / 2/5 = 13
		//      row #2:  8/5 / 1/5 = 8
		
		// The smallest non-negative value of the *positive* two is 8 so R2 again is our pivot row
		// we want the columns of non-pivot rows to be 0
		
		// Step 7: Row reductions
		//       5 * R2      -> R2
		//    -2/5 * R2 + R1 -> R1
		//       6 * R2 + R3 -> R3
		
		eq.process("A = [" +			
			   //   x,  y,  z, s1, s2, Z
				 "  0, -2,  1,  1, -1, 0,  2;" +
				 "  1,  5,  1,  0,  1, 0,  8;" +
				 "  0, 30,  1,  0,  8, 1, 64 " +
				"]");
		
		// The last row has no more negative indicators. This is now OPTIMAL!!
		// This is our final tableau.
		// x = 8, y = 0, z = 0, s1 = 2, s2 = 0, z = 64
		// First column of Row 2 is 1, therefore x = 8
		// According to the constraint x + 5y + z <= 8, therefore y and z are both 0.
		// Therefore the maximum value of 64 at (8, 0, 0)
	}	

}
