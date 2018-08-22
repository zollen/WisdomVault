import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class JardanExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " +
							" 5, 4, 2, 1;" +
							" 0, 1,-1,-1;" +
							"-1,-1, 3, 0;" +
							" 1, 1,-1, 2" +
						"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		System.out.println(A);

		EigenDecomposition eigen = new EigenDecomposition(MatrixUtils.createRealMatrix(MatrixFeatures.array(A)));
		DMatrixRMaj D = new DMatrixRMaj(eigen.getD().getData());
		DMatrixRMaj V = new DMatrixRMaj(eigen.getV().getData());
		
		System.out.println(V);
		System.out.println(D);
	}

}
