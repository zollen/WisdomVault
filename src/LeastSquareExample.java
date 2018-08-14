import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.linsol.LinearSolverDense;

public class LeastSquareExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [1, 1, 1; 2, 4, 1; 3, 2, 1 ]");
		eq.process("b = [ 5; 2; 1 ]");
	
		DMatrixRMaj A = eq.lookupMatrix("A");
		DMatrixRMaj b = eq.lookupMatrix("b");
		DMatrixRMaj x = new DMatrixRMaj(A.numCols, 1);
		
		eq.process("P = A * inv(A' * A) * A' ");
		eq.process("p = P * b");
		DMatrixRMaj P = eq.lookupMatrix("P");
		DMatrixRMaj p = eq.lookupMatrix("p");
			
		System.out.println("<=== Projection Matrix ===>");
		System.out.println(P);
		
		System.out.println("<=== p : Proj_a(b) ===>");
		System.out.println(p);
		
		
		LinearSolverDense<DMatrixRMaj> x2 =  LinearSolverFactory_DDRM.leastSquares(A.numRows, A.numCols);
		x2.setA(A);
		
		CommonOps_DDRM.fill(x, 0);
		x2.solve(b, x);
		
		System.out.println("<=== new x with ejml LeastSquares ===>");
		System.out.println(x);
		
		System.out.println("<=== new x ===>");
		eq.process("K = inv(A' * A) * A' * b");
		System.out.println(eq.lookupMatrix("K"));

		
		DMatrixRMaj y = new DMatrixRMaj(A.numRows, 1);
		CommonOps_DDRM.mult(A, x, y);
		
		System.out.println("<=== Verification of new x ===>");
		System.out.println(y);
		
		
		
		eq.process("E = b - p");
		DMatrixRMaj E = eq.lookupMatrix("E");
		
		System.out.println("<=== E ===>");
		System.out.println(E);
		
		
		
		DMatrixRMaj I = CommonOps_DDRM.identity(P.numRows, P.numCols);
		DMatrixRMaj c = new DMatrixRMaj(P.numRows, P.numCols);
		CommonOps_DDRM.subtract(I, P, c);
		
		DMatrixRMaj k = new DMatrixRMaj(P.numRows, 1);
		CommonOps_DDRM.mult(c, b, k);
		
		System.out.println("<=== Verification of E = (I - P) * b ===>");
		System.out.println(k);
		
		System.out.println("RANK: " + MatrixFeatures.rank(A));
	}
}
