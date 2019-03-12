package linearalgebra;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.NormOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;

public class ConditionNumberExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		
		eq.process("A = [ " +
							" 1,  2,  3,  4,  5,  6,  7,  9;" +
							" 2,  3,  4,  5,  6,  7,  8, 10;" +
							" 3,  4,  5,  6,  7,  8, 10, 11;" +
							" 4,  5,  6,  7,  8, 10, 11, 12;" +
							" 5,  6,  7,  8, 10, 11, 12, 13;" +
							" 6,  7,  8, 10, 11, 12, 13, 14;" +
							" 7,  8, 10, 11, 12, 13, 14, 15;" +
							" 8, 10, 11, 12, 13, 14, 15, 16 " +
						"]");
		eq.process("M = 1 + (A * 1/1000)");	
		DMatrixRMaj M = eq.lookupDDRM("M");
		
		eq.process("bo = [ 1; 1; 1; 1; 1; 1; 1; 1 ]");
		eq.process("K = [ M, bo ]");
		eq.process("K = rref(K)");
		eq.process("Ko = K(:,8)'");
		System.out.println(eq.lookupDDRM("Ko"));
	
	
		// Now we change one constant by 1 part of 1000 and ask the same question
		
		eq.process("be = [ 0.999; 1; 1; 1; 1; 1; 1; 1 ]");
		eq.process("K = [ M, be ]");	
		eq.process("K = rref(K)");
		eq.process("Ke = K(:,8)'");
		System.out.println(eq.lookupDDRM("Ke"));
		
		SingularValueDecomposition<DMatrixRMaj> svd = DecompositionFactory_DDRM.svd(M.numRows, M.numCols, false, false, true);
		System.out.println("SVD(M): " + svd.decompose(M));
		DMatrixRMaj S = svd.getW(null);
		eq.alias(S, "D");

		eq.process("CN = D(0,0) / D(7,7)");
		
		System.out.println("Condition Number: " + eq.lookupDouble("CN"));
		System.out.println("This matrix is ill-conditioned because the condition number is too large!!");
		
		DMatrixRMaj Ko = eq.lookupDDRM("Ko");
		DMatrixRMaj Ke = eq.lookupDDRM("Ke");
		DMatrixRMaj Bo = eq.lookupDDRM("bo");
		DMatrixRMaj Be = eq.lookupDDRM("be");
		
		DMatrixRMaj nom = new DMatrixRMaj(Ko.numRows, Ko.numCols);
		CommonOps_DDRM.subtract(Ke, Ko, nom);
		
		DMatrixRMaj denom = new DMatrixRMaj(Bo.numRows, Bo.numCols);
		CommonOps_DDRM.subtract(Be, Bo, denom);
		
		System.out.println("Relative Error: " + (NormOps_DDRM.normP1(nom)/NormOps_DDRM.normP1(Ko)) / (NormOps_DDRM.normP1(denom)/NormOps_DDRM.normP1(Bo)));
		
	}

}
