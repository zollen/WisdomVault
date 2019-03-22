package machinelearning;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class CARTExercise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// defining data format

		ArrayList<String> vals = new ArrayList<String>();
		vals.add("1");
		vals.add("0");

		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("goodCirculation", vals);
		Attribute attr2 = new Attribute("chestPain", vals);
		Attribute attr3 = new Attribute("blockedArteries", vals);
		Attribute attr4 = new Attribute("heartDisease", vals);
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		attrs.add(attr4);

		// defining data dictionary

		Map<Attribute, List<String>> definition = new LinkedHashMap<Attribute, 
						List<String>>();
		definition.put(attr1, vals);
		definition.put(attr2, vals);
		definition.put(attr3, vals);
		definition.put(attr4, vals);

		// training

		List<Instance> training = generateData(100, 0, attrs);

		Gini gini = new Gini(definition, attr4);

		CARTNode.Strategy.Builder<Gini> builder = new CARTNode.Strategy.Builder<Gini>(gini);
		
		CARTNode<Gini> root = builder.build(training);
		
		System.out.println(root.toAll());

	}

	public static List<Instance> generateData(int size, int seed, ArrayList<Attribute> attrs) {

		Random rand = new Random(seed);

		Instances training = new Instances("TRAINING", attrs, size);

		for (int i = 0; i < size; i++) {
			Instance data = new DenseInstance(5);

			int gc = rand.nextInt() % 2 == 0 ? 0 : 1;
			int cp = rand.nextInt() % 2 == 0 ? 0 : 1;
			int ba = rand.nextInt() % 2 == 0 ? 0 : 1;

			data.setValue(attrs.get(0), String.valueOf(gc));
			data.setValue(attrs.get(1), String.valueOf(cp));
			data.setValue(attrs.get(2), String.valueOf(ba));

			double diag = rand.nextDouble() + (gc * -0.6 + cp * 0.2 + ba * 0.3);

			data.setValue(attrs.get(3), diag < 0.6 ? "0" : "1");

			training.add(data);
		}

		return training;
	}

	public static class Gini implements CARTNode.Strategy {

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
		public Attribute cls() {
			return cls;
		}
		
		@Override
		public Map<Attribute, List<String>> definition() {
			return definition;
		}

		@Override
		public CARTNode<Gini> calculate(double ggini, List<Instance> instances) {
			
			CARTNode.Strategy.Builder<Gini> builder = new CARTNode.Strategy.Builder<Gini>(this);
			DoubleAdder min = new DoubleAdder();
			min.add(ggini);
			
			PlaceHolder<CARTNode<Gini>> holder = new PlaceHolder<CARTNode<Gini>>();
			
			this.definition.entrySet().stream().forEach(p -> {
				
				if (p.getKey() != this.cls) {
			
					p.getValue().stream().forEach(v -> {
					
						List<String> list = new ArrayList<String>();
						list.add(v);
						list.add(v);
					
						CARTNode<Gini> node = builder.test(p.getKey(), list, instances);
						double score = node.score();
					
						if (min.doubleValue() > score) {
							min.reset();
							min.add(score);
							node.value(v);
							holder.data(node);
						}
					});
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
			if (binary) {
				return instances.stream().filter(p -> !value.equals(p.stringValue(node.attr()))).collect(Collectors.toList());
			}
			else {
				return instances.stream().filter(p ->  value.equals(p.stringValue(node.attr()))).collect(Collectors.toList());
			}
		}
	}
}
