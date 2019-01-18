public class MonteCarloExercise2 {
	
	@FunctionalInterface
	interface Function<One, Two, Three, Four, Five> {
	    public Five apply(One one, Two two, Three three, Four four);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Function<Boolean, Boolean, Boolean, Boolean, Boolean> df = (a, b, c, d) -> (a && b) || (c && !d);
		// A,  B,  C,  D   RESULT
		// ----------------------
		// T,  T,  T,  T   T1
		// T,  T,  T,  F   T1, T2
		// T,  T,  F,  T   T1
		// T,  T,  F,  F   T1
		// T,  F,  T,  T   F
		// T,  F,  T,  F   T2
		// T,  F,  F,  T   F
		// T,  F,  F,  F   F
		// F,  T,  T,  T   F
		// F,  T,  T,  F   T2
		// F,  T,  F,  T   F
		// F,  T,  F,  F   F
		// F,  F,  T,  T   F
		// F,  F,  T,  F   T2
		// F,  F,  F,  T   F
		// F,  F,  F,  F   F
		
		// Q: 8 elements
		//--------------
		// T,  T,  T,  T   T1
		// T,  T,  T,  F,  T1
		// T,  T,  T,  F,  T2  <-- remove from set R
		// T,  T,  F,  T   T1
		// T,  T,  F,  F   T1
		// T,  F,  T,  F   T2
		// F,  T,  T,  F   T2
		// F,  F,  T,  F   T2
		
		// R: 7 elements
		//--------------
		// T,  T,  T,  T   T1
		// T,  T,  T,  F,  T1
		// T,  T,  F,  T   T1
		// T,  T,  F,  F   T1
		// T,  F,  T,  F   T2
		// F,  T,  T,  F   T2
		// F,  F,  T,  F   T2
		
		// Choose T2
		// 2^(4-2) = 4
		// P(T2) = 4 / |Q| = 4 / 8 = 0.5
	}
	
	

}
