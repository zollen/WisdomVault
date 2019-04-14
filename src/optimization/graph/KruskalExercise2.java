package optimization.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

/**
 * Kruskal algorithm results in a minimum spanning tree against a
 * connected, uni/by-directional, weighted graph with unknown start and end states.
 * 
 * The algorithm computes the spanning tree of traversing all nodes with the least amount of cost.
 * (without circles)
 * 
 * Difference between uni-directional and bi-directional
 * 	1. register(...)
 * 	2. Edge.equals(...)
 * 
 * https://www.youtube.com/watch?v=71UQH7Pr9kU&t=1s
 * @author zollen
 *
 */
public class KruskalExercise2 {
	
	private static final String [] LABELS = { "A", "B", "C", "D", "E", "F", "G", "H", "I" };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
				/*  A,  B,  C,  D,  E,  F,  G,  H,  I  */
	/* A */ 	"	0,  4,  0,  0,  0,  0,  0,  8,  0;" +
	/* B */		"   4,  0,  8,  0,  0,  0,  0, 11,  0;" +
	/* C */		"   0,  8,  0,  7,  0,  4,  0,  0,  2;" +
	/* D */		"   0,  0,  7,  0,  9, 14,  0,  0,  0;" +
	/* E */		"   0,  0,  0,  9,  0, 10,  0,  0,  0;" +
	/* F */		"   0,  0,  4, 14, 10,  0,  2,  0,  0;" +
	/* G */		"   0,  0,  0,  0,  0,  2,  0,  1,  6;" +
	/* H */		"   8,  11, 0,  0,  0,  0,  1,  0,  7;" +
	/* I */		"   0,  0,  2,  0,  0,  0,  6,  7,  0 " +
				"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		Set<Edge> initial = new HashSet<Edge>();
		
		for (int col = 0; col < A.numCols; col++) {
			
			for (int row = 0; row < A.numRows; row++) {
				
				int weight = (int) A.get(row, col);
				
				if (weight > 0) {		
					initial.add(new Edge(row, col, weight));
				}
			}
		}
		
		List<Edge> list = new ArrayList<Edge>(initial);		
		Collections.sort(list, new Comparator<Edge>() {

			@Override
			public int compare(Edge o1, Edge o2) {
				// TODO Auto-generated method stub
				return Integer.valueOf(o1.getWeight()).compareTo(o2.getWeight());
			}			
		});
	
		
		// Kruskal algo begin

		Map<Integer, Set<Integer>> map = new HashMap<Integer, Set<Integer>>();	
		List<Edge> pool = new ArrayList<Edge>();
		
		while (list.size() > 0) {
			
			Edge first = list.stream().findFirst().get();
			list.remove(first);
			
			if (circle(map, first)) {
				continue;
			}
			
			register(map, first);
			
			pool.add(first);
		}
		
		pool.stream().forEach(p -> System.out.println(p));
		System.out.println("Number of edges: " + pool.size());

