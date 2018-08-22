import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class LinearDependency {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A =  [1, 1, 1, 5; -2, 3, 1, 2; 3, 4, -2, 3 ]");	
		DMatrixRMaj A = eq.lookupDDRM("A");
		DMatrixRMaj b = new DMatrixRMaj(A.numRows, 1);
		
		System.out.println("RANK : " + MatrixFeatures.rank(A));
		
		if (MatrixFeatures.isSquare(A))
			System.out.println("DET: " + CommonOps_DDRM.det(A));
		
		System.out.println("Linear Independence: " + MatrixFeatures.isLinearIndependent(A));
		
		System.out.println(A);
	
		DMatrixRMaj n = MatrixFeatures.nullSpace(A);
		
		System.out.println("<=== Coefficients of row nullspace ===>");
		System.out.println(n);		
		
		CommonOps_DDRM.fill(b, 0);
		CommonOps_DDRM.mult(A, n, b);
		
		System.out.println("<=== Verification of coefficients ===>");
		System.out.println(b);
	}

}
