package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class LDAClassifier2 {
	
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
		
		
		// with Linear Discriminant Analysis (LDA):
		// ( mean(PASSED) - mean(FAILED) )^2 / ( how_much_scatter(PASSED)^2 + how_much_scatter(FAILED)^2 )
		// We want to maximize the mean(PASSED) - mean(FAILED), but
		// minimize the variation - bottom part (how_much_scatter(PASSED)^2 + how_much_scatter(FAILED)^2)
		
		
		Instances training = generateTrainingData(attrs);

		List<Instance> passedList = getInstances(attr3, VALUE_QUALITY_PASSED, training);
		List<Instance> failedList = getInstances(attr3, VALUE_QUALITY_FAILED, training);
		
		DMatrixRMaj passed = new DMatrixRMaj(convert(attr1, attr2, passedList));
		DMatrixRMaj failed = new DMatrixRMaj(convert(attr1, attr2, failedList));
		
		DMatrixRMaj globalMean = avg(attr1, attr2, training);
		DMatrixRMaj passedMean = avg(attr1, attr2, passedList);
		DMatrixRMaj failedMean = avg(attr1, attr2, failedList);
		
		// Matrix_passed - [ avg_x, avg_y ]
		DMatrixRMaj passed2 = meanError(passed, globalMean);
		// Matrix_failed - [ avg_x, avg_y ]
		DMatrixRMaj failed2 = meanError(failed, globalMean);
				
		// CoVar(passed) = (Matrix_passed * Matrix_passed') / number of passed
		DMatrixRMaj passedCoVar = covariance(passed2);	
		// CoVar(failed) = (Matrix_failed * Matrix_failed') / number of failed
		DMatrixRMaj failedCoVar = covariance(failed2);
		
		List<DMatrixRMaj> covars = new ArrayList<DMatrixRMaj>();
		covars.add(passedCoVar);
		covars.add(failedCoVar);
		
		// CoVar(pool) = CoVar(passed) * 4 / 7 + CoVar(failed) * 3 / 7
		DMatrixRMaj covar = covariance(training.size(), covars);
		
		// inv(CoVar(pool))
		DMatrixRMaj _covar = new DMatrixRMaj(covar.numRows, covar.numCols);
		CommonOps_DDRM.invert(covar, _covar);
		
		
		// Prior probability vector.
		// If prior probabilty is not available, then we assume all events are independent and use the regular probability
		// P = [ 4 / 7, 3 / 7 ]
		DMatrixRMaj P = new DMatrixRMaj(2, 1);
		P.set(0, 0, (double) passedList.size() / training.size());
		P.set(1, 0, (double) failedList.size() / training.size());
		
		
		System.out.println("============= PASSED ==============");
		discriminantAll(passedMean, _covar, P, passed);
		
		System.out.println("============= FAILED ==============");
		discriminantAll(failedMean, _covar, P, failed);
		
		// Model Coefficients(B) = inv(C) * (mean1 - mean2)
		DMatrixRMaj diffMean = new DMatrixRMaj(2, 1);
		DMatrixRMaj B = new DMatrixRMaj(2, 1);
		CommonOps_DDRM.subtract(passedMean, failedMean, diffMean);
		CommonOps_DDRM.mult(_covar, diffMean, B);
		
		System.out.println("Model Coefficients: " + B);
		
		// The scoring function
		System.out.println("Z = (" + ff.format(B.get(0, 0)) + ") * x + (" + ff.format(B.get(1, 0)) + ") * y");
		
		// For Accessing the effectiveness of the separation of the two classes
		// Use Mahalanobis distance = B' * (mean1 - mean2)
		DMatrixRMaj dist = new DMatrixRMaj(1, 1);
		CommonOps_DDRM.multTransA(B, diffMean, dist);
		System.out.println("Mahalanobis: " + ff.format(Math.sqrt(dist.get(0, 0))));
		
		
		// TEST data (x)
		System.out.println("Testing [2.81, 5.46]...");
		DMatrixRMaj test = new DMatrixRMaj(2, 1);
		test.set(0, 0, 2.81);
		test.set(1, 0, 5.46);
		
		// B' * (x - (mean1 + mean2) / 2)       > log(P(c1)/P(c2))
		// B' * x  -  B' * (mean1 + mean2) / 2  > log(P(c1)/P(c2))
		// Proj_B(x) - Proj_B(avg(means))       > log(P(c1)/P(c2))
		double prob = Math.log((4.0 / 7.0) / (3.0 / 7.0));
		
		DMatrixRMaj res = new DMatrixRMaj(2, 1);
		CommonOps_DDRM.add(passedMean, failedMean, diffMean);
		CommonOps_DDRM.scale(0.5, diffMean);
		CommonOps_DDRM.subtract(test, diffMean, res);
		CommonOps_DDRM.multTransA(B, res, dist);
		System.out.println("ln(P(c1)/P(c2)): " + ff.format(prob));
		System.out.println("Projecting(x) onto the max separating direction: " + ff.format(dist.get(0, 0)));
		System.out.println(ff.format(dist.get(0, 0)) + " > " + ff.format(prob) + " is not true. Therefore [2.81, 5.46] does not passed. It belongs to c2");
	}
	
	public static void discriminantAll(DMatrixRMaj mean, DMatrixRMaj coVar, DMatrixRMaj P, DMatrixRMaj data) {
		
		
		for (int row = 0; row < data.numRows; row++) {
			
			DMatrixRMaj x = new DMatrixRMaj(data.numCols, 1);
			StringBuilder builder = new StringBuilder();
			builder.append("[");
			
			for (int col = 0; col < data.numCols; col++) {
				
				if (builder.length() > 1)
					builder.append(", ");
		
				builder.append(ff.format(data.get(row, col)));
				
				x.set(col, 0, data.get(row, col));
			}
			builder.append("] ==> [");
			
			DMatrixRMaj nn = discriminant(mean, coVar, P, x);
			
			boolean flag = false;
			for (int rr = 0; rr < nn.numRows; rr++) {
				if (flag)
					builder.append(", ");
				builder.append(ff.format(nn.get(rr, 0)));
				flag = true;
			}
			
			builder.append("]");
			
			System.out.println(builder.toString());	
		}
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
