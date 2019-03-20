package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import machinelearning.CARTExercise2.Node.Strategy;
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
		
		
		// defining data dictionary
		
		Map<Attribute, List<String>> definition = new LinkedHashMap<Attribute, List<String>>();
		definition.put(attr1, vals);
		definition.put(attr2, vals);
		definition.put(attr3, vals);
		definition.put(attr4, vals);

		// training
		
		List<Instance> training = generateData(100, 0, attrs);
		
		Gini gini = new Gini(definition, attr4);

		Node<Gini> root = gini.create(training);
		
		System.out.println(root.toAll());

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

	public static class Gini implements Node.Strategy {
		
		private Map<Attribute, List<String>> definition = null;
		private List<Attribute> attrs = null;
		private Attribute cls = null;
		
		public Gini(Map<Attribute, List<String>> definition, Attribute cls) {
			this.definition = definition;
			this.attrs = definition.keySet().stream().collect(Collectors.toList());
			
			this.cls = cls;
			this.attrs.remove(cls);
		}	

		@Override
		public Node<Gini> create(List<Instance> instances) {
			// TODO Auto-generated method stub
			return construct(Double.MAX_VALUE, this.attrs, instances);
		}
		
		@Override
		public double score(Node<?> node) {
			// TODO Auto-generated method stub
			
			// gini impurities
			DoubleAdder sum = new DoubleAdder();

			if (node.inputs.size() <= 0)
				return 0.0;

			if (node.children.size() <= 0) {

				node.data.entrySet().stream().forEach(p -> {
					sum.add(Math.pow((double) p.getValue().size() / node.inputs.size(), 2));
				});

				return 1 - sum.doubleValue();
			} else {

				node.children.entrySet().stream().forEach(p -> {

					sum.add((double) node.data.get(p.getKey()).size() /
							node.inputs.size() * score(p.getValue()));
				});

				return sum.doubleValue();
			}
		}
		
		private Node<Gini> test(Attribute attr, Attribute cattr, List<Instance> instances) {
			Node<Gini> node = create(attr, instances);
			
			node.data.entrySet().stream().forEach(p -> {
				
				Node<Gini> child = create(cattr);
				node.add(p.getKey(), child);
			});
			
			return node;
		}
		
		private Node<Gini> create(Attribute attr) {
			return new Node<Gini>(this, attr, definition.get(attr));
		}
		
		private Node<Gini> create(Attribute attr, List<Instance> instances) {
			return new Node<Gini>(this, attr, definition.get(attr), instances);
		}

		private Node<Gini> construct(double ggini, List<Attribute> attrs, List<Instance> instances) {
			
			if (attrs.size() <= 0)
				return this.create(cls);
			
			List<Attribute> list = new ArrayList<Attribute>(attrs);
			
			double min = ggini;
			Node<Gini> target = null;
		
			// determining the next attribute with the lowest gini score
			for (Attribute attr : list) {
				
				Node<Gini> node = test(attr, cls, instances);
				double score = node.score();
				
				if (min > score) {
					min = score;
					target = node;
				}			
			}
			
	
			// recursively constructing the tree
			if (target != null) {
				
				final Node<Gini> parent = target;
			
				list.remove(target.attr);
				
				Node<Gini> node = create(target.attr, instances);
						
				node.data.entrySet().stream().forEach(p -> {
					
					final double score = score(parent.children.get(p.getKey()));
				
					Node<Gini> child = construct(score, list, p.getValue());
					if (child != null) {
						node.add(p.getKey(), child);
					}
				});
				
				return node;
			}
			
			return this.create(cls);
		}
	}

	public static class Node<T extends Strategy> {

		private static final DecimalFormat ff = new DecimalFormat("0.000");

		private Map<String, List<Instance>> data = new HashMap<String, List<Instance>>();
		private Map<String, Node<T>> children = new HashMap<String, Node<T>>();
		private List<Instance> inputs = null;
		private Attribute attr = null;
		private String label = null;
		private List<String> values = null;
		private T strategy = null;
		

		public Node(T strategy, Attribute attr, List<String> values) {
			this(strategy, attr, values, new ArrayList<Instance>());
		}

		public Node(T strategy, Attribute attr, List<String> values, List<Instance> instances) {

			this.attr = attr;
			this.label = this.attr.name();
			this.values = values;
			this.strategy = strategy;

			this.setInstances(instances);
		}

		public void add(String value, Node<T> child) {
			child.setInstances(this.data.get(value));
			this.children.put(value, child);
		}

		public String label() {
			return label;
		}
		
		public double score() {
			return strategy.score(this);
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

			return label() + " ==> " + this.values.stream().map(p -> "[" + p + "]: " + data.get(p).size())
					.collect(Collectors.joining(", ")) + "   Score: " + ff.format(this.score());
		}

		private List<Instance> filter(String value, List<Instance> instances) {
			return instances.stream().filter(p -> value.equals(p.stringValue(attr))).collect(Collectors.toList());
		}

		private String indent(String value, int indent) {

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < indent; i++) {
					builder.append(" ");
			}

			if (builder.length() > 0)
				builder.append("+--<" + value + ">- ");

			return builder.toString();
		}

		private String toAll(String value, int indent) {

			StringBuilder builder = new StringBuilder();

			builder.append(indent(value, indent));
			builder.append(this);
			builder.append("\n");

			children.entrySet().forEach(p -> {

				builder.append(p.getValue().toAll(p.getKey(), indent + 10));
			});

			return builder.toString();
		}
		
		
		public static interface Strategy {

			public double score(Node<?> node);
				
			public Node<?> create(List<Instance> instances);
		}
	
	}
}
