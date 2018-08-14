import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class SVDExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
							"       0,  1,  1;" + 
							" sqrt(2),  2,  0;" +
							"       0,  1,  1 " +
						"]");

		DMatrixRMaj A = eq.lookupMatrix("A");
		
		DMatrixRMaj c1 = new DMatrixRMaj(A.numCols, A.numCols);
		CommonOps_DDRM.multInner(A, c1);
		eq.alias(c1, "C1");
		
		DMatrixRMaj c2 = new DMatrixRMaj(A.numRows, A.numRows);
		CommonOps_DDRM.multOuter(A, c2);
		eq.alias(c2, "C2");
		
		System.out.println("Rank(A): " + MatrixFeatures.rank(A));
		System.out.println("DefPos(A): "  + MatrixFeatures.isPositiveDefinite(A));
		
		eq.process("S = [ sqrt(8), 0, 0; 0, sqrt(2), 0; 0, 0, 0 ]");
		
		{
	
			SingularValueDecomposition svd = new SingularValueDecomposition(
					MatrixUtils.createRealMatrix(MatrixFeatures.array(A)));


			DMatrixRMaj S = new DMatrixRMaj(svd.getS().getData());
			DMatrixRMaj U = new DMatrixRMaj(svd.getU().getData());
			DMatrixRMaj V = new DMatrixRMaj(svd.getV().getData());
			eq.alias(U, "U");
			eq.alias(V, "V");
			eq.alias(S, "S");
	
			eq.process("K = U * S * V'");
			
			System.out.println("Using SingularValueDecomposition");
		//	System.out.println("[V] " + V);
		//	System.out.println("[U] " + U);
			System.out.println("RESULT: " + eq.lookupMatrix("K"));
		}
		
		{
			eq.process("U = [ 1, 1, 1; 2, -1, 0; 1, 1, -1 ]");
			eq.process("V = [ sqrt(2), -1, 3; 3, 0, -3./sqrt(2); 1, sqrt(2), 3./sqrt(2) ]");
			
			DMatrixRMaj U = eq.lookupMatrix("U");
			DMatrixRMaj V = eq.lookupMatrix("V");
			
			U = unit(U);
			V = unit(V);
			eq.alias(U, "U");
			eq.alias(V, "V");
			
			eq.process("K = U * S * V'");
			
			System.out.println("Using EigenComp(A A') and 1/sigma * A' u_i = v_i");
			System.out.println("RESULT: " + eq.lookupMatrix("K"));
			
			
			
		}
		
		{
			EigenDecomposition eigenV = new EigenDecomposition(MatrixUtils.createRealMatrix(MatrixFeatures.array(c1)));
			DMatrixRMaj V = new DMatrixRMaj(eigenV.getV().getData());
			
			eq.alias(V, "V");
			
			eq.process("U1 = 1./sqrt(8) * A * V(0:2,0)");
			eq.process("U2 = 1./sqrt(2) * A * V(0:2,1)");
			eq.process("U3 = [ 1./sqrt(2); 0; -1./sqrt(2) ]");
					
			eq.process("U = [ U1, U2, U3 ]");
			
			DMatrixRMaj U = eq.lookupMatrix("U");
			U = unit(U);
			eq.alias(U, "U");
			
			eq.process("K = U * S * V'");
			
			System.out.println("Using EigenComp(A' A).getV() and 1/sigma * A v_i = u_i");
			System.out.println("RESULT: " + eq.lookupMatrix("K"));	
		
		}
	
	}
	
	private static DMatrixRMaj unit(DMatrixRMaj A) {
		
		DMatrixRMaj uVec = new DMatrixRMaj(A.numRows, A.numCols);
		
		for (int i = 0; i < A.numCols; i++) {
			
			double tot = 0d;
			for (int j = 0; j < A.numRows; j++) {
				tot += A.get(j, i) * A.get(j, i);
			}
			
			tot = Math.sqrt(tot);
			
			for (int j = 0; j < A.numRows; j++) {
				uVec.set(j, i, (double)(A.get(j, i) / tot));
			}
		}
		
		return uVec;	
	}

}
