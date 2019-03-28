package machinelearning.classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.DoubleAdder;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class GiniClassifier1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// defining data format

		ArrayList<String> vals = new ArrayList<String>();
		vals.add("T");
		vals.add("F");

		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("chestPain", vals);
		Attribute attr2 = new Attribute("goodCirculation", vals);
		Attribute attr3 = new Attribute("blockedArteries", vals);
		Attribute attr4 = new Attribute("heartDisease", vals);
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		attrs.add(attr4);


		// training

		List<Instance> training = generateTrainingData(100, 0, attrs);

		Gini gini = new Gini(attrs, attr4);

		CARTNode.Strategy.Builder<Gini> builder = new CARTNode.Strategy.Builder<Gini>(gini);
		
		CARTNode<Gini> root = builder.build(training);
		
		System.out.println(root.toAll());

	}

	public static List<Instance> generateTrainingData(int size, int seed, ArrayList<Attribute> attrs) {

		Instances training = new Instances("TRAINING", attrs, size);

		Instance data1 = new DenseInstance(4);	
		data1.setValue(attrs.get(0), "F");
		data1.setValue(attrs.get(1), "F");
		data1.setValue(attrs.get(2), "F");
		data1.setValue(attrs.get(3), "F");
		training.add(data1);
		
		Instance data2 = new DenseInstance(4);	
		data2.setValue(attrs.get(0), "T");
		data2.setValue(attrs.get(1), "T");
		data2.setValue(attrs.get(2), "T");
		data2.setValue(attrs.get(3), "T");
		training.add(data2);
		
		Instance data3 = new DenseInstance(4);	
		data3.setValue(attrs.get(0), "T");
		data3.setValue(attrs.get(1), "T");
		data3.setValue(attrs.get(2), "F");
		data3.setValue(attrs.get(3), "F");
		training.add(data3);
		
		Instance data4 = new DenseInstance(4);	
		data4.setValue(attrs.get(0), "T");
		data4.setValue(attrs.get(1), "F");
		data4.setValue(attrs.get(2), "T");
		data4.setValue(attrs.get(3), "T");
		training.add(data4);
		
		Instance data5 = new DenseInstance(4);	
		data5.setValue(attrs.get(0), "F");
		data5.setValue(attrs.get(1), "T");
		data5.setValue(attrs.get(2), "F");
		data5.setValue(attrs.get(3), "F");
		training.add(data5);
		
		Instance data6 = new DenseInstance(4);	
		data6.setValue(attrs.get(0), "F");
		data6.setValue(attrs.get(1), "T");
		data6.setValue(attrs.get(2), "T");
		data6.setValue(attrs.get(3), "F");
		training.add(data6);
		
		Instance data7 = new DenseInstance(4);	
		data7.setValue(attrs.get(0), "T");
		data7.setValue(attrs.get(1), "F");
		data7.setValue(attrs.get(2), "F");
		data7.setValue(attrs.get(3), "F");
		training.add(data7);
		
		Instance data8 = new DenseInstance(4);	
		data8.setValue(attrs.get(0), "T");
		data8.setValue(attrs.get(1), "F");
		data8.setValue(attrs.get(2), "T");
		data8.setValue(attrs.get(3), "T");
		training.add(data8);
		
	
		training.setClassIndex(training.numAttributes() - 1);

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
						
				this.definition().get(p).stream().forEach(v -> {
						
					List<String> list = new ArrayList<String>();
					list.add("T");
					list.add("T");
					
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
			DoubleAdder sum = new DoubleAdder();
			
			if (node.inputs().size() <= 0)
				return 0.0;

			if (node.children().size() <= 0) {
				
				return 1 - node.data().entrySet().stream().mapToDouble(
						p -> Math.pow((double) p.getValue().size() / node.inputs().size(), 2)).sum();
			} else {

				node.children().entrySet().stream().forEach(p -> {

					sum.add((double) node.data().get(p.getKey()).size() / node.inputs().size() * score(p.getValue()));
				});

				return sum.doubleValue();
			}
		}
	}
}
