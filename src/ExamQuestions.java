import org.ejml.equation.Equation;

public class ExamQuestions {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 1, 1, -1; 1, 1, -1; 1, 1, -1 ]");
		eq.process("T = A'"); 
		eq.process("R = rref(T)");
		eq.process("x = [ 1; 2; 3]"); 
		eq.process("y = [ 1; 0; 1 ]"); 
		eq.process("ANS = A * x");
		
		
		
	
		System.out.println(eq.lookupMatrix("A"));
		System.out.println(eq.lookupMatrix("T"));
		System.out.println(eq.lookupMatrix("R"));
		System.out.println(eq.lookupMatrix("ANS"));
		
	}

}
