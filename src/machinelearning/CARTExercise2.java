package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class CARTExercise2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// defining data format
		
		ArrayList<String> vals = new ArrayList<String>();
		vals.add("1");
		vals.add("0");
		
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("goodCirculation", vals);
		Attribute attr2 = new Attribute("chestPain", vals);
		Attribute attr3 = new Attribute("blockedArteries", vals);
		Attribute attr4 = new Attribute("heartDisease", vals);
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		attrs.add(attr4);
		
		// training
	
		List<Instance> training = generateData(100, 0, attrs);
		
		Node gc = new Node(attr1, vals, training);
		Node hd1 = new Node(attr4, vals);
		Node hd2 = new Node(attr4, vals);
		
		gc.add("1", hd1);
		gc.add("0", hd2);
		
		System.out.println(gc.toAll());
		
	}
	
	public static List<Instance> generateData(int size, int seed, ArrayList<Attribute> attrs) {
		
		Random rand = new Random(seed);
		
		Instances training = new Instances("TRAINING", attrs, size);
		
		for (int i = 0; i < size; i++) {
			Instance data = new DenseInstance(5);
			
			int gc = rand.nextInt() % 2 == 0 ? 0 : 1;
			int cp = rand.nextInt() % 2 == 0 ? 0 : 1;
			int ba = rand.nextInt() % 2 == 0 ? 0 : 1;
			
			data.setValue(attrs.get(0), String.valueOf(gc));
			data.setValue(attrs.get(1), String.valueOf(cp));
			data.setValue(attrs.get(2), String.valueOf(ba));
		
			double diag = rand.nextDouble() + (gc * -0.6 + cp * 0.2 + ba * 0.3);
			
			data.setValue(attrs.get(3), diag < 0.6 ? "0" : "1");
		
			training.add(data);
		}
		
		return training;
	}
	
	
	public static class Node {
		
		private static final DecimalFormat ff = new DecimalFormat("0.000");
				
		private Map<String, List<Instance>> data = new HashMap<String, List<Instance>>();
		private Map<String, Node> children = new HashMap<String, Node>();
		private List<Instance> inputs = null;
		private Attribute attr = null;
		private String label = null;
		private List<String> values = null;
		
		public Node(Attribute attr, List<String> values) {
			this(attr, values, new ArrayList<Instance>());
		}
		
		public Node(Attribute attr, List<String> values, List<Instance> instances) {
			
			this.attr = attr;
			this.label = this.attr.name();
			this.values = values;
			
			this.setInstances(instances);
		}
		
		public void add(String value, Node child) {
			child.setInstances(this.data.get(value));
			this.children.put(value, child);
		}
		
		public String label() {
			return label;
		}
		
		public void setInstances(List<Instance> instances) {
			
			this.inputs = instances;
			
			for (String value : this.values) {
				data.put(value, filter(value, this.inputs));
			}
		}
		
		public String toAll() {
			return toAll("", 0);
		}
		
		@Override
		public String toString() {
				
			return label() + " ==> " +
					this.values.stream().map(p -> "[" + p + "]: " + 
							data.get(p).size()).collect(Collectors.joining(", ")) + 
							"   Gini: " + ff.format(this.gini());
		}
		
		private List<Instance> filter(String value, List<Instance> instances) {
			return instances.stream().filter(p -> value.equals(p.stringValue(attr))).collect(Collectors.toList());
		}
		
		private double gini() {
			// gini impurities
			DoubleAdder sum = new DoubleAdder();
			
			if (inputs.size() <= 0)
				return 0.0;
			
			if (children.size() <= 0) {
		
				data.entrySet().stream().forEach(p -> {
					sum.add(Math.pow((double) p.getValue().size() / inputs.size(), 2));
				});
				
				return 1 - sum.doubleValue();
			}
			else {
				
				children.entrySet().stream().forEach(p -> {
					
					sum.add((double) data.get(p.getKey()).size() / inputs.size() * p.getValue().gini());
				});
				
				return sum.doubleValue();
			}
		}
		
		private String indent(String value, int indent) {
			
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < indent; i++) {
				if (i < 3)
					builder.append(" ");
				else
				if (i == 3)
					builder.append("+");
				else
				if (i > 3)
					builder.append("-");
			}
				
			if (builder.length() > 0)
				builder.append("--<" + value + ">- ");
			
			return builder.toString();
		}
		
		private String toAll(String value, int indent) {
			
			StringBuilder builder = new StringBuilder();
			
			builder.append(indent(value, indent));
			builder.append(this);
			builder.append("\n");
			
			children.entrySet().forEach(p -> {
				
				builder.append(p.getValue().toAll(p.getKey(), indent + 5));
			});
			
			return builder.toString();
		}

	}
}
