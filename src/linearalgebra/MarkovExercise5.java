package linearalgebra;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class MarkovExercise5 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		
		// Page 306, Figure 10.3 Page Ranking Problem
		
		{
			// e = 0.05 and F = 0.7
			
			eq.process("A = [ " +
					 /* U,    V,    W,    X,    Y,    Z,    =   */
			/* U */ "   1,    0,    0,    0,    0,    0,  0.05;" +
			/* V */ "   0,    1,    0,    0,    0, -0.7,  0.05;" +
			/* W */ "   0,    0,    1,    0,    0,    0,  0.05;" +
			/* X */ "-0.35, -0.35, -0.35, 1,    0,    0,  0.05;" +
			/* Y */ "-0.35, -0.35, -0.35, 0,    1,    0,  0.05;" +
			/* Z */ "   0,    0,    0,  -0.7,  -0.7,  1,  0.05 " + 
						"]");

			DMatrixRMaj A = eq.lookupDDRM("A");
			DMatrixRMaj B = new DMatrixRMaj(A.numRows, A.numCols);

			System.out.println("Rank(A): " + MatrixFeatures.rank(A));
			CommonOps_DDRM.rref(A.copy(), A.numCols - 1, B);

			System.out.println(B);
		}
		
		{
			// e = 0.05 and F = 0.7
			
			eq.process("M = [" +
						/*   U,    V,    W,    X,    Y,    Z  */
			/* U */		" 0.05, 0.05, 0.05, 0.05, 0.05, 0.05;" +
			/* V */		" 0.05, 0.05, 0.05, 0.05, 0.05, 0.75;" +
			/* W */		" 0.05, 0.05, 0.05, 0.05, 0.05, 0.05;" +
			/* X */		" 0.40, 0.40, 0.40, 0.05, 0.05, 0.05;" +
			/* Y */		" 0.40, 0.40, 0.40, 0.05, 0.05, 0.05;" +
			/* Z */		" 0.05, 0.05, 0.05, 0.75, 0.75, 0.05 " +
					"]");
			
			eq.process("K = M - eye(M)");
			eq.process("K = [ K, [ 0; 0; 0; 0; 0; 0 ]]");
			eq.process("K = [ K ; [ 1, 1, 1, 1, 1, 1, 1 ]]");
			
			DMatrixRMaj K = eq.lookupDDRM("K");
			CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);
			
			System.out.println("e = 0.05 and F = 0.7");
			System.out.println(K);
		
		}
		
		{
			// e = 0.05 and F = 0.7
			// Adding U -> W
			// Adding W -> U
			
			eq.process("M = [" +
						      /*   U,    V,          W,    X,    Y,    Z  */
			/* U */		"       0.05, 0.05, 0.05+0.7/3, 0.05, 0.05, 0.05;" +
			/* V */		"       0.05, 0.05,       0.05, 0.05, 0.05, 0.75;" +
			/* W */		" 0.05+0.7/3, 0.05,       0.05, 0.05, 0.05, 0.05;" +
			/* X */		" 0.05+0.7/3, 0.40, 0.05+0.7/3, 0.05, 0.05, 0.05;" +
			/* Y */		" 0.05+0.7/3, 0.40, 0.05+0.7/3, 0.05, 0.05, 0.05;" +
			/* Z */		"       0.05, 0.05,       0.05, 0.75, 0.75, 0.05 " +
					"]");
						
			eq.process("K = M - eye(M)");
			eq.process("K = [ K, [ 0; 0; 0; 0; 0; 0 ]]");
			eq.process("K = [ K ; [ 1, 1, 1, 1, 1, 1, 1 ]]");
						
			DMatrixRMaj K = eq.lookupDDRM("K");
			CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);
						
			System.out.println("e = 0.05 and F = 0.7 with new paths U->W and W->U");
			System.out.println(K);

		}
		
		{
			// e = 0.15 and F = 0.1
			
			eq.process("M = [" +
					/*   U,    V,    W,    X,    Y,    Z  */
		/* U */		" 0.15, 0.15, 0.15, 0.15, 0.15, 0.15;" +
		/* V */		" 0.15, 0.15, 0.15, 0.15, 0.15, 0.25;" +
		/* W */		" 0.15, 0.15, 0.15, 0.15, 0.15, 0.15;" +
		/* X */		" 0.20, 0.20, 0.20, 0.15, 0.15, 0.15;" +
		/* Y */		" 0.20, 0.20, 0.20, 0.15, 0.15, 0.15;" +
		/* Z */		" 0.15, 0.15, 0.15, 0.25, 0.25, 0.15 " +
					"]");
			
			eq.process("K = M - eye(M)");
			eq.process("K = [ K, [ 0; 0; 0; 0; 0; 0 ]]");
			eq.process("K = [ K ; [ 1, 1, 1, 1, 1, 1, 1 ]]");
			
			DMatrixRMaj K = eq.lookupDDRM("K");
			CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);
			
			System.out.println("e = 0.15 and F = 0.1");
			System.out.println(K);
			
		}

	}

}
