package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Function;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class GradientBoostClassifier {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final String VALUE_COLOR_GREEN = "Green";
	private static final String VALUE_COLOR_BLUE = "Blue";
	private static final String VALUE_COLOR_RED = "Red";
	
	private static final String VALUE_GENDER_MALE = "Male";
	private static final String VALUE_GENDER_FEMALE = "Female";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ArrayList<String> genderVals = new ArrayList<String>();
		genderVals.add(VALUE_GENDER_MALE);
		genderVals.add(VALUE_GENDER_FEMALE);
		
		ArrayList<String> colorVals = new ArrayList<String>();
		colorVals.add(VALUE_COLOR_GREEN);
		colorVals.add(VALUE_COLOR_BLUE);
		colorVals.add(VALUE_COLOR_RED);
		
		
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("height", 1);
		Attribute attr2 = new Attribute("color", colorVals, 2);
		Attribute attr3 = new Attribute("gender", genderVals, 3);
		Attribute attr4 = new Attribute("weight", 4);
		
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		attrs.add(attr4);
		
		Instances training = generateTrainingData(attrs);
		print(training, attr4);
		
		avg(training, attr4);
		print(training, attr4);

		Gini gini = new Gini(attrs, attr4);
		
		
		CARTNode.Strategy.Builder<Gini> builder = new CARTNode.Strategy.Builder<Gini>(gini);
		
		CARTNode<Gini> root = builder.build(training);
		
		System.out.println(root.toAll());

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
				
				System.err.println("KONGS: " + p);
				List<Object> vals = possibleValues(p, instances);
		
				vals.stream().forEach(v -> {
			
					List<Object> list = new ArrayList<Object>();
					list.add(v);
					list.add(v);
										
					CARTNode<Gini> node = builder.test(p, list, instances);
					double score = node.score();
				System.err.println(node.toAll() + v + " , " + p + " , " + score);	
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
			DoubleAdder sum = new DoubleAdder();

			if (node.inputs().size() <= 0)
				return 0.0;

			if (node.children().size() <= 0) {

				node.data().entrySet().stream().forEach(p -> {
					sum.add(Math.pow((double) p.getValue().size() / node.inputs().size(), 2));
				});
System.err.println("ERROR!!!: " + sum.doubleValue());
				return 1 - sum.doubleValue();
			} else {

				node.children().entrySet().stream().forEach(p -> {

					sum.add((double) node.data().get(p.getKey()).size() / node.inputs().size() * score(p.getValue()));
				});

				return sum.doubleValue();
			}
		}
	}
	
	public static void avg(Instances instances, Attribute attr) {
		
		double total = instances.stream().mapToDouble(p -> p.value(attr)).sum();
		double avg = total / instances.size();
		
		instances.stream().forEach(p -> p.setValue(attr, p.value(attr) - avg));
	}
	
	public static void print(Instances instances, Attribute attr) {
		
		Function<Instance, String> func = p ->  String.valueOf(ff.format(p.value(attr)));
		
		System.out.println(instances.stream().map(func).collect(Collectors.joining(", ")));
	}
	
	public static List<Double> get(Instances instances, Attribute attr) {
		
		List<Double> vals = new ArrayList<Double>();
		
		instances.stream().forEach(p -> vals.add(p.value(attr)));
		
		return vals;
	}
	
	public static Instances generateTrainingData(ArrayList<Attribute> attrs) {

		Instances training = new Instances("TRAINING", attrs, 6);
		
		Instance data1 = new DenseInstance(4);	
		data1.setValue(attrs.get(0), 1.6);
		data1.setValue(attrs.get(1), VALUE_COLOR_BLUE);
		data1.setValue(attrs.get(2), VALUE_GENDER_MALE);
		data1.setValue(attrs.get(3), 88);
		training.add(data1);
		
		Instance data2 = new DenseInstance(4);	
		data2.setValue(attrs.get(0), 1.6);
		data2.setValue(attrs.get(1), VALUE_COLOR_GREEN);
		data2.setValue(attrs.get(2), VALUE_GENDER_FEMALE);
		data2.setValue(attrs.get(3), 76);
		training.add(data2);
		
		Instance data3 = new DenseInstance(4);	
		data3.setValue(attrs.get(0), 1.5);
		data3.setValue(attrs.get(1), VALUE_COLOR_BLUE);
		data3.setValue(attrs.get(2), VALUE_GENDER_FEMALE);
		data3.setValue(attrs.get(3), 56);
		training.add(data3);
		
		Instance data4 = new DenseInstance(4);	
		data4.setValue(attrs.get(0), 1.8);
		data4.setValue(attrs.get(1), VALUE_COLOR_RED);
		data4.setValue(attrs.get(2), VALUE_GENDER_MALE);
		data4.setValue(attrs.get(3), 73);
		training.add(data4);
		
		Instance data5 = new DenseInstance(4);	
		data5.setValue(attrs.get(0), 1.5);
		data5.setValue(attrs.get(1), VALUE_COLOR_GREEN);
		data5.setValue(attrs.get(2), VALUE_GENDER_MALE);
		data5.setValue(attrs.get(3), 77);
		training.add(data5);
		
		Instance data6 = new DenseInstance(4);	
		data6.setValue(attrs.get(0), 1.4);
		data6.setValue(attrs.get(1), VALUE_COLOR_BLUE);
		data6.setValue(attrs.get(2), VALUE_GENDER_FEMALE);
		data6.setValue(attrs.get(3), 57);
		training.add(data6);
		
		training.setClassIndex(training.numAttributes() - 1);
		
		return training;
	}

}
