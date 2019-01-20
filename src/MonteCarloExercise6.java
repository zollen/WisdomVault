import java.util.Random;

public class MonteCarloExercise6 {
	
	private static final int LEN = 200;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			
		monteCarlo(1000);
	}
	
	public static void monteCarlo(int n) {
		
		Random rand = new Random(System.nanoTime());
		
		int withIn = 0;
		for (int i = 0; i < n; i++) {
			
			StringBuilder builder = new StringBuilder();
			for (int j = 0; j < LEN; j++) {
				int x = Math.abs(rand.nextInt() * 3) % 10;
				builder.append(('0' + x));
			}
			
			System.out.println(builder.toString());
			
			if (check(builder.toString())) {
				withIn++;
			}
		}
		
		double enclosing = LEN;
		double p = (double) withIn / n;
		double sample = LEN * p;
		double sd = Math.sqrt(p * (1 - p) / n);
		
		System.out.println("STDDEV: " + sd);
		System.out.println("P: " + p);
		System.out.println("SAMPLE: " + sample);
	
		int lower = (int) (sample - sd * enclosing * 1.96);
		int upper = (int) (sample + sd * enclosing * 1.96);
		
		System.out.println("95% Confident Interval: " +  lower + ", " + upper);

	}

	public static boolean check(String str) {
		
		for (int i = 0; i < str.length() - 4; i++) {
			
			if (str.charAt(i) < str.charAt(i + 1) &&
					str.charAt(i + 1) < str.charAt(i + 2) &&
					str.charAt(i + 2) < str.charAt(i + 3) &&
					str.charAt(i + 3) < str.charAt(i + 4)) {
				return true;
			}
		}
		
		return false;
	}

}
