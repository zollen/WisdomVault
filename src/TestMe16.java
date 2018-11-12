import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class TestMe16 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
							 /* U,    V,    W,    X,    Y,    Z,   =  */
			/* U */			"   1,    0,    0,    0,    0,    0,  0.05;" + 
			/* V */			"   0,    1,    0,    0,    0, -0.7,  0.05;" + 
			/* W */			"   0,    0,    1,    0,    0,    0,  0.05;" +
			/* X */			"-0.35, -0.35, -0.35, 1,    0,    0,  0.05;" +
			/* Y */			"-0.35, -0.35, -0.35, 0,    1,    0,  0.05;" +
			/* Z */			"   0,    0,    0,  -0.7,  -0.7,  1,  0.05 " +
						"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		DMatrixRMaj B = new DMatrixRMaj(A.numRows, A.numCols);
		
		System.out.println("Rank(A): " + MatrixFeatures.rank(A));
		CommonOps_DDRM.rref(A.copy(), A.numCols - 1, B);
		
		System.out.println(B);
		
		
	}

}
