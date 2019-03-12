package optimization;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.linsol.LinearSolverDense;

public class LeastSquareApprox2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// (0, 0, 0), (0, 1, 1), (1, 2, 2), (2, 1, 0), (2, 2, 2)
		// (1, 6, 12.05, 5.03, 13.08)
		// A + Bx + Cy + Dz = k
		// ==========
		// A + 0B + 0C + 0D = 1
		// A + 0B + 1C + 1D = 6
		// A + 1B + 2C + 2D = 12.05
		// A + 2B + 1C + 0D = 5.03
		// A + 2B + 2C + 2D = 13.08
		
		
		Equation eq = new Equation();
		eq.process("A = [" +
						" 1, 0, 0, 0;" +
						" 1, 0, 1, 1;" +
						" 1, 1, 2, 2;" +
						" 1, 2, 1, 0;" +
						" 1, 2, 2, 2 " +
						"]");
		
		eq.process("b = [ 1; 6; 12.05; 5.03; 13.08 ]"); 
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		DMatrixRMaj x = new DMatrixRMaj(A.numCols, 1);
        DMatrixRMaj y = eq.lookupDDRM("b");
		
		// x = [ A; B; C; D ]
		// Ax = b is not solvable!!!!!!!!!
		
		LinearSolverDense<DMatrixRMaj> x2 =  LinearSolverFactory_DDRM.leastSquares(A.numRows, A.numCols);
		x2.setA(A);
		
		CommonOps_DDRM.fill(x, 0);
		x2.solve(y, x);
		
		System.out.println(x);
		
		eq.alias(x, "x");
		
		// (0, 0, 0), (0, 1, 1), (1, 2, 2), (2, 1, 0), (2, 2, 2)
		// (1, 6, 12.05, 5.03, 13.08)
		// =============
		// 0.998 + 0(1.036) + 0(1.960) + 0(3.046) = 0.998
		// 0.998 + 0(1.036) + 1(1.960) + 1(3.046) = 6.004
		// 0.998 + 1(1.036) + 2(1.960) + 2(3.046) = 12.046
		// 0.998 + 2(1.036) + 1(1.960) + 0(3.046) = 5.03
		// 0.998 + 2(1.036) + 2(1.960) + 2(3.046) = 13.082
		
		eq.process("B = A * x");
		
		DMatrixRMaj B = eq.lookupDDRM("B");
		
		System.out.println(B);
		
		double avg = (double) (y.get(0, 0) + y.get(1, 0) + y.get(2, 0) + y.get(3, 0) + y.get(4, 0)) /
						y.numRows;
		
		// Calculating R^2 = 1 - ∑( (measured[i] - compared[i])^2 ) / ∑( (measured[i] - mean)^2 )
		// if R^2 close to 1, the compared curve is closely matched with the measured curve
		// If R^2 close to 0, the compared curve is no resemble with the measured curve
		double r_sq = 1 - (double) ((B.get(0, 0) - y.get(0, 0)) * (B.get(0, 0) - y.get(0, 0)) + 
									(B.get(1, 0) - y.get(1, 0)) * (B.get(1, 0) - y.get(1, 0)) + 
									(B.get(2, 0) - y.get(2, 0)) * (B.get(2, 0) - y.get(2, 0)) +
									(B.get(3, 0) - y.get(3, 0)) * (B.get(3, 0) - y.get(3, 0)) +
									(B.get(4, 0) - y.get(4, 0)) * (B.get(4, 0) - y.get(4, 0))) 
									/
									((y.get(0, 0) - avg) * (y.get(0, 0) - avg) + 
									 (y.get(1, 0) - avg) * (y.get(1, 0) - avg) + 
									 (y.get(2, 0) - avg) * (y.get(2, 0) - avg) +
									 (y.get(3, 0) - avg) * (y.get(3, 0) - avg) +
									 (y.get(4, 0) - avg) * (y.get(4, 0) - avg));
		
		System.out.println(r_sq);				
	}

}
