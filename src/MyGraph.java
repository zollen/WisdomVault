import org.ejml.equation.Equation;


public class MyGraph {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " +
						   /* 1  2, 3, 4, 5, 6 */
							" 0, 1, 0, 1, 0, 1;" +  // 1
							" 0, 1, 1, 1, 0, 0;" +  // 2
							" 0, 1, 0, 1, 0, 0;" +  // 3
							" 0, 1, 1, 1, 0, 1;" +  // 4
							" 1, 0, 0, 1, 0, 1;" +  // 5
							" 1, 0, 0, 0, 1, 0 "  + // 6
						 "]");

			
		eq.process("K = A * A");
		
		// matrix of total number of paths from i -> j when path-length = 2
		System.out.println(eq.lookupMatrix("K"));
	}

}
