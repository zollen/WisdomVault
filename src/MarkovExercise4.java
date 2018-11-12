import org.ejml.equation.Equation;

public class MarkovExercise4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("M = [ " + 
							"   0,   0,   0,   0,   0,  1;" + 
							" 0.5,   0, 0.5, 0.5,   0,  0;" + 
							"   0, 0.5,   0,   0, 0.5,  0;" +
							" 0.5, 0.5, 0.5,   0,   0,  0;" +
							"   0,   0,   0, 0.5,   0,  0;" +
							"   0,   0,   0,   0, 0.5,  0 " +
						"]");
		eq.process("X = [ 1./15; 4./15; 3./15; 4./15; 2./15; 1./15 ]");
		
		eq.process("K = M * X");
		
		System.out.println(eq.lookupDDRM("K"));
		
		eq.process("K = M - eye(M)");
		eq.process("K = [ K, [ 0; 0; 0; 0; 0; 0 ]]");
		eq.process("K = [ K ; [ 1, 1, 1, 1, 1, 1, 1 ]]");
		eq.process("K1 = rref(K)");
		
		System.out.println("SteadyState: " + eq.lookupDDRM("K1"));
		
		
	}

}
