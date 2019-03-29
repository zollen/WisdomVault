package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.StatUtils;

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
		
		training.stream().forEach(p -> System.out.println(p));

		StdDev stddev = new StdDev(attrs, attr4, training.size());
		
		
		CARTNode.Strategy.Builder<StdDev> builder = new CARTNode.Strategy.Builder<StdDev>(stddev);
		

		
		CARTNode<StdDev> root = builder.build(training);
		
		System.out.println(root.toAll());

	}
	
	
	private static class StdDev extends CARTNode.Strategy {
		
		private int total = 0;

		public StdDev(List<Attribute> attrs, Attribute cls, int size) {
			super(attrs, cls);			
			this.total = size;
		}
		
		@Override
		public CARTNode<StdDev> calculate(double last, List<Attribute> attrs, List<Instance> instances) {
			
			CARTNode.Strategy.Builder<StdDev> builder = 
					new CARTNode.Strategy.Builder<StdDev>(this);
			DoubleAdder max = new DoubleAdder();
			max.add(Double.MIN_VALUE);

			PlaceHolder<CARTNode<StdDev>> holder = new PlaceHolder<CARTNode<StdDev>>();

			attrs.stream().forEach(p -> {
				
				List<Object> vals = this.possibleValues(p, instances);
				
				vals.stream().forEach(v -> {
					
					List<Object> list = new ArrayList<Object>();
					list.add(v);
					list.add(v);

					CARTNode<StdDev> node = builder.test(p, list, instances);
					double score = node.score();
					double ratio = (double) instances.size() / this.total;					
	
					if (max.doubleValue() < score && ratio > 0.1) {
						max.reset();
						max.add(score);
						holder.data(node);
					}
				});
			});
		
			return holder.data();
		}

		@Override
		public double score(CARTNode<?> node) {
			// TODO Auto-generated method stub
			return sd(node);
		}
		
		@SuppressWarnings("unused")
		private double cv(List<Instance> instances) {
			
			double [] data = instances.stream().mapToDouble(
					v -> Double.valueOf(v.stringValue(cls))).toArray();
			
			double mean = StatUtils.mean(data);
			double sd = StatUtils.variance(data);
			if (sd == 0)
				return 0;
			
			return Math.sqrt(sd) / mean;
		}
		
		private double sd(CARTNode<?> node) {
					
			DoubleAdder sum = new DoubleAdder();
		
			node.data().entrySet().stream().forEach(p -> {
				
				if (p.getValue().size() > 1) {
					double ssd = ssd(p.getValue());
					sum.add(ssd * (double) p.getValue().size() / node.inputs().size());
				}		
			});
		
			// calculating standard deviation reduction	(SDR)
			double result = ssd(node.inputs()) - sum.doubleValue();
			
			if (result < 0)
				result = 0.0001;
			
			return result;
		}
		
		private double ssd(List<Instance> instances) {

			// calculating the standard deviation before the splits
			double [] data = instances.stream().mapToDouble(
						p -> p.value(cls)).toArray();
						
			if (data.length <= 0)
				return 0;
			
			return Math.sqrt(StatUtils.variance(data));
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
