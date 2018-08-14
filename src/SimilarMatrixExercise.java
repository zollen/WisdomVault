import java.util.List;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class SimilarMatrixExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("B = [ " + 
						"  0,  1,  0;" +
						"  1,  0,  0;" +
						"  1, -1,  3" +
					"]");
		eq.process("P = [" +
						" 0,  0,  1;" +
						" 0,  1,  1;" +
						" 1,  1,  1"  +
					"]");
		eq.process("PIV = inv(P)");
		eq.process("D = [" +
						"  3,  2,  2;" +
						"  0, -1,  0;" +
						"  0,  1,  1" +
					"]");
		
		DMatrixRMaj B = eq.lookupMatrix("B");
		DMatrixRMaj D = eq.lookupMatrix("D");
		
		EigenMatrix BB = new EigenMatrix(new EigenDecomposition(MatrixUtils.createRealMatrix(MatrixFeatures.array(B))));
		List<EigenVector> vec1 = BB.getVectors();
		for (EigenVector vec : vec1) {
			System.out.println(vec);
		}
		
		EigenMatrix DD = new EigenMatrix(new EigenDecomposition(MatrixUtils.createRealMatrix(MatrixFeatures.array(D))));
		List<EigenVector> vec2 = DD.getVectors();
		for (EigenVector vec : vec2 ) {
			System.out.println(vec);
		}
		
		eq.process("VB = [ 1, 1, 0; 2, -2, -1; 0, 0, 1 ]");
		eq.process("VD = [ 1, 0, 1; 1, -4,  2; 1, 0, 0 ]");
		
		eq.process("P = VB * inv(VD)");
		eq.process("K = P * B * inv(P)");
		
		System.out.println(eq.lookupMatrix("P"));
		System.out.println(eq.lookupMatrix("K"));
		
		

/*
		eq.process("KING = [" +
						"-3,  0,  0,  1,  0,  0,  0,  0,  0,   0;" +
						" 1,  0,  0, -3,  0,  0,  0,  0,  0,   0;" +
						" 2, -1,  1,  0, -1,  0,  0,  0,  0,   0;" +
						" 0, -1,  0,  2, -1,  1,  0,  0,  0,   0;" +
						" 0, -1,  0,  0,  1,  0,  2, -4,  1,   0;" +
						" 2,  0,  1,  0,  0, -1,  0,  0,  0,   0;" +
						" 0,  0, -1,  2,  0,  1,  0,  0,  0,   0;" +
						" 0,  0, -1,  0,  0,  1,  2,  0, -2,   0" +
						"]");
						
		DMatrixRMaj a = eq.lookupMatrix("KING");
		
		DMatrixRMaj reduced = new DMatrixRMaj(a.numRows, a.numCols);
		
		CommonOps_DDRM.rref(a, a.numCols - 1, reduced);

		System.out.println(reduced);
*/		
		
	}

}
