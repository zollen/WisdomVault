import org.ejml.equation.Equation;

public class TestMe7 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		
		eq.process("Ass = [ " +
							"  2,  1, -1;" +
							"  0, -1,  3 " +
						"]");
		
		eq.process("B = [" +
							" 1,  0,  5;" +
							" 2, -1,  2;" +
							" 1,  0, -2 " +
						"]");
		
		eq.process("C = [" +
							" 2,  -1;" +
							" 1,   1 " +
						"]");
		
		eq.process("Xs3 = [ 1; 2; 3 ]");
		
		eq.process("Xs2 = Ass * Xs3");
				
		eq.process("Xb = inv(B) * Xs3");
		eq.process("Xc = inv(C) * Xs2");
		System.out.println("Xb ==> " + eq.lookupMatrix("Xb"));

		System.out.println("Ass * Xs3: " + eq.lookupMatrix("Xs2"));
		System.out.println("Xc ==> " + eq.lookupMatrix("Xc"));
		
		eq.process("K = inv(C) * Ass * B * Xb");
		System.out.println("inv(C) * Ass * B * Xb = Xc: " + eq.lookupMatrix("K"));

		
	}
}
