package linearalgebra;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class MarkovExercise3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
						"  0.7,  0.2,  0.1;" + 
						"  0.1,  0.6,    0;" + 
						"  0.2,  0.2,  0.9" +
					"]");
		eq.process("X = [ 1; 0; 0 ]");

		DMatrixRMaj A = eq.lookupDDRM("A");
		RealMatrix AA = MatrixUtils.createRealMatrix(MatrixFeatures.array(A));

		EigenDecomposition eigen = new EigenDecomposition(AA);

		EigenMatrix matrix = new EigenMatrix(eigen);
		
		System.out.println("DET(A): " + CommonOps_DDRM.det(A));
		System.out.println("RANK(A): " + MatrixFeatures.rank(A));
		System.out.println("TRACE(A): " + CommonOps_DDRM.trace(A));
		System.out.println("Has Complex(A): " + eigen.hasComplexEigenvalues());
		System.out.println("Eigen Values(A): " + matrix.getValues());
		
		
		eq.process("K = A - eye(A)");
		eq.process("K = [ K, [ 0; 0; 0 ]]");
		eq.process("K = [ K ; [ 1, 1, 1, 1 ]]");
		
		DMatrixRMaj K = eq.lookupDDRM("K");
		
		System.out.println(K);
		
		CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);

		System.out.println("SteadyState ==> Rref(K): " + K);
	}

}
