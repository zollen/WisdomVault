package machinelearning.classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class GiniClassifier2 {
		
	private static final String VALUE_CLASS_DOG = "Dog";
	private static final String VALUE_CLASS_CAT = "Cat";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// defining data format
/*
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
*/		
		ArrayList<String> clsVals = new ArrayList<String>();
		clsVals.add(VALUE_CLASS_DOG);
		clsVals.add(VALUE_CLASS_CAT);
		

		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("weight", 1);
		Attribute attr2 = new Attribute("height", 2);
		Attribute attr3 = new Attribute("animal", clsVals, 3);
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
	
	
		// training

		List<Instance> training = generateTrainingData(attrs);

		Gini gini = new Gini(attrs, attr3);

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
		data1.setValue(attrs.get(0), 8.5);
		data1.setValue(attrs.get(1), 13);
	//	data1.setValue(attrs.get(2), VALUE_CLASS_CAT);
		testing.add(data1);
		
		Instance data2 = new DenseInstance(3);	
		data2.setValue(attrs.get(0), 11);
		data2.setValue(attrs.get(1), 7.6);
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
		data1.setValue(attrs.get(0), 8);
		data1.setValue(attrs.get(1), 8);
		data1.setValue(attrs.get(2), VALUE_CLASS_DOG);
		training.add(data1);
		
		Instance data2 = new DenseInstance(3);	
		data2.setValue(attrs.get(0), 50);
		data2.setValue(attrs.get(1), 40);
		data2.setValue(attrs.get(2), VALUE_CLASS_DOG);
		training.add(data2);
		
		Instance data3 = new DenseInstance(3);	
		data3.setValue(attrs.get(0), 8);
		data3.setValue(attrs.get(1), 9);
		data3.setValue(attrs.get(2), VALUE_CLASS_CAT);
		training.add(data3);
		
		Instance data4 = new DenseInstance(3);	
		data4.setValue(attrs.get(0), 15);
		data4.setValue(attrs.get(1), 12);
		data4.setValue(attrs.get(2), VALUE_CLASS_DOG);
		training.add(data4);
		
		Instance data5 = new DenseInstance(3);	
		data5.setValue(attrs.get(0), 9);
		data5.setValue(attrs.get(1), 10);
		data5.setValue(attrs.get(2), VALUE_CLASS_CAT);
		training.add(data5);

		return training;
	}

	private static class Gini extends CARTNode.Strategy {

		public Gini(List<Attribute> attrs, Attribute cls) {
			super(attrs, cls);
		}
		
		@Override
		public CARTNode<Gini> calculate(double last, List<Attribute> attrs, List<Instance> instances) {
			
			CARTNode.Strategy.Builder<Gini> builder = new CARTNode.Strategy.Builder<Gini>(this);
			DoubleAdder min = new DoubleAdder();
			min.add(last);
			
			PlaceHolder<CARTNode<Gini>> holder = new PlaceHolder<CARTNode<Gini>>();
				
			attrs.stream().forEach(p -> {
					
				List<Object> vals = possibleValues(p, instances);
					
				vals.stream().forEach(v -> {
								
					List<Object> list = new ArrayList<Object>();
					list.add(v);
					list.add(v);
					
					CARTNode<Gini> node = builder.test(p, list, instances);
					double score = node.score();
					
					if (min.doubleValue() > score) {
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
			
			if (node.inputs().size() <= 0)
				return 0.0;

			if (node.children().size() <= 0) {
				
				return 1 - node.data().entrySet().stream().mapToDouble(
						p -> Math.pow(p.getValue().size() / node.inputs().size(), 2)).sum();
			} else {

				DoubleAdder sum = new DoubleAdder();
				node.children().entrySet().stream().forEach(p -> {

					sum.add((double) node.data().get(p.getKey()).size() / node.inputs().size() * score(p.getValue()));
				});

				return sum.doubleValue();
			}
		}
	}
}
