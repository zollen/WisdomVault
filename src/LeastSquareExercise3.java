import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.linsol.LinearSolverDense;

public class LeastSquareExercise3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		// (0, 2, 1), (0, 4, 3), (1, 4, 5), (1, 8, 6), (1, 8, 10), (4, 8, 14), (5, 9, 13)
		eq.process("A = [" +
						" 0, 0, 1, 1,  1,  4,  5;" +
						" 2, 4, 4, 8,  8,  8,  9;" +
						" 1, 3, 5, 6, 10, 14, 13 " +
					"]");

		DMatrixRMaj A = eq.lookupDDRM("A");

		// A + Bx + Cy = z
		// ==========
		// A + 0B + 2C = 1
		// A + 0B + 4C = 3
		// A + 1B + 4C = 5
		// A + 1B + 8C = 6
		// A + 1B + 8C = 10
		// A + 4B + 8C = 14
		// A + 5B + 9C = 13
		
		eq.process("AXY = [" +
							" 1, 0, 2;" +
							" 1, 0, 4;" +
							" 1, 1, 4;" +
							" 1, 1, 8;" +
							" 1, 1, 8;" +
							" 1, 4, 8;" +
							" 1, 5, 9 " +
						"]");
		
		eq.process("BXY = [ 1; 3; 5; 6; 10; 14; 13 ]");
	
		DMatrixRMaj AXY = eq.lookupDDRM("AXY");
		DMatrixRMaj yXY = eq.lookupDDRM("BXY");
		DMatrixRMaj xXY = new DMatrixRMaj(AXY.numCols, 1);
		
		LinearSolverDense<DMatrixRMaj> x1 =  LinearSolverFactory_DDRM.leastSquares(AXY.numRows, AXY.numCols);
		x1.setA(AXY);
		x1.solve(yXY, xXY);
		
		System.out.println(xXY);
		eq.alias(xXY, "xXY");
		
		eq.process("K = AXY * xXY");
		
		DMatrixRMaj K1 = eq.lookupDDRM("K");
		
		DMatrixRMaj Pln1 = new DMatrixRMaj(3, K1.numRows);
		for (int i = 0; i < Pln1.numCols; i++) {
			Pln1.set(0, i, A.get(0, i));
			Pln1.set(1, i, A.get(1, i));
			Pln1.set(2, i, K1.get(i, 0));
		}
		
		System.out.println("PLANE#1: "  + Pln1);
		
		// (0, 2, 1), (0, 4, 3), (1, 4, 5), (1, 8, 6), (1, 8, 10), (4, 8, 14), (5, 9, 13)
		// A + Bx + Cy = z
		// ==========
		
		// A + 2B + 1C = 0
		// A + 4B + 3C = 0
		// A + 4B + 5C = 1
		// A + 8B + 6C = 1
		// A + 8B + 10C = 1
		// A + 8B + 14C = 4
		// A + 9B + 13C = 5
		
		eq.process("AYZ = [" +
				" 1, 2, 1;" +
				" 1, 4, 3;" +
				" 1, 4, 5;" +
				" 1, 8, 6;" +
				" 1, 8, 10;" +
				" 1, 8, 14;" +
				" 1, 9, 13 " +
			"]");

		eq.process("BYZ = [ 0; 0; 1; 1; 1; 4; 5 ]");

		
		
		DMatrixRMaj AYZ = eq.lookupDDRM("AYZ");
		DMatrixRMaj yYZ = eq.lookupDDRM("BYZ");
		DMatrixRMaj xYZ = new DMatrixRMaj(AYZ.numCols, 1);
		
		LinearSolverDense<DMatrixRMaj> x2 =  LinearSolverFactory_DDRM.leastSquares(AYZ.numRows, AYZ.numCols);
		x2.setA(AYZ);
		x2.solve(yYZ, xYZ);
		
		System.out.println(xYZ);
		eq.alias(xYZ, "xYZ");
		
		eq.process("K = AYZ * xYZ");
		
		DMatrixRMaj K2 = eq.lookupDDRM("K");
		
		DMatrixRMaj Pln2 = new DMatrixRMaj(3, K2.numRows);
		for (int i = 0; i < Pln2.numCols; i++) {
			Pln2.set(0, i, K2.get(i, 0));
			Pln2.set(1, i, A.get(1, i));
			Pln2.set(2, i, A.get(2, i));
		}
		
		System.out.println("PLANE#2: "  + Pln2);
		
		SingularValueDecomposition svd = new SingularValueDecomposition(
				MatrixUtils.createRealMatrix(MatrixFeatures.array(A)));
		
		DMatrixRMaj U = new DMatrixRMaj(svd.getU().getData());
		
		System.out.println("EigenVectors: " + U);
		
		System.out.println("Best Fit Plane: 0.829x + 0.322y - 0.457z = 0");
		
	}
	
	public static double variance(DMatrixRMaj avg, DMatrixRMaj A) {
		
		double sum = 0d;
		
		for (int i = 0; i < A.numCols; i++) {
					
			DMatrixRMaj target = new DMatrixRMaj(3, 1);
				
			target.set(0, 0, A.get(0, i));
			target.set(1, 0, A.get(1, i));
			target.set(2, 0, A.get(2, i));
			
			double dist = dist(target, avg);
				
			sum += Math.abs(dist * dist);
		}
		
		return sum / A.numCols;
	}
	
	public static double dist(DMatrixRMaj a, DMatrixRMaj b) {
		
		return Math.sqrt(
				(b.get(0, 0) - a.get(0, 0)) * (b.get(0, 0) - a.get(0, 0)) + 
				(b.get(1, 0) - a.get(1, 0)) * (b.get(1, 0) - a.get(1, 0)) +
				(b.get(2, 0) - a.get(2, 0)) * (b.get(2, 0) - a.get(2, 0))
				);	
	}
	
	public static DMatrixRMaj avg(DMatrixRMaj A) {
		
		DMatrixRMaj avg = new DMatrixRMaj(A.numRows, 1);
		
		for (int row = 0; row < A.numRows; row++) {
			
			double val = 0d;
			for (int col = 0; col < A.numCols; col++) {
				val += A.get(row, col);
			}
			
			avg.set(row, 0, val / A.numCols);
		}
		
		
		return avg;
				
	}
	
}
