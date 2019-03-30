package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.Precision;

import machinelearning.classifier.CARTNode.CARTKey;
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
	
	private static final double LEARNING_RATE = 0.1;

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

		List<Double> first = get(training, attr4);
		
		double avg = avg(training, attr4);
		
		System.out.println("AVG: " + ff.format(avg));
		List<Double> avgs = new ArrayList<Double>();
		for (int i = 0; i < training.size(); i++)
			avgs.add(avg);
		
		System.out.println("Original Values");
		System.out.println("---------------");
		print(0, training, attr4);
		
		System.out.println("Residuals Values");
		System.out.println("----------------");
		adjust(training, attr4, avgs);
		print(0, training, attr4);
	
		List<Double> last = null;
		Set<CARTNode<StdDev>> forest = new HashSet<CARTNode<StdDev>>();
		
		int i = 1;
		for (; i <= 1000; i++) {
			
			StdDev stddev = new StdDev(attrs, attr4);		
			CARTNode.Strategy.Builder<StdDev> builder = new CARTNode.Strategy.Builder<StdDev>(stddev);		
			CARTNode<StdDev> root = builder.build(training);
			stddev.normalized(root, training);
			
		//	System.out.println(root.toAll());
		
			List<Double> residuals = get(root, training);
			List<Double> results = new ArrayList<Double>();
			
			// gradient adjustment
			for (int j = 0; j < first.size(); j++) {
				double residual = residuals.get(j);
				double result = LEARNING_RATE * residual;
				results.add(result);
			}
			
			// accumulated adjustment
			avgs = add(avgs, results);
			
			// new residuals
			results = substract(first, avgs);
			
			set(training, attr4, results);
			
			print(i, training, attr4);
			
			forest.add(root);
			
			
			if (last != null) {
				if (equals(results, last, 0.0001))
					break;
			}
			
			last = copy(results);
		}
		
		Instances testing = new Instances("TESTING", attrs, 1);
		Instance test = new DenseInstance(4);	
		test.setValue(attr1, 1.65);
		test.setValue(attr2, VALUE_COLOR_RED);
		test.setValue(attr3, VALUE_GENDER_MALE);
		testing.add(test);
		
		// prediction
		double weight = avg;
		for (CARTNode<?> node : forest) {
			
			CARTNode<?> res = node.classify(testing.get(0));
			Number num = (Number) res.data().keySet().stream().findFirst().get().get();
			weight += 0.1 * num.doubleValue();
		}
		
		System.out.println(testing.get(0) + " == predicted weight ==> " + weight);
		
	}
	
	
	private static class StdDev extends CARTNode.Strategy {

		public StdDev(List<Attribute> attrs, Attribute cls) {
			super(attrs, cls);
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
	
					if (max.doubleValue() < score && last > 0.1 && instances.size() > 1) {
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
		
		@Override
		public double stop(CARTNode<?> node, CARTKey key, double last, double score) {
			
			List<Instance> instances = node.data().get(key);
		
			return cv(instances);
		}
		
		@Override
		public List<Object> possibleValues(Attribute attr, List<Instance> instances) {
			
			List<Object> vals = new ArrayList<Object>();
			
			if (attr.isNominal()) {
				
				Enumeration<Object> o = attr.enumerateValues();
				while (o != null && o.hasMoreElements())
					vals.add(o.nextElement());
			}
			else
			if (attr.isNumeric()) {
				
				List<Double> nums = new ArrayList<Double>();
				
				instances.stream().forEach(p -> nums.add(p.value(attr)));
				
				Collections.sort(nums);
		
				vals.addAll(nums);
			}
			
			return vals;
		}

		private double cv(List<Instance> instances) {
			
			double [] data = instances.stream().mapToDouble(
					v -> v.value(cls)).toArray();
			
			double mean = StatUtils.mean(data);
			double sd = StatUtils.populationVariance(data);
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
				result = 0.00001;
			
			return result;
		}
		
		private double ssd(List<Instance> instances) {

			// calculating the standard deviation before the splits
			double [] data = instances.stream().mapToDouble(
						p -> p.value(cls)).toArray();
						
			if (data.length <= 0)
				return 0;
			
			return Math.sqrt(StatUtils.populationVariance(data));
		}
		
		public void normalized(CARTNode<?> root, List<Instance> instances) {
			
			for (Instance instance : instances) {
				
				CARTNode<?> node = root.classify(instance);
				
				DoubleAdder sum = new DoubleAdder();
				DoubleAdder num = new DoubleAdder();
				List<Instance> inst = new ArrayList<Instance>();
				
				if (node != null) {
					node.data().entrySet().stream().forEach(p -> {
						
						sum.add(((Number)p.getKey().get()).doubleValue());
						num.add(1);
						inst.addAll(p.getValue());
					});
					
					double avg = sum.doubleValue() / num.doubleValue();
					
					node.data().clear();
					node.data().put(new CARTKey(avg, 0), inst);
				}	
			}
		}
	}
	
	public static List<Double> get(CARTNode<?> root, List<Instance> instances) {
		
		List<Double> list = new ArrayList<Double>();
		
		for (Instance instance : instances) {
			
			CARTNode<?> node = root.classify(instance);
			
			if (node != null) {
				Number num = (Number) node.data().keySet().stream().findFirst().get().get();
				list.add(num.doubleValue());
			}	
		}
		
		return list;
	}
	
	public static List<Double> add(List<Double> left, List<Double> right) {
		
		List<Double> result = new ArrayList<Double>();
		
		for (int i = 0; i < left.size(); i++) {
			
			Double ll = left.get(i);
			Double rr = right.get(i);
			
			result.add(ll + rr);
		}
		
		return result;
	}
	
	public static List<Double> substract(List<Double> left, List<Double> right) {
		
		List<Double> result = new ArrayList<Double>();
		
		for (int i = 0; i < left.size(); i++) {
			
			Double ll = left.get(i);
			Double rr = right.get(i);
			
			result.add(ll - rr);
		}
		
		return result;
	}
	
	public static boolean equals(List<Double> left, List<Double> right, double tol) {
		
		if (left.size() != right.size())
			return false;
		
		boolean res = false;
		
		for (int i = 0; i < left.size(); i++) {
			
			Double ll = left.get(i);
			Double rr = right.get(i);
			
			if (ll != rr)
				res = false;
			
			if (Precision.equals(ll, rr, tol))
				res = true;
			else
				break;
		}
		
		return res;
	}
	
	public static double avg(Instances instances, Attribute attr) {
		
		double total = instances.stream().mapToDouble(p -> p.value(attr)).sum();
		return total / instances.size();
	}
	
	public static void adjust(Instances instances, Attribute attr, List<Double> diffs) {
		
		AtomicInteger index = new AtomicInteger();
		instances.stream().forEach(p -> 
			p.setValue(attr, p.value(attr) - diffs.get(index.getAndIncrement())));
	}
	
	public static void print(int round, Instances instances, Attribute attr) {
		
		Function<Instance, String> func = p ->  String.valueOf(ff.format(p.value(attr)));
		
		System.out.println("Round #" + round + ": " + instances.stream().map(func).collect(Collectors.joining(", ")));
	}
	
	public static List<Double> get(Instances instances, Attribute attr) {
		
		List<Double> vals = new ArrayList<Double>();
		
		instances.stream().forEach(p -> vals.add(p.value(attr)));
		
		return vals;
	}
	
	public static void set(Instances instances, Attribute attr, List<Double> values) {
		
		AtomicInteger index = new AtomicInteger();
		instances.stream().forEach(p -> p.setValue(attr, values.get(index.getAndIncrement())));
	}
	
	public static List<Double> copy(List<Double> source) {
		
		List<Double> results = new ArrayList<Double>();
		
		for (Double src : source) {		
			results.add(Double.valueOf(src));
		}
		
		return results;
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
