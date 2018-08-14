import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.decomposition.LUDecomposition;
import org.ejml.interfaces.linsol.LinearSolverDense;


public class LUDecompositionExercise {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 1, 2, 3, 4; 2, 1, 2, 1; 5, 3, 1, 5; 3, 1, 3, 1 ]");
		DMatrixRMaj A = eq.lookupMatrix("A");
		
		System.out.println(A);
		System.out.println("Det(A): " + CommonOps_DDRM.det(A));
		
		LinearSolverDense<DMatrixRMaj> solver = LinearSolverFactory_DDRM.lu(A.numRows);
		
		solver.setA(A);
		
		LUDecomposition<DMatrixRMaj> lu = solver.getDecomposition();
		
		DMatrixRMaj U = lu.getUpper(null);
		DMatrixRMaj L = lu.getLower(null);
		DMatrixRMaj P = lu.getRowPivot(null);
		
		
		System.out.println("<==== U =====>");
		eq.alias(U, "U");
		System.out.println(U);
		
		System.out.println("<==== L =====>");
		eq.alias(L, "L");
		System.out.println(L);
		
		System.out.println("<==== P =====>");
		eq.alias(P, "P");
		System.out.println(P);
		
		
		eq.process("Z = inv(P) * L * U");
		DMatrixRMaj Z = eq.lookupMatrix("Z");
		System.out.println(Z);
		
		System.out.println("A = Z: " + MatrixFeatures.isIdentical(A, Z, 0.00000001));
	}

}
