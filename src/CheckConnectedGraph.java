import java.util.HashSet;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class CheckConnectedGraph {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Assumption: 0 is always the start state
		// Assumption: must be non-direct graphes
		// Assumption: non-direct graphes -> symmetric matrice
		
		Equation eq = new Equation();
		
		// 0 - 1
		//      1 - 2
		//      1 - 3
		eq.process("A = [" +
							" 0, 1, 0, 0;" +
							" 1, 0, 1, 1;" +
							" 0, 1, 0, 0;" +
							" 0, 1, 0, 0 " +
						"]");
				
		DMatrixRMaj A = eq.lookupDDRM("A");	
		System.out.println("CONNECTED GRAPH(A): " + connectedGraph(A));
		
		// 0 - 1
		//     1 - 3
		//     2 - 4
		eq.process("B = [" +
							" 0, 1, 0, 0, 0;" +
							" 1, 0, 0, 1, 0;" +
							" 0, 0, 0, 0, 1;" +
							" 0, 1, 0, 0, 0;" +
							" 0, 0, 1, 0, 0 " +
						"]");
		
		DMatrixRMaj B = eq.lookupDDRM("B");	
		System.out.println("CONNECTED GRAPH(B): " + connectedGraph(B));
		
		// 0 - 2
		//     2 - 1
		eq.process("C = [" +
							" 0, 0, 1;" +
							" 0, 0, 1;" +
							" 1, 1, 0 " +
						"]");
		
		DMatrixRMaj C = eq.lookupDDRM("C");	
		System.out.println("CONNECTED GRAPH(C): " + connectedGraph(C));
		
		// 0 - 5 - 4 - 3
		//     6 - 2
		eq.process("D = [" +
							" 0, 0, 0, 0, 0, 1, 0;" +
							" 0, 0, 0, 0, 0, 0, 0;" +
							" 0, 0, 0, 0, 0, 0, 1;" +
							" 0, 0, 0, 0, 1, 0, 0;" +
							" 0, 0, 0, 1, 0, 1, 0;" +
							" 1, 0, 0, 0, 1, 0, 0;" +
							" 0, 0, 1, 0, 0, 0, 0 " +
						"]");
		
		DMatrixRMaj D = eq.lookupDDRM("D");	
		System.out.println("CONNECTED GRAPH(D): " + connectedGraph(D));				
						
		
	}
	
	public static boolean connectedGraph(DMatrixRMaj A) {
		
		Set<Integer> all = new HashSet<Integer>();
		
		all.add(0);
		check(A, all, 0);
		
		if (A.numCols == all.size())
			return true;
		
		return false;
	}

	public static void check(DMatrixRMaj A, Set<Integer> all, int node) {
		
		for (int i = 0; i < A.numRows; i++) {
			
			if (A.get(i, node) == 1 && !all.contains(i)) {
		//		System.out.println("CHECK: " + i);
				all.add(i);
				check(A, all, i);
			}		
		}
	}

}
