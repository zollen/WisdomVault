import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class MarkovExercise9 {
	
	private static final String [] TOKENS = { "A", "B", "C", "D", "E", "F" };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " +
				 /* A,    B,    C,    D,    E,   F */
		/* A */ "   0,    0,    0,    0,    0,   0;" +
		/* B */ "   1,    0,    1,    0,    1,   0;" +
		/* C */ "   0,    1,    0,    0,    1,   0;" +
		/* D */ "   1,    0,    0,    0,    1,   0;" +
		/* E */ "   0,    1,    0,    1,    0,   0;" +
		/* F */ "   0,    1,    1,    0,    1,   0 " +
					"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		System.out.println(analysis(A));
	}
	
	public static DMatrixRMaj analysis(DMatrixRMaj A) {
		
		// M[P <- Q] = 1 / N if Q has no outlinks
		// M[P <- Q] = (1 - F) / N if Q has some outlinks but not to P
		// M[P <- Q] = (1 - F) / N  +  F/O(Q) if Q has links to P
				
		// F = 0.7
		
		DMatrixRMaj B = new DMatrixRMaj(A.numRows, A.numCols);
		int N = A.numCols;
		double F = 0.7d;
		
		for (int from = 0; from < A.numCols; from++) {
				
			for (int to = 0; to < A.numRows; to++) {
				
				if (hasLink(A, to, from)) {
					B.set(to, from, (double) (1 - F)/N + (double)F/numOutLinks(A, from));
				}
				else {
					if (hasOutLinks(A, from)) {
						B.set(to, from, (double) (1 - F)/N);
					}
					else {
						B.set(to, from, (double) 1/N);
					}
				}
			}
		}
		
		
		return B;
		
	}
	
	public static boolean hasLink(DMatrixRMaj A, int i, int j) {
		return ((int) A.get(i, j) == 1);
	}
	
	public static int numOutLinks(DMatrixRMaj A, int j) {
		
		int count = 0;
		
		for (int i = 0; i < A.numRows; i++) {
			
			if ((int) A.get(i, j) == 1)
				count++;
		}
		
		return count;
	}
	
	public static boolean hasOutLinks(DMatrixRMaj A, int j) {
		
		for (int i = 0; i < A.numRows; i++) {
			
			if ((int) A.get(i, j) == 1)
				return true;
		}
		
		return false;	
	}

}