		List<Node> results = construct(pool);
		results.stream().forEach(p -> {
			
			System.out.println("====  Score: [" + score(p) + "]  ===");
			System.out.println(p);
			
		});
		
	}
	
	public static List<Node> construct(List<Edge> pool) {
		
		Map<Integer, Node> nodes = new HashMap<Integer, Node>();
		
		for (Edge edge : pool) {
			
			create(nodes, edge.getFrom(), edge.getTo(), edge.getWeight());
			create(nodes, edge.getTo(), edge.getFrom(), edge.getWeight());
		}

/*
		nodes.entrySet().stream().forEach(p -> {
			
			System.err.println("SCORE: " + score(p.getValue()));
			System.err.println(p.getValue());
		});
*/
		
		
		AtomicInteger max = new AtomicInteger();		
		nodes.entrySet().stream().forEach(p -> {
			int score = score(p.getValue());
			if (max.intValue() < score) {
				max.set(score);
			}
		});
		
		
		List<Node> candidates = new ArrayList<Node>();
		nodes.entrySet().stream().forEach(p -> {
			int score = score(p.getValue());
			if (max.intValue() == score) {
				candidates.add(p.getValue());
			}
		});
		
		return candidates;
	}
	
	public static void create(Map<Integer, Node> nodes, int from , int to, int weight) {
		
		Node node = nodes.get(from);
		
		if (node == null) {	
			Node parent = new Node(from, weight);
			Node child = nodes.get(to);
			if (child == null)
				child = new Node(to, weight);
			parent.getChildren().add(child);
			
			nodes.put(from, parent);
			nodes.put(to, child);
		}
		else {
			Node child = nodes.get(to);
			if (child == null)
				child = new Node(to, weight);
			node.getChildren().add(child);
			
			nodes.put(to, child);
		}
	}
	
	public static boolean circle(Map<Integer, Set<Integer>> map, Edge target) {
		
		Set<Integer> visited = new HashSet<Integer>();
		visited.add(target.getFrom());
			
		return circle(map, visited, target.getFrom(), target.getTo());
	}
	
	private static boolean circle(Map<Integer, Set<Integer>> map, Set<Integer> visited, 
							int last, int current) {
				
		if (visited.contains(current))
			return true;
		
		visited.add(current);
				
		Set<Integer> set = map.get(current);
		
		if (set != null) {		
			for (Integer s : set) {			
				if (s != last && circle(map, visited, current, s))
					return true;		
			}
		}
		
		return false;
	}
	
	private static void register(Map<Integer, Set<Integer>> map, Edge edge) {
			
		register(map, edge.getFrom(), edge.getTo());
		register(map, edge.getTo(), edge.getFrom());
	}
	
	private static void register(Map<Integer, Set<Integer>> map, int from, int to) {
		
		Set<Integer> dests = map.get(from);
		if (dests == null) {
			dests = new HashSet<Integer>();
			map.put(from, dests);
		}
			
		dests.add(to);
	}
	
	private static int score(Node node) {
		
		int count = count(new HashSet<Integer>(), node);
		int depth = depth(new HashSet<Integer>(), 0, node);
		
		// tree with largest number of nodes and minimum depth has the highest score
		return (int) Math.pow(count, 2) + (int) Math.pow(count - depth, 2);
	}
	
	private static int depth(Set<Integer> visited, int depth, Node node) {
		
		if (visited.contains(node.getId()))
			return depth;
		
		int max = 0;
		
		visited.add(node.getId());
		
		for (Node child : node.getChildren()) {
			
			int dep = depth(visited, depth + 1, child);
			if (max < dep)
				max = dep;
		}
		
		return max;
	}
	
	private static int count(Set<Integer> visited, Node node) {
		
		int count = 1;
		
		visited.add(node.getId());
		
		for (Node child : node.getChildren()) {
			
			if (!visited.contains(child.getId()))
				count += count(visited, child);
		}
		
		return count;
	}
	
	public static class Edge {
		
		private static final int PRIME1 = 31;
		private static final int PRIME2 = 37;

		private int from;		
		private int to;	
		private int weight;
		
		public Edge(int to, int from, int weight) {
			this.to = to;
			this.from = from;
			this.weight = weight;
		}
		
		public int getFrom() {
			return from;
		}

		public void setFrom(int from) {
			this.from = from;
		}

		public int getTo() {
			return to;
		}

		public void setTo(int to) {
			this.to = to;
		}

		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}
		
		@Override
		public int hashCode() {
			int result = 1;
			result = (from + PRIME1) * (to + PRIME1);
			result = result + PRIME2 * weight;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			Edge other = (Edge) obj;
			if (from == other.from && to == other.to && weight == other.weight)
				return true;
			
			if (from == other.to && to == other.from && weight == other.weight)
				return true;
				
			return false;
		}
		
		@Override
		public String toString() {
			return "Edge [From=" + LABELS[from] + " <==> " + "To=" + 
						LABELS[to] + ", Weight=" + weight + "]";
		}
	}
	
	public static class Node {
		
		private int weight;
		
		private int id;
		
		private List<Node> children = new ArrayList<Node>();
		
		public Node(int id) {
			this.id = id;
		}
		
		public Node(int id, int weight) {
			this.id = id;
			this.weight = weight;
		}
		
		public int getId() {
			return id;
		}
		
		public void setId(int id) {
			this.id = id;
		}
		
		public int getWeight() {
			return weight;
		}

		public void setWeight(int weight) {
			this.weight = weight;
		}

		public List<Node> getChildren() {
			return children;
		}

		public void setChildren(List<Node> children) {
			this.children = children;
		}
		
		@Override
		public String toString() {
			return toString(new HashSet<Integer>(), 0);
		}
		
		private String toString(Set<Integer> visited, int indent) {
			
			StringBuilder builder = new StringBuilder();
			
			visited.add(this.getId());
			
			for (int i = 0; i < indent; i++)
				builder.append(" ");
			
			builder.append("Node: [" + LABELS[id] + ":" + weight + "]\n");
			children.stream().forEach(p -> {
				
				if (!visited.contains(p.getId()))
					builder.append(p.toString(visited, indent + 3)); 
			});
			
			return builder.toString();
		}
	}
}
