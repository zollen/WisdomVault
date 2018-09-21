import java.text.DecimalFormat;
import java.util.Random;

import org.ejml.data.DMatrixRMaj;
import org.ejml.simple.SimpleMatrix;

public class TextRecogExercise2 {

	private static final DecimalFormat formatter = new DecimalFormat("0.000");

	private static final int FRIEND = 0;
	private static final int HELLO = 1;
	private static final int REWARD = 2;
	private static final int MONEY = 3;
	private static final int CONCLUSION = 4;

	private static final int ONE = 1;
	private static final int ZERO = 0;

	public static void main(String[] args) {

		Random rand = new Random(System.nanoTime());
		SimpleMatrix AA = SimpleMatrix.random_DDRM(9000, 5, 0, 1, rand);
		DMatrixRMaj A = AA.getDDRM();

		for (int i = 0; i < A.numRows; i++) {
			for (int j = 0; j < A.numCols; j++) {
				A.set(i, j, A.get(i, j) > 0.5 ? 1 : 0);
			}
		}

		DMatrixRMaj classifier = learnClassifier(A);
		System.out.println(classifier);

		SimpleMatrix ins = SimpleMatrix.random_DDRM(50, 4, 0, 1, rand);
		DMatrixRMaj inputs = ins.getDDRM();
		for (int i = 0; i < inputs.numRows; i++) {
			for (int j = 0; j < inputs.numCols; j++) {
				inputs.set(i, j, inputs.get(i, j) > 0.7 ? 1 : 0);
			}
		}
		
		applyClassifier(classifier, inputs);
	}

	public static DMatrixRMaj learnClassifier(DMatrixRMaj A) {

		DMatrixRMaj classifier = new DMatrixRMaj(2, 5);

		// Freq(Personal, friend) = #T(Personal, friend) / #T(friend)
		classifier.set(0, 0, (double) countPersonal(FRIEND, A) / countOne(FRIEND, A));
		// Freq(Personal, hello) = #T(Personal, hello) / #T(hello)
		classifier.set(0, 1, (double) countPersonal(HELLO, A) / countOne(HELLO, A));
		// Freq(Personal, reward) = #T(Personal, reward) / #T(reward)
		classifier.set(0, 2, (double) countPersonal(REWARD, A) / countOne(REWARD, A));
		// Freq(Personal, money) = #T(Personal, money) / #T(money)
		classifier.set(0, 3, (double) countPersonal(MONEY, A) / countOne(MONEY, A));
		// Freq(Personal) = #T(Personal) / #T
		classifier.set(0, 4, (double) countOne(CONCLUSION, A) / A.numRows);

		// Freq(Spam, friend) = #T(Spam, friend) / #T(friend)
		classifier.set(1, 0, (double) countSpam(FRIEND, A) / countOne(FRIEND, A));
		// Freq(Spam, hello) = #T(Spam, hello) / #T(hello)
		classifier.set(1, 1, (double) countSpam(HELLO, A) / countOne(HELLO, A));
		// Freq(Spam, reward) = #T(Spam, reward) / #T(reward)
		classifier.set(1, 2, (double) countSpam(REWARD, A) / countOne(REWARD, A));
		// Freq(Spam, money) = #T(Spam, money) / #T(money)
		classifier.set(1, 3, (double) countSpam(MONEY, A) / countOne(MONEY, A));
		// Freq(Spam) = #T(Spam) / #T
		classifier.set(1, 4, (double) countZero(CONCLUSION, A) / A.numRows);

		return classifier;
	}
	
	public static void applyClassifier(DMatrixRMaj classifier, DMatrixRMaj inputs) {
		
		for (int sample = 0; sample < inputs.numRows; sample++) {

			System.out.println("[" + (int) inputs.get(sample, 0) + ", " + (int) inputs.get(sample, 1) + ", "
					+ (int) inputs.get(sample, 2) + ", " + (int) inputs.get(sample, 3) + "]");

			for (int type = 0; type < classifier.numRows; type++) {

				double val = Math.log1p(classifier.get(type, 4));

				for (int attr = 0; attr < inputs.numCols; attr++) {
					if (inputs.get(sample, attr) == ONE)
						val += Math.log1p(classifier.get(type, attr));
				}

				System.out.println("Index: [" + type + "] ===> [" + formatter.format(val) + "]");
			}

			System.out.println();
		}	
	}

	public static int countOne(int col, DMatrixRMaj A) {
		return count(col, ONE, A);
	}

	public static int countZero(int col, DMatrixRMaj A) {
		return count(col, ZERO, A);
	}

	public static int countPersonal(int col, DMatrixRMaj A) {
		return count(CONCLUSION, ONE, col, ONE, A);
	}

	public static int countSpam(int col, DMatrixRMaj A) {
		return count(CONCLUSION, ZERO, col, ONE, A);
	}

	public static int count(int col, int val, DMatrixRMaj A) {

		int count = 0;
		for (int i = 0; i < A.numRows; i++)
			if (A.get(i, col) == val)
				count++;

		return count;
	}

	public static int count(int col1, int val1, int col2, int val2, DMatrixRMaj A) {

		int count = 0;

		for (int i = 0; i < A.numRows; i++)
			if (A.get(i, col1) == val1 && A.get(i, col2) == val2)
				count++;

		return count;
	}

}
