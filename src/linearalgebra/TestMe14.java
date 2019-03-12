package linearalgebra;
import java.text.DecimalFormat;
import java.util.Random;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.NormOps_DDRM;
import org.ejml.dense.row.RandomMatrices_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;

public class TestMe14 {
	
	private static final DecimalFormat formatter = new DecimalFormat("0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " +
							" 1,  0,  1,  0;" +
							" 2,  4,  6,  7;" +
							" 3,  8, 10,  2 " +
						"]");
		
		
		DMatrixRMaj A = eq.lookupDDRM("A");
	
		
		SingularValueDecomposition<DMatrixRMaj> svd = 
				DecompositionFactory_DDRM.svd(A.numRows, A.numCols, true, true, false);
		
		int rank = MatrixFeatures.rank(A);
		System.out.println("Rank(A): " + rank);
		System.out.println("SVD(A): " + svd.decompose(A));
		
		DMatrixRMaj U = svd.getU(null, false);
		DMatrixRMaj V = svd.getV(null, true);
		DMatrixRMaj S = svd.getW(null);
		
		// calculating the min frobenius and absolute distances
		// calculating the max frobenius and absolute distances
		for (int i = 1; i <= rank; i++) {
			
			DMatrixRMaj ss = reconstruct(S, i);
			
			DMatrixRMaj su = new DMatrixRMaj(U.numRows, ss.numCols);
			DMatrixRMaj suv = new DMatrixRMaj(U.numRows, ss.numCols);
			CommonOps_DDRM.mult(U, ss, su);
			CommonOps_DDRM.mult(su, V, suv);
		
			DMatrixRMaj suv_a = new DMatrixRMaj(U.numRows, ss.numCols);
			CommonOps_DDRM.subtract(suv, A, suv_a);
	
			System.out.println("Rank(suv, " + i + "): [" + MatrixFeatures.rank(suv) +
					"], frobenius(" + i + ", suv_a): " + formatter.format(NormOps_DDRM.normF(suv_a)) + 
					", " + "Absolute(" + i + ", suv_a): " + formatter.format(NormOps_DDRM.elementP(suv_a, 1)));
		}
		
		
	
		DMatrixRMaj orth = RandomMatrices_DDRM.orthogonal(3, 3, new Random(System.currentTimeMillis()));

		System.out.println("RandomOrth(3,3): " + orth);
		System.out.println("Det(orth): " + formatter.format(CommonOps_DDRM.det(orth)));
		
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
