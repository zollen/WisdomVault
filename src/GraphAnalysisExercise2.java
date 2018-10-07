import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class GraphAnalysisExercise2 {
	
	// A <-> B <-> D
	// A <-> C <-> D
	// B <-> C
	
	// A : 0
	// B : 1
	// C : 2
	// D : 3
	
	// AB, BA : 01, 10 : 1
	// AC, CA : 02, 20 : 3
	// BD, DB : 13, 31 : 5
	// CD, DC : 23, 32 : 7
	// BC, CB : 12, 21 : 9
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [" +
						"   0,  0.9,  0.9,    0;" +
						" 0.9,    0,  0.9,  0.9;" +
						" 0.9,  0.9,    0,  0.9;" +
						"   0,  0.9,  0.9,    0 " +
					 "]");
						
		DMatrixRMaj A = eq.lookupDDRM("A");
		System.out.println(A);
		System.out.println("Rank(A): " + MatrixFeatures.rank(A));
		
		eq.process("K = A * A");
		
		System.out.println(eq.lookupDDRM("K"));
	}
	

	
}
