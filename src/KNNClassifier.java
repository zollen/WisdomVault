import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class KNNClassifier {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ArrayList<Attribute> attrs1 = new ArrayList<Attribute>();
		Attribute x0 = new Attribute("x0");
		Attribute x1 = new Attribute("x1");
		Attribute x2 = new Attribute("x2");
		Attribute x3 = new Attribute("x3");
		
		attrs1.add(x0);
		attrs1.add(x1);
		attrs1.add(x2);
		attrs1.add(x3);
			
		ArrayList<String> classVal = new ArrayList<String>();
		classVal.add("Y");
		classVal.add("N");
		
		Attribute cls = new Attribute("class", classVal);
			
		attrs1.add(cls);
			
		Instances training = new Instances("X", attrs1, 5);
		training.setClassIndex(training.numAttributes() - 1);
		
		Instance training1 = new DenseInstance(5);
		training1.setValue(x0, 5.1);
		training1.setValue(x1, 3.5);
		training1.setValue(x2, 1.4);
		training1.setValue(x3, 0.2);
		training1.setValue(cls, "Y");
		
		Instance training2 = new DenseInstance(5);
		training2.setValue(x0, 4.9);
		training2.setValue(x1, 3.0);
		training2.setValue(x2, 1.4);
		training2.setValue(x3, 0.1);
		training2.setValue(cls, "N");
		
		Instance training3 = new DenseInstance(5);
		training3.setValue(x0, 4.7);
		training3.setValue(x1, 3.2);
		training3.setValue(x2, 1.3);
		training3.setValue(x3, 0.2);
		training3.setValue(cls, "N");
		
		Instance training4 = new DenseInstance(5);
		training4.setValue(x0, 4.6);
		training4.setValue(x1, 3.1);
		training4.setValue(x2, 1.5);
		training4.setValue(x3, 0.1);
		training4.setValue(cls, "Y");
		
		Instance training5 = new DenseInstance(5);
		training5.setValue(x0, 5.0);
		training5.setValue(x1, 3.6);
		training5.setValue(x2, 1.4);
		training5.setValue(x3, 0.2);
		training5.setValue(cls, "N");
		
		training.add(training1);
		training.add(training2);
		training.add(training3);
		training.add(training4);
		training.add(training5);
		
		
		ArrayList<Attribute> attrs2 = new ArrayList<Attribute>();
		attrs2.add(new Attribute("x0"));
		attrs2.add(new Attribute("x1"));
		attrs2.add(new Attribute("x2"));
		attrs2.add(new Attribute("x3"));
		attrs2.add(cls);
		
		Instances test = new Instances("T", attrs2, 2);
		
		Instance test1 = new DenseInstance(5);
		test1.setValue(x0, 4.8);
		test1.setValue(x1, 3.1);
		test1.setValue(x2, 1.5);
		test1.setValue(x3, 0.2);
		test1.setValue(cls, "Y");
		
		Instance test2 = new DenseInstance(5);
		test2.setValue(x0, 5.0);
		test2.setValue(x1, 3.4);
		test2.setValue(x2, 1.4);
		test2.setValue(x3, 0.1);
		test2.setValue(cls, "N");
		
		test.add(test1);
		test.add(test2);
		
		test.setClassIndex(test.numAttributes() - 1);
	
		Classifier ibk = new IBk();		
		ibk.buildClassifier(training);
 
		int class1 = (int) ibk.classifyInstance(test.get(0));
		int class2 = (int) ibk.classifyInstance(test.get(1));
 
		String [] desc = { "APPROVED", "NOT APPROVED" };
		System.out.println("first: " + desc[class1] + "\nsecond: " + desc[class2]);

	}

}
