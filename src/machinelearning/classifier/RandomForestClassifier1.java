package machinelearning.classifier;
import java.util.ArrayList;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.trees.RandomTree;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class RandomForestClassifier1 {
	
	private static final String VALUE_OUTLOOK_SUNNY = "sunny";
	private static final String VALUE_OUTLOOK_OVERCAST = "overcast";
	private static final String VALUE_OUTLOOK_RAINY = "rainy";
	
	private static final String VALUE_TEMP_HOT = "hot";
	private static final String VALUE_TEMP_MILD = "mild";
	private static final String VALUE_TEMP_COOL = "cool";
	
	private static final String VALUE_HUMIDITY_HIGH = "high";
	private static final String VALUE_HUMIDITY_NORMAL = "normal";
	
	private static final String VALUE_WINDY_TRUE = "true";
	private static final String VALUE_WINDY_FALSE = "false";
	
	private static final String VALUE_PLAY_YES = "Yes";
	private static final String VALUE_PLAY_NO = "No";
	
	/*
	 * Learning trees are very popular base models for ensemble methods. Strong learners 
	 * composed of multiple trees can be called “forests”. Trees that compose a forest can 
	 * be chosen to be either shallow (few depths) or deep (lot of depths, if not fully grown). 
	 * Shallow trees have less variance but higher bias and then will be better choice for 
	 * sequential methods that we will described thereafter. Deep trees, on the other side, 
	 * have low bias but high variance and, so, are relevant choices for bagging method that 
	 * is mainly focused at reducing variance.
	 * 
	 * The random forest approach is a bagging method where deep trees, fitted on bootstrap 
	 * samples, are combined to produce an output with lower variance. However, random 
	 * forests also use another trick to make the multiple fitted trees a bit less 
	 * correlated with each others: when growing each tree, instead of only sampling over 
	 * the observations in the dataset to generate a bootstrap sample, we also sample over 
	 * features and keep only a random subset of them to build the tree.
	 */

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		ArrayList<String> outlookVals = new ArrayList<String>();
		outlookVals.add(VALUE_OUTLOOK_SUNNY);
		outlookVals.add(VALUE_OUTLOOK_OVERCAST);
		outlookVals.add(VALUE_OUTLOOK_RAINY);
		
		ArrayList<String> tempVals = new ArrayList<String>();
		tempVals.add(VALUE_TEMP_HOT);
		tempVals.add(VALUE_TEMP_MILD);
		tempVals.add(VALUE_TEMP_COOL);
		
		ArrayList<String> humidityVals = new ArrayList<String>();
		humidityVals.add(VALUE_HUMIDITY_HIGH);
		humidityVals.add(VALUE_HUMIDITY_NORMAL);
		
		ArrayList<String> windyVals = new ArrayList<String>();
		windyVals.add(VALUE_WINDY_TRUE);
		windyVals.add(VALUE_WINDY_FALSE);
		
		ArrayList<String> playVals = new ArrayList<String>();
		playVals.add(VALUE_PLAY_YES);
		playVals.add(VALUE_PLAY_NO);
		

		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("outlook", outlookVals);
		Attribute attr2 = new Attribute("temperaure", tempVals);
		Attribute attr3 = new Attribute("humidity", humidityVals);
		Attribute attr4 = new Attribute("windy", windyVals);
		Attribute attr5 = new Attribute("play", playVals);
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		attrs.add(attr4);
		attrs.add(attr5);
		
		Instances training = generateTrainingData(attrs);	
		
		RandomTree tree = new RandomTree();
		tree.setKValue(2);
	
		// Decision Trees in general are not good at classifying new data
		// This is where RamdomForest comes in.
		// It vastly improves the performance of classifying new data.
		// 
		// Creating a ramdom forest
		// 1. Resample the training data with a same size data with replacement (duplicated data) 
		// 2. Run the resampled data with Decision Tree (but randomly picked K number of attributes for building each node
		// 3. repeat step 1 and step 2 until we have a large number of trees.
		// 4. Input new test data to the forest, the majority result wins
		
		
		RandomForest forest = new RandomForest();
		forest.setClassifier(tree);
		forest.setSeed(1);
		forest.setNumIterations(50);  //  number of trees
		forest.setBreakTiesRandomly(true);
		forest.setDebug(true);
		forest.setComputeAttributeImportance(true);
		
		forest.buildClassifier(training);
	
		
		Instances testing = generateTestData(attrs);
		
		Evaluation eval = new Evaluation(testing);
		eval.evaluateModel(forest, testing);
		
		System.out.println(forest.globalInfo());
		System.out.println(eval.toSummaryString());
		System.out.println(eval.toMatrixString());
		System.out.println("Error Rate: " + eval.errorRate());
		System.out.println("==================================");
		System.out.println(forest);
		
	}
	
	public static Instances generateTestData(ArrayList<Attribute> attrs) {
		
		Instances testing = new Instances("TESTING", attrs, 3);
		
		Instance data1 = new DenseInstance(5);	
		data1.setValue(attrs.get(0), VALUE_OUTLOOK_SUNNY);
		data1.setValue(attrs.get(1), VALUE_TEMP_COOL);
		data1.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data1.setValue(attrs.get(3), VALUE_WINDY_TRUE);
		data1.setValue(attrs.get(4), VALUE_PLAY_NO);
		testing.add(data1);
		
		Instance data2 = new DenseInstance(5);	
		data2.setValue(attrs.get(0), VALUE_OUTLOOK_RAINY);
		data2.setValue(attrs.get(1), VALUE_TEMP_COOL);
		data2.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data2.setValue(attrs.get(3), VALUE_WINDY_TRUE);
		data2.setValue(attrs.get(4), VALUE_PLAY_NO);
		testing.add(data2);
		
		Instance data3 = new DenseInstance(5);	
		data3.setValue(attrs.get(0), VALUE_OUTLOOK_RAINY);
		data3.setValue(attrs.get(1), VALUE_TEMP_MILD);
		data3.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data3.setValue(attrs.get(3), VALUE_WINDY_FALSE);
		data3.setValue(attrs.get(4), VALUE_PLAY_YES);
		testing.add(data3);
		
		testing.setClassIndex(testing.numAttributes() - 1);
		
		
		return testing;
	}
	
	public static Instances generateTrainingData(ArrayList<Attribute> attrs) {

		Instances training = new Instances("TRAINING", attrs, 14);
		
		// AnswerS:
		// outlook
		//   +-<sunny>    - humidity
		//						+-<high> - no
		//						+-<normal> - yes
		//   +-<overcast> - yes
		//   +-<rainy>    - windy
		//						+-<false> - yes
		//						+-<true>  - no
		
		Instance data1 = new DenseInstance(5);	
		data1.setValue(attrs.get(0), VALUE_OUTLOOK_SUNNY);
		data1.setValue(attrs.get(1), VALUE_TEMP_HOT);
		data1.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data1.setValue(attrs.get(3), VALUE_WINDY_FALSE);
		data1.setValue(attrs.get(4), VALUE_PLAY_NO);
		training.add(data1);
		
		Instance data2 = new DenseInstance(5);	
		data2.setValue(attrs.get(0), VALUE_OUTLOOK_SUNNY);
		data2.setValue(attrs.get(1), VALUE_TEMP_HOT);
		data2.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data2.setValue(attrs.get(3), VALUE_WINDY_TRUE);
		data2.setValue(attrs.get(4), VALUE_PLAY_NO);
		training.add(data2);
		
		Instance data3 = new DenseInstance(5);	
		data3.setValue(attrs.get(0), VALUE_OUTLOOK_OVERCAST);
		data3.setValue(attrs.get(1), VALUE_TEMP_HOT);
		data3.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data3.setValue(attrs.get(3), VALUE_WINDY_FALSE);
		data3.setValue(attrs.get(4), VALUE_PLAY_YES);
		training.add(data3);
		
		Instance data4 = new DenseInstance(5);	
		data4.setValue(attrs.get(0), VALUE_OUTLOOK_RAINY);
		data4.setValue(attrs.get(1), VALUE_TEMP_MILD);
		data4.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data4.setValue(attrs.get(3), VALUE_WINDY_FALSE);
		data4.setValue(attrs.get(4), VALUE_PLAY_YES);
		training.add(data4);
		
		Instance data5 = new DenseInstance(5);	
		data5.setValue(attrs.get(0), VALUE_OUTLOOK_RAINY);
		data5.setValue(attrs.get(1), VALUE_TEMP_COOL);
		data5.setValue(attrs.get(2), VALUE_HUMIDITY_NORMAL);
		data5.setValue(attrs.get(3), VALUE_WINDY_FALSE);
		data5.setValue(attrs.get(4), VALUE_PLAY_YES);
		training.add(data5);
		
		Instance data6 = new DenseInstance(5);	
		data6.setValue(attrs.get(0), VALUE_OUTLOOK_RAINY);
		data6.setValue(attrs.get(1), VALUE_TEMP_COOL);
		data6.setValue(attrs.get(2), VALUE_HUMIDITY_NORMAL);
		data6.setValue(attrs.get(3), VALUE_WINDY_TRUE);
		data6.setValue(attrs.get(4), VALUE_PLAY_NO);
		training.add(data6);
		
		Instance data7 = new DenseInstance(5);	
		data7.setValue(attrs.get(0), VALUE_OUTLOOK_OVERCAST);
		data7.setValue(attrs.get(1), VALUE_TEMP_COOL);
		data7.setValue(attrs.get(2), VALUE_HUMIDITY_NORMAL);
		data7.setValue(attrs.get(3), VALUE_WINDY_TRUE);
		data7.setValue(attrs.get(4), VALUE_PLAY_YES);
		training.add(data7);
		
		Instance data8 = new DenseInstance(5);	
		data8.setValue(attrs.get(0), VALUE_OUTLOOK_SUNNY);
		data8.setValue(attrs.get(1), VALUE_TEMP_MILD);
		data8.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data8.setValue(attrs.get(3), VALUE_WINDY_FALSE);
		data8.setValue(attrs.get(4), VALUE_PLAY_NO);
		training.add(data8);
		
		Instance data9 = new DenseInstance(5);	
		data9.setValue(attrs.get(0), VALUE_OUTLOOK_SUNNY);
		data9.setValue(attrs.get(1), VALUE_TEMP_COOL);
		data9.setValue(attrs.get(2), VALUE_HUMIDITY_NORMAL);
		data9.setValue(attrs.get(3), VALUE_WINDY_FALSE);
		data9.setValue(attrs.get(4), VALUE_PLAY_YES);
		training.add(data9);
		
		Instance data10 = new DenseInstance(5);	
		data10.setValue(attrs.get(0), VALUE_OUTLOOK_RAINY);
		data10.setValue(attrs.get(1), VALUE_TEMP_MILD);
		data10.setValue(attrs.get(2), VALUE_HUMIDITY_NORMAL);
		data10.setValue(attrs.get(3), VALUE_WINDY_FALSE);
		data10.setValue(attrs.get(4), VALUE_PLAY_YES);
		training.add(data10);
		
		Instance data11 = new DenseInstance(5);	
		data11.setValue(attrs.get(0), VALUE_OUTLOOK_SUNNY);
		data11.setValue(attrs.get(1), VALUE_TEMP_MILD);
		data11.setValue(attrs.get(2), VALUE_HUMIDITY_NORMAL);
		data11.setValue(attrs.get(3), VALUE_WINDY_TRUE);
		data11.setValue(attrs.get(4), VALUE_PLAY_YES);
		training.add(data11);
		
		Instance data12 = new DenseInstance(5);	
		data12.setValue(attrs.get(0), VALUE_OUTLOOK_OVERCAST);
		data12.setValue(attrs.get(1), VALUE_TEMP_MILD);
		data12.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data12.setValue(attrs.get(3), VALUE_WINDY_TRUE);
		data12.setValue(attrs.get(4), VALUE_PLAY_YES);
		training.add(data12);
		
		Instance data13 = new DenseInstance(5);	
		data13.setValue(attrs.get(0), VALUE_OUTLOOK_OVERCAST);
		data13.setValue(attrs.get(1), VALUE_TEMP_HOT);
		data13.setValue(attrs.get(2), VALUE_HUMIDITY_NORMAL);
		data13.setValue(attrs.get(3), VALUE_WINDY_FALSE);
		data13.setValue(attrs.get(4), VALUE_PLAY_YES);
		training.add(data13);
		
		Instance data14 = new DenseInstance(5);	
		data14.setValue(attrs.get(0), VALUE_OUTLOOK_RAINY);
		data14.setValue(attrs.get(1), VALUE_TEMP_MILD);
		data14.setValue(attrs.get(2), VALUE_HUMIDITY_HIGH);
		data14.setValue(attrs.get(3), VALUE_WINDY_TRUE);
		data14.setValue(attrs.get(4), VALUE_PLAY_NO);
		training.add(data14);
		
		training.setClassIndex(training.numAttributes() - 1);

		return training;
	}

}
