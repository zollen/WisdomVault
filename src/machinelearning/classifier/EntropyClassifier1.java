package machinelearning.classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class EntropyClassifier1 {

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


		// training

		List<Instance> training = generateTrainingData(attrs);

		Entropy entropy = new Entropy(attrs, attr4);
	
		CARTNode.Strategy.Builder<Entropy> builder = new CARTNode.Strategy.Builder<Entropy>(entropy);

		CARTNode<Entropy> root = builder.build(training);

		System.out.println(root.toAll());

	}

	public static List<Instance> generateTrainingData(ArrayList<Attribute> attrs) {

		Instances training = new Instances("TRAINING", attrs, 14);

		// Answers:
		// age
		// 	+-<old>  - down
		// 	+-<mild> - competition
		//		   +-<yes> - down
		// 		   +-<no>  - up
		//  +-<new>  - up

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

	private static class Entropy extends CARTNode.Strategy {

		public Entropy(List<Attribute> attrs, Attribute cls) {
			super(attrs, cls);
		}

		@Override
		public CARTNode<Entropy> calculate(double last, List<Attribute> attrs, List<Instance> instances) {

			CARTNode.Strategy.Builder<Entropy> builder = new CARTNode.Strategy.Builder<Entropy>(this);
			DoubleAdder max = new DoubleAdder();
			max.add(Double.MIN_VALUE);

			PlaceHolder<CARTNode<Entropy>> holder = new PlaceHolder<CARTNode<Entropy>>();

			attrs.stream().forEach(p -> {

				CARTNode<Entropy> node = builder.test(p, this.definition().get(p), instances);
				double score = node.score();

				if (max.doubleValue() < score) {
					max.reset();
					max.add(score);
					holder.data(node);
				}
			});
			
			return holder.data();
		}

		@Override
		public double score(CARTNode<?> node) {
			// TODO Auto-generated method stub
			return entropy(node.attr(), node.inputs());
		}
		
		private double gain(Attribute attr, List<Instance> instances) {
			
			Map<String, List<Instance>> profitCategory = this.spreads(cls, instances);
			DoubleAdder info = new DoubleAdder();

			List<Integer> terms = new ArrayList<Integer>();

			profitCategory.entrySet().stream().forEach(p -> {
				terms.add(p.getValue().size());
			});

			terms.stream().forEach(p -> {
				if (p.doubleValue() != 0)
					info.add(-1 * p.doubleValue() / instances.size()
							* Math.log(p.doubleValue() / instances.size()) / Math.log(2));
			});
				
			return info.doubleValue();
		}

		private double entropy(Attribute attr, List<Instance> instances) {

			Map<String, List<Instance>> profitCategory = this.spreads(cls, instances);
			double gain = gain(attr, instances);


			DoubleAdder entropies = new DoubleAdder();

			this.definition().get(attr).stream().forEach(v -> {

				DoubleAdder entropy = new DoubleAdder();
				DoubleAdder subtotal = new DoubleAdder();
				List<Integer> terms = new ArrayList<Integer>();

				profitCategory.entrySet().stream().forEach(c -> {

					List<Instance> list = this.spreads(attr, c.getValue()).get(v);
					int size = 0;
					if (list != null)
						size = list.size();

					subtotal.add(size);
					terms.add(size);
				});
				
				terms.stream().forEach(e -> {
					if (e != 0) {
						entropy.add(-1 * e.doubleValue() / subtotal.doubleValue()
								* Math.log(e.doubleValue() / subtotal.doubleValue()) / Math.log(2));
					}
				});

				if (entropy.doubleValue() != 0 && instances.size() != 0)
					entropies.add(entropy.doubleValue() * subtotal.doubleValue() / instances.size());
			});

			return gain - entropies.doubleValue();
		}

		private Map<String, List<Instance>> spreads(Attribute attr, List<Instance> instances) {

			return instances.stream().collect(Collectors.groupingBy(p -> p.stringValue(attr)));
		}
	}
}
