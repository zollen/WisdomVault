import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class PositiveDefiniteExercise {
	
	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	//	DMatrixRMaj A = RandomMatrices_DDRM.symmetricPosDef(5,  new Random(1));
		
		Equation eq = new Equation();
		eq.process("A = [" +
					"  2, -1,  0;" +
				     "-1,  2, -1;" +
					"  0, -1,  2" +
					"]");
		
		DMatrixRMaj A = eq.lookupMatrix("A");
		
		System.out.println(A);
		
		RealMatrix AA = MatrixUtils.createRealMatrix(MatrixFeatures.array(A));
		EigenDecomposition eigen = new EigenDecomposition(AA);
		LUDecomposition lu = new LUDecomposition(AA);

		{	
			EigenMatrix matrix = new EigenMatrix(eigen);
			List<EigenVector> vectors = matrix.getVectors();
					
			System.out.println("RANK(A): " + MatrixFeatures.rank(A));
			System.out.println("DET(A): " + getDeterminants(A));
			System.out.println("Pivot(A): " + getPivots(getU(lu)));
			System.out.println("Eigen Values(A): " + matrix.getValues());
			System.out.println("TRACE(A): " + ff.format(CommonOps_DDRM.trace(A)));	
			System.out.println("Symm(A): " + MatrixFeatures.isSymmetric(A));
			System.out.println("Pos-Def(A): " + MatrixFeatures.isPositiveDefinite(A));
			System.out.println("Has Complex(A): " + eigen.hasComplexEigenvalues());
			System.out.println("Orthogonal(D): " + MatrixFeatures.isOrthogonal(getD(eigen), 0.0000001d));
			System.out.println("<======== Eigen Vectors (V) =========>");
			for (EigenVector vector : vectors) {
				System.out.println(vector);
			}
			System.out.println("<=== U ===>");
			System.out.println(getU(lu));
			System.out.println("<=== P ===>");
			System.out.println(getP(lu));
			System.out.println("<=== L ===>");
			System.out.println(getL(lu));
		
		}
	
	}
	
	private static String getDeterminants(DMatrixRMaj A) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < A.numCols; i++) {
			if (builder.length() > 0)
				builder.append(", ");
			
			DMatrixRMaj subA = CommonOps_DDRM.extract(A, 0, i + 1, 0, i + 1);
			builder.append(ff.format(CommonOps_DDRM.det(subA)));
		}
		
		return builder.toString();
		
	}
	private static String getPivots(DMatrixRMaj A) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < A.numCols; i++) {
			if (builder.length() > 0)
				builder.append(", ");
			
			builder.append(ff.format(A.get(i, i)));
		}
		
		return builder.toString();
	}
	
	private static DMatrixRMaj getD(EigenDecomposition eigen) {
		return new DMatrixRMaj(eigen.getD().getData());
	}
	
	private static DMatrixRMaj getV(EigenDecomposition eigen) {
		return new DMatrixRMaj(eigen.getV().getData());
	}
	
	private static DMatrixRMaj getU(LUDecomposition lu) {
		return new DMatrixRMaj(lu.getU().getData());
	}
	
	private static DMatrixRMaj getL(LUDecomposition lu) {
		return new DMatrixRMaj(lu.getL().getData());
	}
	
	private static DMatrixRMaj getP(LUDecomposition lu) {
		return new DMatrixRMaj(lu.getP().getData());
	}

}
