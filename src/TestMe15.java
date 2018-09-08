import java.text.DecimalFormat;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.linsol.svd.SolvePseudoInverseSvd_DDRM;
import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

public class TestMe15 {
	
	private static DecimalFormat formatter = new DecimalFormat("0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
							"   13./9,   11./9,   11./9,   13./9;" + 
							" -23./18, -25./18, -23./18, -25./18;" + 
							"    5./9,    7./9,    5./9,    7./9 " + 
						"]");

		DMatrixRMaj A = eq.lookupDDRM("A");
		System.out.println("Rank(A): " + MatrixFeatures.rank(A));
	
		eq.process("K1 = det(A * A')");
		System.out.println("det(A A'): " + formatter.format(eq.lookupDouble("K1")));
		
		eq.process("K2 = det(A' * A)");
		System.out.println("det(A' A): " + formatter.format(eq.lookupDouble("K2"))); 
		
		System.out.println("det(A A') <> 0 and det(A' A) = 0. Therefore this is a right inverse");
		
		SolvePseudoInverseSvd_DDRM svdi = new SolvePseudoInverseSvd_DDRM(5, 5);
		
		System.out.println("svdi(A): " + svdi.setA(A));
		
		DMatrixRMaj B = new DMatrixRMaj(5, 5);
		svdi.invert(B);
		
		System.out.println("svdi(A): " + B);
		
		SimpleMatrix m = SimpleMatrix.wrap(A);
		System.out.println("pseudo(A): " + m.pseudoInverse());
		
		SimpleSVD<SimpleMatrix> svd = new SimpleSVD<SimpleMatrix>(A, false);
		
		DMatrixRMaj W = svd.getW().getDDRM();
		DMatrixRMaj U = svd.getU().getDDRM();
		DMatrixRMaj V = svd.getV().getDDRM();
		
		DMatrixRMaj S = new DMatrixRMaj(W.numRows, W.numCols);
		for (int i = 0; i < W.numRows; i++) 
			for (int j = 0; j < W.numCols; j++)
				S.set(i, i, 1 / W.get(i, i));
			
		eq.alias(S, "S");
		eq.alias(V, "V");
		eq.alias(U, "U");
		
		eq.process("K = V * S' * U'");
		System.out.println(" V inv(S) U': " + eq.lookupDDRM("K"));
		
	}
	

}
