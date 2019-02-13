import java.text.DecimalFormat;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.ejml.data.DMatrixRMaj;

public class MLEExercise1 {
	
	private static DecimalFormat formatter = new DecimalFormat("0.0000000");
	
	private static final int SIZE = 10;
	private static final double MEAN = 0d;
	private static final double SD = 1d;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DMatrixRMaj A = new DMatrixRMaj(SIZE, 1);
		NormalDistribution dist = new NormalDistribution(new JDKRandomGenerator(), MEAN, SD);
		
		for (int i = 0; i < A.numRows; i++) {
			A.set(i, 0, Math.abs(dist.sample()));
		}
		
		// The MLE of mean and standard of Gaussian Distribution
		// df/du = Exp(A)
		// df/ds = Std(A)
		
		System.out.println(A);
		
		
		double total = 1d;
		for (int i = 0; i < A.numRows; i++) {
			total *= Math.pow(Math.E, -1 * ((A.get(i, 0) - MEAN) * (A.get(i, 0) - MEAN)) / (2 * SD * SD)) / (Math.sqrt(2 * Math.PI) * SD);
		}
		
		System.out.println("P(D): " + formatter.format(total));
		
		
		double mean = 0d;
		for (int i = 0; i < A.numRows; i++) {
			mean += A.get(i, 0);
		}
		
		mean /= A.numRows;	
		System.out.println("Exp(D): Mean = " + formatter.format(mean));
		
		total = 1d;
		
		for (int i = 0; i < A.numRows; i++) {
			total *= Math.pow(Math.E, -1 * ((A.get(i, 0) - mean) * (A.get(i, 0) - mean)) / (2 * SD * SD)) / (Math.sqrt(2 * Math.PI) * SD);
		}
		
		System.out.println("P(D with mean of " + formatter.format(mean) + "): " + formatter.format(total));
	}

}
