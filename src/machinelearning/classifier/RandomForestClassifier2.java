package machinelearning.classifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.DoubleAdder;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class RandomForestClassifier2 {
	
	private static final Random rand = new Random();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// defining data format

		ArrayList<String> vals = new ArrayList<String>();
		vals.add("T");
		vals.add("F");

		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("chestPain", vals, 1);
		Attribute attr2 = new Attribute("goodCirculation", vals, 2);
		Attribute attr3 = new Attribute("blockedArteries", vals, 3);
		Attribute attr4 = new Attribute("weight", 4);
		Attribute attr5 = new Attribute("heartDisease", vals, 5);
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		attrs.add(attr4);
		attrs.add(attr5);


		// training

		Instances training = generateTrainingData(attrs);
		
		Set<CARTNode<Gini>> forest = new HashSet<CARTNode<Gini>>();
		
		int k = 2;
		
		for (int i = 1; i <= 100; i++) {
			
			Instances instances = resampleTrainingData(attrs, training);
			
			List<Attribute> nattrs = resampleAttributes(attrs, attr5, k);

			Gini tree = new Gini(nattrs, attr5);

			CARTNode.Strategy.Builder<Gini> builder = new CARTNode.Strategy.Builder<Gini>(tree);
		
			CARTNode<Gini> root = builder.build(instances);
			
			forest.add(root);
		
//			System.out.println(nattrs);
//			System.out.println("Round #" + i + root.toAll());
		}
		
		Instances testing = generateTestingData(attrs);
		
		List<CARTNode<?>> results = new ArrayList<CARTNode<?>>();
		
		forest.stream().forEach(p -> results.add(p.classify(testing.get(0))));
		
		DoubleAdder tt = new DoubleAdder();
		DoubleAdder ff = new DoubleAdder();
		results.stream().forEach(p -> {
			
			p.data().entrySet().stream().forEach(d -> {
				
				if ("T".equals(d.getKey().get()))
					tt.add(d.getValue().size() > 0 ? 1 : 0);
				else
					ff.add(d.getValue().size() > 0 ? 1 : 0);
			});
		});
			
		System.out.println(testing.get(0) + " ---> " + "T: " + tt + ", F: " + ff);

	}
	
	public static Instance copy(ArrayList<Attribute> attrs, Instance instance) {
		
		Instance data = new DenseInstance(5);
		
		data.setValue(attrs.get(0), instance.stringValue(attrs.get(0)));
		data.setValue(attrs.get(1), instance.stringValue(attrs.get(1)));
		data.setValue(attrs.get(2), instance.stringValue(attrs.get(2)));
		data.setValue(attrs.get(3), instance.value(attrs.get(3)));
		data.setValue(attrs.get(4), instance.stringValue(attrs.get(4)));
		
		return data;
	}
	
	public static List<Attribute> resampleAttributes(ArrayList<Attribute> attrs, Attribute cls, int k) {
		
		List<Attribute> aa = new ArrayList<Attribute>();
		
		for (int i = 0; i < attrs.size(); i++) {		
			aa.add(attrs.get(i));
		}
		
		aa.remove(cls);
		
		for (int i = 0; i < aa.size(); i++) {
			int from = rand.nextInt(aa.size());
			int to = rand.nextInt(aa.size());
			
			Attribute tattr = aa.get(to);
			Attribute fattr = aa.get(from);
			aa.set(to, fattr);
			aa.set(from, tattr);
		}
		
		List<Attribute> list = aa.subList(0, k);
		list.add(attrs.get(attrs.size() - 1));
		
		return list;
	}
	
	public static Instances resampleTrainingData(ArrayList<Attribute> attrs, Instances instances) {
		
		Instances data = new Instances("TRANING", attrs, instances.size());
		
		for (int i = 0; i < instances.size(); i++) {		
			data.add(copy(attrs, instances.get(rand.nextInt(instances.size()))));
		}
		
		data.setClassIndex(data.numAttributes() - 1);
			
		return data;
	}
	
	public static Instances generateTestingData(ArrayList<Attribute> attrs) {
		
		Instances testing = new Instances("TESTING", attrs, 1);
		
		Instance data = new DenseInstance(5);	
		data.setValue(attrs.get(0), "T");
		data.setValue(attrs.get(1), "F");
		data.setValue(attrs.get(2), "F");
		data.setValue(attrs.get(3), 168);
		data.setValue(attrs.get(4), "F");
		testing.add(data);
		
		testing.setClassIndex(testing.numAttributes() - 1);
		
		return testing;
	}

	public static Instances generateTrainingData(ArrayList<Attribute> attrs) {

		Instances training = new Instances("TRAINING", attrs, 4);

		Instance data1 = new DenseInstance(5);	
		data1.setValue(attrs.get(0), "F");
		data1.setValue(attrs.get(1), "F");
		data1.setValue(attrs.get(2), "F");
		data1.setValue(attrs.get(3), 125);
		data1.setValue(attrs.get(4), "F");
		training.add(data1);
		
		Instance data2 = new DenseInstance(5);	
		data2.setValue(attrs.get(0), "T");
		data2.setValue(attrs.get(1), "T");
		data2.setValue(attrs.get(2), "T");
		data2.setValue(attrs.get(3), 180);
		data2.setValue(attrs.get(4), "T");
		training.add(data2);
		
		Instance data3 = new DenseInstance(5);	
		data3.setValue(attrs.get(0), "T");
		data3.setValue(attrs.get(1), "T");
		data3.setValue(attrs.get(2), "F");
		data3.setValue(attrs.get(3), 210);
		data3.setValue(attrs.get(4), "F");
		training.add(data3);
		
		Instance data4 = new DenseInstance(5);	
		data4.setValue(attrs.get(0), "T");
		data4.setValue(attrs.get(1), "F");
		data4.setValue(attrs.get(2), "T");
		data4.setValue(attrs.get(3), 167);
		data4.setValue(attrs.get(4), "T");
		training.add(data4);
		
		training.setClassIndex(training.numAttributes() - 1);

		return training;
	}

	private static class Gini extends CARTNode.Strategy {

		public Gini(List<Attribute> attrs, Attribute cls) {
			super(attrs, cls);
		}
		
		@Override
		public Attribute cls() {
			return cls;
		}
		
		@Override
		public CARTNode<Gini> calculate(double last, List<Attribute> attrs, List<Instance> instances) {
			
			CARTNode.Strategy.Builder<Gini> builder = new CARTNode.Strategy.Builder<Gini>(this);
			DoubleAdder min = new DoubleAdder();
			min.add(last);
			
			PlaceHolder<CARTNode<Gini>> holder = new PlaceHolder<CARTNode<Gini>>();
				
			attrs.stream().forEach(p -> {
										
				CARTNode<Gini> node = builder.test(p, this.definition().get(p), instances);
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
	}
}
