package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
	private List<?> values = null;
	private T strategy = null;
	private CARTNode<T> parent = null;
	private boolean isBinaryChoices = false;

	public CARTNode(T strategy, Attribute attr) {
		this(strategy, attr, null, new ArrayList<Instance>());
	}

	public CARTNode(T strategy, Attribute attr, List<?> values) {
		this(strategy, attr, values, new ArrayList<Instance>());
	}
	
	public CARTNode(T strategy, Attribute attr, List<?> values, List<Instance> instances) {
	
		this.attr = attr;
		this.label = this.attr.name();
		this.strategy = strategy;
		this.values = values != null ? values : strategy.definition().get(attr);
		this.isBinaryChoices = (this.values.size() == 2 && 
				this.values.get(0).equals(this.values.get(1)));

		this.setInstances(instances);
	}

	public void add(String value, CARTNode<T> child) {
		
		child.setInstances(this.data.get(value));
		child.parent(this);
		this.children.put(value, child);
	}
	
	public void classify(Instance instance) {
		
		List<Instance> instances = new ArrayList<Instance>();
		instances.add(instance);
		
		if (this.attr() == this.strategy.cls()) {
			System.out.println(instance + "|  RESULT: " + this);
			return;
		}
		
		this.data.entrySet().stream().forEach(p -> {
			
			String value = p.getKey();
			boolean isChoice = this.isBinaryChoices;
			int pos = value.indexOf(":");
			if (pos > 0) {
				
				if (value.endsWith("T"))
					isChoice = false;
				else
				if (value.endsWith("F"))
					isChoice = true;
				
				value = value.substring(0, pos);
			}
			
			List<Instance> list = this.filter(isChoice, value, instances);
			if (list.size() > 0) {
				CARTNode<T> child = this.children.get(p.getKey());
				if (child != null) {
					child.classify(instances);
				}
			}
		});
		
	}
	
	public void classify(List<Instance> instances) {
		
		for (Instance instance : instances) {
			classify(instance);
		}
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

	public List<?> values() {
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
		for (Object value : this.values) {
			
			boolean choice = this.isBinaryChoices;
			String postfix = "";
			if (this.isBinaryChoices && index == 0) {
				postfix = ":T";
				choice = false;
			}
			
			if (this.isBinaryChoices && index == 1) {
				postfix = ":F";
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

	private List<Instance> filter(boolean binary, Object value, List<Instance> instances) {
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
			
			builder.append(p.getValue().toAll(label() + this.strategy.op() + p.getKey(), indent + 10));
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
				return new CARTNode<T>(this.strategy, attr);
			}
			
			public CARTNode<T> create(Attribute attr, List<?> values) {
				return new CARTNode<T>(this.strategy, attr, values);
			}
			
			public CARTNode<T> create(Attribute attr, List<?> values, List<Instance> instances) {
				return new CARTNode<T>(this.strategy, attr, values, instances);
			}
					
			public CARTNode<T> test(Attribute attr, List<?> values, List<Instance> instances) {
				
				CARTNode<T> node = create(attr, values, instances);
				
				node.data().entrySet().stream().forEach(p -> {

					CARTNode<T> child = create(this.strategy.cls());
					node.add(p.getKey(), child);
				});

				return node;
			}
			
			public CARTNode<T> build(List<Instance> instances) {
				
				List<Attribute> list = new ArrayList<Attribute>(this.strategy.definition().keySet());
				list.remove(this.strategy.cls());
				
				return construct(Double.MAX_VALUE, list, instances);
			}
			
			private CARTNode<T> construct(double last, List<Attribute> attrs, List<Instance> instances) {
					
				Strategy.Builder<T> builder = new Strategy.Builder<T>(this.strategy);
				
				if (attrs.size() <= 0 || last <= 0)
					return builder.create(this.strategy.cls());
	
				List<Attribute> list = new ArrayList<Attribute>(attrs);

				CARTNode<?> target = this.strategy.calculate(last, attrs, instances);
				
				// recursively constructing the tree
				if (target != null) {

					list.remove(target.attr());
				
					CARTNode<T> node = builder.create(target.attr(), target.values(), instances);

					node.data().entrySet().stream().forEach(p -> {

						double score = this.strategy.score(target.children().get(p.getKey()));
						
						if (score == last) {
							score = -1;
						}
						
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
		
		public Map<Attribute, List<?>> definition();
		
		public Attribute cls();
		
		public String op();
		
		public CARTNode<?> calculate(double score, List<Attribute> attrs, List<Instance> instances);

		public double score(CARTNode<?> node);

		public List<Instance> filter(boolean binary, CARTNode<?> node, Object value, List<Instance> instances);
	}
	
}