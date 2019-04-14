package optimization.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class BINode {
	
	private static String [] LABELS = null;
	
	private int weight;
	
	private int id;
	
	private List<BINode> children = new ArrayList<BINode>();
	
	public BINode(int id) {
		this.id = id;
	}
	
	public BINode(int id, int weight) {
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

	public List<BINode> getChildren() {
		return children;
	}

	public void setChildren(List<BINode> children) {
		this.children = children;
	}
	
	public int score() {
		
		int count = this.count();
		int depth = this.depth();
		
		// tree with largest number of nodes and minimum depth has the highest score
		return (int) Math.pow(count, 2) + (int) Math.pow(count - depth, 2);
	}
	
	public int count() {
		return getCount(new HashSet<Integer>());
	}
	
	public int depth() {
		return getDepth(new HashSet<Integer>(), 0);
	}
	
	@Override
	public String toString() {
		return toString(new HashSet<Integer>(), 0);
	}
	
	public static void setLABELS(String [] labels) {
		LABELS = labels;
	}
	
	public static List<BINode> construct(List<BIEdge> pool) {
		
		Map<Integer, BINode> nodes = new HashMap<Integer, BINode>();
		
		for (BIEdge edge : pool) {
			
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
			int score = p.getValue().score();
			if (max.intValue() < score) {
				max.set(score);
			}
		});
		
		
		List<BINode> candidates = new ArrayList<BINode>();
		nodes.entrySet().stream().forEach(p -> {
			int score = p.getValue().score();
			if (max.intValue() == score) {
				candidates.add(p.getValue());
			}
		});
		
		return candidates;
	}
	
	public static void create(Map<Integer, BINode> nodes, int from , int to, int weight) {
		
		BINode node = nodes.get(from);
		
		if (node == null) {	
			BINode parent = new BINode(from, weight);
			BINode child = nodes.get(to);
			if (child == null)
				child = new BINode(to, weight);
			parent.getChildren().add(child);
			
			nodes.put(from, parent);
			nodes.put(to, child);
		}
		else {
			BINode child = nodes.get(to);
			if (child == null)
				child = new BINode(to, weight);
			node.getChildren().add(child);
			
			nodes.put(to, child);
		}
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
	
	private int getDepth(Set<Integer> visited, int depth) {
		
		if (visited.contains(this.getId()))
			return depth;
		
		int max = 0;
		
		visited.add(this.getId());
		
		for (BINode child : this.getChildren()) {
			
			int dep = child.getDepth(visited, depth + 1);
			if (max < dep)
				max = dep;
		}
		
		return max;
	}
	
	private int getCount(Set<Integer> visited) {
		
		int count = 1;
		
		visited.add(this.getId());
		
		for (BINode child : this.getChildren()) {
			
			if (!visited.contains(child.getId()))
				count += child.getCount(visited);
		}
		
		return count;
	}
}