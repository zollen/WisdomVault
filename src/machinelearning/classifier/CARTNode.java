package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.Instance;

public class CARTNode<T extends CARTNode.Strategy> {

	private static final DecimalFormat ff = new DecimalFormat("0.000");

	private Map<CARTKey, List<Instance>> data = new LinkedHashMap<CARTKey, List<Instance>>();
	private Map<CARTKey, CARTNode<T>> children = new LinkedHashMap<CARTKey, CARTNode<T>>();
	private List<Instance> inputs = null;
	private Attribute attr = null;
	private String label = null;
	private List<?> values = null;
	private T strategy = null;
	private CARTNode<T> parent = null;
	private boolean isBinaryChoices = false;

	
	public CARTNode(T strategy, Attribute attr, List<?> values) {
		this(strategy, attr, values, new ArrayList<Instance>());
	}
	
	public CARTNode(T strategy, Attribute attr, List<?> values, List<Instance> instances) {
	
		this.attr = attr;
		this.label = this.attr.name();
		this.strategy = strategy;
		this.values = values != null ? values : strategy.possibleValues(attr, instances);
		this.isBinaryChoices = (this.values.size() == 2 && 
				this.values.get(0).equals(this.values.get(1)));

		this.setInstances(instances);
	}

	public void add(CARTKey value, CARTNode<T> child) {
		
		child.setInstances(this.data.get(value));
		child.parent(this);
		this.children.put(value, child);
	}
	
	public CARTNode<?> classify(Instance instance) {
		
		List<Instance> instances = new ArrayList<Instance>();
		instances.add(instance);
		
		if (this.attr() == this.strategy.cls()) {
			return this;
		}
		
		DoubleAdder index = new DoubleAdder();
		PlaceHolder<CARTNode<?>> target = new PlaceHolder<CARTNode<?>>();
		this.data.entrySet().stream().forEach(p -> {
			
			CARTKey value = p.getKey();
		
			List<Instance> list = this.filter(this.isBinaryChoices(), 
											index.intValue(), value.get(), instances);
			if (list.size() > 0) {
				CARTNode<T> child = this.children.get(p.getKey());
				if (child != null) {
					target.data(child.classify(instance));
				}
			}
			
			index.add(1);
		});
		
		return target.data();	
	}
	
