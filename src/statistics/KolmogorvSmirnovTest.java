package statistics;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

public class KolmogorvSmirnovTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KolmogorovSmirnovTest ks = new KolmogorovSmirnovTest();
		
		double [] a = new double[10];
		double [] b = new double[10];
		double [] c = new double[10];
		
		a[0] = 0d;
		a[1] = 1d;
		a[2] = 2d;
		a[3] = 3d;
		a[4] = 4d;
		a[5] = 5d;
		a[6] = 6d;
		a[7] = 7d;
		a[8] = 8d;
		a[9] = 9d;
		
		b[0] = 0d;
		b[1] = 1d;
		b[2] = 4d;
		b[3] = 9d;
		b[4] = 16d;
		b[5] = 25d;
		b[6] = 36d;
		b[7] = 49d;
		b[8] = 64d;
		b[9] = 81d;
		
		for (int i = 0; i < 10; i++)
			c[i] = b[i] - 0.9d;
		
		// Null hypothesis: both vectors fit perfectly
		
		// A small p-value (typically <= 0.05) indicates strong evidence against 
		// the null hypothesis, so you reject the null hypothesis.

		// A large p-value (> 0.05) indicates weak evidence against the null hypothesis, 
		// so you fail to reject the null hypothesis.
		
		System.out.println("P-Value: " + ks.kolmogorovSmirnovTest(a, a));
		System.out.println("P-Value: " + ks.kolmogorovSmirnovTest(a, b));
		System.out.println("P-Value: " + ks.kolmogorovSmirnovTest(c, b));
	}

}
