import org.ejml.equation.Equation;

public class ShiftCurveExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
						" 0, 0, 0, 0, 0, 0;" +
						" 0, 0, 0, 0, 0, 0;" +
						" 1, 0, 0, 0, 0, 0;" +
						" 0, 1, 0, 0, 0, 0;" +
						" 0, 0, 1, 0, 0, 0;" +
						" 0, 0, 0, 1, 0, 0 " +
					"]");
		
		eq.process("X = [" +
						" 2; 5; 8; 7; 1; 4 " +
						"]");
		
		eq.process("K = A * X");
		System.out.println(eq.lookupMatrix("K"));
	}

}
