import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class PCAExperiment3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Consider the following set of three-dimensional points
		// D = {(0,2,1), (0,4,3), (1,4,5), (1,8,6), (1,8,10), (4,8,14), (5,9,13)}

		// Perform a PCA with PC1 and PC2
		
		Equation eq = new Equation();
		// (0, 2, 1), (0, 4, 3), (1, 4, 5), (1, 8, 6), (1, 8, 10), (4, 8, 14), (5, 9, 13)
		eq.process("A = [" +
						" 0, 0, 1, 1,  1,  4,  5;" +
						" 2, 4, 4, 8,  8,  8,  9;" +
						" 1, 3, 5, 6, 10, 14, 13 " +
					"]");
				
		DMatrixRMaj A = eq.lookupDDRM("A");
	
		A = translate(avg(A), A);
				
		SingularValueDecomposition svd = new SingularValueDecomposition(
				MatrixUtils.createRealMatrix(MatrixFeatures.array(A)));
		
		DMatrixRMaj S = new DMatrixRMaj(svd.getS().getData());
		DMatrixRMaj U = new DMatrixRMaj(svd.getU().getData());
		DMatrixRMaj V = new DMatrixRMaj(svd.getVT().getData());
		
		System.out.println("EigenVectors: " + U);
			
		// Caution: calculating SS and Variance only make sense with projected lines, *not* projected planes.
		// Projected planes uses the normal as the plane's equation, which may confuse user
		// perspective. The EignVectors are always vectors(lines), not planes!
		
		// PC1 = [ 0.304, 0.427, 0.852 ], PC2 = [-0.472, 0.844, -0.254 ]
		
		DMatrixRMaj c = new DMatrixRMaj(A.numRows - 1, A.numCols);
		
		DMatrixRMaj PC = null;
		
		{
			// Pick the first two EigenVectors as principle components
			int [] rows = { 0, 1, 2 };
			int [] cols = { 0, 1 };
			PC = copy(U, rows, cols);
		
			System.out.println("PC: " + PC);
		}
		
		DMatrixRMaj SS = null;
		
		{
			// Pick the first two EigenValues as principle components 'stretch' factors
			int [] rows = { 0, 1, 2 };
			int [] cols = { 0, 1 };
			
			SS = copy(S, rows, cols);
			
			System.out.println("SS: " + SS);
		}
		
		System.out.println("V: " + V);
		CommonOps_DDRM.transpose(SS);
		CommonOps_DDRM.mult(SS, V, c);
		
		System.out.println("S_3x2' * V' = " + c);
		
		
		
		
		CommonOps_DDRM.fill(c, 0d);
		
		CommonOps_DDRM.transpose(PC);
		CommonOps_DDRM.mult(PC, A, c);
	
		System.out.println("U_3x2 * A = " + c);
		
	}
	
	public static DMatrixRMaj translate(DMatrixRMaj T, DMatrixRMaj A) {
		
		DMatrixRMaj B = new DMatrixRMaj(A.numRows, A.numCols);
		
		for (int col = 0; col < A.numCols; col++) {
			
			for (int row = 0; row < A.numRows; row++) {
				B.set(row, col, A.get(row, col) - T.get(row, 0));		
			}
		}
		
		return B;
	}
	
	public static double dist(DMatrixRMaj a, DMatrixRMaj b) {
		
		return Math.sqrt(
				(b.get(0, 0) - a.get(0, 0)) * (b.get(0, 0) - a.get(0, 0)) + 
				(b.get(1, 0) - a.get(1, 0)) * (b.get(1, 0) - a.get(1, 0)) 
				);	
	}
	
	public static DMatrixRMaj avg(DMatrixRMaj A) {
		
		DMatrixRMaj avg = new DMatrixRMaj(A.numRows, 1);
		
		for (int row = 0; row < A.numRows; row++) {
			
			double val = 0d;
			for (int col = 0; col < A.numCols; col++) {
				val += A.get(row, col);
			}
			
			avg.set(row, 0, val / A.numCols);
		}
		
		
		return avg;
				
	}
	
	public static DMatrixRMaj copy (DMatrixRMaj A, int col) {
		
		DMatrixRMaj B = new DMatrixRMaj(A.numRows, 1);
		
		for (int i = 0; i < A.numRows; i++)
			B.set(i, 0, A.get(i, col));
		
		return B;
	}
	
	public static DMatrixRMaj copy(DMatrixRMaj A, int [] rows, int [] cols) {
		
		DMatrixRMaj B = new DMatrixRMaj(rows.length, cols.length);
		
		for (int col = 0; col < cols.length; col++) {
			for (int row = 0; row < rows.length; row++) {
				B.set(row, col, A.get(rows[row], cols[col]));
			}
		}
		
		return B;
	}
	
}
