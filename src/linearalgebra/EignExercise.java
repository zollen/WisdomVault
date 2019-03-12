package linearalgebra;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class EignExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ pow(e, 1), pow(e, 3); pow(e, 2), pow(e, 2) ]");
		eq.process("Q = inv(A)");
		DMatrixRMaj A = eq.lookupDDRM("A");

		System.out.println(A);
		
		DMatrixRMaj Q = eq.lookupDDRM("Q");

		System.out.println(Q);
		
		
/*
		EigenDecomposition<DMatrixRMaj> eigen = DecompositionFactory.eig(A.numCols, false);
		
		System.out.println("DECOMP: " + eigen.decompose(A));
		int size = eigen.getNumberOfEigenvalues();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < size; i++) {
			if (builder.length() > 0)
				builder.append(", ");
			builder.append(Math.round(eigen.getEigenvalue(i).real));	
		}
		
		System.out.println("Eigen Values(A): " + builder.toString());
*/		
/*		
		eq.process("S = [ -1, 1; 1, 1 ]");
		DMatrixRMaj S = eq.lookupDDRM("S");
		
		System.out.println(S);
		
		eq.process("D = inv(S) * A * S");
		
		System.out.println(eq.lookupDDRM("D"));
			
	
		
		eq.process("K = (A - D * D * eye(A))");
		DMatrixRMaj K = eq.lookupDDRM("K");
		
		System.out.println(K);
		
		System.out.println("Det(K): " + CommonOps_DDRM.det(K));
		System.out.println("Rank(K): " + MatrixFeatures.rank(K));
*/		
	
		eq.process("K = A - (-1 * eye(A))");
		
		DMatrixRMaj K = eq.lookupDDRM("K");
	
		System.out.println("<=== A ===>");
		System.out.println("RANK(A): " + MatrixFeatures.rank(A));
		System.out.println("DET(A): " + CommonOps_DDRM.det(A));

		System.out.println("<=== A - (1)I ===>");
		System.out.println("RANK(A - (1)I): " + MatrixFeatures.rank(K));
		System.out.println(K);
		
	
		
		DMatrixRMaj reduced = new DMatrixRMaj(K.numRows, K.numCols);
		CommonOps_DDRM.rref(K, K.numCols, reduced);
		
		System.out.println(reduced);

	}
	
}
