import java.util.Random;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.NormOps_DDRM;
import org.ejml.dense.row.RandomMatrices_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;

public class LossyCompressionExercise {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DMatrixRMaj A = RandomMatrices_DDRM.rectangle(512, 400, 100, 0, new Random());
		for (int i = 0; i < A.numRows; i++) 
			for (int j = 0; j < A.numCols; j++)
				A.set(i, j, (int) A.get(i, j));
		
		
		SingularValueDecomposition<DMatrixRMaj> svd = 
				DecompositionFactory_DDRM.svd(A.numRows, A.numCols, true, true, false);
		
		int rank = MatrixFeatures.rank(A);
		System.out.println("Rank(A): " + rank);
		System.out.println("SVD(A): " + svd.decompose(A));
		
		DMatrixRMaj U = svd.getU(null, false);
		DMatrixRMaj V = svd.getV(null, true);
		DMatrixRMaj S = svd.getW(null);
		DMatrixRMaj result = null;
		
		// keep only the first 10% of the column for strong compression
		
		for (int i = 1; i <= (A.numCols * 0.1) ; i++) {
		
			DMatrixRMaj ss = reconstruct(S, i);
			DMatrixRMaj uu = reconstruct(U, i);
			DMatrixRMaj vv = reconstruct(V, i);
			
			DMatrixRMaj su = new DMatrixRMaj(uu.numRows, ss.numCols);
			DMatrixRMaj suv = new DMatrixRMaj(uu.numRows, ss.numCols);
			CommonOps_DDRM.mult(uu, ss, su);
			CommonOps_DDRM.mult(su, vv, suv);
			
			result = suv;
			
			DMatrixRMaj _diff = new DMatrixRMaj(A.numRows, A.numCols);
			CommonOps_DDRM.subtract(A, suv, _diff);
			System.out.println("[" + i + "] ==> " + NormOps_DDRM.elementP(_diff, 1) / (A.numCols * A.numRows));
		}
		
		System.out.println(A);
		System.out.println("=== AFTER COMPRESSION =============================================================================");
		System.out.println(result);
	
	}
	
	public static DMatrixRMaj reconstruct(DMatrixRMaj m, int col) {
		
		DMatrixRMaj subject = new DMatrixRMaj(m.numRows, m.numCols);
		
		for (int i = 0; i < col && i < m.numCols; i++) {
			
			for (int j = 0; j < m.numRows; j++) {
				
				subject.set(j, i, m.get(j, i));
			}
		}
		
		return subject;
		
	}

}
