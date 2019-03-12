package linearalgebra;

import java.text.DecimalFormat;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class EigenExercise3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 2, 1; 1, 2 ]");
		DMatrixRMaj A = eq.lookupDDRM("A");

		RealMatrix m = MatrixUtils.createRealMatrix(MatrixFeatures.array(A));
		
		DecimalFormat ff = new DecimalFormat("#,##0.000");
		LUDecomposition lu = new LUDecomposition(m);
		System.out.println("Is Non Singular(m): " + lu.getSolver().isNonSingular());
	
		EigenDecomposition eigen = new EigenDecomposition(m);
		System.out.println("Det(A): " + Math.round(eigen.getDeterminant()));
		System.out.println("Rank(A): " + MatrixFeatures.rank(A));
		System.out.println("Trace(A): " + Math.round(CommonOps_DDRM.trace(A)));
		
		double [] vals = eigen.getRealEigenvalues();
		StringBuilder builder = new StringBuilder();
		for (double val : vals) {
			if (builder.length() > 0)
				builder.append(", ");
			builder.append(ff.format(val));
		}
		
		System.out.println("Eigen Values(A): " + builder.toString());
		System.out.println("Has Complex(A): " + eigen.hasComplexEigenvalues());
		
		DMatrixRMaj S = new DMatrixRMaj(eigen.getV().getData());
		DMatrixRMaj D = new DMatrixRMaj(eigen.getD().getData());
		
		System.out.println("Det(S): " + Math.round(CommonOps_DDRM.det(S)));
		System.out.println("Rank(S): " + MatrixFeatures.rank(S));
		System.out.println("Trace(S): " + Math.round(CommonOps_DDRM.trace(S)));
		System.out.println("Det(D): " + Math.round(CommonOps_DDRM.det(D)));
		System.out.println("Rank(D): " + MatrixFeatures.rank(D));
		System.out.println("Trace(D): " + Math.round(CommonOps_DDRM.trace(D)));
		
		eq.alias(S, "S");
		eq.alias(D, "D");
		eq.process("L = [ 3, 0; 0, 3 ]");
		
		eq.process("K1 = det(D - L)");
		eq.process("K2 = det(A - L)");
		
		System.out.println("det(D - L): " + Math.round(eq.lookupDouble("K1")));
		System.out.println("det(A - L): " + Math.round(eq.lookupDouble("K2")));
		
	}

}