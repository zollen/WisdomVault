package linearalgebra;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;

public class AllSpaces {
	
	public static void main(String [] args) {
		
		Equation eq = new Equation();
		eq.process("A = [ 1, 0, 0, 0; 1, 1, 1, 1; 1, 3, 9, 27; 1, 4, 16, 64 ]");	
		DMatrixRMaj A = eq.lookupDDRM("A");
		DMatrixRMaj I = CommonOps_DDRM.identity(A.numRows, A.numCols);
		
		DMatrixRMaj reduced = new DMatrixRMaj(A.numRows, A.numCols + I.numCols);
		DMatrixRMaj copied = new DMatrixRMaj(A.numRows, A.numCols + I.numCols);
		SimpleMatrix Am = SimpleMatrix.wrap(copied);
		Am.insertIntoThis(0, 0, SimpleMatrix.wrap(A));
		Am.insertIntoThis(0, A.numCols, SimpleMatrix.wrap(I));
		
		CommonOps_DDRM.rref(copied, A.numCols, reduced);
		DMatrixRMaj E = CommonOps_DDRM.extract(reduced, 0, A.numRows,
													A.numCols, A.numCols + I.numCols);
		
		
		
		
		System.out.println(A);
		
		System.out.println("RANK : " + MatrixFeatures.rank(A));
		System.out.println("Dim(N(A)): " + MatrixFeatures.nullity(A));
		System.out.println("Linear Independence: " + MatrixFeatures.isLinearIndependent(A));
		
		System.out.println("<====== RREF ======>");
		System.out.println(reduced);
		

		DMatrixRMaj b = new DMatrixRMaj(A.numRows, A.numCols);
		DMatrixRMaj Ee = new DMatrixRMaj(A.numRows, A.numRows);
		
		for (int i = 0; i < A.numRows && i < Ee.numRows; i++) {
			for (int j = 0; j < A.numCols && j < Ee.numCols; j++) {
				Ee.set(i, j, E.get(i, j));
			}
		}
		
		CommonOps_DDRM.mult(Ee, A, b);
		
		System.out.println("<===== E ======>");
		System.out.println(Ee);
		
		System.out.println("<====== Verification of E =======>");
		System.out.println(b);	
	}

}
