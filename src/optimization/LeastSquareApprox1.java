package optimization;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.linsol.LinearSolverDense;

public class LeastSquareApprox1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// (0, 6), (1, 0), (2, 0)
		// C + Dx = y
		// ==========
		// C + 0D = 6
		// C + 1D = 0
		// C + 2D = 0
		
		Equation eq = new Equation();
		eq.process("A = [" +
						" 1, 0;" +
						" 1, 1;" +
						" 1, 2 " +
						"]");
		
		eq.process("b = [ 6; 0; 0 ]"); 
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		DMatrixRMaj x = new DMatrixRMaj(A.numCols, 1);
        DMatrixRMaj y = eq.lookupDDRM("b");
		
		// x = [ C; D ]
		// Ax = b is not solvable!!!!!!!!!
		
		LinearSolverDense<DMatrixRMaj> x2 =  LinearSolverFactory_DDRM.leastSquares(A.numRows, A.numCols);
		x2.setA(A);
		
		CommonOps_DDRM.fill(x, 0);
		x2.solve(y, x);
		
		System.out.println(x);
		
		eq.alias(x, "x");
		
		// C = 5, D = -3
		// (0, 5), (1, 2), (2, -1)
		// =============
		// 5 + 0(-3) = 5
		// 5 + 1(-3) = 2
		// 5 + 2(-3) = -1
		
		eq.process("B = A * x");
		
		System.out.println(eq.lookupDDRM("B"));
		
		// Calculating R^2 = 1 - ∑( (measured[i] - compared[i])^2 ) / ∑( (measured[i] - mean)^2 )
		// if R^2 close to 1, the compared curve is closely matched with the measured curve
		// If R^2 close to 0, the compared curve is no resemble with the measured curve
		double r_sq = 1 - (double) ((5 - 6) * (5 - 6) + (0 - 2) * (0 - 2) + (0 - -1) * (0 - -1)) /
						((6 - 2) * (6 - 2) + (0 - 2) * (0 - 2) + (0 - 2) * (0 - 2));
		
		System.out.println(r_sq);				
	}

}
