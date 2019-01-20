import java.util.Random;

public class MonteCarloExercise4 {
	
	@FunctionalInterface
	interface Function<X, Y, A> {
	    public A apply(X x, Y y);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Actual Area: 3.63
		Function<Double, Double, Boolean> df = (x, y) -> x * x + x * y + y * y <= 1;
		
		monteCarlo(df, 1000, -2, 2, -2, 2);
	}
	
	public static void monteCarlo(Function<Double, Double, Boolean> df, int n, int x1, int x2, int y1, int y2) {
		
		Random rand = new Random(System.nanoTime());
		
		int withIn = 0;
		for (int i = 0; i < n; i++) {
			double x = Math.abs(rand.nextDouble() * 157) % x2; 
			double y = Math.abs(rand.nextDouble() * 157) % y2;
			
			if (rand.nextBoolean())
				x *= -1;
			if (rand.nextBoolean())
				y *= -1;
			
		
			if (df.apply(x, y)) {
				withIn++;
			}
		}
		
		double p = (double) withIn / n;
		
		double enclosing = (x2 - x1) * (y2 - y1);
		double area = enclosing* p;
		double sd = Math.sqrt(p * (1 - p) / n);
		
		System.out.println(p);
		System.out.println(area);
		
		double lower = area - sd * enclosing * 1.96;
		double upper = area + sd * enclosing * 1.96;
		
		System.out.println("95% Confident Interval: " +  lower + ", " + upper);

	}	
	


}
