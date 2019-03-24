package machinelearning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class GiniExercise2 {
		
	private static final String VALUE_CLASS_DOG = "Dog";
	private static final String VALUE_CLASS_CAT = "Cat";
	
	private static final boolean numericData = false;


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// defining data format

		ArrayList<String> weightVals = new ArrayList<String>();
		weightVals.add("8");
		weightVals.add("9");
		weightVals.add("15");
		weightVals.add("50");
		
		ArrayList<String> heightVals = new ArrayList<String>();
		heightVals.add("8");
		heightVals.add("9");
		heightVals.add("10");
		heightVals.add("12");
		heightVals.add("40");
		
		ArrayList<String> clsVals = new ArrayList<String>();
		clsVals.add(VALUE_CLASS_DOG);
		clsVals.add(VALUE_CLASS_CAT);
		

		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("weight", weightVals);
		Attribute attr2 = new Attribute("height", heightVals);
		Attribute attr3 = new Attribute("animal", clsVals);
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		

		// defining data dictionary

		Map<Attribute, List<String>> definition = new LinkedHashMap<Attribute, List<String>>();
		definition.put(attr1, weightVals);
		definition.put(attr2, heightVals);
		definition.put(attr3, clsVals);
		
	
		// training

		List<Instance> training = generateTrainingData(attrs);

		Gini gini = new Gini(definition, attr3);

		CARTNode.Strategy.Builder<Gini> builder = new CARTNode.Strategy.Builder<Gini>(gini);
		
		CARTNode<Gini> root = builder.build(training);
		
		System.out.println(root.toAll());
		
		List<Instance> testing = generateTestData(attrs);
		
		for (Instance instance : testing)
			root.classify(instance);
	}
	
	public static List<Instance> generateTestData(ArrayList<Attribute> attrs) {
		
		Instances testing = new Instances("TESTING", attrs, 2);
		
		Instance data1 = new DenseInstance(3);	
		data1.setValue(attrs.get(0), "8");
		data1.setValue(attrs.get(1), "10");
	//	data1.setValue(attrs.get(2), VALUE_CLASS_CAT);
		testing.add(data1);
		
		Instance data2 = new DenseInstance(3);	
		data2.setValue(attrs.get(0), "9");
		data2.setValue(attrs.get(1), "8");
	//	data2.setValue(attrs.get(2), VALUE_CLASS_DOG);
		testing.add(data2);
		
		return testing;
	}

	public static List<Instance> generateTrainingData(ArrayList<Attribute> attrs) {

		Instances training = new Instances("TRAINING", attrs, 5);
		
		// ANSWERS:
		// weight <= 12.0
		//    + - true  - height <= 8.5
		//					+ - dog (1)
		//					+ - cat (2)
		//    + - false - dog (2)
		
		Instance data1 = new DenseInstance(3);	
		data1.setValue(attrs.get(0), "8");
		data1.setValue(attrs.get(1), "8");
		data1.setValue(attrs.get(2), VALUE_CLASS_DOG);
		training.add(data1);
		
		Instance data2 = new DenseInstance(3);	
		data2.setValue(attrs.get(0), "50");
		data2.setValue(attrs.get(1), "40");
		data2.setValue(attrs.get(2), VALUE_CLASS_DOG);
		training.add(data2);
		
		Instance data3 = new DenseInstance(3);	
		data3.setValue(attrs.get(0), "8");
		data3.setValue(attrs.get(1), "9");
		data3.setValue(attrs.get(2), VALUE_CLASS_CAT);
		training.add(data3);
		
		Instance data4 = new DenseInstance(3);	
		data4.setValue(attrs.get(0), "15");
		data4.setValue(attrs.get(1), "12");
		data4.setValue(attrs.get(2), VALUE_CLASS_DOG);
		training.add(data4);
		
		Instance data5 = new DenseInstance(3);	
		data5.setValue(attrs.get(0), "9");
		data5.setValue(attrs.get(1), "10");
		data5.setValue(attrs.get(2), VALUE_CLASS_CAT);
		training.add(data5);

		return training;
	}

	private static class Gini implements CARTNode.Strategy {

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
		public Map<Attribute, List<String>> definition() {
			return definition;
		}
		
		@Override
		public String op() {
			return " >= ";
		}
		
		@Override
		public Attribute cls() {
			return cls;
		}
		
		@Override
		public CARTNode<Gini> calculate(double ggini, List<Attribute> attrs, List<Instance> instances) {
			
			CARTNode.Strategy.Builder<Gini> builder = new CARTNode.Strategy.Builder<Gini>(this);
			DoubleAdder min = new DoubleAdder();
			min.add(ggini);
			
			PlaceHolder<CARTNode<Gini>> holder = new PlaceHolder<CARTNode<Gini>>();
				
			attrs.stream().forEach(p -> {
					
				List<String> vals = values1(this.definition().get(p));
					
				if (numericData)
					vals = values2(this.definition().get(p));
		
				vals.stream().forEach(v -> {
								
					List<String> list = new ArrayList<String>();
					list.add(v);
					list.add(v);
					
					CARTNode<Gini> node = builder.test(p, list, instances);
					double score = node.score();
					
					System.err.println(node + " ---- " + score + ", " + ggini);
							
					if (min.doubleValue() >= score) {
						min.reset();
						min.add(score);
						holder.data(node);
					}
				});
			});
				
			return holder.data();
		}

		@Override
		public double score(CARTNode<?> node) {
			// TODO Auto-generated method stub

			// gini impurities
			DoubleAdder sum = new DoubleAdder();

			if (node.inputs().size() <= 0)
				return 0.0;

			if (node.children().size() <= 0) {

				node.data().entrySet().stream().forEach(p -> {
					sum.add(Math.pow((double) p.getValue().size() / node.inputs().size(), 2));
				});

				return 1 - sum.doubleValue();
			} else {

				node.children().entrySet().stream().forEach(p -> {

					sum.add((double) node.data().get(p.getKey()).size() / node.inputs().size() * score(p.getValue()));
				});

				return sum.doubleValue();
			}
		}

		@Override
		public List<Instance> filter(boolean binary, CARTNode<?> node, String value, List<Instance> instances) {
			
			if (node.attr() == this.cls) {
				return instances.stream().filter(p ->  value.equals(p.stringValue(node.attr()))).collect(Collectors.toList());
			}
		
			
			if (binary)
				return instances.stream().filter(p -> { 
					return Double.valueOf(p.stringValue(node.attr())) < Double.valueOf(value);
				}).collect(Collectors.toList());
			else
				return instances.stream().filter(p ->  {
					return Double.valueOf(p.stringValue(node.attr())) >= Double.valueOf(value);
				}).collect(Collectors.toList());
		}
		
		// treat the possible values as ranked data
		private List<String> values1(List<String> vals) {
			
			Collections.sort(vals, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					// TODO Auto-generated method stub
					return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
				}
			});
			
			List<String> nList = new ArrayList<String>();
			
			int first = Integer.valueOf(vals.get(0));
			int last = Integer.valueOf(vals.get(vals.size() - 1 > 0 ? vals.size() - 1 : 0)) - 1;
			
			for (int i = first; i <= last; i++)
				nList.add(String.valueOf(i));
		
			return nList;
		}
		
		// treat the possible values as numeric data
		private List<String> values2(List<String> vals) {
			
			Collections.sort(vals, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					// TODO Auto-generated method stub
					return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
				}
			});
			
			List<String> nList = new ArrayList<String>();
			
			for (int i = 0; vals.size() > 0 && i < vals.size() - 1; i++) {
				double d1 = Double.valueOf(vals.get(i + 1));
				double d2 = Double.valueOf(vals.get(i));
				nList.add(String.valueOf((double) (d1 + d2) / 2));
			}
		
			return nList;
		}
	}
}
