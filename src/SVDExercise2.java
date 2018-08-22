import java.util.List;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class SVDExercise2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
							"  1,  1,  2,  3;" + 
							" -2, -1,  0,  2;" +
							"  0,  2, -1, -2" +
						"]");

		DMatrixRMaj A = eq.lookupDDRM("A");
		
		DMatrixRMaj c1 = new DMatrixRMaj(A.numCols, A.numCols);
		CommonOps_DDRM.multInner(A, c1);
		eq.alias(c1, "C1");
		
		DMatrixRMaj c2 = new DMatrixRMaj(A.numRows, A.numRows);
		CommonOps_DDRM.multOuter(A, c2);
		eq.alias(c2, "C2");
		
		EigenDecomposition eigenV = new EigenDecomposition(MatrixUtils.createRealMatrix(MatrixFeatures.array(c1)));
		DMatrixRMaj V = new DMatrixRMaj(eigenV.getV().getData());
		eq.alias(V, "V");
		
		EigenDecomposition eigenU = new EigenDecomposition(MatrixUtils.createRealMatrix(MatrixFeatures.array(c2)));
		DMatrixRMaj U = new DMatrixRMaj(eigenU.getV().getData());
		eq.alias(U, "U");
		
		System.out.println("Rank(A): " + MatrixFeatures.rank(A));
		System.out.println("DefPos(A): "  + MatrixFeatures.isPositiveDefinite(A));
		
		printEigens("A' A ", c1, eigenV);
		printEigens("A  A'", c2, eigenU);
			
		{			
			DMatrixRMaj S = new DMatrixRMaj(c2.numCols, c1.numCols); 
			CommonOps_DDRM.fill(S, 0d);
			
			EigenDecomposition eigen = eigenU;
			
			EigenMatrix mat = new EigenMatrix(eigen);
			List<EigenVector> vecs = mat.getVectors();
			int ii = 0;
			for (EigenVector vec : vecs) {
				S.set(ii, ii, Math.sqrt(vec.getValue()));
				ii++;
			}
			eq.alias(S, "S");

			
			ii = 0;
			for (EigenVector vec : vecs) {
				eq.alias(vec.getVector(), "U" + ii);
				if (S.get(ii, ii) != 0)
					eq.process("V" + ii + " = 1./" + S.get(ii, ii) + " * A' * U" + ii);	
				ii++;
			}
			
			eq.process("U = [ U0, U1, U2 ]");
			U = eq.lookupDDRM("U");
			U = unit(U);
			eq.alias(U, "U");
			
			eq.process("V3 = [ -1; 0; 2; -1 ]");
			eq.process("V = [ V0, V1, V2, V3 ]");
			
			V = eq.lookupDDRM("V");
			
			V = unit(V);
			eq.alias(V, "V");
			
			
			eq.process("K = U * S * V'");
			System.out.println("RESULT: " + eq.lookupDDRM("K"));	
			
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
	
	private static void printEigens(String title, DMatrixRMaj AA, EigenDecomposition eigen) {
		
		EigenMatrix matrix = new EigenMatrix(eigen);
		List<EigenVector> vectors = matrix.getVectors();
		
		System.out.println("<========= [ " + title + " ] ==========>");
		System.out.println(AA);
		
		for (EigenVector vector : vectors) {
			System.out.println(vector);
		}
		
		System.out.println("\n");
	}

}
