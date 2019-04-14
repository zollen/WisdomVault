package optimization;

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

import machinelearning.classifier.PlaceHolder;

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
public class KruskalExercise {
	
	private static final String [] LABELS = { "A", "B", "C", "D", "E", "F", "G" };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		// A - 2 -> B
		//			B - 4 -> C
		//			B - 3 -> E
		// A <- 2 - B
		// A - 3 -> D
		//			D - 5 -> C
		//			D - 7 -> F
		// A <- 3 - D			
		// A - 3 -> C
		// 			C - 4 -> B
		//			C - 5 -> D
		//			C - 1 - >E
		// A <- 3 -	C
		// D - 5 -> C
		// D - 3 -> A
		// D - 7 -> F
		//			F - 9 -> G
		// D <- 7 - F
		// E - 3 -> B
		// E - 1 -> C
		// E - 8 -> F
		eq.process("A = [ " + 
				/*  A,  B,  C,  D,  E,  F,  G */
	/* A */ 	"	0,  2,  3,  3,  0,  0,  0;" +
	/* B */		"   2,  0,  4,  0,  3,  0,  0;" +
	/* C */		"   3,  4,  0,  5,  1,  0,  0;" +
	/* D */		"   3,  0,  5,  0,  0,  7,  0;" +
	/* E */		"   0,  3,  1,  0,  0,  8,  0;" +
	/* F */		"   0,  0,  0,  7,  8,  0,  9;" +
	/* G */		"   0,  0,  0,  0,  0,  9,  0 " +
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

		System.out.println(construct(pool));
	}
	
	public static Node construct(List<Edge> pool) {
		
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
		PlaceHolder<Node> holder = new PlaceHolder<Node>();
		nodes.entrySet().stream().forEach(p -> {
			int score = score(p.getValue());
			if (max.intValue() < score) {
				max.set(score);
				holder.data(p.getValue());
			}
		});
		
		return holder.data();
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
