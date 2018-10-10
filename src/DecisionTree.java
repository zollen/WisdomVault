import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DecisionTree<T> {
	
	private INode<T> root = null;

	public DecisionTree(INode<T> root) {
		this.root = root;
	}
	
	public void add(INode<T> child) {
		root.add(child);
	}
	
	public void add(double prob, INode<T> child) {
		root.add(prob, child);
	}
	
	public void add(String desc, INode<T> child) {
		root.add(desc, child);
	}
	
	public void add(String desc, double prob, INode<T> child) {
		root.add(desc, prob, child);
	}
	
	public void analysis() {
		
		analysis(root);
	}
	
	private void analysis(INode<T> node) {
		
		Map<Branch, INode<T>> children = node.children();
		Set<Branch> keys = children.keySet();
		
		switch(node.type) {
		case DecisionNode.NODETYPE:
			
		break;
		case ChanceNode.NODETYPE:
			
		break;
		case UtilityNode.NODETYPE:
			
		break;
		}
		
		for (Branch key : keys) {
			
			INode<T> child = children.get(key);
				
			analysis(child);
		}
	}
	
	@Override
	public String toString() {
		return toString(0, null, root);
	}
	
	private String toString(int indent, Branch brch, INode<T> parent) {
		
		StringBuilder builder = new StringBuilder();
		Map<Branch, INode<T>> children = parent.children();
		Set<Branch> keys = children.keySet();
		
		for (int i = 0; i < indent; i++)
				builder.append(" ");
		
		if (brch != null)
			builder.append(brch + " ==> ");
		
		switch(parent.type) {
		case DecisionNode.NODETYPE:
			builder.append("? ");
		break;
		case ChanceNode.NODETYPE:
			builder.append("% ");
		break;
		case UtilityNode.NODETYPE:
			builder.append("@ ");
		break;
		}
		
		builder.append(parent.data.toString() + "\n");
		
		for (Branch key : keys) {
			
			INode<T> child = children.get(key);
				
			builder.append(toString(indent + 4, key, child));
		}
		
		return builder.toString();
	}
	
	public static <T> DecisionNode<T> createDecisionNode(T data) {
		return new DecisionNode<T>(data, DecisionNode.NODETYPE);
	}
	
	public static <T> ChanceNode<T> createChanceNode(T data) {
		return new ChanceNode<T>(data, ChanceNode.NODETYPE);
	}
	
	public static <T> UtilityNode<T> createUtilityNode(T data) {
		return new UtilityNode<T>(data, UtilityNode.NODETYPE);
	}
	
	
	
	
	
	public static abstract class INode<T> {
		
		protected T data = null;
		protected int type = -1;
		protected Map<Branch, INode<T>> children = null;
		
		public INode(T data, int type) {
			this.data = data;
			this.type = type;
			this.children = new HashMap<Branch, INode<T>>();
		}	
		
		public void add(INode<T> child) {
			children.put(new Branch(), child);
		}
		
		public void add(double prob, INode<T> child) {
			children.put(new Branch(prob), child);
		}
		
		public void add(String desc, INode<T> child) {
			children.put(new Branch(desc), child);
		}
		
		public void add(String desc, double prob, INode<T> child) {
			children.put(new Branch(desc, prob), child);
		}
		
		public Map<Branch, INode<T>> children() {
			return children;
		}
	}
	
	public static class DecisionNode<T> extends INode<T> {
		
		private static final int NODETYPE = 100;

		private DecisionNode(T data, int type) {
			super(data, type);
		}
	}
	
	public static class ChanceNode<T> extends INode<T> {
		
		private static final int NODETYPE = 101;
		
		private ChanceNode(T data, int type) {
			super(data, type);
		}
	}
	
	public static class UtilityNode<T> extends INode<T> {
		
		private static final int NODETYPE = 102;
		
		private UtilityNode(T data, int type) {
			super(data, type);
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
	
}
