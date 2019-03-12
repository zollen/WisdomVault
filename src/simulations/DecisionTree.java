package simulations;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.Set;

public class DecisionTree<T extends DecisionTree.Computable> {
	
	private static final int DESCISION_NODETYPE = 100;
	private static final int CHANCE_NODETYPE = 101;
	private static final int UTILITY_NODETYPE = 102;
	
	private Node<T> root = null;

	public DecisionTree(Node<T> root) {
		this.root = root;
	}
	
	public void add(Node<T> child) {
		root.add(child);
	}
	
	public void add(double prob, Node<T> child) {
		root.add(prob, child);
	}
	
	public void add(String desc, Node<T> child) {
		root.add(desc, child);
	}
	
	public void add(String desc, double prob, Node<T> child) {
		root.add(desc, prob, child);
	}
	
	public double analysis() {	
		return root.analysis();
	}
	
	@Override
	public String toString() {
		return root.toString(0, null);
	}
	
	public static <T extends DecisionTree.Computable> DecisionNode<T> createDecisionNode(T data) {
		return new DecisionNode<T>(data);
	}
	
	public static <T extends DecisionTree.Computable> ChanceNode<T> createChanceNode(T data) {
		return new ChanceNode<T>(data);
	}
	
	public static <T extends DecisionTree.Computable> UtilityNode<T> createUtilityNode(T data) {
		return new UtilityNode<T>(data);
	}
	
	
	
	
	
	public static abstract class Node<T extends DecisionTree.Computable> {
		
		protected T data = null;
		protected int type = -1;
		protected Map<Branch, Node<T>> children = null;
		
		public Node(T data, int type) {
			this.data = data;
			this.type = type;
			this.children = new LinkedHashMap<Branch, Node<T>>();
		}	
		
		public void add(Node<T> child) {
			children.put(new Branch(), child);
		}
		
		public void add(double prob, Node<T> child) {
			children.put(new Branch(prob), child);
		}
		
		public void add(String desc, Node<T> child) {
			children.put(new Branch(desc), child);
		}
		
		public void add(String desc, double prob, Node<T> child) {
			children.put(new Branch(desc, prob), child);
		}
		
		public Map<Branch, Node<T>> children() {
			return children;
		}
		
		public abstract double analysis(); 
		
		protected abstract String prefix();
		
		protected String toString(int indent, Branch brch) {
			StringBuilder builder = new StringBuilder();
			Map<Branch, Node<T>> children = this.children();
			Set<Branch> keys = children.keySet();
			
			for (int i = 0; i < indent; i++)
					builder.append(" ");
			
			if (brch != null)
				builder.append(brch + " ==> " + this.prefix() + " ");
			
			builder.append("[" + this.data.toString() + "]\n");
			
			for (Branch key : keys) {
				
				Node<T> child = children.get(key);
					
				builder.append(child.toString(indent + 4, key));
			}
			
			return builder.toString();
		}
		
		protected Collection<Double> calculate() {
			
			Set<Branch> branches = children.keySet();
			List<Double> list = new ArrayList<Double>();
			
			for (Branch branch : branches) {
				
				double tmp = 1d;
				Node<T> child = children.get(branch);
				
				switch(child.type) {
				case DESCISION_NODETYPE:
				case CHANCE_NODETYPE: 
					if (branch.prob != null)
						tmp = branch.prob;
	
					list.add(tmp * child.analysis());
				break;
				case UTILITY_NODETYPE:
					if (branch.prob != null)
						tmp = branch.prob;
					
					list.add(tmp * child.analysis());						
				break;
				}
			}
			
			return list;		
		}
	}
	
	public static class DecisionNode<T extends DecisionTree.Computable> extends Node<T> {
		
		private DecisionNode(T data) {
			super(data, DESCISION_NODETYPE);
		}
		
		@Override
		public double analysis() {
			
			Collection<Double> list = calculate();
			
			OptionalDouble dd = list.stream().mapToDouble(b -> b).max();
			if (dd != null && dd.isPresent())
				return dd.getAsDouble();
			
			return 0d;
		}
		
		@Override
		protected String prefix() {
			return "?";
		}
		
		@Override
		public String toString(int indent, Branch brch) {
			return super.toString(indent, brch);
		}
	}
	
	public static class ChanceNode<T extends DecisionTree.Computable> extends Node<T> {
		
		private ChanceNode(T data) {
			super(data, CHANCE_NODETYPE);
		}
		
		@Override
		public double analysis() {
			
			Collection<Double> list = calculate();
			
			return list.stream().mapToDouble(b -> b).sum();
		}
		
		@Override
		protected String prefix() {
			return "%";
		}
		
		@Override
		public String toString(int indent, Branch brch) {
			return super.toString(indent, brch);
		}
	}
	
	public static class UtilityNode<T extends DecisionTree.Computable> extends Node<T> {
		
		private UtilityNode(T data) {
			super(data, UTILITY_NODETYPE);
		}
		
		@Override
		public double analysis() {
			return data.value();
		}
		
		@Override
		protected String prefix() {
			return "@";
		}
		
		@Override
		public String toString(int indent, Branch brch) {
			return super.toString(indent, brch);
		}
	}
	
	public static class Branch {
		
		private static final Random rand = new Random();
		
		private String id = null;
		private Double prob = null;	
		private String desc = null;
		
		public Branch() {
			this.id = String.valueOf(System.nanoTime()) + String.valueOf(rand.nextInt());
			this.prob = null;
			this.desc = "";
		}
		
		public Branch(double prob) {
			this.id = String.valueOf(System.nanoTime()) + String.valueOf(rand.nextInt());
			this.prob = prob;
			this.desc = null;
		}
		
		public Branch(String desc) {
			this.id = String.valueOf(System.nanoTime()) + String.valueOf(rand.nextInt());
			this.prob = null;
			this.desc = desc;
		}
		
		public Branch(String desc, double prob) {
			this.id = String.valueOf(System.nanoTime()) + String.valueOf(rand.nextInt());
			this.prob = prob;
			this.desc = desc;
		}
		
		@Override
		public boolean equals(Object o) {
			Branch b = (Branch) o;
			return id.equals(b.id);
		}
	
		@Override
		public int hashCode() {
			return id.hashCode();
		}
		
		@Override
		public String toString() {
			return (desc != null ? desc.toString() : "") + (prob != null ? prob.toString() : "");
		}
	}
	
	public static interface Computable {
		
		public double value();
		
	}
	
}
