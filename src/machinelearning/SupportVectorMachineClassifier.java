package machinelearning;
import java.util.ArrayList;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.NormalizedPolyKernel;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class SupportVectorMachineClassifier {
	
	public static final Coord2D [] TRAINING = {
			new Coord2D(1, 1, "Y"),
			new Coord2D(-1, 0, "Y"),
			new Coord2D(-1, -1, "Y"),
			new Coord2D(2, 1, "Y"),
			new Coord2D(-2, -2, "Y"),
			new Coord2D(3, -2, "Y"),
			new Coord2D(10, 12, "N"),
			new Coord2D(15, 5, "N"),
			new Coord2D(13, 7, "N"),
			new Coord2D(15, 0, "N"),
			new Coord2D(15, 1, "N"),
			new Coord2D(16, -3, "N"),
			new Coord2D(12, -8, "N"),
			new Coord2D(14, -7, "N"),
			new Coord2D(10, -10, "N"),
			new Coord2D(7, -12, "N"),
			new Coord2D(3, -15, "N"),
			new Coord2D(-2, -17, "N"),
			new Coord2D(-5, -16, "N"),
			new Coord2D(-7, -12, "N"),
			new Coord2D(-10, -8, "N"),
			new Coord2D(-13, -5, "N"),
			new Coord2D(-16, -2, "N"),
			new Coord2D(-18, 0, "N"),
			new Coord2D(-15, 3, "N"),
			new Coord2D(-12, 8, "N"),
			new Coord2D(-8, 10, "N"),
			new Coord2D(-4, 12, "N"),
			new Coord2D(2, 15, "N"),
			new Coord2D(6, 14, "N")		
	}; 
	
	public static final Coord2D [] TESTING = {
			new Coord2D(3, 3, "Y"),
			new Coord2D(-2, -3, "Y"),
			new Coord2D(2, -10, "N"),
			new Coord2D(-3, 10, "N")
	};

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		Instances training = generateTraining(TRAINING);
		Instances test = generateTest(TESTING);
		

		Classifier smo = new SMO();		
		
		
		((SMO)smo).setC(1.0);
		((SMO)smo).setEpsilon(1.0E-12);
		NormalizedPolyKernel kernel = new NormalizedPolyKernel();
		kernel.setCacheSize(999999);
		kernel.setExponent(20);
		((SMO)smo).setKernel(kernel);


		System.out.println("C: " + ((SMO)smo).getC());
		System.out.println("Epsilon: " + ((SMO)smo).getEpsilon());
		System.out.println("Capabilities: " + ((SMO)smo).getCapabilities());
		System.out.println("Calibrator: " + ((SMO)smo).getCalibrator());
		System.out.println("Kernel: " + ((SMO)smo).getKernel());
		
		
		smo.buildClassifier(training);
		
		System.out.println(((SMO)smo).globalInfo());
		
		Evaluation evaluation = new Evaluation(training);
		evaluation.evaluateModel(smo, test);
		System.out.println(evaluation.toSummaryString());
		
	
		String [] desc = { "Y", "N" };
		for (int i = 0; i < TESTING.length; i++) {		
				int result = (int) smo.classifyInstance(test.get(i));
				System.out.println(TESTING[i] + " Actual: " + desc[result]);
		}
	}
	
	public static Instances generateTest(Coord2D [] samples) {
		
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute x = new Attribute("x");
		Attribute y = new Attribute("y");
		
		ArrayList<String> classVal = new ArrayList<String>();
		classVal.add("Y");
		classVal.add("N");
		
		Attribute cls = new Attribute("class", classVal);
		
		attrs.add(x);
		attrs.add(y);
		attrs.add(cls);
		
		Instances test = new Instances("TEST", attrs, samples.length);
		test.setClassIndex(test.numAttributes() - 1);
		
		for (int i = 0; i < samples.length; i++) {
				
			Instance data = new DenseInstance(3);
			data.setValue(x, samples[i].getX());
			data.setValue(y, samples[i].getY());
			data.setValue(cls, samples[i].getClassify());
			
			test.add(data);
		}
		
		return test;
	}
	
	public static Instances generateTraining(Coord2D [] samples) {
		
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute x = new Attribute("x");
		Attribute y = new Attribute("y");
	
		attrs.add(x);
		attrs.add(y);
			
		ArrayList<String> classVal = new ArrayList<String>();
		classVal.add("Y");
		classVal.add("N");
		
		Attribute cls = new Attribute("class", classVal);
			
		attrs.add(cls);
			
		Instances training = new Instances("TRAINING", attrs, samples.length);	
		training.setClassIndex(training.numAttributes() - 1);
		
		for (int i = 0; i < samples.length; i++) {
			
			Instance data = new DenseInstance(3);
			data.setValue(x, samples[i].getX());
			data.setValue(y, samples[i].getY());
			data.setValue(cls, samples[i].getClassify());
			training.add(data);
		}
		
		return training;
	}
	
	public static class Coord2D extends Vector2D {
		
		private static final long serialVersionUID = 1L;
		
		public String classify = null;
		
		public Coord2D(double x, double y) {
			super(x, y);
		}
		
		public Coord2D(double x, double y, String classify) {
			super(x, y);
			this.classify = classify;
		}
		
		public String getClassify() {
			return classify;
		}

		public void setClassify(String classify) {
			this.classify = classify;
		}
		
		@Override
		public String toString() {
			
			return "{" + this.getX() + ", " + this.getY() + "} Expected: " + classify;
		}
	}
}
