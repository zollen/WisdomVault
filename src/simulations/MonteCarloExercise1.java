package simulations;
import java.util.Random;
import java.util.function.DoubleFunction;

public class MonteCarloExercise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Math integral of (e^(-x), 0, 1) = 0.6321205588285577
		// Math integral of (e^(-x), 0, 2) = 0.8646647167633873
		// Math integral of (e^(-x), 0, 3) = 0.9502129316321361
		DoubleFunction<Double> df = (d) -> Math.pow(Math.E, d * -1);
		
		System.out.println("RESULT (0, 1): " + integrate(df, 0, 1));
		System.out.println("RESULT (0, 2): " + integrate(df, 0, 2));
		System.out.println("RESULT (0, 3): " + integrate(df, 0, 3));
	}
	
	public static double integrate(DoubleFunction<Double> df, double lower, double upper) {
		
		Random rand = new Random(System.nanoTime());
		
		int count = 0;
		for (int i = 0; i < 1000000; i++) {
			double x = (Math.abs(rand.nextDouble() * 157) % (upper - lower) + lower);
			double y = rand.nextDouble();
			double yy = df.apply(x);
			
			if (y <= yy)
				count++;
		}
		
		return (double) count / 1000000 * (upper - lower);
		
	}

}
