package optimization;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConstructCodeAnalysis {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Map<String, Double> dist = new LinkedHashMap<String, Double>();
		
		// L(A)->00,   L(B)->01,  L(C)->100, L(D)->101, L(E)->11000
		// P(A)= 0.3,  P(B)=0.27, P(C)=0.21, P(D)=0.17, P(E)=0.05
		// L(A)=2,     L(B)=2,    L(C)=3,    L(D)=3,    L(E)=5
		
		dist.put("A", Double.valueOf(0.3));
		dist.put("B", Double.valueOf(0.27));
		dist.put("C", Double.valueOf(0.21));
		dist.put("D", Double.valueOf(0.17));
		dist.put("E", Double.valueOf(0.05));
		
		List<String> list = new ArrayList<String>();
		list.add("A");
		list.add("B");
		list.add("C");
		list.add("D");
		list.add("E");
		
		Node<String> root = new Node<String>();
		construct(root, 6);

		
		String [] labels = { "A", "B", "C", "D", "E" };
		
		AtomicInteger idx = new AtomicInteger();
		dist.entrySet().forEach(p -> {

			int depth = -1 * (int) Math.floor(Math.log(p.getValue()) / Math.log(2));
			String label = labels[idx.getAndIncrement()];
			prune(root, label, depth);		
		});	

		System.out.println(root);

	}
	
	public static boolean prune(Node<String> node, String label, int depth) {
		
		boolean res = false;
		
		if (node != null && depth <= 0) {
			node.setValue(label);
			node.setLeft(null);
			node.setRight(null);
			
			return true;
		}
		
		if (node.getLeft() != null && node.getLeft().getValue() == null) {
			res = prune(node.getLeft(), label, depth - 1);
		}

		
		if (node.getRight() != null && node.getRight().getValue() == null && !res) {
			res = prune(node.getRight(), label, depth - 1);
		}
		
		return res;
	}
	
	public static Node<String> construct(Node<String> node, int depth) {
		
		if (depth <= 0) {
			return null;
		}
		
		Node<String> left = new Node<String>();
		Node<String> right = new Node<String>();
		
		node.setLeft(construct(left, depth - 1));
		node.setRight(construct(right, depth - 1));
		
		return node;
	}
	
	@SuppressWarnings("rawtypes")
	public static class Node<T> {
		
		private Node<T> parent = null;
		private Node<T> left = null;
		private Node<T> right = null;
		private T value = null;
		
		public Node() {}
		public Node(T val) { this.value = val; }
		public Node(T val, Node<T> left, Node<T> right) {
			this.value = val;
			this.left = left;
			this.right = right;
		}
		
		public Node<T> getLeft() {
			return left;
		}
		public void setLeft(Node<T> left) {
			this.left = left;
			if (left != null)
				left.setParent(this);
		}
		public Node<T> getRight() {
			return right;
		}
		public void setRight(Node<T> right) {
			this.right = right;
			if (right != null)
				right.setParent(this);
		}
		public T getValue() {
			return value;
		}
		public void setValue(T value) {
			this.value = value;
		}
		
		public Node<T> getParent() {
			return parent;
		}
		
		public void setParent(Node<T> parent) {
			this.parent = parent;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			
			_toString(this, builder, 0);
			
			return builder.toString();
		}
		
		private void _toString(Node node, StringBuilder builder, int indent) {
			
			for (int i = 0; i < indent; i++)
				if (i < indent - 3)
					builder.append(" ");
				else
				if (i == indent - 3)
					builder.append("+");
				else
					builder.append("-");
			
			if (node.getParent() != null) {
				if (node.getParent().getLeft() != null && node.getParent().getLeft() == node)
					builder.append("0-");
				if (node.getParent().getRight() != null && node.getParent().getRight() == node)
					builder.append("1-");
			}
			
			builder.append("[" + (node.getValue() != null ? node.getValue() : "") + "]" + "\n");
			
			if (node.getLeft() != null) {
				_toString(node.getLeft(), builder, indent + 3);
			}
			
			if (node.getRight() != null) {
				_toString(node.getRight(), builder, indent + 3);
			}
		}		
	}

}
