import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HuffmanCodeExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// P(A)= 0.3,  P(B)=0.27, P(C)=0.21, P(D)=0.17, P(E)=0.05
		// L(A)=2,     L(B)=2,    L(C)=3,    L(D)=3,    L(E)=5
		
		List<Node<String>> pool = new ArrayList<Node<String>>();
		pool.add(new Node<String>("A", 0.3d));
		pool.add(new Node<String>("B", 0.27d));
		pool.add(new Node<String>("C", 0.21d));
		pool.add(new Node<String>("D", 0.17d));
		pool.add(new Node<String>("E", 0.05d));
		
		Node<String> root = huffmanCode(pool);
		
		System.out.println(root);
		
	}
	
	public static Node<String> huffmanCode(List<Node<String>> pool) {

		while (pool.size() > 1) {
			
			Collections.sort(pool);

			Node<String> node1 = null;
			Node<String> node2 = null;

			if (pool.size() > 0)
			node1 = pool.remove(0);

			if (pool.size() > 0)
			node2 = pool.remove(0);

			Node<String> node = new Node<String>("X", node1, node2);

			pool.add(node);
		}
		
		return pool.parallelStream().filter(p -> p.getWeight() == 1).distinct().findFirst().orElse(null);
	}
		
	
	
	@SuppressWarnings("rawtypes")
	public static class Node<T> implements Comparable<Node<T>> {
		
		private Node<T> parent = null;
		private Node<T> left = null;
		private Node<T> right = null;
		private T value = null;
		private double weight = 0d;
		
		public Node() {}
		
		public Node(T val) { 
			this(val, null, null);
		}
		
		public Node(T val, double weight) {
			this(val, null, null, weight);
		}
		
		public Node(T val, Node<T> left, Node<T> right, double weight) {
			this(val, left, right);
			this.setWeight(weight);
		}
		
		public Node(T val, Node<T> left, Node<T> right) {
			this.setValue(val);
			this.setLeft(left);
			this.setRight(right);
			this.setWeight((left != null ? left.getWeight() : 0d) +
						(right != null ? right.getWeight() : 0d));
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
		
		public double getWeight() {
			return weight;
		}
		public void setWeight(double weight) {
			this.weight = weight;
		}
		
		public String toLocal() {
			return "value=" + value + ", weight=" + weight;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			
			_toString(this, builder, 0);
			
			return builder.toString();
		}
		
		@Override
		public int compareTo(Node<T> o) {
			// TODO Auto-generated method stub
			return (int) (this.getWeight() * 100 - o.getWeight() * 100);
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
			
			builder.append("[" + 
					(node.getValue() != null ? node.getValue() : "") + 
					(node.getWeight() > 0 ? "], [" + node.getWeight() + "]" : "]")
					+ "\n");
			
			if (node.getLeft() != null) {
				_toString(node.getLeft(), builder, indent + 3);
			}
			
			if (node.getRight() != null) {
				_toString(node.getRight(), builder, indent + 3);
			}
		}	
	}

}
