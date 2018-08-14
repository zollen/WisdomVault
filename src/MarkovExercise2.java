import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class MarkovExercise2 {

	private static final long ROUND = 2;

	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
						"    0,  1,  0, 0.1;" + 
						"  0.5, 0, 0.2, 0.3;" + 
						"  0.2, 0, 0.7, 0.6;" +
						"  0.3, 0, 0.1, 0" +
					"]");
		eq.process("X = [ 1; 0; 0; 0 ]");

		DMatrixRMaj A = eq.lookupMatrix("A");
		RealMatrix AA = MatrixUtils.createRealMatrix(MatrixFeatures.array(A));

		EigenDecomposition eigen = new EigenDecomposition(AA);

		EigenMatrix matrix = new EigenMatrix(eigen);
		List<EigenVector> vectors = matrix.getVectors();

		{
			int index = 0;
			double total = 1L;
			StringBuilder builder = new StringBuilder();
			for (EigenVector vector : vectors) {
				eq.alias(vector.getValue(), "LAMDA" + index);
				eq.alias(vector.getVector(), "V" + index);

				if (index > 0)
					builder.append(", ");
				builder.append("V" + index);

				index++;
				
				total *= vector.getValue();
			}

			System.out.println("DET(A): " + CommonOps_DDRM.det(A));
			System.out.println("Product of all Lamdas(A): " + ff.format(total));
			System.out.println("RANK(A): " + MatrixFeatures.rank(A));
			System.out.println("TRACE(A): " + CommonOps_DDRM.trace(A));
			System.out.println("Has Complex(A): " + eigen.hasComplexEigenvalues());
			System.out.println("Eigen Values(A): " + matrix.getValues());
			for (EigenVector vector : vectors) {
				System.out.println(vector);
			}

			System.out.println(A);

			eq.alias(ROUND, "n");
			
			eq.process("K = [ " + builder.toString() + ", X ]");

			DMatrixRMaj K = eq.lookupMatrix("K");

			CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);

			System.out.println("Rref(K): " + K);
		
			builder = new StringBuilder();
			StringBuilder formula = new StringBuilder();
			builder.append("ANS = ");
			formula.append("ANSWER = ");
			for (int k = 0; k < matrix.getSize(); k++) {
				if (k > 0) {
					builder.append(" + ");
					formula.append(" + ");
				}

				eq.process("C" + k + " = K(" + k + "," + (K.numCols - 1) + ")");
			
				builder.append("(C" + k + " * pow(LAMDA" + k + ", n) * V" + k + ")");
				formula.append("(" + ff.format(eq.lookupDouble("C" + k)) + " * ("
						+ ff.format(eq.lookupDouble("LAMDA" + k)) + ")^" + ROUND + " * " + "V" + k + ")");
			}

			System.out.println(builder.toString());
			System.out.println(formula.toString());
			eq.process(builder.toString());

			System.out.println(eq.lookupMatrix("ANS"));

			eq.process("ANS2 = C0 * V0");

			System.out.println("STEADY STATE: " + eq.lookupMatrix("ANS2"));
		}
		
		{
			/* IMPORTANT TECHNIQUE WHEN EIGEN VALUES ARE NOT REILABLE */
			eq.process("K = A - eye(A)");
			eq.process("K = [ K, [ 0; 0; 0; 0 ]]");
			eq.process("K = [ K ; [ 1, 1, 1, 1, 1 ]]");
			
			DMatrixRMaj K = eq.lookupMatrix("K");
			
			CommonOps_DDRM.rref(K.copy(), K.numCols - 1, K);

			System.out.println("SteadyState ==> Rref(K): " + K);
			
		}

	}

}
