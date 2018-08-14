import java.text.DecimalFormat;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVectorFormat;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class MarkovExample {

	private static final int ROUND = 999999;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
							"0.1, 0.5, 0.4, 0.1;" + 
							"0.3, 0.1, 0.2, 0.1;" + 
							"0.4, 0.2, 0.1, 0.1;" + 
							"0.2, 0.2, 0.3, 0.7 " + 
						"]");
		eq.process("X = [ 0; 0; 0; 1000 ]");

		DMatrixRMaj A = eq.lookupMatrix("A");
		DMatrixRMaj X = eq.lookupMatrix("X");

		RealMatrix AA = MatrixUtils.createRealMatrix(MatrixFeatures.array(A));

		DecimalFormat ff = new DecimalFormat("#,##0.000");
		DecimalFormat ft = new DecimalFormat("#,##0.000000");
		RealVectorFormat f1 = new RealVectorFormat("", "", ", ", ff);
		EigenDecomposition eigen = new EigenDecomposition(AA);
		double[] vals = eigen.getRealEigenvalues();

		StringBuilder builder = new StringBuilder();
		DMatrixRMaj[] vecs = new DMatrixRMaj[vals.length];
		{
			int i = 0;
			for (double val : vals) {

				builder.append(ff.format(val) + " => " + f1.format(eigen.getEigenvector(i)) + "\n");
				eq.alias(val, "LAMDA" + i);

				double[] data = new double[eigen.getEigenvector(i).getDimension()];
				for (int k = 0; k < data.length; k++) {
					data[k] = eigen.getEigenvector(i).getEntry(k);
				}
				RealMatrix tmp = MatrixUtils.createColumnRealMatrix(data);

				vecs[i] = new DMatrixRMaj(tmp.getData());
				eq.alias(vecs[i], "V" + i);

				i++;
			}
		}

		DMatrixRMaj D = new DMatrixRMaj(eigen.getD().getData());
		DMatrixRMaj S = new DMatrixRMaj(eigen.getV().getData());
		eq.alias(D, "D");
		eq.alias(S, "S");

		System.out.println(A);
		System.out.println("DET(A): " + CommonOps_DDRM.det(A));
		System.out.println("RANK(A): " + MatrixFeatures.rank(A));
		System.out.println("TRACE(A): " + CommonOps_DDRM.trace(A));
		System.out.println("Eigen Values(A):\n" + builder.toString());
		System.out.println("Has Complex(A): " + eigen.hasComplexEigenvalues());

		StringBuilder builder1 = new StringBuilder();
		StringBuilder builder2 = new StringBuilder();
		{
			builder2.append(" S * ");
			for (int i = 0; i < ROUND; i++) {
				if (builder1.length() > 0) {
					builder1.append(" * ");
					builder2.append(" * ");
				}
				builder1.append("A");
				builder2.append("D");
			}
			builder2.append(" * inv(S)");
		}
		
		
		long startTime1 = 0L;
		long endTime1 = 0L;
		long startTime2 = 0L;
		long endTime2 = 0L;
		long startTime3 = 0L;
		long endTime3 = 0L;
		long startTime4 = 0L;
		long endTime4 = 0L;
		long startTime5 = 0L;
		long endTime5 = 0L;
		long startTime6 = 0L;
		long endTime6 = 0L;

		startTime1 = System.nanoTime();
		eq.process("ANS1 = " + builder1.toString() + " * X");
		endTime1 = System.nanoTime();

		startTime2 = System.nanoTime();
		eq.process("ANS2 = " + builder2.toString() + " * X");
		endTime2 = System.nanoTime();

		eq.process("K = [ S, X ]");

		DMatrixRMaj K = eq.lookupMatrix("K");

		System.out.println(K);

		CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);

		eq.process("C0 = K(0,4)");
		eq.process("C1 = K(1,4)");
		eq.process("C2 = K(2,4)");
		eq.process("C3 = K(3,4)");

		eq.alias(ROUND, "n");
		startTime3 = System.nanoTime();
		{
			// using java classes for calculation would be much faster than
			// using ejml equations
			eq.process(
				"ANS3 = (C0 * pow(LAMDA0, n) * V0) + (C1 * pow(LAMDA1, n) * V1) + (C2 * pow(LAMDA2, n) * V2) + (C3 * pow(LAMDA3, n) * V3)");
		}
		endTime3 = System.nanoTime();

		System.out.println(K);

		DMatrixRMaj Y1 = new DMatrixRMaj(X.numRows, X.numCols);
		startTime4 = System.nanoTime();
		{
			DMatrixRMaj t = A.copy();
			for (int i = 1; i < ROUND; i++) {
				CommonOps_DDRM.mult(t.copy(), A, t);
			}
			CommonOps_DDRM.mult(t, X, Y1);
		}
		endTime4 = System.nanoTime();

		double C0 = eq.lookupDouble("C0");
		double C1 = eq.lookupDouble("C1");
		double C2 = eq.lookupDouble("C2");
		double C3 = eq.lookupDouble("C3");
		DMatrixRMaj b0 = null;
		DMatrixRMaj b1 = null;
		DMatrixRMaj b2 = null;
		DMatrixRMaj b3 = null;

		b0 = vecs[0].copy();
		b1 = vecs[1].copy();
		b2 = vecs[2].copy();
		b3 = vecs[3].copy();

		startTime5 = System.nanoTime();
		DMatrixRMaj Y2 = new DMatrixRMaj(X.numRows, X.numCols);
		{
			DMatrixRMaj SINV = new DMatrixRMaj(S.numRows, S.numCols);
			CommonOps_DDRM.invert(S, SINV);
			DMatrixRMaj t = D.copy();
			for (int i = 1; i < ROUND; i++) {
				CommonOps_DDRM.mult(t.copy(), D, t);
			}
			CommonOps_DDRM.mult(S, t.copy(), t);
			CommonOps_DDRM.mult(t.copy(), SINV, t);
			CommonOps_DDRM.mult(t, X, Y2);
		}
		endTime5 = System.nanoTime();

		
		
		b0 = vecs[0].copy();
		b1 = vecs[1].copy();
		b2 = vecs[2].copy();
		b3 = vecs[3].copy();

		startTime6 = System.nanoTime();
		DMatrixRMaj Y3 = new DMatrixRMaj(X.numRows, X.numCols);
		{
			CommonOps_DDRM.scale(C0 * Math.pow(vals[0], ROUND), b0);
			CommonOps_DDRM.scale(C1 * Math.pow(vals[1], ROUND), b1);
			CommonOps_DDRM.scale(C2 * Math.pow(vals[2], ROUND), b2);
			CommonOps_DDRM.scale(C3 * Math.pow(vals[3], ROUND), b3);

			CommonOps_DDRM.add(Y3.copy(), b0, Y3);
			CommonOps_DDRM.add(Y3.copy(), b1, Y3);
			CommonOps_DDRM.add(Y3.copy(), b2, Y3);
			CommonOps_DDRM.add(Y3.copy(), b3, Y3);
		}
		endTime6 = System.nanoTime();

		
		
		System.out.println("ANS1 eq[A^n * X]\n" + eq.lookupMatrix("ANS1"));
		System.out.println("ANS2 eq[S D^n inv(S) * X]\n" + eq.lookupMatrix("ANS2"));
		System.out
				.println("ANS3 eq[C0 * LAMDA0^n * V0 + C1 * LAMDA1^n * V1 + C2 * LAMDA2^n * V2 + C3 * LAMDA3^n * V3]\n"
						+ eq.lookupMatrix("ANS3"));
		System.out.println("ANS4 java[A^n * X]\n" + Y1);
		System.out.println("ANS5 java[S D^n inv(S) * X]\n" + Y2);
		System.out.println(
				"ANS6 java[C0 * LAMDA0^n * V0 + C1 * LAMDA1^n * V1 + C2 * LAMDA2^n * V2 + C3 * LAMDA3^n * V3]\n" + Y3);

		System.out.println("Round: " + ROUND);
		System.out.println("EQ(A^nX)  |EQ(SD^nSX)|EQ(SOL)   |JAVA(A^nX)|JAVA(SD^nSX)|JAVA(SOL)");
		System.out.println(ft.format((double) (endTime1 - startTime1) / 1000000000) + "  |"
				+ ft.format((double) (endTime2 - startTime2) / 1000000000) + "  |"
				+ ft.format((double) (endTime3 - startTime3) / 1000000000) + "  |"
				+ ft.format((double) (endTime4 - startTime4) / 1000000000) + "  |"
				+ ft.format((double) (endTime5 - startTime5) / 1000000000) + "    |"
				+ ft.format((double) (endTime6 - startTime6) / 1000000000));

	}

}
