package optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class OptimalGraph {
	
	private static final Map<Integer, Boolean> alreadyExtended = new HashMap<Integer, Boolean>();	
	private static final Map<Integer, Integer> heuristicInfo = new HashMap<Integer, Integer>();
	
	private static final int START_STATE_A = 0;
	private static final int STATE_B = 1;
	private static final int STATE_C = 2;
	private static final int STATE_D = 3;
	private static final int STATE_E = 4;
	private static final int STATE_F = 5;
	private static final int END_STATE_G = 6;

	public static void main(String ...args) {
		
		// A - 4 - B
		// A - 3 - C
		// A - 7 - E
		// B - 6 - C
		// B - 5 - D
		// C - 11 - D
		// D - 2 - E
		// D - 2 - F
		// D - 10 - G
		// G - 3 - F
		
		Equation eq = new Equation();
		eq.process("A = [ " + 
				 //    A,  B,  C,  D,  E,  F,  G
			/* A */ "  0,  4,  3,  0,  7,  0,  0;" +
			/* B */	"  4,  0,  6,  5,  0,  0,  0;" +
			/* C */ "  3,  6,  0, 11,  8,  0,  0;" +
			/* D */ "  0,  5, 11,  0,  2,  2,  0;" +
			/* E */ "  7,  0,  8,  2,  0,  0,  5;" +
			/* F */ "  0,  0,  0,  2,  0,  0,  3;" +
			/* G */ "  0,  0,  0, 10,  5,  3,  0 " +
		"]"); 
		
		// Shortest path features that could optimizes the performance:
		// 1. Node that has already been visited or extended, skip
		// 2. Heuristic - any additional information that help speed up the search (i.e. estimation of the distance left to the end state)
		//	  Let G be the end state (Goal). 
		//	  Let H(X -> G) be the estimated distance from X to the end state G. 
		//    Let D(X -> Y) be the actual distance between X and Y
		
		//	  There are two types of heuristic information: 
		//		2.1 Admissible    H(X -> G) <= D(X -> G) : the estimated distance must be smaller or equal to the actual distance
		// 		2.2 Consistency | H(X -> G) - H(Y -> G) | <= D(X -> Y)
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		heuristicInfo.put(START_STATE_A, 20);
		alreadyExtended.put(START_STATE_A, false);
		
		heuristicInfo.put(STATE_B, 23);
		alreadyExtended.put(STATE_B, false);
		
		heuristicInfo.put(STATE_C, 22);
		alreadyExtended.put(STATE_C, false);
		
		heuristicInfo.put(STATE_D, 21);
		alreadyExtended.put(STATE_D, false);
		
		heuristicInfo.put(STATE_E, 8);
		alreadyExtended.put(STATE_E, false);
		
		heuristicInfo.put(STATE_F, 7);
		alreadyExtended.put(STATE_F, false);
		
		heuristicInfo.put(END_STATE_G, 5);
		alreadyExtended.put(END_STATE_G, false);
		
		Node<Integer> root = findShortedPath(A, START_STATE_A);
		
		System.out.println(root);		
	}
	
	public static <T> Node<T> findShortedPath(DMatrixRMaj A, int from) {
		
		Node<T> node = create(from);
		
		
		// whatever implementation
		
		return node;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Node<T> create(Integer state) {
		return new Node<T>((T) state);
	}
	
	
	public static class Node<T> {

		private T data;
		
		private List<Node<?>> children = new ArrayList<Node<?>>();
		
		public Node() {}
		
		public Node(T data) {
			this.data = data;
		}
		
		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public List<Node<?>> getChildren() {
			return children;
		}

		public void setChildren(List<Node<?>> children) {
			this.children = children;
		}
		
		@Override
		public String toString() {
			return toString(0);
		}
		
		private String toString(int indent) {
			
			StringBuilder builder = new StringBuilder();
			
			for (int i = 0; i < indent; i++)
				builder.append(" ");
			
			builder.append("Node: [" + data + "]\n");
			children.stream().forEach(p -> builder.append(p.toString(indent + 3)));
			
			return builder.toString();
		}
		
	}
}
