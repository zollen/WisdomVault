package linearalgebra;
import java.text.DecimalFormat;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class MarkovExercise1 {
	
	private static final long ROUND = 9999999;


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
							"0,    0.05, 0.1, 0;" + 
							"0.75, 0.2,  0.4, 0.15;" + 
							"0.2,  0.3,  0.3, 0.3;" +
							"0.05, 0.45, 0.2, 0.55" + 
						"]");
		eq.process("X = [ 0; 1; 0; 0 ]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		RealMatrix AA = MatrixUtils.createRealMatrix(MatrixFeatures.array(A));

		DecimalFormat ff = new DecimalFormat("#,##0.000");
		
		EigenDecomposition eigen = new EigenDecomposition(AA);
		double[] vals = eigen.getRealEigenvalues();
		
		DMatrixRMaj D = new DMatrixRMaj(eigen.getD().getData());
		DMatrixRMaj S = new DMatrixRMaj(eigen.getV().getData());
		eq.alias(D, "D");
		eq.alias(S, "S");
		
		S.set(0, 0, S.get(0, 0) * -1);
		S.set(1, 0, S.get(1, 0) * -1);
		S.set(2, 0, S.get(2, 0) * -1);
		S.set(3, 0, S.get(3, 0) * -1);
		
		{
			int i = 0;
			DMatrixRMaj[] vecs = new DMatrixRMaj[vals.length];
			StringBuilder builder = new StringBuilder();
			for (double val : vals) {

				eq.alias(val, "LAMDA" + i);
				double[] data = new double[eigen.getEigenvector(i).getDimension()];
				for (int k = 0; k < data.length; k++) {
					
					if (Math.round(val) == 1)
						data[k] = Math.abs(eigen.getEigenvector(i).getEntry(k));
					else
						data[k] = eigen.getEigenvector(i).getEntry(k);
				}
				RealMatrix tmp = MatrixUtils.createColumnRealMatrix(data);
				vecs[i] = new DMatrixRMaj(tmp.getData());
				eq.alias(vecs[i], "V" + i);

				builder.append(ff.format(val) + " ==> " + 
								ff.format(data[0]) + ", " + ff.format(data[1]) + ", " + 
								ff.format(data[2]) + ", " + ff.format(data[3]) + "\n");
				i++;
			}
			
			eq.process("K = [ S, X ]");

			DMatrixRMaj K = eq.lookupDDRM("K");
			CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);

			eq.process("C0 = K(0,4)");
			eq.process("C1 = K(1,4)");
			eq.process("C2 = K(2,4)");
			eq.process("C3 = K(3,4)");
			
			System.out.println("DET(A): " + CommonOps_DDRM.det(A));
			System.out.println("RANK(A): " + MatrixFeatures.rank(A));
			System.out.println("TRACE(A): " + CommonOps_DDRM.trace(A));
			System.out.println("Eigen Values(A):\n" + builder.toString());
			System.out.println("Has Complex(A): " + eigen.hasComplexEigenvalues());
		}
		
		
		System.out.println(A);
		
		eq.alias(ROUND, "n");
	
		{
			eq.process(
				"ANS = (C0 * pow(LAMDA0, n) * V0) + (C1 * pow(LAMDA1, n) * V1) + (C2 * pow(LAMDA2, n) * V2) + (C3 * pow(LAMDA3, n) * V3)");
			
			System.out.println(eq.lookupDDRM("ANS"));
			
			eq.process("ANS2 = C0 * V0");
			
			System.out.println("STEADY STATE: " + eq.lookupDDRM("ANS2"));
		}
		
		{
			// Incorporate A + B + C + D = 1 into the first equation
			eq.process("K = [ " + 
							"0,    1.05,  1.1, 	   1,  1;" +
							"0.75, -0.8,  0.4,  0.15,  0;" +
							"0.2,   0.3, -0.7,   0.3,  0;" +
							"0.05, 0.45,  0.2, -0.45,  0" +			
					" ]");
			
			/*** OR ***/
			
			eq.process("K = [ " + 
					"-1,   0.05,  0.1, 	   0,  0;" +
					"0.75, -0.8,  0.4,  0.15,  0;" +
					"0.2,   0.3, -0.7,   0.3,  0;" +
					"0.05, 0.45,  0.2, -0.45,  0;" +
					"    1,    1,    1,    1,  1" +
			" ]");
			
			DMatrixRMaj K = eq.lookupDDRM("K");
		
			CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);
			
			System.out.println(K);

		}
		
	}

}
