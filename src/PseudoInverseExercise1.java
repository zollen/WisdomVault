import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;
import org.ejml.simple.ops.SimpleOperations_DDRM;

public class PseudoInverseExercise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		
		eq.process("A = [ " +
							"  2,  1;" +
							"  0,  1;" +
							"  4,  1 " +
						"]");

/*		eq.process("A = [ " +
  							" 2,  1,  3;" +
  							" 1,  0, -1 " +
 						" ]");  */
		
		DMatrixRMaj A = eq.lookupMatrix("A");
		System.out.println("Rank(A): " + MatrixFeatures.rank(A));

		DMatrixRMaj K = null;
		
		eq.process("K = A' * A");
		K = eq.lookupMatrix("K");
		System.out.println("Rank(A' A): " + MatrixFeatures.rank(K) + " ==> " + K);
		
		eq.process("K = A * A'");
		K = eq.lookupMatrix("K");
		System.out.println("Rank(A A'): " + MatrixFeatures.rank(K) + " ==> " + K);

		
		SimpleSVD<SimpleMatrix> svd = new SimpleSVD<SimpleMatrix>(A, false);
		SimpleMatrix W = svd.getW();
		SimpleMatrix U = svd.getU();
		SimpleMatrix V = svd.getV();
		eq.alias(W, "W");
		eq.alias(U, "U");
		eq.alias(V, "V");
		
		W.set(0,  0, 1 / W.get(0, 0));
		W.set(1, 1, 1 / W.get(1, 1));
		
		eq.process("K = V * W' * U'");
		
		System.out.println(" V inv(W) U' ===> " + eq.lookupMatrix("K"));
		
		
		DMatrixRMaj B = new DMatrixRMaj(A.numRows, A.numCols);
		SimpleOperations_DDRM op = new SimpleOperations_DDRM();
		op.pseudoInverse(A, B);
		
		System.out.println("PseudoInv(A) ===> " + B);

	}
}
