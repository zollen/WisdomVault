package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.ejml.data.Complex_F64;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class LDAClassifier3 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final String VALUE_QUALITY_PASSED = "Y";
	private static final String VALUE_QUALITY_FAILED = "N";
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ArrayList<String> clsVals = new ArrayList<String>();
		clsVals.add(VALUE_QUALITY_PASSED);
		clsVals.add(VALUE_QUALITY_FAILED);
		

		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		Attribute attr1 = new Attribute("curvature", 1);
		Attribute attr2 = new Attribute("diameter", 2);
		Attribute attr3 = new Attribute("quality", clsVals, 3);
		attrs.add(attr1);
		attrs.add(attr2);
		attrs.add(attr3);
		
		
		// LDA Algorithm
		// 1. Compute the mean vector for each class: Mc = (Σ xi) / ni
		// 2. Compute the total mean vector M = (Σ xi) / N
		// 3. Compute the within-class scatter Sw = Σ Σ (xj - Mc)(xj - Mc)'
		// 4. Compute the between-class scatter Sb = Σ (Mc - M)(Mc - M)'
		// 5. Eigen Decomposition [V, D] = eig(inv(Sw) * Sb)
		// 6. Get the projection matrix P is composed of the top-p eigenvectors corresponding to the largest eigenvalues
		
		Instances training = generateTrainingData(attrs);

		List<Instance> passedList = getInstances(attr3, VALUE_QUALITY_PASSED, training);
		List<Instance> failedList = getInstances(attr3, VALUE_QUALITY_FAILED, training);
		
		DMatrixRMaj all = new DMatrixRMaj(convert(attr1, attr2, training));
		DMatrixRMaj passed = new DMatrixRMaj(convert(attr1, attr2, passedList));
		DMatrixRMaj failed = new DMatrixRMaj(convert(attr1, attr2, failedList));
		
		// *** Step 1 ***
		DMatrixRMaj passedMean = avg(attr1, attr2, passedList);
		DMatrixRMaj failedMean = avg(attr1, attr2, failedList);
		
		// *** Step 2 ***
		DMatrixRMaj globalMean = avg(attr1, attr2, training);
		
		// *** Step 3 ***
		// Matrix_passed - [ x - avg_x_passed, y - avg_y_passed ]
		DMatrixRMaj passed2 = meanError(passed, passedMean);
		// Matrix_failed - [ x - avg_x_failed, y - avg_y_failed ]
		DMatrixRMaj failed2 = meanError(failed, failedMean);
		
		// CoVar(passed) = (Matrix_passed * Matrix_passed') / number of passed
		DMatrixRMaj passedWithinClasses = covariance(passed2);	
		// CoVar(failed) = (Matrix_failed * Matrix_failed') / number of failed
		DMatrixRMaj failedWithinClasses = covariance(failed2);
		
		List<DMatrixRMaj> covars1 = new ArrayList<DMatrixRMaj>();
		covars1.add(passedWithinClasses);
		covars1.add(failedWithinClasses);
		
		// CoVar(withinClasses) = CoVar(passed) * 4 / 7 + CoVar(failed) * 3 / 7
		DMatrixRMaj covarw = covariance(training.size(), covars1);
		
		// inv(CoVar(withinClasses))
		DMatrixRMaj _covarw = new DMatrixRMaj(covarw.numRows, covarw.numCols);
		CommonOps_DDRM.invert(covarw, _covarw);
		
		// *** Step 4 ***
		// Matrix_passed - [ avg_passed, avg_global ]
		DMatrixRMaj passed3 = new DMatrixRMaj(2, 1);
		CommonOps_DDRM.subtract(passedMean, globalMean, passed3);
		DMatrixRMaj failed3 = new DMatrixRMaj(2, 1);
		CommonOps_DDRM.subtract(failedMean, globalMean, failed3);
		
		DMatrixRMaj betweenClasses = new DMatrixRMaj(2, 2);
		betweenClasses.set(0, 0, passed3.get(0, 0));
		betweenClasses.set(1, 0, passed3.get(1, 0));
		betweenClasses.set(0, 1, failed3.get(0, 0));
		betweenClasses.set(0, 1, failed3.get(1, 0));
		
		// CoVar(betweenClasses) = (Matrix_passed * Matrix_passed') / number of passed
		DMatrixRMaj covarb = covariance(betweenClasses);		
		
		// S = variance between classes / variance within classes
		DMatrixRMaj S = new DMatrixRMaj(covarb.numRows, covarb.numCols);
		
		CommonOps_DDRM.mult(_covarw, covarb, S);
		
		// *** Step 5 ***
		SimpleMatrix mat = new SimpleMatrix(S);		
		SimpleEVD<SimpleMatrix> eigen = mat.eig();
		
		
		List<Complex_F64> list = eigen.getEigenvalues();
		System.out.println("EigenValues(inv(Sw) * Sb) = {" + ff.format(list.get(0).real) + ", " + ff.format(list.get(1).real) + "}");
		
		// *** Step 6 ***
		DMatrixRMaj proj = new DMatrixRMaj(2, 2);
		proj.set(0, 0, eigen.getEigenVector(0).get(0, 0));
		proj.set(1, 0, eigen.getEigenVector(0).get(1, 0));
		proj.set(0, 1, eigen.getEigenVector(1).get(0, 0));
		proj.set(1, 1, eigen.getEigenVector(1).get(1, 0));
		
		DMatrixRMaj projected = new DMatrixRMaj(all.numCols, all.numRows);
		CommonOps_DDRM.multTransB(proj, all, projected);
		
		System.out.println("Projected training data");
		System.out.println(projected);
		
		// TEST data (x)
		System.out.println("Testing [2.81, 5.46]...");
		DMatrixRMaj test = new DMatrixRMaj(2, 1);
		test.set(0, 0, 2.81);
		test.set(1, 0, 5.46);
		
		DMatrixRMaj res = new DMatrixRMaj(2, 1);
		CommonOps_DDRM.mult(proj, test, res);
		
		System.out.println("Projected test data");
		System.out.println(res);
		
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
	
	public static Instances generateTrainingData(ArrayList<Attribute> attrs) {

		Instances training = new Instances("TRAINING", attrs, 5);
		
		Instance data1 = new DenseInstance(3);	
		data1.setValue(attrs.get(0), 2.95);
		data1.setValue(attrs.get(1), 6.63);
		data1.setValue(attrs.get(2), VALUE_QUALITY_PASSED);
		training.add(data1);
		
		Instance data2 = new DenseInstance(3);	
		data2.setValue(attrs.get(0), 2.53);
		data2.setValue(attrs.get(1), 7.79);
		data2.setValue(attrs.get(2), VALUE_QUALITY_PASSED);
		training.add(data2);
		
		Instance data3 = new DenseInstance(3);	
		data3.setValue(attrs.get(0), 3.57);
		data3.setValue(attrs.get(1), 5.65);
		data3.setValue(attrs.get(2), VALUE_QUALITY_PASSED);
		training.add(data3);
		
		Instance data4 = new DenseInstance(3);	
		data4.setValue(attrs.get(0), 3.16);
		data4.setValue(attrs.get(1), 5.47);
		data4.setValue(attrs.get(2), VALUE_QUALITY_PASSED);
		training.add(data4);
		
		Instance data5 = new DenseInstance(3);	
		data5.setValue(attrs.get(0), 2.58);
		data5.setValue(attrs.get(1), 4.46);
		data5.setValue(attrs.get(2), VALUE_QUALITY_FAILED);
		training.add(data5);
		
		Instance data6 = new DenseInstance(3);	
		data6.setValue(attrs.get(0), 2.16);
		data6.setValue(attrs.get(1), 6.22);
		data6.setValue(attrs.get(2), VALUE_QUALITY_FAILED);
		training.add(data6);
		
		Instance data7 = new DenseInstance(3);	
		data7.setValue(attrs.get(0), 3.27);
		data7.setValue(attrs.get(1), 3.52);
		data7.setValue(attrs.get(2), VALUE_QUALITY_FAILED);
		training.add(data7);
		
		training.setClassIndex(training.numAttributes() - 1);

		return training;
	}
}
