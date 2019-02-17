import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise1 {

	private static final DecimalFormat ff = new DecimalFormat("0.######");
	
	private static final String [] STATES = { "0", "1" };
	
	private static final String[] SEQUENCE = { "A", "C", "T", "G" };
	
	private static final int [] CONVERT = { 0, 1, 2, 3 };

	private static DMatrixRMaj T = null;
	private static DMatrixRMaj E = null;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * states = ('0', '1') observations = ('A', 'C', 'T', 'G')
		 * 
		 * start_probability = {
		 * 			'0': 0.5, 
		 * 			'1': 0.5
		 * }
		 * 
		 * transition_probability = { 
		 * 			'0' : {'0': 0.75, '1': 0.25}, 
		 * 			'1' : {'0': 0.25, '1': 0.75}
		 * }
		 * 
		 * emission_probability = { 
		 * 			'0' : {'A': 0.45, 'C': 0.05, 'T': 0.05, 'G': 0.45},
		 * 			'1' : {'A': 0.05, 'C': 0.45, 'T': 0.45, 'G': 0.05}
		 * }
		 */

		Equation eq = new Equation();
		eq.process("T = [ " +
						/* 0,     1 */
			/* 0 */ "   0.75,  0.25;" +
			/* 1 */ "   0.25,  0.75 " +
						"]");

		eq.process("E = [" +
					/* 0,     1 */
		/* A */ "   0.45,  0.05;" +
		/* C */ "   0.05,  0.45;" +
		/* T */ "   0.05,  0.45;" +
		/* G */ "   0.45,  0.05 " + 
					"]");
		
		eq.process("S = [0.5; 0.5]");
		
		eq.process("Ea = [" +
					" 0.45,    0;" +
					"    0, 0.05 " +
					"]");
		
		eq.process("Ec = [" +
					" 0.05,    0;" +
					"    0, 0.45 " +
					"]");
		
		eq.process("Et = [" +
					" 0.05,    0;" +
					"    0, 0.45 " +
					"]");
		
		eq.process("Eg = [" +
					" 0.45,    0;" +
					"    0, 0.05 " +
					"]");

		T = eq.lookupDDRM("T");
		E = eq.lookupDDRM("E");
	

		{
			System.out.println("===================Forward===================");
			
			Map<Integer, Double> starts = new LinkedHashMap<Integer, Double>();
			starts.put(0, 0.5d);
			starts.put(1, 0.5d);

			forward(starts, SEQUENCE);
			
			eq.process("F1 = Ea * S");
			eq.process("F2 = Ec * T * F1");
			eq.process("F3 = Et * T * F2");
			eq.process("F4 = Eg * T * F3");
			
			System.out.print("F1: ");
			DMatrixRMaj F1 = eq.lookupDDRM("F1");
			F1.print("%2.3f");
			System.out.print("F2: ");
			DMatrixRMaj F2 = eq.lookupDDRM("F2");
			F2.print("%2.5f");
			System.out.print("F3: ");
			DMatrixRMaj F3 = eq.lookupDDRM("F3");
			F3.print("%2.6f");
			System.out.print("F4: ");
			DMatrixRMaj F4 = eq.lookupDDRM("F4");
			F4.print("%2.6f");
			
		}
		
		{
			System.out.println("==================Viterbi====================");


			Map<Integer, Double> starts = new LinkedHashMap<Integer, Double>();
			starts.put(0, 0.5d);
			starts.put(1, 0.5d);
			
		
			viterbi(starts, SEQUENCE);
			System.out.println("A0 -> C1 -> T1 -> G0");
			
			eq.process("V1 = Ea * S");
			eq.process("V2 = Ec * T * max(V1) * [ 1; 0 ]"); // [1; 0] <- state 0 has larger value, pick state 0
			eq.process("V3 = Et * T * max(V2) * [ 0; 1 ]"); // [0; 1] <- state 1 has larger value, pick state 1
			eq.process("V4 = Eg * T * max(V3) * [ 0; 1 ]"); // [0; 1] <- state 1 has larger value, pick state 1
					
			System.out.print("V1: ");
			DMatrixRMaj V1 = eq.lookupDDRM("V1");
			V1.print("%2.3f");
			System.out.print("V2: ");
			DMatrixRMaj V2 = eq.lookupDDRM("V2");
			V2.print("%2.6f");
			System.out.print("V3: ");
			DMatrixRMaj V3 = eq.lookupDDRM("V3");
			V3.print("%2.6f");
			System.out.print("V4: ");
			DMatrixRMaj V4 = eq.lookupDDRM("V4");
			V4.print("%2.6f");
		}
		
		{
			System.out.println("===================Backward===================");
			
			Map<Integer, Double> ends = new LinkedHashMap<Integer, Double>();
			ends.put(0, 1d);
			ends.put(1, 1d);
		
			backward(ends, SEQUENCE);
			
			eq.process("B4 = [1; 1]");
			eq.process("B3 = T' * Eg * B4");
			eq.process("B2 = T' * Et * B3");
			eq.process("B1 = T' * Ec * B2");
			
			System.out.print("B4: ");
			DMatrixRMaj B4 = eq.lookupDDRM("B4");
			B4.print("%2.0f");
			System.out.print("B3: ");
			DMatrixRMaj B3 = eq.lookupDDRM("B3");
			B3.print("%2.2f");
			System.out.print("B2: ");
			DMatrixRMaj B2 = eq.lookupDDRM("B2");
			B2.print("%2.3f");
			System.out.print("B1: ");
			DMatrixRMaj B1 = eq.lookupDDRM("B1");
			B1.print("%2.6f");
		}
		
		System.out.println();
		System.out.println("Posterior Probability Of Position #2");
		System.out.println("PP(0) = F(C0) * B(C0) = 0.00875 * 0.03 = " + ff.format(0.00875 * 0.03));
		System.out.println("PP(1) = F(C1) * B(C1) = 0.03375 * 0.055 = " + ff.format(0.03375 * 0.055));
		System.out.println("PP(#2) = PP(0) + PP(1) = " + ff.format(new Double(0.002118)));
		System.out.println("Posterior Probability Of Position #3");
		System.out.println("PP(#3) = F(T0) * B(T0) + F(T1) * B(T1) = " + ff.format(new Double(0.002118)));
		System.out.println("Verifying the Probability with Forward(A,C,T,G)");
		System.out.println("PP(ACTG) = F(G0) + F(G1) = " + ff.format(0.002118));
	}
	
	@SuppressWarnings("deprecation")
	public static void forward(Map<Integer, Double> starts, String [] sequence) {

		Map<String, Double> probs = new LinkedHashMap<String, Double>();
		final Map<String, Double> ss = probs;
		
		starts.entrySet().stream().forEach(p -> { 
				ss.put("0" + "#" + String.valueOf(p.getKey()), p.getValue() * E.get(CONVERT[0], p.getKey())); 
		});
		
		Set<Integer> tos = new LinkedHashSet<Integer>(starts.keySet());
		
		for (int step = 0 + 1; step < sequence.length; step++) {
			
			Set<Integer> nexts = new LinkedHashSet<Integer>();

			for (int to : tos) {
				
				{
					double sum = 0d;
					for (int from = 0; from < T.numCols; from++) {
					
						if (T.get(to, from) > 0 && E.get(CONVERT[step], to) > 0) {
					
							double last = 0d;
							if (probs.get(String.valueOf(step - 1) + "#" + String.valueOf(from)) != null)
								last = probs.get(String.valueOf(step - 1) + "#" + String.valueOf(from));
			
							sum += (double) last * T.get(to, from) * E.get(CONVERT[step], to);
						}
					}
					
					probs.put(String.valueOf(step) + "#" + String.valueOf(to), sum);
				}
				
				
				{
					int from = to;
					for (int _to = 0; _to < T.numRows; _to++) {
						if (T.get(_to, from) > 0)
							nexts.add(_to);
					}
				}
			}
			
			tos = nexts;
		}
		
		probs.entrySet().stream().forEach( p -> System.out.println(
				p.getKey() + sequence[new Integer(p.getKey().substring(0, 1))] +
				" ===> " + 
				ff.format(p.getValue())));
	}
	
	@SuppressWarnings("deprecation")
	public static void backward(Map<Integer, Double> ends, String [] sequence) {
		
		Map<String, Double> probs = new LinkedHashMap<String, Double>();
		final Map<String, Double> ss = probs;
		
		ends.entrySet().stream().forEach(p -> ss.put(String.valueOf(sequence.length - 1) + "#" + String.valueOf(p.getKey()), p.getValue())); 
		
		Set<Integer> froms = new LinkedHashSet<Integer>(ends.keySet());
		
		for (int step = sequence.length - 2; step >= 0; step--) {
			
			Set<Integer> nexts = new LinkedHashSet<Integer>();
			
			for (int from : froms) {
				
				{
					double sum = 0d;				
					for (int to = 0; to < T.numRows; to++) {
				
						if (T.get(to, from) > 0 && E.get(CONVERT[step], to) > 0) {
						
							double last = 0d;
							if (probs.get(String.valueOf((step + 1) + "#" + String.valueOf(to))) != null)
								last = probs.get(String.valueOf((step + 1) + "#" + String.valueOf(to)));
						
							sum += (double) last * T.get(to, from) * E.get(CONVERT[step + 1], to);
						}
					}

					probs.put(String.valueOf(step) + "#" + String.valueOf(from), sum);
				}
				
				{
					for (int _to = 0; _to < T.numRows; _to++) {
						if (T.get(_to, from) > 0)
							nexts.add(_to);
					}	
				}
			}
				
			froms = nexts;
		}
		
		probs.entrySet().stream().forEach( p -> System.out.println(
				p.getKey() +
				sequence[new Integer(p.getKey().substring(0,  1))] + 
				" ===> " + 
				ff.format(p.getValue())));
	}
	
	public static void viterbi(Map<Integer, Double> starts, String [] sequence) {
		
		Map<String, Double> probs = new LinkedHashMap<String, Double>();
		final Map<String, Double> ss = probs;
		
		starts.entrySet().stream().forEach(
				p -> { 
					ss.put("0" + "#" + STATES[p.getKey()] + sequence[0], p.getValue() * E.get(CONVERT[0], p.getKey()));
				}
		);
		
		Set<Integer> froms = new LinkedHashSet<Integer>(starts.keySet());
		Set<Integer> nexts = new LinkedHashSet<Integer>();
		
		for (int step = 1; step < sequence.length; step++) {
			
			for (int from : froms) {
				
				for (int to = 0; to < T.numRows; to++) {
					
					if (T.get(to, from) > 0 && E.get(CONVERT[step], to) > 0) {
						
						double left = 0.0d;
						double right = 0.0d;
						if (ss.get((step - 1) + "#" + STATES[from] + sequence[step - 1]) != null) {
							left = ss.get((step - 1) + "#" + STATES[from] + sequence[step - 1]);
						}
						
						if (ss.get(step + "#" + STATES[to] + sequence[step]) != null) {
							right = ss.get(step + "#" + STATES[to] + sequence[step]);
						}
						
						if (left > 0) {							
							left = left * T.get(to, from) * E.get(CONVERT[step], to);
							
							if (left > right)
								ss.put(step + "#" + STATES[to] + sequence[step], left);
						}	
						
						nexts.add(to);
					}
				}
			}
			
			froms = new LinkedHashSet<Integer>(nexts);
			nexts.clear();
		}
		
		
		ss.entrySet().stream().forEach(p -> System.out.println(p.getKey() + " ==> " + ff.format(p.getValue())));
	}
}
