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

	Map<String, List<Instance>> data = new HashMap<String, List<Instance>>();
	Map<String, CARTNode<T>> children = new HashMap<String, CARTNode<T>>();
	List<Instance> inputs = null;
	Attribute attr = null;
	private String label = null;
	private List<String> values = null;
	private T strategy = null;

	public CARTNode(T strategy, Attribute attr, List<String> values) {
		this(strategy, attr, values, new ArrayList<Instance>());
	}

	public CARTNode(T strategy, Attribute attr, List<String> values, List<Instance> instances) {

		this.attr = attr;
		this.label = this.attr.name();
		this.values = values;
		this.strategy = strategy;

		this.setInstances(instances);
	}

	public void add(String value, CARTNode<T> child) {
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

		return label() + " ==> "
				+ this.values.stream().map(p -> "[" + p + "]: " + data.get(p).size()).collect(Collectors.joining(", "))
				+ "   Score: " + ff.format(this.score());
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

		public double score(CARTNode<?> node);

		public CARTNode<?> create(List<Instance> instances);
	}

}