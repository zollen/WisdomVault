package machinelearning.classifier;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class GradientBoostClassifier {
	
	private static final String VALUE_COLOR_GREEN = "Green";
	private static final String VALUE_COLOR_BLUE = "Blue";
	private static final String VALUE_COLOR_RED = "Red";
	
	private static final String VALUE_GENDER_MALE = "Male";
	private static final String VALUE_GENDER_FEMALE = "Female";

	public static void main(String[] args) {
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
		
		training.stream().forEach(p -> System.out.println(p));
		
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
		
		return training;
	}

}
