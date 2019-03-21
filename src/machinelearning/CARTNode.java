package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.Instance;

public class CARTNode<T extends CARTNode.Strategy> {

	private static final DecimalFormat ff = new DecimalFormat("0.000");

	private Map<String, List<Instance>> data = new HashMap<String, List<Instance>>();
	private Map<String, CARTNode<T>> children = new HashMap<String, CARTNode<T>>();
	private List<Instance> inputs = null;
	private Attribute attr = null;
	private String label = null;
	private List<String> values = null;
	private String value = null;
	private T strategy = null;
	private CARTNode<T> parent = null;

	public CARTNode(T strategy, Attribute attr, String value) {
		this(strategy, attr, value, new ArrayList<Instance>());
	}

	public CARTNode(T strategy, Attribute attr, String value, List<Instance> instances) {

		this.attr = attr;
		this.label = this.attr.name();
		this.strategy = strategy;
		this.values = strategy.definition().get(attr);
		this.value = value;

		this.setInstances(instances);
	}

	public void add(String value, CARTNode<T> child) {
		child.setInstances(this.data.get(value));
		child.parent(this);
		this.children.put(value, child);
	}

	public String label() {
		return label;
	}
	
	public void parent(CARTNode<T> node) {
		this.parent = node;
	}
	
	public CARTNode<T> parent() {
		return parent;
	}
	
	public List<Instance> inputs() {
		return inputs;
	}
	
	public String value() {
		return value;
	}
	
	public Attribute attr() {
		return attr;
	}
	
	public Map<String, List<Instance>> data() {
		return data;
	}
	
	public Map<String, CARTNode<T>> children() {
		return children;
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

		return label() + " ==> "
				+ this.values.stream().map(p -> "[" + p + "]: " + data.get(p).size()).collect(Collectors.joining(", "))
				+ "   Score: " + ff.format(this.score());
	}

	private List<Instance> filter(String value, List<Instance> instances) {
		return strategy.filter(this, value, instances);
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
		
		public Map<Attribute, List<String>> definition();

		public double score(CARTNode<?> node);

		public CARTNode<?> create(List<Instance> instances);
		
		public List<Instance> filter(CARTNode<?> node, String value, List<Instance> instances);
	}

}