package simulations;
public class MonteCarloExercise3 {
	
	@FunctionalInterface
	interface Function<One, Two, Three, Four, Five> {
	    public Five apply(One one, Two two, Three three, Four four);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		@SuppressWarnings("unused")
		Function<Boolean, Boolean, Boolean, Boolean, Boolean> df = (a, b, c, d) -> (a && b) || (c && !d) || (!a && c);
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
		// F,  T,  T,  T   T3
		// F,  T,  T,  F   T2, T3
		// F,  T,  F,  T   F
		// F,  T,  F,  F   F
		// F,  F,  T,  T   T3
		// F,  F,  T,  F   T2, T3
		// F,  F,  F,  T   F
		// F,  F,  F,  F   F
		
		// Q: 12 elements
		//--------------
		// T,  T,  T,  T   T1
		// T,  T,  T,  F,  T1
		// T,  T,  T,  F,  T2  <-- remove from set R
		// T,  T,  F,  T   T1
		// T,  T,  F,  F   T1
		// T,  F,  T,  F   T2
		// F,  T,  T,  T   T3
		// F,  T,  T,  F   T2
		// F,  T,  T,  F   T3
		// F,  F,  T,  T   T3
		// F,  F,  T,  F   T2
		// F,  F,  T,  F   T3
		
		// R: 9 elements
		//--------------
		// T,  T,  T,  T   T1
		// T,  T,  T,  F,  T1
		// T,  T,  F,  T   T1
		// T,  T,  F,  F   T1
		// T,  F,  T,  F   T2
		// F,  T,  T,  F   T2
		// F,  F,  T,  F   T2
		// F,  T,  T,  T   T3
		// F,  F,  T,  T   T3
		
		// m = | a, b, c, d | = 4
		// k = total conditional statements linked by *or* = 3
		
		// Choose T1
		// 2^(4-2) = 4
			
		// Choose T2
		// 2^(4-2) = 4
			
		// Choose T3
		// 2^(4 - 2) = 4
		
		// total *true* cases |Q|: total(T1) + total(T2) + total(T3) = 12
		
		// P(T1) = 4 / 12 = 1/3
		// P(T2) = 4 / 12 = 1/3
		// P(T3) = 4 / 12 = 1/3
	}
	
	

}