	public void classify(List<Instance> instances) {
		
		for (Instance instance : instances) {
			System.out.println(instance + "| RESULT: " + classify(instance));
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
	
	public Map<CARTKey, List<Instance>> data() {
		return data;
	}
	
	public Map<CARTKey, CARTNode<T>> children() {
		return children;
	}
	
	public double score() {
		return strategy.score(this);
	}
	
	public T strategy() {
		return strategy;
	}

	public void setInstances(List<Instance> instances) {

		this.inputs = instances;

		int index = 0;
		
		if (this.values.size() == 1) {
			data.put(new CARTKey(values.get(0), index), instances);
		}
		else {
			for (Object value : this.values) {
			
				data.put(new CARTKey(value, index), filter(this.isBinaryChoices(), 
													index, value, this.inputs));
			
				index++;
			}
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
				+ this.data.entrySet().stream().map(p -> "[" + p.getKey().get(this.isBinaryChoices()) + "]: " + p.getValue().size()).collect(Collectors.joining(", "))
				+  "   Score: " + ff.format(this.score());
	}

	private List<Instance> filter(boolean binary, int index, Object value, List<Instance> instances) {

		if (value instanceof String) {
			
			if (binary) {
				if (index == 1) {
					return instances.stream().filter(p -> !value.equals(p.stringValue(this.attr())))
						.collect(Collectors.toList());
				}
			}
		}
		else 
		if (value instanceof Number) {
				
			if (binary) {
				if (index == 0) {
					return instances.stream().filter(p -> 
						p.value(this.attr()) >= ((Number)value).doubleValue()
							).collect(Collectors.toList());
				}
				else {
					return instances.stream().filter(p -> 
						p.value(this.attr()) < ((Number)value).doubleValue()
							).collect(Collectors.toList());
				}
			}
			else {
				return instances.stream().filter(p -> 
					p.value(this.attr()) == ((Number)value).doubleValue()
						).collect(Collectors.toList());
			}
		}
		
		return instances.stream().filter(p -> value.equals(p.stringValue(this.attr())))
				.collect(Collectors.toList());
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
		
		List<String> op =  new ArrayList<String>();
		children.keySet().stream().forEach(p -> op.add(" == "));
		
		if (this.isBinaryChoices && children.size() == 2) {
			if (this.attr.isNominal()) {
				op.set(0, " == ");
				op.set(1,  " <> ");
			}
			else
			if (this.attr.isNumeric()) {
				op.set(0, " >= ");
				op.set(1,  " < ");
			}
		}
			

		AtomicInteger count = new AtomicInteger(0);
		children.entrySet().forEach(p -> {
			
			builder.append(p.getValue().toAll(label() + op.get(count.getAndIncrement()) + p.getKey().get(), indent + 10));
		});

		return builder.toString();
	}
	
	
	public static class CARTKey {
		
		private int seq;
		private Object val;
		
		public CARTKey(Object val, int seq) {
			this.val = val;
			this.seq = seq;
		}
		
		public Object get() {
			return val;
		}	
		
		public Object get(boolean binary) {
			
			if (val instanceof Number) {
				return ff.format(val) + ": " + seq;
			}
			
			return val + ": " + seq;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Integer.valueOf(seq).hashCode();
			result = prime * result + ((val == null) ? 0 : val.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CARTKey other = (CARTKey) obj;
			if (seq != other.seq)
				return false;
			if (val == null) {
				if (other.val != null)
					return false;
			} else if (!val.equals(other.val))
				return false;
			return true;
		}
		
		
	}


	public static abstract class Strategy {
		
		public static class Builder<T extends CARTNode.Strategy> {
			
			private T strategy;
		
			public Builder(T strategy) {
				this.strategy = strategy;
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

					CARTNode<T> child = create(this.strategy.cls(), 
							this.strategy.possibleValues(this.strategy.cls(), instances));
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
					return builder.create(this.strategy.cls(), 
							this.strategy.possibleValues(this.strategy.cls(), instances));
	
				List<Attribute> list = new ArrayList<Attribute>(attrs);

				CARTNode<?> target = this.strategy.calculate(last, attrs, instances);
				
				// recursively constructing the tree
				if (target != null) {

					list.remove(target.attr());
				
					CARTNode<T> node = builder.create(target.attr(), target.values(), instances);

					node.data().entrySet().stream().forEach(p -> {

						double score = this.strategy.score(target.children().get(p.getKey()));
				
						score = this.strategy.stop(node, p.getKey(), last, score);

						CARTNode<T> child = construct(score, list, p.getValue());
						if (child != null) {
							node.add(p.getKey(), child);
						}
					});

					return node;
				}

				return builder.create(this.strategy.cls(),
						this.strategy.possibleValues(this.strategy.cls(), instances));
			}
		}
		
		protected List<Attribute> attrs = null;
		protected Attribute cls = null;
		protected Map<Attribute, List<?>> definition = null;
		
		public Strategy(List<Attribute> attrs, Attribute cls) {
			this.attrs = new ArrayList<Attribute>(attrs);	
			this.cls = cls;
			this.definition = new LinkedHashMap<Attribute, List<?>>();
			
			for (Attribute attr : attrs) {
				
				Enumeration<Object> vals = attr.enumerateValues();
				List<Object> values = new ArrayList<Object>();
				
				while (vals != null && vals.hasMoreElements()) 
					values.add(vals.nextElement());
				
				this.definition.put(attr, values);
			}	
			
			this.attrs.remove(this.cls);
		}
		
		public void add(Attribute attr, List<Instance> instances) {
			
			this.definition.put(attr, possibleValues(attr, instances));
		}
		
		public Map<Attribute, List<?>> definition() {
			return definition;
		}
		
		public List<Attribute> attrs() {
			return attrs;
		}
	
		public Attribute cls() {
			return cls;
		}
		
		public List<Object> possibleValues(Attribute attr, List<Instance> instances) {
			
			List<Object> vals = new ArrayList<Object>();
			
			if (attr.isNominal()) {
				
				Enumeration<Object> o = attr.enumerateValues();
				while (o != null && o.hasMoreElements())
					vals.add(o.nextElement());
			}
			else
			if (attr.isNumeric()) {
				
				Set<Double> nn = new HashSet<Double>();
				
				instances.stream().forEach(p -> nn.add(p.value(attr)));
				
				List<Double> nums = new ArrayList<Double>(nn);
				
				Collections.sort(nums);
		
				
				if (nums.size() == 1) {
					vals.addAll(nums);
				}
				else {
					for (int i = 0; nums.size() > 0 && i < nums.size() - 1; i++) {
						double d1 = nums.get(i + 1).doubleValue();
						double d2 = nums.get(i).doubleValue();
						vals.add(Double.valueOf((d1 + d2) / 2));
					}	
				}
			}
			
			return vals;
		}
		
		public double stop(CARTNode<?> node, CARTKey key, double last, double score) {
			if (score == last)
				return -1;
			
			return score;
		}
		
		public abstract CARTNode<?> calculate(double score, List<Attribute> attrs, List<Instance> instances);

		public abstract double score(CARTNode<?> node);
	}
	
}