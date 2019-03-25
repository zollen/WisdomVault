package machinelearning;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class EntropyExercise1 {
		
	private static final String VALUE_AGE_OLD = "old";
	private static final String VALUE_AGE_MILD = "mild";
	private static final String VALUE_AGE_NEW = "new";
	
	private static final String VALUE_COMPETITION_YES = "yes";
	private static final String VALUE_COMPETITION_NO = "no";
	
	private static final String VALUE_TYPE_SOFTWARE = "software";
	private static final String VALUE_TYPE_HARDWARE = "hardware";
	
	private static final String VALUE_PROFIT_UP = "up";
	private static final String VALUE_PROFIT_DOWN = "down";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// defining data format

		ArrayList<String> ageVals = new ArrayList<String>();
		ageVals.add(VALUE_AGE_OLD);
		ageVals.add(VALUE_AGE_MILD);
		ageVals.add(VALUE_AGE_NEW);
		
		ArrayList<String> compVals = new ArrayList<String>();
		compVals.add(VALUE_COMPETITION_YES);
		compVals.add(VALUE_COMPETITION_NO);
		
		ArrayList<String> typeVals = new ArrayList<String>();
		typeVals.add(VALUE_TYPE_SOFTWARE);
		typeVals.add(VALUE_TYPE_HARDWARE);
	
		ArrayList<String> profitVals = new ArrayList<String>();
		profitVals.add(VALUE_PROFIT_UP);
		profitVals.add(VALUE_PROFIT_DOWN);
		

		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("age", ageVals);
		Attribute attr2 = new Attribute("competition", compVals);
		Attribute attr3 = new Attribute("type", typeVals);
		Attribute attr4 = new Attribute("profit", profitVals);
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		attrs.add(attr4);
		

		// defining data dictionary

		Map<Attribute, List<String>> definition = new LinkedHashMap<Attribute, List<String>>();
		definition.put(attr1, ageVals);
		definition.put(attr2, compVals);
		definition.put(attr3, typeVals);
		definition.put(attr4, profitVals);
		
	
		// training

		List<Instance> training = generateTrainingData(attrs);	

		Entropy entropy = new Entropy(definition, attr4);
		
		System.out.println("RESULT: " + entropy.entropy(training));
/*
		CARTNode.Strategy.Builder<Entropy> builder = new CARTNode.Strategy.Builder<Entropy>(gini);
		
		CARTNode<Entropy> root = builder.build(training);
		
		System.out.println(root.toAll());
*/
	}
	
	public static List<Instance> generateTrainingData(ArrayList<Attribute> attrs) {

		Instances training = new Instances("TRAINING", attrs, 14);
		
		// Answers:
		// age
		//   +-<old>  - down
		//   +-<mild> - competition
		//					+-<yes> - down
		//					+-<no>  - up
		//   +-<new>  - up
		
		
		Instance data1 = new DenseInstance(4);	
		data1.setValue(attrs.get(0), VALUE_AGE_OLD);
		data1.setValue(attrs.get(1), VALUE_COMPETITION_YES);
		data1.setValue(attrs.get(2), VALUE_TYPE_SOFTWARE);
		data1.setValue(attrs.get(3), VALUE_PROFIT_DOWN);
		training.add(data1);
		
		Instance data2 = new DenseInstance(4);	
		data2.setValue(attrs.get(0), VALUE_AGE_OLD);
		data2.setValue(attrs.get(1), VALUE_COMPETITION_NO);
		data2.setValue(attrs.get(2), VALUE_TYPE_SOFTWARE);
		data2.setValue(attrs.get(3), VALUE_PROFIT_DOWN);
		training.add(data2);
		
		Instance data3 = new DenseInstance(4);	
		data3.setValue(attrs.get(0), VALUE_AGE_OLD);
		data3.setValue(attrs.get(1), VALUE_COMPETITION_NO);
		data3.setValue(attrs.get(2), VALUE_TYPE_HARDWARE);
		data3.setValue(attrs.get(3), VALUE_PROFIT_DOWN);
		training.add(data3);
		
		Instance data4 = new DenseInstance(4);	
		data4.setValue(attrs.get(0), VALUE_AGE_MILD);
		data4.setValue(attrs.get(1), VALUE_COMPETITION_YES);
		data4.setValue(attrs.get(2), VALUE_TYPE_SOFTWARE);
		data4.setValue(attrs.get(3), VALUE_PROFIT_DOWN);
		training.add(data4);
		
		Instance data5 = new DenseInstance(4);	
		data5.setValue(attrs.get(0), VALUE_AGE_MILD);
		data5.setValue(attrs.get(1), VALUE_COMPETITION_YES);
		data5.setValue(attrs.get(2), VALUE_TYPE_HARDWARE);
		data5.setValue(attrs.get(3), VALUE_PROFIT_DOWN);
		training.add(data5);
		
		Instance data6 = new DenseInstance(4);	
		data6.setValue(attrs.get(0), VALUE_AGE_MILD);
		data6.setValue(attrs.get(1), VALUE_COMPETITION_NO);
		data6.setValue(attrs.get(2), VALUE_TYPE_HARDWARE);
		data6.setValue(attrs.get(3), VALUE_PROFIT_UP);
		training.add(data6);
		
		Instance data7 = new DenseInstance(4);	
		data7.setValue(attrs.get(0), VALUE_AGE_MILD);
		data7.setValue(attrs.get(1), VALUE_COMPETITION_NO);
		data7.setValue(attrs.get(2), VALUE_TYPE_SOFTWARE);
		data7.setValue(attrs.get(3), VALUE_PROFIT_UP);
		training.add(data7);
		
		Instance data8 = new DenseInstance(4);	
		data8.setValue(attrs.get(0), VALUE_AGE_NEW);
		data8.setValue(attrs.get(1), VALUE_COMPETITION_YES);
		data8.setValue(attrs.get(2), VALUE_TYPE_SOFTWARE);
		data8.setValue(attrs.get(3), VALUE_PROFIT_UP);
		training.add(data8);
		
		Instance data9 = new DenseInstance(4);	
		data9.setValue(attrs.get(0), VALUE_AGE_NEW);
		data9.setValue(attrs.get(1), VALUE_COMPETITION_NO);
		data9.setValue(attrs.get(2), VALUE_TYPE_HARDWARE);
		data9.setValue(attrs.get(3), VALUE_PROFIT_UP);
		training.add(data9);
		
		Instance data10 = new DenseInstance(4);	
		data10.setValue(attrs.get(0), VALUE_AGE_NEW);
		data10.setValue(attrs.get(1), VALUE_COMPETITION_NO);
		data10.setValue(attrs.get(2), VALUE_TYPE_SOFTWARE);
		data10.setValue(attrs.get(3), VALUE_PROFIT_UP);
		training.add(data10);

		return training;
	}

	private static class Entropy implements CARTNode.Strategy {

		private Map<Attribute, List<String>> definition = null;
		private List<Attribute> attrs = null;
		private Attribute cls = null;
		
		public Entropy(Map<Attribute, List<String>> definition, Attribute cls) {
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
			return " == ";
		}
		
		@Override
		public Attribute cls() {
			return cls;
		}
		
		@Override
		public CARTNode<Entropy> calculate(double last, List<Attribute> attrs, List<Instance> instances) {
			
			CARTNode.Strategy.Builder<Entropy> builder = new CARTNode.Strategy.Builder<Entropy>(this);
			DoubleAdder min = new DoubleAdder();
			min.add(last);
			
			PlaceHolder<CARTNode<Entropy>> holder = new PlaceHolder<CARTNode<Entropy>>();
				
			attrs.stream().forEach(p -> {
										
				CARTNode<Entropy> node = builder.test(p, this.definition().get(p), instances);
				double score = node.score();
					
				if (min.doubleValue() > score) {
					min.reset();
					min.add(score);
					holder.data(node);
				}
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
			
			return instances.stream().filter(p ->  value.equals(p.stringValue(node.attr()))).collect(Collectors.toList());
		}
		
		private double entropy(List<Instance> instances) {
			
			Map<String, List<Instance>> profitCategory = this.spreads(cls, instances);
			DoubleAdder total = new DoubleAdder();
			DoubleAdder infoGain = new DoubleAdder();
			
			{
				List<Integer> terms = new ArrayList<Integer>();
		
				profitCategory.entrySet().stream().forEach(p -> {			
					total.add(p.getValue().size());
					terms.add(p.getValue().size());
				});
			
				terms.stream().forEach(p -> {
					infoGain.add(-1 * p.doubleValue() / total.doubleValue() * 
							Math.log(p.doubleValue() / total.doubleValue()) / Math.log(2));	
				});
			}
			
			
			List<Double> all = new ArrayList<Double>();
			
			this.attrs.stream().forEach(p -> {
				
				DoubleAdder entropies = new DoubleAdder();
				
				this.definition().get(p).stream().forEach(v -> {
					
					DoubleAdder entropy = new DoubleAdder();
					DoubleAdder subtotal = new DoubleAdder();
					List<Integer> terms = new ArrayList<Integer>();
					
					profitCategory.entrySet().stream().forEach(c -> {
						
						List<Instance> list = this.spreads(p, c.getValue()).get(v);
						int size = 0;
						if (list != null)
							size = list.size();
						
						subtotal.add(size);
						terms.add(size);	
					});
					
			
					terms.stream().forEach(e -> {
						if (e != 0) {
							entropy.add(-1 * e.doubleValue() / subtotal.doubleValue() * 
								Math.log(e.doubleValue() / subtotal.doubleValue()) / Math.log(2));	
						}
					});

					entropies.add(entropy.doubleValue() * subtotal.doubleValue() / total.doubleValue());
				});
				
				all.add(1.0 - entropies.doubleValue());
			});
	
			
			return all.stream().mapToDouble(p -> p).max().orElse(0.0);
		}
		
		private Map<String, List<Instance>> spreads(Attribute attr, List<Instance> instances) {
			
			return instances.stream().collect(Collectors.groupingBy(p -> p.stringValue(attr)));
		}
	}
}
