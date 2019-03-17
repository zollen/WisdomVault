package machinelearning;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ClassificationPrototype {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//========================================
		// Chest Pain   | Good Blood Circulation  | Blocked Arteries  ||   Heart Disease
		//    N         |         N               |      N            ||   N
		//    Y         |         Y               |      Y            ||   Y
		//    Y         |         Y               |      N            ||   N
		//    ............................................................
		
		// Summary
		// =======
		// Chest Pain <==> Heart Disease
		//		+- Y  <==> Y:105, N:39
		//		+- N  <==> Y:34,  N:125 
		//
		// Good Blood Circulation <==> Heart Disease
		//		+- Y  <==> Y:37,  N:127
		//		+- N  <==> Y:100, N:33
		//
		// Blocked Arteries <==> Heart Disease
		//		+- Y  <==> Y:92, N:31
		//		+- N  <==> Y:45, N:129
		
		Map<String, Node> map = new HashMap<String, Node>();
		
		String [] labels = {
				"Chest Pain",
				"Good Blood Circulation",
				"Blocked Arteries"			
		};
		
		map.put(labels[0] + "(Y)", new Node(labels[0], 105, 39));
		map.put(labels[0] + "(N)", new Node(labels[0], 34, 125));
		map.put(labels[1] + "(Y)", new Node(labels[1], 37, 127));
		map.put(labels[1] + "(N)", new Node(labels[1], 100, 33));
		map.put(labels[2] + "(Y)", new Node(labels[2], 92, 31));
		map.put(labels[2] + "(N)", new Node(labels[2], 45, 129));
		
		

		// Classification And Regression Trees (CART)
		Node root = buildCART(labels, map);
		
		System.out.println(root.toAll(0));
		
	}
	
	public static Node buildCART(String [] labels, Map<String, Node> map) {
		
		Node node = null;
		
		{
			String key = null;
			double min = Double.MAX_VALUE;
			
			for (String label : labels) {
			
				Node yy = map.get(label + "(Y)");
				if (yy == null)
					continue;
			
				Node nn = map.get(label + "(N)");
				if (nn == null)
					continue;
			
				double yhdY = yy.getYes();
				double yhdN = yy.getNo();
			
				double nhdY = nn.getYes();
				double nhdN = nn.getNo();
			
				Node nodeY = new Node(label + "(Y)", yhdY, yhdN);
				Node nodeN = new Node(label + "(N)", nhdY, nhdN);
				Node parent = new Node(label, nodeY, nodeN);
				
				if (min > parent.getGini()) {
					min = parent.getGini();
					key = label;
					node = parent;
				}
			}
		
			if (key != null) {
				map.remove(key + "(Y)");
				map.remove(key + "(N)");
			}
		}
		
		
		return node;
	}
	
	
	public static class Node {
		
		private static final DecimalFormat ff = new DecimalFormat("0.000");
		
		private String label = null;
		private Node left = null;
		public Node right = null;
		
		private double yes = 0d;
		private double no = 0d;
		private double gini = 0d;
		
		public Node(String label, Node left, Node right) {
			this.label = label;
			this.left = left;
			this.right = right;
			this.gini = gini(left, right);
		}
		
		public Node(String label, double yes, double no) {
			this.label = label;
			this.yes = yes;
			this.no = no;
			this.gini = gini(yes, no);
		}
			
		public double getYes() {
			return yes;
		}
		
		public void setYes(double yes) {
			this.yes = yes;
		}
		
		public double getNo() {
			return no;
		}
		
		public void setNo(double no) {
			this.no = no;
		}
		
		public double getGini() {
			return gini;
		}
		
		public String toAll(int indent) {
			
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < indent; i++)
				builder.append(" ");
			
			builder.append(this.toString());
			builder.append("\n");
			
			return builder.toString() + (left != null ? left.toAll(indent + 3) : "") + 
					(right != null ? right.toAll(indent + 3) : "");
		}
		
		public String toString() {
			String yesStr = yes > 0 ? "Yes: [" + yes + "]" : "";
			String noStr = no > 0 ? "No: [" + no + "]" : "";
			String giniStr = gini > 0 ? "Gini: [" + ff.format(gini) + "]" : "";
			
			StringBuilder builder = new StringBuilder();
			builder.append(label);
			
			boolean inserted = false;
			
			if (yesStr.length() > 0) {
				builder.append(" ===> ");
				builder.append(yesStr);
				inserted = true;
			}
			
			if (noStr.length() > 0) {
				if (inserted)
					builder.append(", ");
				else
					builder.append(" ===> ");
				builder.append(noStr);
			}
			
			if (giniStr.length() > 0) {
				if (inserted)
					builder.append(", ");
				else
					builder.append(" ===> ");
				builder.append(giniStr);
			}
			
			return builder.toString();
		}
		
		protected double gini(double left, double right) {
			// gini impurities
			return (1 - Math.pow(left / (left + right), 2)) - Math.pow(right / (left + right), 2);
		}
		
		protected double gini(Node node1, Node node2) {
			// gini impurities
			double total = 0d;
			double totalLeft = 0d;
			double totalRight = 0d;
			double giniLeft = 0d;
			double giniRight = 0d;
			double result = 0d;
			
			if (node1 != null) {
				total += node1.getYes() +  node1.getNo();
				totalLeft = node1.getYes() +  node1.getNo();
				giniLeft = node1.getGini();
			}
			
			if (node2 != null) {
				total += node2.getYes() +  node2.getNo();
				totalRight = node2.getYes() +  node2.getNo();
				giniRight = node2.getGini();
			}
			
			if (total <= 0) {
				result = 0;
			}
			else {
				result = (totalLeft / total) * giniLeft +
						(totalRight / total) * giniRight;
			}	
			
			return result;
		}
		
	}

}
