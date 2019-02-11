import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.MatrixFeatures_DDRM;
import org.ejml.dense.row.SingularOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.SingularValueDecomposition_F64;


public class MatrixFeatures extends MatrixFeatures_DDRM {

	
	/**
	 * Computes the x of a nullspace matrix.
	 *
	 * @param A
	 *            Matrix whose rank is to be calculated. Not modified.
	 * @param x
	 *            The coefficients for computing the nullspace.
	 * @return The matrix's nullspace.
	 */
	public static DMatrixRMaj nullSpace(DMatrixRMaj A) {
		SingularValueDecomposition_F64<DMatrixRMaj> svd = DecompositionFactory_DDRM.svd(A.numRows, A.numCols, true, true, false);
		
		A = A.copy();
		
		if (!svd.decompose(A))
			throw new RuntimeException("Decomposition failed");
		
        DMatrixRMaj V = svd.getV(null, false);

        if( V.numCols != svd.numCols() ) {
            throw new IllegalArgumentException("Can't compute the null space using a compact SVD for a matrix of this size.");
        }
        
        
        DMatrixRMaj nullSpace = new DMatrixRMaj(svd.numCols(), 1);

        
        SingularOps_DDRM.nullSpace(svd, nullSpace, 0.0000001d);
        
        return nullSpace;
	}
	
	/**
	 * Determines the linear dependent or independent of matrix A
	 * 
	 * @param A
	 * 			Matrix whose linear dependent/independent is determined. Not modified
	 * @return  boolean
	 */
	public static boolean isLinearIndependent(DMatrixRMaj A) {
		int rank = rank(A);
		
		if (rank == A.numCols && rank == A.numRows)
			return CommonOps_DDRM.invert(A, new DMatrixRMaj(A.numRows, A.numCols));
		
		return false;
			
	}
	
	public static boolean isColsLinearIndependent(DMatrixRMaj A) {
		int rank = rank(A);
		
		if (rank == A.numCols)
			return true;
		
		return false;
	}
	
	public static double[][] array(DMatrixRMaj A) {
		
		double [][] arr = new double[A.numRows][A.numCols];
		
		for (int i = 0; i < A.numRows; i++) {
			for (int j = 0; j < A.numCols; j++) {
				arr[i][j] = A.get(i, j);
			}
		}
		
		return arr;
	}

}
