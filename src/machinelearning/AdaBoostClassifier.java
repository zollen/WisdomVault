package machinelearning;

import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class AdaBoostClassifier {
	
	// Ada Boost 
	// 1. Round #1: Classify the training data
	// 2. Round #1: Identify the incorrectly classified data
	// 3. Round #2: Recreate a set of new training data of the same size but 
	//							randomly pick from the original training data
	//							however last incorrectly classified data has more weight and
	//							therefore get picked more frequently and repeatly.
	// 4. Round #2 Classify the training data again and repeat step #2.
	// 

	public static void main(String[] args) throws Exception {
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

		Instances training = generateData(100, 0, attrs);
		Instances testing = generateData(2, 6, attrs);
		
		Classifier ada = new AdaBoostM1();
		ada.buildClassifier(training);

		System.out.println(((AdaBoostM1) ada).globalInfo());

		Evaluation evaluation = new Evaluation(training);
		evaluation.evaluateModel(ada, testing);
		System.out.println(evaluation.toSummaryString());

		String[] desc = { "APPROVED", "NOT APPROVED" };
		for (int i = 0; i < testing.size(); i++) {
			
			int res = (int) ada.classifyInstance(testing.get(i));
			System.out.println(testing.get(i) + "  RESULT: " + desc[res]);
		}

	}

	public static Instances generateData(int size, int seed, ArrayList<Attribute> attrs) {

		Random rand = new Random(seed);

		Instances training = new Instances("TRAINING", attrs, size);
		training.setClassIndex(training.numAttributes() - 1);

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

}
