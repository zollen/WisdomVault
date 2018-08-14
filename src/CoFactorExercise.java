import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class CoFactorExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 2, -2, 0, 3, 4; 4, -1, 0, 1, -1; 0, 5, 0, 0, -1; 3, 2, -3, 4, 3; 7, -2, 0, 9, -5 ]");
		DMatrixRMaj A = eq.lookupMatrix("A");
		
		System.out.println("<==== A ====>");
		System.out.println(A);
		System.out.println("Det(A): " + CommonOps_DDRM.det(A));
		
		DMatrixRMaj C = cofactor(A);
		eq.alias(C, "C");
		
		double det = A.get(3, 2) * C.get(3, 2);
		
		System.out.println("Column[" + 2 + "] has 4 zeros entries");
		System.out.println("Det(A) = A[3][2] * cofactors[3][2]: " + det);
		
	
		System.out.println("<==== adj(A) ====>");
		System.out.println(C);
		
		
		eq.process("AI = inv(A)");
		eq.process("AC = inv(det(A)) * C'");
		
		
		System.out.println("<==== inv(A) ====>");
		System.out.println(eq.lookupMatrix("AI"));
		
		System.out.println("<==== 1 / det(A) * adj(A) ====>");
		System.out.println(eq.lookupMatrix("AC"));
		
		System.out.println("inv(A) == 1 / det(A) * adj(A): " + MatrixFeatures.isIdentical(eq.lookupMatrix("AI"), eq.lookupMatrix("AC"), 0.00000001));
		
		
	}
	
	private static DMatrixRMaj cofactor(DMatrixRMaj A) {
		
		DMatrixRMaj C = new DMatrixRMaj(A.numRows, A.numCols);
		
		for (int i = 0; i < A.numRows; i++) {
			for (int j = 0; j < A.numCols; j++) {
				
				DMatrixRMaj matrix = new DMatrixRMaj(A.numRows - 1, A.numCols - 1);
				
				int [] rows = new int[A.numRows - 1];
				for (int ii = 0, index = 0; ii < A.numRows; ii++) {
					if (ii != i) {
						rows[index] = ii;
						index++;
					}
				}
				
				int [] cols = new int[A.numCols - 1];
				for (int jj = 0, index = 0; jj < A.numCols; jj++) {
					if (jj != j) {
						cols[index] = jj;
						index++;
					}
				}
				
				CommonOps_DDRM.extract(A, rows, A.numRows - 1, cols, A.numCols - 1, matrix);
				C.set(i, j, CommonOps_DDRM.det(matrix) * Math.pow(-1, i + j));
			}
		}
		
		return C;
	}

}
