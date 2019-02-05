import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HuffmanCodeExercise2 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// P(A)= 0.3,  P(B)=0.27, P(C)=0.21, P(D)=0.17, P(E)=0.05
		
		List<Node<String>> pool = new ArrayList<Node<String>>();
		pool.add(new Node<String>("A", 0.4d));
		pool.add(new Node<String>("B", 0.15d));
		pool.add(new Node<String>("C", 0.14d));
		pool.add(new Node<String>("D", 0.12d));
		pool.add(new Node<String>("E", 0.1d));
		pool.add(new Node<String>("F", 0.09d));
		
		Node<String> root = huffmanCode(pool);
		
		System.out.println(root);
		
		printCodes(root);
		
		double result = -1 * (0.4 * Math.log(0.4d) / Math.log(2) + 0.15 * Math.log(0.15d) / Math.log(2) +
						0.14 * Math.log(0.14) / Math.log(2) + 0.12 * Math.log(0.12) / Math.log(2) + 
						0.1 * Math.log(0.1) / Math.log(2) + 0.09 * Math.log(0.09) / Math.log(2));
		
		System.out.println("Ent(a,b,c,d,e,f): " + result);
		
		double bpc = 0.4 * 1 + 0.15 * 3 + 0.14 * 3 + 0.12 * 3 + 0.1 * 4 + 0.09 * 4;
		System.out.println("Bpc(Tr, X): " + bpc);
		
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
		
		return pool.remove(0);
	}
	
	public static String scanCodes(Node<String> node, String data) throws Exception {
		
		int i = 0;
		StringBuilder builder = new StringBuilder();
		while (i < data.length() && i >= 0) {
			i = _scanCodes(node, data, i, builder);
		}	
		
		if (i < 0)
			throw new Exception("Invalid String: " + data);
		
		return builder.toString();
	}
	
	public static int _scanCodes(Node<String> node, String data, int position, StringBuilder builder) {
		
		if (!node.getValue().equals("X")) {
			
			builder.append(node.getValue());
			return position;
		}
		
		if (position < data.length() && data.charAt(position) == '0' && node.getLeft() != null) {
			return _scanCodes(node.getLeft(), data, position + 1, builder);
		} 
		else 
		if (position < data.length() && data.charAt(position) == '1' && node.getRight() != null) {
			return _scanCodes(node.getRight(), data, position + 1, builder);
		}
		
		return -1;
	}
	
	public static void printCodes(Node<String> node) {
		
		StringBuilder builder = new StringBuilder();
		
		_printCodes(node, builder);
	}
	
	public static void _printCodes(Node<String> node, StringBuilder builder) {
		
		if (node.getLeft() == null && node.getRight() == null) {
			System.out.println(String.format("%-5s ====> %s", builder.toString(), node.getValue()));
			return;
		}
		
		if (node.getLeft() != null) {
			StringBuilder buf = new StringBuilder(builder);
			buf.append("0");
			_printCodes(node.getLeft(), buf);
		}
		
		if (node.getRight() != null) {
			StringBuilder buf = new StringBuilder(builder);
			buf.append("1");
			_printCodes(node.getRight(), buf);
		}	
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
			return (int) (this.getWeight() * 10000 - o.getWeight() * 10000);
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
