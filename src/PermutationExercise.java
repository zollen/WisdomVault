import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class PermutationExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();

		eq.process("A = [ " +
						/*	  1, 2, 3, 4 */
							" 0, 0, 0, 0;" +  // 1
							" 1, 0, 0, 0;" +  // 2
							" 0, 1, 0, 0;" +  // 3
							" 0, 1, 1, 0 " +  // 4
						 "]");
		
		eq.process("K = eye(A)");
		int total = 0;
		int i = 0;
		for (i = 0; i < 1000; i++) {
			eq.process("K = K * A");
			DMatrixRMaj K = eq.lookupDDRM("K");
			
			int num = total(K);
			total += num;
			System.out.println("TOTAL: " + num + " : " + K);
			
			if (MatrixFeatures.isZeros(K, 0.000001d))
				break;
		}
		
		if (i >= 100)
			System.out.println("TOTAL PATHS: INFINITE");
		else
			System.out.println("TOTAL PATHS: " + total);
	}
	
	public static int total(DMatrixRMaj A) {
		
		int damn = 0;
		
		for (int i = 0; i < A.numRows; i++) {
			
			for (int j = 0; j < A.numCols; j++) {
				damn += A.get(i,  j);
			}
		}
		
		return damn;
	}

}
