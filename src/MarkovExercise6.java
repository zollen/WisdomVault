import org.ejml.equation.Equation;

public class MarkovExercise6 {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// https://www.youtube.com/watch?v=6JVqutwtzmo
		
		Equation eq = new Equation();
		eq.process("A = [ " +
				 /*   A,   B,   C,   D,   E,   F  */
		/* A */   "   0, 0.5,   0,   0, 0.2, 0.1;" +
		/* B */   " 0.1, 0.1,   0, 0.1,   0,   0;" +
		/* C */   "   0, 0.4,   0, 0.5, 0.2,   0;" +
		/* D */   " 0.8,   0, 0.7,   0, 0.6,   0;" +
		/* E */   "   0,   0, 0.3, 0.4,   0, 0.1;" +
		/* F */   " 0.1,   0,   0,   0,   0, 0.8 " + 
				"]");
		
		eq.process("X = [ 1; 0; 0; 0; 0; 0 ]"); 
		
		eq.process("K = A * X");
		
		System.out.println("ONE: " + eq.lookupDDRM("K"));
		
		eq.process("K = A * A * X");
		
		System.out.println("TWO: " + eq.lookupDDRM("K"));
		
		eq.process("K = A * A * A * A * A * A * A * A * A * A * X");
		
		System.out.println("TEN: " + eq.lookupDDRM("K"));
		
		eq.process("K = A - eye(A)");
		eq.process("K1 = [ K, [ 0; 0; 0; 0; 0; 0 ]]");
		eq.process("K2 = [ K1 ; [ 1, 1, 1, 1, 1, 1, 1 ]]");
		eq.process("K3 = rref(K2)");
		eq.process("K4 = K3(0:5,6)");
		
		System.out.println("STEADY STATE: " + eq.lookupDDRM("K4"));
		
		
	}

	
}
