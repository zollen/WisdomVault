package linearalgebra;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class SymMatrixExercise {
	
	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
						"  2, -1,  0;" +
						" -1,  2, -1;" +
						"  0, -1,  2" +
					"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		RealMatrix AA = MatrixUtils.createRealMatrix(MatrixFeatures.array(A));
		EigenDecomposition eigen = new EigenDecomposition(AA);
		
		{
			EigenMatrix matrix = new EigenMatrix(eigen);
			List<EigenVector> vectors = matrix.getVectors();
			

			double productOfEigens = 1d;	
			double sumOfEigens = 0d;
			for (EigenVector vector : vectors) {
				productOfEigens *= vector.getValue();
				sumOfEigens += vector.getValue();
			}
			
			System.out.println("DET(A): " + ff.format(CommonOps_DDRM.det(A)));
			System.out.println("Product of all Lamda(A): " + ff.format(productOfEigens));
			System.out.println("RANK(A): " + MatrixFeatures.rank(A));
			System.out.println("TRACE(A): " + ff.format(CommonOps_DDRM.trace(A)));
			System.out.println("Sum of all Lamda(A): " + ff.format(sumOfEigens));
			System.out.println("Symmetric(A): " + MatrixFeatures.isSymmetric(A));
			System.out.println("Positive Definite(A): " + MatrixFeatures.isPositiveDefinite(A));
			System.out.println("Has Complex(A): " + eigen.hasComplexEigenvalues());
			System.out.println("Eigen Values(A): " + matrix.getValues());
			System.out.println("<======== Eigen Vectors =========>");
			for (EigenVector vector : vectors) {
				System.out.println(vector);
			}

			System.out.println("Orthogonal(D): " + MatrixFeatures.isOrthogonal(new DMatrixRMaj(eigen.getD().getData()), 0.0000001d));
		}
		
		{
			DMatrixRMaj D = new DMatrixRMaj(eigen.getD().getData());
			DMatrixRMaj S = new DMatrixRMaj(eigen.getV().getData());
			
			System.out.println("Orth(S): " + MatrixFeatures.isOrthogonal(S, 0.000001d));

	        eq.alias(D, "D");
	        eq.alias(S, "S");
	        eq.process("K = S * D.^(1./2) * S'");
	        eq.process("SQR = K * K");
	        
	        System.out.println("[A] " + A);
	        System.out.println("[SQR] " + eq.lookupDDRM("SQR"));
		}
	
	}

}
