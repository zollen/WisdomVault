import java.util.Random;
import java.util.function.DoubleFunction;

public class MonteCarloExercise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Math integral of (e^(-x), 0, 1) = 0.6321205588285577
		DoubleFunction<Double> df = (d) -> Math.pow(Math.E, d * -1);
		
		System.out.println("RESULT: " + integrate(df, 0, 1));
	}
	
	public static double integrate(DoubleFunction<Double> df, double lower, double upper) {
		
		Random rand = new Random(System.nanoTime());
		
		int count = 0;
		for (int i = 0; i < 1000000; i++) {
			double x = Math.abs(rand.nextDouble() * 157) % (upper - lower);
			double y = rand.nextDouble();
			double yy = df.apply(x);
			
			if (y <= yy)
				count++;
		}
		
		return (double) count / 1000000 * (upper - lower);
		
	}

}
