package linearalgebra;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class TestMeZ {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [" + 
						" 1, 1;" + 
						" 1, 4;" + 
						" 1, 3;" + 
						" 1, 2 " +
					"]"); 
		
		eq.process("T = [" +
						" 1, 0;" +
						" 1, 0;" +
						" 1, 0;" +
						" 1, 0 " +
					"]");
		


		DMatrixRMaj A = eq.lookupDDRM("A");
		

		SingularValueDecomposition svd = new SingularValueDecomposition(
				MatrixUtils.createRealMatrix(MatrixFeatures.array(A)));

		DMatrixRMaj S = new DMatrixRMaj(svd.getS().getData());
		DMatrixRMaj U = new DMatrixRMaj(svd.getU().getData());
		DMatrixRMaj V = new DMatrixRMaj(svd.getV().getData());

		eq.alias(S, "S");
		eq.alias(U, "U");
		eq.alias(V, "V");
		
		System.out.println("S: " + S);
		System.out.println("U: " + U);
		System.out.println("V: " + V);
		
		// inv(S) * is not necessary in the following 
		// since all classifications would scale with inv(S) anyway
		
		eq.process("K = inv(S) * U' * A");		
		System.out.println("inv(S) * U' * A = " + eq.lookupDDRM("K"));		
		
		eq.process("K = inv(S) * U' * T");
		System.out.println("inv(S) * U' * T = " + eq.lookupDDRM("K"));	
	}

}
