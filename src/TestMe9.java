import java.util.List;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class TestMe9 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		
		eq.process("A = [ " +
						" 0.2, 0.4, 0.3;" +
						" 0.4, 0.2, 0.3;" +
						" 0.4, 0.4, 0.4 " +
					"]");
		
		DMatrixRMaj A = eq.lookupMatrix("A");
		
		System.out.println("DET(A): " + CommonOps_DDRM.det(A));
		System.out.println("RANK(A): " + MatrixFeatures.rank(A));
		System.out.println("TRACE(A): " + CommonOps_DDRM.trace(A));
	
		{
			EigenMatrix matrix = new EigenMatrix(new EigenDecomposition(MatrixUtils.createRealMatrix(MatrixFeatures.array(A))));
	
			List<EigenVector> vectors = matrix.getVectors();
			
			for (EigenVector vector : vectors) {
				System.out.println(vector);
			}
			
			eq.process("K = A - eye(A)");
			eq.process("K = [ K, [ 0; 0; 0 ]]");
			eq.process("K = [ K ; [ 1, 1, 1, 1 ]]");
			
			DMatrixRMaj K = eq.lookupMatrix("K");
			
			System.out.println(K);
			
			CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);

			System.out.println("SteadyState ==> Rref(K): " + K);
		
		}
		
		{
			eq.process("LEFT = inv(A' * A) * A'");
			eq.process("K = LEFT * A");
		
			System.out.println("LEFT INVERSE: " + eq.lookupMatrix("K"));
			
			eq.process("RIGHT = A' * inv(A * A')");
			eq.process("K = A * RIGHT");
		
			System.out.println("RIGHT INVERSE: " + eq.lookupMatrix("K"));
		}
		
	}

}
