import java.util.Random;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.NormOps_DDRM;
import org.ejml.dense.row.RandomMatrices_DDRM;
import org.ejml.dense.row.SpecializedOps_DDRM;
import org.ejml.equation.Equation;

public class TestMe5 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
				"  1,  1,  2;" +
				"  1,  2,  1;" +
				"  2,  1,  6" +
			"]");

		
		eq.process("B = [ " + 
				"  1,  1,  2;" +
				"  1,  2,  1;" +
				"  2,  1,  6" +
			"]");
		
		
		DMatrixRMaj A = eq.lookupDDRM("a");
		DMatrixRMaj B = eq.lookupDDRM("B");
	
		
		NormOps_DDRM.normalizeF(A);
		System.out.println(A);
		
		DMatrixRMaj Ran1 = RandomMatrices_DDRM.orthogonal(3, 3, new Random());
		
		System.out.println(Ran1);
		
		DMatrixRMaj Ran2 = RandomMatrices_DDRM.symmetric(3, 3, 1, new Random());
		
		System.out.println(Ran2);
		
		int [] pivots = { 0, 1, 2 };
		System.out.println(SpecializedOps_DDRM.pivotMatrix(A, pivots, 3, false));
	
		
		System.out.println("Diag+(A): " + MatrixFeatures.isDiagonalPositive(A));
		System.out.println("A == B: "+ MatrixFeatures.isEquals(A, B));
		
	}

}
