package machinelearning;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

import graphics.ImageUtils;
import linearalgebra.MatrixFeatures;

public class PCAExperiment4 {

	private static final DecimalFormat formatter = new DecimalFormat("0.00");

	public static final int WEIGHT = 1000;

	public static String[] TRAINING = { 
					"img/training/obama1.jpg", 
					"img/training/obama2.jpg", 
					"img/training/obama3.jpg",
					"img/training/obama4.jpg", 
					"img/training/obama5.jpg", 
					"img/training/obama6.jpg" 
					};

	public static String[] TEST = { 
				"img/test/obama8.jpg", 
				"img/test/obama9.jpg", 
				"img/test/donald1.jpg",
				"img/test/donald2.jpg", 
				"img/test/bill1.jpg", 
				"img/test/bill2.jpg", 
				"img/test/businessman1.jpg",
				"img/test/businessman2.jpg", 
				"img/test/nixon.jpg", 
				"img/test/nixon2.jpg", 
				"img/test/nixon3.jpg" 
				};

	public static DMatrixRMaj[] database = new DMatrixRMaj[TRAINING.length];
	public static DMatrixRMaj obamaP = new DMatrixRMaj(TRAINING.length, 1);

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		DMatrixRMaj A = null;
		double[][] target = new double[TRAINING.length][];

		for (int i = 0; i < TRAINING.length; i++) {

			double[] arr = load(TRAINING[i]);

			target[i] = arr;

			if (A == null) {
				A = new DMatrixRMaj(arr.length, TRAINING.length);
			}

			for (int j = 0; j < arr.length; j++)
				A.set(j, i, arr[j]);
		}

		SingularValueDecomposition svd = new SingularValueDecomposition(
				MatrixUtils.createRealMatrix(MatrixFeatures.array(A)));

		DMatrixRMaj U = new DMatrixRMaj(svd.getU().getData());

		CommonOps_DDRM.transpose(U);

		for (int i = 0; i < TRAINING.length; i++) {
			prepare(i, TRAINING[i]);
		}

		train(U);
		
		for (int i = 0; i < TEST.length; i++) {
			test(U, TEST[i]);
		}

		
		System.out.println("DONE");

	}

	public static void train(DMatrixRMaj basis) {

		DMatrixRMaj avg = new DMatrixRMaj(basis.numCols, 1);

		for (int row = 0; row < basis.numCols; row++) {

			double sum = 0d;
			for (int col = 0; col < TRAINING.length; col++) {

				sum += database[col].get(row, 0);
			}

			avg.set(row, 0, (double) sum / TRAINING.length);
		}

		CommonOps_DDRM.mult(basis, avg, obamaP);
	}

	public static void prepare(int i, String fileName) throws Exception {

		DMatrixRMaj d = convert(load(fileName));

		database[i] = d;
	}

	public static void test(DMatrixRMaj basis, String fileName) throws Exception {

		DMatrixRMaj d = convert(load(fileName));
		DMatrixRMaj c = new DMatrixRMaj(TRAINING.length, 1);
		CommonOps_DDRM.mult(basis, d, c);

		// using R^2 test

		double sum1 = 0d;
		double sum2 = 0d;
		for (int row = 0; row < c.numRows; row++) {

			double diff = c.get(row, 0) - obamaP.get(row, 0);
			sum1 += diff * diff;

			double avgdiff = obamaP.get(row, 0) - WEIGHT;
			sum2 += avgdiff * avgdiff;
		}

		System.out.println("[" + fileName + "] ==> r: " + formatter.format((double) 1 - sum1 / sum2));
	}

	public static double[] load(String fileName) throws Exception {
		BufferedImage image = ImageUtils.image(fileName);
		image = ImageUtils.resize(image, 200, 200);
		image = ImageUtils.grey(image);
		return ImageUtils.array(image);
	}

	public static DMatrixRMaj convert(double[] arr) {

		DMatrixRMaj mat = new DMatrixRMaj(arr.length, 1);

		for (int i = 0; i < arr.length; i++)
			mat.set(i, 0, arr[i]);

		return mat;
	}

}
