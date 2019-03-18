package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class CARTExercise1 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		List<Instance> training = generateData(100, 0);
		
		Set<Integer> pool = new HashSet<Integer>();
		pool.add(BlockedArteriesStats.ID);
		pool.add(ChestPainStats.ID);
		pool.add(GoodCirculationStats.ID);
		
		Node root = buildCART(Double.MAX_VALUE, pool, training);
		
		System.out.println(root.toAll(0));
		
		
		List<Instance> testing = generateData(1, 4);
		
		root.classify(testing);	
	}
	
	public static Node buildCART(double gini, Set<Integer> pool, List<Instance> instances) {
		
		if (pool.size() <= 0)
			return null;
		
		Node node = new Node(gini, pool, instances);
		if (node.getStats() != null) {
			pool.remove(node.getID());
		}
		else {
			return null;
		}
				
		node.setLeft(buildCART(node.getStats().getLeft().getGini(), 
				new HashSet<Integer>(pool), node.getStats().getYes()));
		
		node.setRight(buildCART(node.getStats().getRight().getGini(), 
				new HashSet<Integer>(pool), node.getStats().getNo()));

		
		return node;
	}
	
	public static List<Instance> generateData(int size, int seed) {
		
		Random rand = new Random(seed);
		
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		attrs.add(GoodCirculationStats.attr);
		attrs.add(ChestPainStats.attr);
		attrs.add(BlockedArteriesStats.attr);
		attrs.add(HeartDiseaseStats.attr);
		
		Instances training = new Instances("TRAINING", attrs, size);
		
		for (int i = 0; i < size; i++) {
			Instance data = new DenseInstance(5);
			
			data.setValue(GoodCirculationStats.attr, rand.nextInt() % 2 == 0 ? 0 : 1);
			data.setValue(ChestPainStats.attr, rand.nextInt() % 2 == 0 ? 0 : 1);
			data.setValue(BlockedArteriesStats.attr, rand.nextInt() % 2 == 0 ? 0 : 1);
			data.setValue(HeartDiseaseStats.attr, rand.nextInt() % 2 == 0 ? "0" : "1");
		
			training.add(data);
		}
		
		return training;
	}
		

	public static abstract class Stats {
		
		private static final DecimalFormat ff = new DecimalFormat("0.000");

		protected List<Instance> yes = null;
		protected List<Instance> no = null;
		protected Stats left = null;
		protected Stats right = null;
		
		public Stats(List<Instance> instances) {
			yes = filterYes(instances);
			no = filterNo(instances);
		}
		
		public List<Instance> getYes() {
			return yes;
		}
		
		public List<Instance> getNo() {
			return no;
		}
		
		public Stats getLeft() {
			return left;
		}
		
		public Stats getRight() {
			return right;
		}
		
		public double getGini() {
			
			if (left == null && right == null) {
				return gini(yes.size(), no.size());
			}
			else {	
				return gini(left, right);
			}
		}
		
		public void setLeft(Stats left) {
			this.left = left;
		}
		
		public void setRight(Stats right) {
			this.right = right;
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
		
		@Override
		public String toString() {
			return getLabel() + " ===> " + " Yes: " + yes.size() + ", No: " + no.size() + ", gini: " + ff.format(this.getGini());
		}

		protected double gini(double val1, double val2) {
			// gini impurities
			return (1 - Math.pow(val1 / (val1 + val2), 2)) - Math.pow(val2 / (val1 + val2), 2);
		}

		protected double gini(Stats node1, Stats node2) {
			// gini impurities
			double total = 0d;
			double totalLeft = 0d;
			double totalRight = 0d;
			double giniLeft = 0d;
			double giniRight = 0d;
			double result = 0d;

			if (node1 != null) {
				total += node1.getYes().size() + node1.getNo().size();
				totalLeft = node1.getYes().size() + node1.getNo().size();
				giniLeft = node1.getGini();
			}

			if (node2 != null) {
				total += node2.getYes().size() + node2.getNo().size();
				totalRight = node2.getYes().size() + node2.getNo().size();
				giniRight = node2.getGini();
			}

			if (total <= 0) {
				result = 0;
			} else {
				result = (totalLeft / total) * giniLeft + (totalRight / total) * giniRight;
			}

			return result;
		}

		protected abstract String getLabel();
		
		protected abstract int getID();
		
		protected abstract List<Instance> filterYes(List<Instance> instances);
		
		protected abstract List<Instance> filterNo(List<Instance> instances);
		
		public abstract boolean classify(Instance instance);
	}

	public static class GoodCirculationStats extends Stats {
		
		public static final int ID = 1;
		
		public static Attribute attr = new Attribute("goodCirculation");

		public GoodCirculationStats(List<Instance> instances) {
			super(instances);
		}

		@Override
		protected List<Instance> filterYes(List<Instance> instances) {
			// TODO Auto-generated method stub
			return instances.stream().filter(p -> p.value(attr) == 1).collect((Collectors.toList()));
		}
		
		@Override
		protected List<Instance> filterNo(List<Instance> instances) {
			// TODO Auto-generated method stub
			return instances.stream().filter(p -> p.value(attr) == 0).collect((Collectors.toList()));
		}
		
		@Override
		public boolean classify(Instance instance) {
			return instance.value(attr) == 1;
		}
		
		@Override
		protected int getID() {
			return GoodCirculationStats.ID;
		}

		@Override
		protected String getLabel() {
			// TODO Auto-generated method stub
			return "Good Circulation";
		}
	}

	public static class ChestPainStats extends Stats {
		
		public static final int ID = 2;
		
		public static Attribute attr = new Attribute("chestPain");

		public ChestPainStats(List<Instance> instances) {
			super(instances);
		}

		@Override
		protected List<Instance> filterYes(List<Instance> instances) {
			// TODO Auto-generated method stub
			return instances.stream().filter(p -> p.value(attr) == 1).collect((Collectors.toList()));
		}
		
		@Override
		protected List<Instance> filterNo(List<Instance> instances) {
			// TODO Auto-generated method stub
			return instances.stream().filter(p -> p.value(attr) == 0).collect((Collectors.toList()));
		}
		
		@Override
		public boolean classify(Instance instance) {
			return instance.value(attr) == 1;
		}
		
		@Override
		protected int getID() {
			return ChestPainStats.ID;
		}

		@Override
		protected String getLabel() {
			// TODO Auto-generated method stub
			return "Chest Pain      ";
		}
	}

	public static class BlockedArteriesStats extends Stats {
		
		public static final int ID = 3;
		
		public static Attribute attr = new Attribute("blockedArteries");

		public BlockedArteriesStats(List<Instance> instances) {
			super(instances);
		}

		@Override
		protected List<Instance> filterYes(List<Instance> instances) {
			// TODO Auto-generated method stub
			return instances.stream().filter(p -> p.value(attr) == 1).collect((Collectors.toList()));
		}
		
		@Override
		protected List<Instance> filterNo(List<Instance> instances) {
			// TODO Auto-generated method stub
			return instances.stream().filter(p -> p.value(attr) == 0).collect((Collectors.toList()));
		}
		
		@Override
		public boolean classify(Instance instance) {
			return instance.value(attr) == 1;
		}
		
		@Override
		protected int getID() {
			return BlockedArteriesStats.ID;
		}

		@Override
		protected String getLabel() {
			// TODO Auto-generated method stub
			return "Blocked Arteries ";
		}
	}
	
	public static class HeartDiseaseStats extends Stats {
		
		public static final int ID = 4;
		
		private static ArrayList<String> classVal = new ArrayList<String>();
		
		static {
			classVal.add("1");
			classVal.add("0");
		}
		
		public static Attribute attr = new Attribute("heartDisease", classVal);

		public HeartDiseaseStats(List<Instance> instances) {
			super(instances);
		}

		@Override
		protected List<Instance> filterYes(List<Instance> instances) {
			// TODO Auto-generated method stub
			return instances.stream().filter(p -> "0".equals(p.stringValue(attr))).collect((Collectors.toList()));
		}

		@Override
		protected List<Instance> filterNo(List<Instance> instances) {
			// TODO Auto-generated method stub
			return instances.stream().filter(p -> "1".equals(p.stringValue(attr))).collect((Collectors.toList()));
		}
		
		@Override
		public boolean classify(Instance instance) {
			return "1".equals(instance.stringValue(attr));
		}
		
		@Override
		protected int getID() {
			return HeartDiseaseStats.ID;
		}
		
		@Override
		protected String getLabel() {
			// TODO Auto-generated method stub
			return "Heart Disease    ";
		}
	}
	
	public static class Node {
		
		private static final DecimalFormat ff = new DecimalFormat("0.000");

		
		private Node left = null;
		private Node right = null;
		private Stats stats = null;
		Set<Integer> pool = null;
		
		public Node(double gini, Set<Integer> pool, List<Instance> instances) {
			this.pool = new HashSet<Integer>(pool);
			this.stats = choose(gini, instances);
			
			System.err.println(this.stats.getClass().getName() + " -- " + 
					ff.format(this.stats.getGini()) + 
					" Left: " + ff.format(this.stats.getLeft().getGini()) + 
					" Right: " + ff.format(this.stats.getRight().getGini()) + 
					" <-- " + gini);
		}
		
		public void setLeft(Node left) {
			this.left = left;
		}
		
		public void setRight(Node right) {
			this.right = right;
		}
		
		public int getID() {
			return stats.getID();
		}
		
		public Stats getStats() {
			return stats;
		}
		
		public boolean classify(Instance instance) {
			
			if (stats.classify(instance)) {
				if (this.left != null)
					return this.left.classify(instance);
				else
					return this.stats.getLeft().classify(instance);
			}
			else {
				if (this.right != null)
					return this.right.classify(instance);
				else
					return this.stats.getRight().classify(instance);
			}
		}
		
		public void classify(List<Instance> instances) {
			
			for (Instance instance : instances) {
				if (classify(instance)) 
					System.out.println(instance + " --- TRUE");
				else
					System.out.println(instance + " --- FALSE");
			}
		}
		
		public String toAll(int indent) {
			
			StringBuilder builder = new StringBuilder();
			
			builder.append(spaces(indent));
			builder.append(this.toString());
			builder.append("\n");
			
			if (left != null) {
				builder.append(left.toAll(indent + 3));
			}
			else {
				builder.append(spaces(indent + 3));
				builder.append(stats.getLeft().toString());
				builder.append("\n");
			}
			
			if (right != null) {
				builder.append(right.toAll(indent + 3));
			}
			else {
				builder.append(spaces(indent + 3));
				builder.append(stats.getRight().toString());
				builder.append("\n");
			}
			
			return builder.toString();
		}
		
		public String toString() {
			return stats.toString();
		}
		
		protected Stats create(int flag, List<Instance> instances) {
			
			Stats stats = null;
			Stats left = null;
			Stats right = null;
			
			switch(flag) {
			
			case GoodCirculationStats.ID:
				stats = new GoodCirculationStats(instances);
				left = new HeartDiseaseStats(stats.getYes());
				right = new HeartDiseaseStats(stats.getNo());
				stats.setLeft(left);
				stats.setRight(right);
				break;
			case ChestPainStats.ID:
				stats = new ChestPainStats(instances);
				left = new HeartDiseaseStats(stats.getYes());
				right = new HeartDiseaseStats(stats.getNo());
				stats.setLeft(left);
				stats.setRight(right);
				break;
			case BlockedArteriesStats.ID:
				stats = new BlockedArteriesStats(instances);
				left = new HeartDiseaseStats(stats.getYes());
				right = new HeartDiseaseStats(stats.getNo());
				stats.setLeft(left);
				stats.setRight(right);
				break;		
			}
			
			return stats;
		}
		
		protected Stats choose(double ggini, List<Instance> instances) {
			
			Stats target = null;
			double min = ggini;
			
			for (Integer id : this.pool) {
				
				Stats stats = create(id, instances);
				
				double gini = stats.getGini();
				
				if (min >= gini) {
					min = gini;
					target = stats;
				}
			}
			
			return target;
		}
		
		private String spaces(int indent) {
			
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < indent; i++)
				builder.append(" ");
			
			return builder.toString();
		}
		
	}

}
