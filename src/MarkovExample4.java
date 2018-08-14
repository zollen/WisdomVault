import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class MarkovExample4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
						"  0.7,  0.25,  0.05;" + 
						"  0.2,  0.65,  0.05;" +
						"  0.1,   0.1,   0.9 " +
						"]");
		
		eq.process("X = [ 400000; 200000; 100000 ]");
		
		eq.process("K = A * X");
		
		DMatrixRMaj K = eq.lookupMatrix("K");
		System.out.println(K);
		
		eq.process("K = A - eye(A)");
		eq.process("K = [ K, [ 0; 0; 0 ]]");
		eq.process("K = [ K ; [ 1, 1, 1, 1 ]]");
		
		K = eq.lookupMatrix("K");
		CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);
		System.out.println("STEADY: " + K);
		
	}

}
