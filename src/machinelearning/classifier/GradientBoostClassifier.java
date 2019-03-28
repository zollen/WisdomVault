package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import weka.classifiers.trees.RandomTree;
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

		RandomTree tree = new RandomTree();
		tree.setKValue(3);
		tree.buildClassifier(training);
		
		System.out.println(tree);
		
		double [][] res = tree.distributionsForInstances(training);
		for (int i = 0; i < res.length; i++) {
			for (int j = 0; j < res[i].length; j++) {
				System.out.println(i + ", " + j + " --> " + res[i][j]);
			}
		}
		
		for (Instance instance : training) {
			System.out.println(instance + " ===> " + tree.classifyInstance(instance));
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
