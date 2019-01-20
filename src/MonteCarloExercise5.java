import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.primes.Primes;

public class MonteCarloExercise5 {
	
	private static final int MILLION = 1000000;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		monteCarlo(1000);
	}
	
	public static void monteCarlo(int n) {
		
		Random rand = new Random(System.nanoTime());
		
		int withIn = 0;
		for (int i = 0; i < n; i++) {
			
			int x = 1;
			while (x <= 1) {
				x = Math.abs(rand.nextInt() * 78498) % MILLION;
			}
		
			if (checkDuplicated(x, Primes.primeFactors(x))) {
				withIn++;
			}
		}
		
		double enclosing = MILLION;
		double p = (double) withIn / n;
		double sample = MILLION * p;
		double sd = Math.sqrt(p * (1 - p) / n);
		
		System.out.println("STDDEV: " + sd);
		System.out.println("P: " + p);
		System.out.println("SAMPLE: " + sample);
	
		int lower = (int) (sample - sd * enclosing * 1.96);
		int upper = (int) (sample + sd * enclosing * 1.96);
		
		System.out.println("95% Confident Interval: " +  lower + ", " + upper);

	}

	public static boolean checkDuplicated(int num, List<Integer> list) {
		
		Set<Integer> set = new HashSet<Integer>();
		
		// String me = list.stream().map(p -> p.toString()).collect(Collectors.joining(", "));
		// System.out.println(num + " ==> " + me);
	
		return list.stream().allMatch(t -> set.add(t));
	}
	


}
