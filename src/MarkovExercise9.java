import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class MarkovExercise9 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " +
				 /* A,    B,    C,    D,    E  */
		/* A */ "   0,    0,    0,    0,    0;" +
		/* B */ "   1,    0,    0,    1,    0;" +
		/* C */ "   1,    0,    0,    0,    0;" +
		/* D */ "   0,    0,    1,    0,    0;" +
		/* E */ "   0,    1,    0,    1,    0 " +
					"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		System.out.println(analysis(A));
	}
	
	public static DMatrixRMaj analysis(DMatrixRMaj A) {
		
		// M[P,Q] = 1 / N if Q has no outlinks
		// M[P,Q] = (1 - F) / N if Q has some outlinks but not to P
		// M[P,Q] = (1 - F) / N  +  F/O(Q) if Q has links to P
				
		// F = 0.7
		// e = 0.05
		
		DMatrixRMaj B = new DMatrixRMaj(A.numRows, A.numCols);
		int N = A.numCols;
		double F = 0.7d;
		
		for (int j = 0; j < A.numCols; j++) {
				
			for (int i = 0; i < A.numRows; i++) {
				
				if (A.get(i, j) == 1) {
				
					if (hasOutLinks(A, j)) {
						
						if (hasReturnLinks(A, i, j)) {
							B.set(i, j, (1 - F) / N + F / numOutLinks(A, j));
						}
						else {
							B.set(i, j, (1 - F) / N);
						}		
					}
					else {
						B.set(i, j, 1 / N);
					}
					
				}
			}
		}
		
		
		return B;
		
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
	
	public static boolean hasReturnLinks(DMatrixRMaj A, int i, int j) {
		
		return ((int)A.get(j, i) == 1);
	}

}
