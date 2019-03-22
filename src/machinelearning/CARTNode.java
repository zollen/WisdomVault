package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.Instance;

public class CARTNode<T extends CARTNode.Strategy> {

	private static final DecimalFormat ff = new DecimalFormat("0.000");

	private Map<String, List<Instance>> data = new LinkedHashMap<String, List<Instance>>();
	private Map<String, CARTNode<T>> children = new LinkedHashMap<String, CARTNode<T>>();
	private List<Instance> inputs = null;
	private Attribute attr = null;
	private String label = null;
	private List<String> values = null;
	private String value = null;
	private T strategy = null;
	private CARTNode<T> parent = null;
	private boolean isBinaryChoices = false;

	public CARTNode(T strategy, Attribute attr, String value) {
		this(strategy, attr, value, new ArrayList<Instance>());
	}
	
	public CARTNode(T strategy, Attribute attr, List<String> values, List<Instance> instances) {
		this(strategy, attr, values, null, instances);
	}

	public CARTNode(T strategy, Attribute attr, String value, List<Instance> instances) {
		this(strategy, attr, null, value, instances);
	}
	
	public CARTNode(T strategy, Attribute attr, List<String> values, String value, List<Instance> instances) {

		this.attr = attr;
		this.label = this.attr.name();
		this.strategy = strategy;
		this.values = values != null ? values : strategy.definition().get(attr);
		this.value = value;
		this.isBinaryChoices = (this.values.size() == 2 && 
				this.values.get(0).equals(this.values.get(1)));

		this.setInstances(instances);
	}

	public void add(String value, CARTNode<T> child) {
		
		child.setInstances(this.data.get(value));
		child.value(value);
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
	
	public void value(String val) {
		this.value = val;
	}
	
	public String value() {
		return value;
	}
	
	public List<String> values() {
		return values;
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

		int index = 0;
		for (String value : this.values) {
			
			boolean choice = this.isBinaryChoices;
			String postfix = "";
			if (this.isBinaryChoices && index == 0) {
				postfix = ":T";
				choice = false;
			}
			
			if (this.isBinaryChoices && index == 1) {
				postfix = ":N";
				choice = true;
			}
			
			data.put(value + postfix, filter(choice, value, this.inputs));
			
			index++;
		}
	}
	
	public boolean isBinaryChoices() {
		return isBinaryChoices;
	}

	public String toAll() {
		return toAll("", 0);
	}

	@Override
	public String toString() {

		return label() + " ==> "
				+ this.data.entrySet().stream().map(p -> "[" + p.getKey() + "]: " + p.getValue().size()).collect(Collectors.joining(", "))
				+ "   Score: " + ff.format(this.score());
	}

	private List<Instance> filter(boolean binary, String value, List<Instance> instances) {
		return strategy.filter(binary, this, value, instances);
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
		
		public static class Builder<T extends CARTNode.Strategy> {
			
			private T strategy;
			
			public Builder(T strategy) {
				this.strategy = strategy;
			}
			
			public CARTNode<T> create(Attribute attr) {
				return new CARTNode<T>(this.strategy, attr, null);
			}

			public CARTNode<T> create(Attribute attr, String value) {
				return new CARTNode<T>(this.strategy, attr, value);
			}

			public CARTNode<T> create(Attribute attr, String value, List<Instance> instances) {
				return new CARTNode<T>(this.strategy, attr, value, instances);
			}
			
			public CARTNode<T> create(Attribute attr, List<String> values, List<Instance> instances) {
				return new CARTNode<T>(this.strategy, attr, values, instances);
			}
			
			public CARTNode<T> create(Attribute attr, List<String> values, String value, List<Instance> instances) {
				return new CARTNode<T>(this.strategy, attr, values, value, instances);
			}
					
			public CARTNode<T> test(Attribute attr, List<String> value, List<Instance> instances) {
				CARTNode<T> node = create(attr, value, instances);

				node.data().entrySet().stream().forEach(p -> {

					CARTNode<T> child = create(this.strategy.cls());
					node.add(p.getKey(), child);
				});

				return node;
			}
			
			public CARTNode<T> build(List<Instance> instances) {
				return construct(Double.MAX_VALUE, this.strategy.definition().keySet(), instances);
			}
			
			private CARTNode<T> construct(double ggini, Collection<Attribute> attrs, List<Instance> instances) {
				Strategy.Builder<T> builder = new Strategy.Builder<T>(this.strategy);
				
				if (attrs.size() <= 0)
					return builder.create(this.strategy.cls());

				List<Attribute> list = new ArrayList<Attribute>(attrs);

				CARTNode<?> target = this.strategy.calculate(ggini, instances);
				
				// recursively constructing the tree
				if (target != null) {

					list.remove(target.attr());

					CARTNode<T> node = builder.create(target.attr(), target.values(), target.value(), instances);

					node.data().entrySet().stream().forEach(p -> {

						final double score = this.strategy.score(target.children().get(p.getKey()));

						CARTNode<T> child = construct(score, list, p.getValue());
						if (child != null) {
							node.add(p.getKey(), child);
						}
					});

					return node;
				}

				return builder.create(this.strategy.cls());
			}
		}
		
		public Map<Attribute, List<String>> definition();
		
		public Attribute cls();
		
		public CARTNode<?> calculate(double ggini, List<Instance> instances);

		public double score(CARTNode<?> node);

		public List<Instance> filter(boolean binary, CARTNode<?> node, String value, List<Instance> instances);
	}
	
}