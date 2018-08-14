import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

public class TestMe8 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		
		eq.process("A = [ " +
							"  2,  1;" +
							"  0,  1;" +
							"  4,  1 " +
						"]");
		
		DMatrixRMaj A = eq.lookupMatrix("A");
		
		eq.process("TA = A' * A");
		DMatrixRMaj TA = eq.lookupMatrix("TA");
		
		eq.process("AT = A * A'");
		DMatrixRMaj AT = eq.lookupMatrix("AT");
	
		System.out.println("Rank(A' A): " + MatrixFeatures.rank(TA));
		System.out.println("[A' A] ==> " + TA);
	
		System.out.println("Rank(A A'): " + MatrixFeatures.rank(AT));
		System.out.println("[A A'] ==> " + AT);
		
		
		
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
		
		System.out.println("W ==> " + W);
		System.out.println("V ==> " + V);
		System.out.println("U ==> " + U);
		
		System.out.println(eq.lookupMatrix("K"));

	}
}
