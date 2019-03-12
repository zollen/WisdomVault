package linearalgebra;

import java.text.DecimalFormat;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixFormat;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class EigenExercise2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 1, 1; 0, 1 ]");
		DMatrixRMaj A = eq.lookupDDRM("A");

		RealMatrix m = MatrixUtils.createRealMatrix(MatrixFeatures.array(A));
		
		DecimalFormat ff = new DecimalFormat("#,##0.000");
		RealMatrixFormat f1 = new RealMatrixFormat("", "", "[ ", " ]", "\n", ", ", ff);
		
		
		LUDecomposition lu = new LUDecomposition(m);
		System.out.println("Is Non Singular(m): " + lu.getSolver().isNonSingular());
	//	RealMatrix pInverse = lu.getSolver().getInverse();		
	//	System.out.println("<===== inv(m) =====>");
	//	System.out.println(f1.format(pInverse));		
		
		
		EigenDecomposition eigen = new EigenDecomposition(m);
		System.out.println("Det(m): " + ff.format(eigen.getDeterminant()));
		RealMatrix d = eigen.getD();
	//	System.out.println("<===== diag(m) =====>");
	//	System.out.println(f1.format(d));
	//	System.out.println("<===== V ======>");
		RealMatrix v = eigen.getV();
	//	System.out.println(f1.format(v));
		
		double [] vals = eigen.getRealEigenvalues();
		StringBuilder builder = new StringBuilder();
		for (double val : vals) {
			if (builder.length() > 0)
				builder.append(", ");
			builder.append(ff.format(val));
		}
		
		System.out.println("Eigen Values(m): " + builder.toString());
		System.out.println("Has Complex(m): " + eigen.hasComplexEigenvalues());
		
		DMatrixRMaj S = new DMatrixRMaj(v.getData());
		DMatrixRMaj D = new DMatrixRMaj(d.getData());
		
		
		eq.alias(S, "S");
		eq.alias(D, "D");
		eq.process("K2 = A * A * A");
		eq.process("K1 = S * D * D * D * inv(S)");
		
		System.out.println("<=== A ===>");
		System.out.println(f1.format(m));
		System.out.println(A);
		
		System.out.println("<=== S ===>");
		System.out.println(S);
		
		System.out.println("<=== D ===>");
		System.out.println(D);
		
		DMatrixRMaj K1 = eq.lookupDDRM("K1");
		System.out.println(K1);
		
		DMatrixRMaj K2 = eq.lookupDDRM("K2");
		System.out.println(K2);
	}

}