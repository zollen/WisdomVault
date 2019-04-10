package machinelearning.classifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class LDAClassifier3 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final String VALUE_FLOWER_SETOSA = "Iris-setosa";
	private static final String VALUE_QUALITY_VERCOLOR = "Iris-versicolor";
	private static final String VALUE_QUALITY_VIGRINICA = "Iris-virginica";
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		// LDA Algorithm
		// 1. Compute the mean vector for each class: Mc = (Σ xi) / ni
		// 2. Compute the total mean vector M = (Σ xi) / N
		// 3. Compute the within-class scatter Sw = Σ Σ (xj - Mc)(xj - Mc)'
		// 4. Compute the between-class scatter Sb = Σ (Mc - M)(Mc - M)'
		// 5. Eigen Decomposition [V, D] = eig(inv(Sw) * Sb)
		// 6. Get the projection matrix P is composed of the top-p eigenvectors corresponding to the largest eigenvalues
		
		Instances training = generateTrainingData("data/iris.arff.txt");
		List<Attribute> attrs = get(training);
		Attribute cls = training.classAttribute();
		
		List<Instance> setosaList = getInstances(cls, VALUE_FLOWER_SETOSA, training);
		List<Instance> vercolorList = getInstances(cls, VALUE_QUALITY_VERCOLOR, training);
		List<Instance> virginicaList = getInstances(cls, VALUE_QUALITY_VIGRINICA, training);
		
		System.out.println(setosaList.size());
		System.out.println(vercolorList.size());
		System.out.println(virginicaList.size());
		
		
	}
	
	public static DMatrixRMaj discriminant(DMatrixRMaj mean, DMatrixRMaj coVar, DMatrixRMaj P, DMatrixRMaj x) {
		
		DMatrixRMaj tmp1 = new DMatrixRMaj(1, coVar.numCols);
		DMatrixRMaj tmp2 = new DMatrixRMaj(1, 1);
		DMatrixRMaj tmp3 = new DMatrixRMaj(1, 1);
		DMatrixRMaj result = P.copy();
		
		CommonOps_DDRM.multTransA(mean, coVar, tmp1);
		CommonOps_DDRM.mult(tmp1, x, tmp2);
		
		double first = tmp2.get(0, 0);
		
		CommonOps_DDRM.multTransA(-0.5, mean, coVar, tmp1);
		CommonOps_DDRM.mult(tmp1, mean, tmp3);
		
		double second = tmp3.get(0, 0);
		
		double last = first + second;
		
		CommonOps_DDRM.elementExp(P, result);
		CommonOps_DDRM.add(result, last);
		
		return result;
	}
	
	public static DMatrixRMaj meanError(DMatrixRMaj A, DMatrixRMaj mean) {
		
		DMatrixRMaj E = new DMatrixRMaj(A.numRows, A.numCols);
		
		for (int col = 0; col < mean.numRows; col++) {
			
			for (int row = 0; row < A.numRows; row++) {
				
				E.set(row, col, A.get(row, col) - mean.get(col, 0));
			}
		}
		
		return E;
	}
	
	public static DMatrixRMaj covariance(int total, List<DMatrixRMaj> list) {
		
		DMatrixRMaj C = new DMatrixRMaj(list.get(0).numRows, list.get(0).numCols);
		CommonOps_DDRM.fill(C, 0.0);
		
		for (DMatrixRMaj A : list) {
			
			DMatrixRMaj S = new DMatrixRMaj(A.numRows, A.numCols);
			CommonOps_DDRM.scale((double) A.numRows / total, A, S);
			CommonOps_DDRM.addEquals(C, S);
		}
		
		return C;
	}
	
	public static DMatrixRMaj covariance(DMatrixRMaj A) {
		
		DMatrixRMaj C = new DMatrixRMaj(A.numCols, A.numCols);
		
		CommonOps_DDRM.multInner(A, C);
		CommonOps_DDRM.scale((double) 1 / A.numRows, C);

		return C;
	}
	
	public static DMatrixRMaj get(List<Attribute> attrs, Instance instance) {
		
		List<Double> vals = new ArrayList<Double>();
		for (Attribute attr : attrs) {
			vals.add(instance.value(attr));
		}
		
		DMatrixRMaj data = new DMatrixRMaj(attrs.size(), 1);
		for (int i = 0; i < attrs.size(); i++)
			data.set(i, 0, vals.get(i));
		
		return data;
	}
	
	public static DMatrixRMaj avg(List<Attribute> attrs, List<Instance> instances) {
		
		List<Double> vals = new ArrayList<Double>();
		for (Attribute attr : attrs) {
			vals.add(instances.stream().mapToDouble(p -> p.value(attr)).average().getAsDouble());
		}
		
		DMatrixRMaj data = new DMatrixRMaj(attrs.size(), 1);
		for (int i = 0; i < attrs.size(); i++)
			data.set(i, 0, vals.get(i));
		
		return data;
	}
	
	public static double [][] convert(List<Attribute> attrs, List<Instance> instances) {
		
		double [][] data = new double[instances.size()][attrs.size()];
		
		AtomicInteger index = new AtomicInteger();

		instances.stream().forEach(p -> {
			
			AtomicInteger col = new AtomicInteger();
			
			int idx = index.getAndIncrement();
			
			for (Attribute attr : attrs)
				data[idx][col.getAndIncrement()] = p.value(attr);
		});
		
		return data;
	}
	
	public static List<Instance> getInstances(Attribute attr, String val, Instances instances) {
		
		return instances.stream().filter(p -> val.equals(p.stringValue(attr))).collect(Collectors.toList());
	}
	
	public static Instances generateTrainingData(String fileName) {

		Instances training = null;
		
		try {
			BufferedReader reader =
				   new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			training = arff.getData();
			training.setClassIndex(training.numAttributes() - 1);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return training;
	}
	
	public static List<Attribute> get(Instances instances) {
		
		List<Attribute> attrs = new ArrayList<Attribute>();
		
		Enumeration<Attribute> list = instances.enumerateAttributes();
		Attribute cls = instances.classAttribute();
		
		while (list != null && list.hasMoreElements()) {
			attrs.add(list.nextElement());
		}
			
		return attrs;
	}
}
