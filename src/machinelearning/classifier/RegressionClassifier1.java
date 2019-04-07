package machinelearning.classifier;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class RegressionClassifier1 {
	
	private static final String VALUE_CLASS_DOG = "Dog";
	private static final String VALUE_CLASS_CAT = "Cat";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
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
		
		
		Instances training = generateTrainingData(attrs);

		LDA classifier = new LDA();
		
		classifier.setDebug(true);
		classifier.buildClassifier(training);
		
		System.out.println(classifier.globalInfo());
		System.out.println(classifier);
		
		// prediction
		Instances testing = generateTestData(attrs);
		
		
		for (Instance instance : testing)
			System.out.println(instance + " ==> " + classifier.classifyInstance(instance));
		
	}
	
	public static Instances generateTestData(ArrayList<Attribute> attrs) {
		
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
		
		testing.setClassIndex(testing.numAttributes() - 1);
		
		return testing;
	}

	public static Instances generateTrainingData(ArrayList<Attribute> attrs) {

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
		
		training.setClassIndex(training.numAttributes() - 1);

		return training;
	}
}
