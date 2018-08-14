import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class SmoothCurveExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
							"   1,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0;" + 
							"1./3,1./3,1./3,   0,   0,   0,   0,   0,   0,   0,   0;" +
							"   0,1./3,1./3,1./3,   0,   0,   0,   0,   0,   0,   0;" +
							"   0,   0,1./3,1./3,1./3,   0,   0,   0,   0,   0,   0;" +
							"   0,   0,   0,1./3,1./3,1./3,   0,   0,   0,   0,   0;" +
							"   0,   0,   0,   0,1./3,1./3,1./3,   0,   0,   0,   0;" +
							"   0,   0,   0,   0,   0,1./3,1./3,1./3,   0,   0,   0;" +
							"   0,   0,   0,   0,   0,   0,1./3,1./3,1./3,   0,   0;" +
							"   0,   0,   0,   0,   0,   0,   0,1./3,1./3,1./3,   0;" +
							"   0,   0,   0,   0,   0,   0,   0,   0,1./3,1./3,1./3;" +
							"   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   1 " + 
						"]");
		
		eq.process("X = [" +
							" 0.0941; 0.4514; 0.6371; 0.9001; 0.8884; 0.9844; 1.0431; 0.8984; 0.7319; 0.3911; -0.0929 " +
						"]");
		
		eq.process("K = A * X");
	
		DMatrixRMaj K = eq.lookupMatrix("K");
		
		K.print("%2.4f");
	}

}
