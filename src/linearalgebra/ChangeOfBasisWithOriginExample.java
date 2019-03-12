package linearalgebra;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class ChangeOfBasisWithOriginExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		// Coordinate system [alpha]
		//   Origin: O
		//   Basis: x, y
		// Coordinate system [beta]
		//   Origin: P
		//   Basis: i, j
		//
		// Coords(P, alpha) = (2, 3)
		// Coords(i, alpha) = (4, 3)
		// Coords(j, alpha) = (-3, 4)
		
		// Question 7.1a What are alpha coordinate of beta(1, 2)
		// Question 7.2b What are beta coordinate of alpha(1, 2)
		
		eq.process("B= [ " +
							" 4, -3, 2;" +
							" 3,  4, 3;" +
							" 0,  0, 1 " +
						"]");
		
		eq.process("b = [ 1; 2; 1 ]");
		
		eq.process("K1 = B * b");
		
		
		DMatrixRMaj K1 = eq.lookupDDRM("K1");
		System.out.println("Answer 7.1a (beta -> alpha): " + K1);
		
		eq.process("K2 = inv(B) * b");
		
		DMatrixRMaj K2 = eq.lookupDDRM("K2");
		System.out.println("Answer 7.1b (alpha -> beta): " + K2);
		
	}

}
