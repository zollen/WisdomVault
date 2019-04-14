package optimization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

/**
 * Prim algorithm results in a minimum spanning tree against a
 * connected, uni/by-directional, weighted graph with unknown start and end states.
 * 
 * The algorithm computes the spanning tree of traversing all nodes with the least amount of cost.
 * (without circles)
 * 
 * Difference between uni-directional and bi-directional
 * 	1. register(...)
 * 	2. Edge.equals(...)
 * 
 * https://www.youtube.com/watch?v=cplfcGZmX7I
 * @author zollen
 *
 */
public class PrimExercise {
	
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
	/* C */		"   3,  4,  0,  5,  1,  6,  0;" +
	/* D */		"   3,  0,  5,  0,  0,  7,  0;" +
	/* E */		"   0,  3,  1,  0,  0,  8,  0;" +
	/* F */		"   0,  0,  6,  7,  8,  0,  9;" +
	/* G */		"   0,  0,  0,  0,  0,  9,  0 " +
				"]");
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		// Prim Algo begin
		
		List<Edge> pool = new ArrayList<Edge>();
		int last = pool.size();
		
		Set<Integer> visited = new HashSet<Integer>();
		
		// randomly pick a start node
		visited.add(0);   /* let's pick A */
			
		while (pool.size() < LABELS.length - 1) {
			
			last = pool.size();
			
			int min = Integer.MAX_VALUE;
			Edge edge = null;
			for (Integer from : visited) {
			
				for (int to = 0; to < A.numRows; to++) {
					int dist = (int) A.get(to, from);
					if (dist > 0 && min > dist && !visited.contains(to)) {
						min = dist;
						edge = new Edge(to, from, dist);
					}
				}
			}
		
			if (edge != null) {
				visited.add(edge.getTo());
				pool.add(edge);
			}
			
			if (last == pool.size())
				break;
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
