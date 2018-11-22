import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.MatrixFeatures_DDRM;
import org.ejml.equation.Equation;

public class MarkovExercise7 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// https://www.youtube.com/watch?v=6JVqutwtzmo

		Equation eq = new Equation();
		
		// N = 3
		{
			eq.process("A = [ " +
						 /* -3,  -2,  -1,   0,   1,   2,   3 */
				/* -3 */ "   1, 0.5,   0,   0,   0,   0,   0;" +
				/* -2 */ "   0,   0, 0.5,   0,   0,   0,   0;" +
				/* -1 */ "   0, 0.5,   0, 0.5,   0,   0,   0;" +
				/*  0 */ "   0,   0, 0.5,   0, 0.5,   0,   0;" +
				/*  1 */ "   0,   0,   0, 0.5,   0, 0.5,   0;" +
				/*  2 */ "   0,   0,   0,   0, 0.5,   0,   0;" +
				/*  3 */ "   0,   0,   0,   0,   0, 0.5,   1 " + 
						"]");

			DMatrixRMaj A = eq.lookupDDRM("A");
			System.out.println("Rank(A): " + MatrixFeatures_DDRM.rank(A));

			eq.process("K = A - eye(A)");
			eq.process("K1 = [ K, [ 0; 0; 0; 0; 0; 0; 0 ]]");
			eq.process("K2 = [ K1 ; [ 1, 1, 1, 1, 1, 1, 1, 1 ]]");
			eq.process("K3 = rref(K2)");

			System.out.println("STEADY STATE: " + eq.lookupDDRM("K3"));

		}
		
		// N = 50
		{
			int N = 50;
			DMatrixRMaj A = new DMatrixRMaj((2 * N) + 1, (2 * N) + 1);
			
			for (int j = 0; j < (2 * N + 1); j++) {
				
				for (int i = 0; i < (2 * N + 1); i++) {
					
					if (i == 0 && j == 0) {
						A.set(i, j, 1);
					}
					else
					if (i == 2 * N && j == 2 * N) {
						A.set(i, j, 1);
					}
					else
					if (j > 0 && j < 2 * N && (i == j - 1 || i == j + 1)) {
						A.set(i, j, 0.5);
					}
				}
			}
			
			eq.alias(A, "A");
			
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < 50; i++) {
				buf.append("A * ");
			}
			
			DMatrixRMaj X = new DMatrixRMaj(2 * N + 1, 1);
			X.set(N, 0, 1);
			
			eq.alias(X, "X");
			
			eq.process("K = " + buf.toString() + " X");
			
			DMatrixRMaj D = new DMatrixRMaj(2 * N + 1, 1);
			for (int i = 0; i < 2 * N + 1; i++) {
				D.set(i, 0, i - N);
			}
			
			eq.alias(D, "D");
			
			eq.process("KK = [ D, K ]");
			
			
			System.out.println(eq.lookupDDRM("KK"));
		}
	}

}
