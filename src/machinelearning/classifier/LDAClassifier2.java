package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class LDAClassifier2 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
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

		List<Instance> dogList = getInstances(attr3, VALUE_CLASS_DOG, training);
		List<Instance> catList = getInstances(attr3, VALUE_CLASS_CAT, training);
		
		DMatrixRMaj coVarDogs = new DMatrixRMaj(new Covariance(convert(attr1, attr2, dogList))
							.getCovarianceMatrix().getData());
		
		DMatrixRMaj coVarCats = new DMatrixRMaj(new Covariance(convert(attr1, attr2, catList))
							.getCovarianceMatrix().getData());
		
		DMatrixRMaj dogMean = avg(attr1, attr2, dogList);
		DMatrixRMaj catMean = avg(attr1, attr2, catList);
		
		Equation eq = new Equation();
		eq.alias(coVarDogs, "C_DOGS");
		eq.alias(coVarCats, "C_CATS");
		
		eq.alias(dogMean, "DMEAN");
		eq.alias(catMean, "CMEAN");
		
		// with LDA:
		// ( mean(DOGS) - mean(CATS) )^2 / ( how_much_scatter(DOGS)^2 + how_much_scatter(CATS)^2 )
		// We want to maximize the mean(DOGS) - mean(CATS), but
		// minimize the variation - bottom part (how_much_scatter(DOGS)^2 + how_much_scatter(CATS)^2)
		
		eq.process("C = 3./5 * C_DOGS + 2./5 * C_CATS");
		eq.process("B = inv(C) * (DMEAN - CMEAN)");
		
		DMatrixRMaj B = eq.lookupDDRM("B");
		System.out.println("Separation Line on the scattor plot = (" + ff.format(B.get(0, 0))  + ") * weight + (" +
							ff.format(B.get(1, 0)) + ") * height");
		
		// Calculating Mahalanobis distance - how well DOGS and CATS are separated from each other
		// Large positive integer indicates a small overlap between two groups, which means
		// good separation between the two classes by the linear model
		// Separation Line: Z = x * b1 + y * b2
		eq.process("DIST = B' * (DMEAN - CMEAN)");
		System.out.println("Mahalanobis Distance: " + 
							ff.format(Math.sqrt(eq.lookupDouble("DIST"))));
	
		
		// prediction: B' * ( x - (DMEAN + CMEAN) / 2 ) > log(P(DOGS) / P(CATS))
		Instances testing = generateTestData(attrs);
		
		double ratio = Math.log((3.0 / 5.0) / (2.0 / 5.0));
		
		for (Instance test : testing) {
			DMatrixRMaj mat = get(attr1, attr2, test);
			eq.alias(mat, "x");
			eq.process("K = B' * ( x - (DMEAN + CMEAN) / 2)");
			System.out.println(test + " ==> " + (eq.lookupDouble("K") > ratio ? "DOGS" : "CATS"));
		}
		
	}
	
	public static DMatrixRMaj get(Attribute attr1, Attribute attr2, Instance instance) {
		
		double x = instance.value(attr1);
		double y = instance.value(attr2);
		
		DMatrixRMaj data = new DMatrixRMaj(2, 1);
		data.set(0, 0, x);
		data.set(1, 0, y);
		
		return data;
	}
	
	public static DMatrixRMaj avg(Attribute attr1, Attribute attr2, List<Instance> instances) {
		
		double x = instances.stream().mapToDouble(p -> p.value(attr1)).average().getAsDouble();
		double y = instances.stream().mapToDouble(p -> p.value(attr2)).average().getAsDouble();
		
		DMatrixRMaj data = new DMatrixRMaj(2, 1);
		data.set(0, 0, x);
		data.set(1, 0, y);
		
		return data;
	}
	
	public static double [][] convert(Attribute attr1, Attribute attr2, List<Instance> instances) {
		
		double [][] data = new double[instances.size()][2];
		
		AtomicInteger index = new AtomicInteger();
		instances.stream().forEach(p -> {
			
			int idx = index.getAndIncrement();
			
			data[idx][0] = p.value(attr1);
			data[idx][1] = p.value(attr2);
		});
		
		return data;
	}
	
	public static List<Instance> getInstances(Attribute attr, String val, Instances instances) {
		
		return instances.stream().filter(p -> val.equals(p.stringValue(attr))).collect(Collectors.toList());
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
		data2.setValue(attrs.get(0), 20);
		data2.setValue(attrs.get(1), 15);
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
