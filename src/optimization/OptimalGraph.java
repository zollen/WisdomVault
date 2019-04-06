package optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
	private static final int END_STATE_F = 5;
	private static final int STATE_G = 6;
	
	private static int fDist = Integer.MAX_VALUE;
	
	private static final String [] LABELS = { "A", "B", "C", "D", "E", "F", "G" };

	public static void main(String ...args) {
			
		Equation eq = new Equation();
		eq.process("A = [ " + 
				 //    A,  B,  C,  D,  E,  F,  G
			/* A */ "  0,  4,  3,  0,  7,  0,  0;" +
			/* B */	"  4,  0,  6,  5,  0,  0,  0;" +
			/* C */ "  3,  6,  0, 11,  8,  0,  0;" +
			/* D */ "  0,  5, 11,  0,  2,  2, 10;" +
			/* E */ "  7,  0,  8,  2,  0,  0,  5;" +
			/* F */ "  0,  0,  0,  2,  0,  0,  3;" +
			/* G */ "  0,  0,  0, 10,  5,  3,  0 " +
		"]"); 
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		{
			Map<Integer, Integer> vertices = new LinkedHashMap<Integer, Integer>();
			vertices.put(START_STATE_A, 0);
			for (int i = 1; i <= 6; i++)
				vertices.put(i, Integer.MAX_VALUE);
			
			Node root = dijkstra(A, START_STATE_A, vertices, new HashSet<Integer>());
		
			System.out.println("Number of Nodes: " + count(root));
			shortestPath(A, root);
		}
		
		
		// Shortest path features that could optimizes the performance:
		// 1. Node that has already been extended, skip
		// 2. Heuristic - any additional information that help speed up the search (i.e. estimation of the distance left to the end state)
		//	  Let F be the end state (Goal). 
		//	  Let H(X -> F) be the estimated distance from X to the end state F. 
		//    Let D(X -> Y) be the actual distance between X and Y
		
		//	  There are two types of heuristic information: 
		//		2.1 Admissible    H(X -> F) <= D(X -> F) : the estimated distance must be smaller or equal to the actual distance
		// 		2.2 Consistency  abs( H(X -> F) - H(Y -> F) ) <= D(X -> Y)
		
		
		
		heuristicInfo.put(START_STATE_A, 15);
		alreadyExtended.put(START_STATE_A, false);
		
		heuristicInfo.put(STATE_B, 12);
		alreadyExtended.put(STATE_B, false);
		
		heuristicInfo.put(STATE_C, 12);
		alreadyExtended.put(STATE_C, false);
		
		heuristicInfo.put(STATE_D, 10);
		alreadyExtended.put(STATE_D, false);
		
		heuristicInfo.put(STATE_E, 5);
		alreadyExtended.put(STATE_E, false);
		
		heuristicInfo.put(END_STATE_F, 0);
		alreadyExtended.put(END_STATE_F, false);
		
		heuristicInfo.put(STATE_G, 5);
		alreadyExtended.put(STATE_G, false);
		
		
		{
			Map<Integer, Integer> vertices = new LinkedHashMap<Integer, Integer>();
			vertices.put(START_STATE_A, 0);
			for (int i = 1; i <= 6; i++)
				vertices.put(i, Integer.MAX_VALUE);
			
			// dijkstra with new features!
			Node root = dijkstra2(A, START_STATE_A, vertices);
		
			System.out.println("Number of Nodes: " + count(root));
			shortestPath(A, root);
		}
	}
	
	public static void shortestPath(DMatrixRMaj A, Node node) {
		
		Map<Integer, List<Map<Integer, Integer>>> results = new TreeMap<Integer, List<Map<Integer, Integer>>>();

		search(A, node, new LinkedHashMap<Integer, Integer>(), results);
		
		Map.Entry<Integer, List<Map<Integer, Integer>>> first = results.entrySet().stream().findFirst().get();
		
		first.getValue().stream().forEach(p -> print(first.getKey(), p));
		
	}
	
	public static int count(Node node) {
		
		int count = 1;
		
		for (Node child : node.getChildren()) {
			
			count += count(child);
		}
		
		return count;
	}
	
	public static void search(DMatrixRMaj A, Node node, Map<Integer, Integer> paths,
			Map<Integer, List<Map<Integer, Integer>>> results) {
		
		Map<Integer, Integer> _paths = new LinkedHashMap<Integer, Integer>(paths);
		
		if (node.getId() == END_STATE_F) {
			
			Integer min = results.keySet().stream().findFirst().orElse(Integer.MAX_VALUE);
			
			if (min == null || (min != null && min.intValue() >= node.getScore())) {
				
				List<Map<Integer, Integer>> list = results.get(node.getScore());
				if (list == null) {
					list = new ArrayList<Map<Integer, Integer>>();
					results.put(node.getScore(), list);
				}
			
				list.add(_paths);
			}
				
			return;
		}
		
	
		for (Node child : node.getChildren()) {
			
			if (node.score + (int) A.get(child.getId(), node.getId()) == child.getScore()) {
				_paths.put(node.id, child.id);
				search(A, child, _paths, results);		
			}
		}
	}
	
	public static Node dijkstra2(DMatrixRMaj A, int from, Map<Integer, Integer> vertices) {
		
		Map<Integer, Integer> _vertices = new LinkedHashMap<Integer, Integer>(vertices);
		
		Integer score = _vertices.get(from);
		Node node = create(from, score);
		
		if (from == END_STATE_F) {
			return node;
		}
	
		// could use heuristic info for excluding any states that have already exceed 
		// the estimated distance from the end state F.
		
		for (int to = STATE_G; to >= STATE_B; to--) {	
			
			if (A.get(to, from) > 0) {
			
				int dist = _vertices.get(from) + (int) A.get(to, from);
				
				if (dist < _vertices.get(to)) {
					_vertices.put(to, dist);
					
					if (to == END_STATE_F) {
						fDist = dist;
					}
					
					if (dist <= fDist) {
						node.getChildren().add(dijkstra2(A, to, _vertices));
					}
				}
			}	
		}
		
		return node;
	}
	
	public static Node dijkstra(DMatrixRMaj A, int from, Map<Integer, Integer> vertices, 
			Set<Integer> states) {
		
		Map<Integer, Integer> _vertices = new LinkedHashMap<Integer, Integer>(vertices);
		
		Set<Integer> _states = new HashSet<Integer>(states);
		_states.add(from);
		
		Integer score = _vertices.get(from);
		Node node = create(from, score);
		
		if (from == END_STATE_F) {
			return node;
		}
	
				
		for (int to = STATE_B; to < A.numRows; to++) {
			
			if (A.get(to, from) > 0) {
			
				int dist = _vertices.get(from) + (int) A.get(to, from);
				
				if (dist < _vertices.get(to)) {
					_vertices.put(to, dist);
				}
				
				if (!_states.contains(to)) {
					node.getChildren().add(dijkstra(A, to, _vertices, _states));
				}
			}	
		}
		
		return node;
	}
	
	public static Node create(int id) {
		return new Node(id);
	}
	
	public static Node create(int id, int score) {
		return new Node(id, score);
	}
	
	public static void print(int scores, Map<Integer, Integer> paths) {
	
		String tmp = paths.entrySet().stream().map(p -> LABELS[p.getKey()] + " ==> " + 
										LABELS[p.getValue()]).collect(Collectors.joining(", "));
		
		System.out.println(tmp + " : " + scores);
	}
	
	
	public static class Node {

		private int score;
		
		private int id;
		
		private List<Node> children = new ArrayList<Node>();
		
		public Node(int id) {
			this.id = id;
		}
		
		public Node(int id, int score) {
			this.id = id;
			this.score = score;
		}
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public int getScore() {
			return score;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public List<Node> getChildren() {
			return children;
		}

		public void setChildren(List<Node> children) {
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
			
			builder.append("Node: [" + LABELS[id] + ":" + score + "]\n");
			children.stream().forEach(p -> builder.append(p.toString(indent + 3)));
			
			return builder.toString();
		}
		
	}
}
