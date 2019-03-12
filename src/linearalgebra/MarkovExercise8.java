package linearalgebra;
import java.text.DecimalFormat;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class MarkovExercise8 {
	
	private static DecimalFormat formatter = new DecimalFormat("0.00000000000000000000");

	private static final int N = 50;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// https://www.youtube.com/watch?v=6JVqutwtzmo

		Equation eq = new Equation();

		DMatrixRMaj A = new DMatrixRMaj((2 * N) + 1, (2 * N) + 1);

		for (int j = 0; j < (2 * N + 1); j++) {

			for (int i = 0; i < (2 * N + 1); i++) {

				if (i == 0 && j == 0) {
					A.set(i, j, 1);
				} else if (i == 2 * N && j == 2 * N) {
					A.set(i, j, 1);
				} else if (j > 0 && j < 2 * N && (i == j - 1 || i == j + 1)) {
					A.set(i, j, 0.5);
				}
			}
		}

		eq.alias(A, "A");
		
		System.out.println("Probabilty of getting to state 50 from state i");
		for (int i = -50; i < 51; i++) {
			System.out.println("Prob(" + i + "): " + prob(eq, i));
		}

	}

	public static String prob(Equation eq, int I) {

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < 50; i++) {
			buf.append("A * ");
		}

		DMatrixRMaj X = new DMatrixRMaj(2 * N + 1, 1);
		X.set(I + N, 0, 1);

		eq.alias(X, "X");

		eq.process("K = " + buf.toString() + " X");
		
		DMatrixRMaj K = eq.lookupDDRM("K");
			
		return formatter.format(K.get(2 * N, 0));
	}

}
