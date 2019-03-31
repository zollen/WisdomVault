package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class RegressionClassifier1 {
	
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

	
		
		// prediction
		Instances testing = generateTestingData(attrs);
		
		
		
		System.out.println("Predicting(1.7, Green, Female) ===> " );
		
	
		System.out.println("Predicting(1.53, Red, Male) ===> " );	
		
		
		System.out.println("Predicting(1.44, Blue, Male) ===> " );
		
	}
	
	public static Instances generateTestingData(ArrayList<Attribute> attrs) {
		
		Instances testing = new Instances("TESTING", attrs, 3);
		Instance test1 = new DenseInstance(4);	
		test1.setValue(attrs.get(0), 1.7);
		test1.setValue(attrs.get(1), VALUE_COLOR_GREEN);
		test1.setValue(attrs.get(2), VALUE_GENDER_FEMALE);
		test1.setValue(attrs.get(3), 75.63);
		testing.add(test1);
		
		Instance test2 = new DenseInstance(4);	
		test2.setValue(attrs.get(0), 1.53);
		test2.setValue(attrs.get(1), VALUE_COLOR_RED);
		test2.setValue(attrs.get(2), VALUE_GENDER_MALE);
		test2.setValue(attrs.get(3), 72.31);
		testing.add(test2);
		
		Instance test3 = new DenseInstance(4);	
		test3.setValue(attrs.get(0), 1.44);
		test3.setValue(attrs.get(1), VALUE_COLOR_BLUE);
		test3.setValue(attrs.get(2), VALUE_GENDER_MALE);
		test3.setValue(attrs.get(3), 73.11);
		testing.add(test3);
		
		testing.setClassIndex(testing.numAttributes() - 1);
		
		return testing;
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
