package machinelearning;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise2 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.########");
	
	private static DMatrixRMaj T = null;
	private static DMatrixRMaj E = null;	
	
	
	private static final String [] STATES = { "1", "2" };
	
	private static final String [] SEQUENCE = { "b", "a", "d" };
	
	private static final int [] CONVERT = { 1, 0, 3 };
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * states = ('1', '2') observations = ('a', 'b', 'c', 'd')
		 * 
		 * start_probability = {'1': 0.6, '2': 0.4}
		 * 
		 * transition_probability = { 
		 * 				'1' : {'1': 0.5, '2': 0.5}, 
		 * 				'2' : {'1': 0.3, '2': 0.7}
		 * }
		 * 
		 * emission_probability = { 
		 * 				'1' : {'a': 0.4, 'b': 0.3, 'c': 0.2, 'd': 0.1},
		 * 				'2' : {'a': 0.3, 'b': 0.2, 'c': 0.3, 'd': 0.2}
		 * }
		 */	
		
		Equation eq = new Equation();		 
		eq.process("T = [" +
					/*      1,   2     */
				/* 1 */	" 0.5, 0.3;" +
				/* 2 */	" 0.5, 0.7 " +
						"]");
		
		eq.process("E = [" +
					/*      1,    2    */
				/* a */	" 0.4,  0.3;" +
				/* b */	" 0.3,  0.2;" +
				/* c */	" 0.2,  0.3;" +
				/* d */	" 0.1,  0.2 " +	
					"]");
		
		eq.process("S = [ 0.6; 0.4 ]");
	
		eq.process("Ea = [" +
				"0.4,   0;" +
				"  0, 0.3 " +
			"]");

		eq.process("Eb = [" +
				"0.3,   0;" +
				"  0, 0.2 " +
			"]");	

		eq.process("Ed = [" +
				" 0.1,   0;" +
				"   0, 0.2 " +
			"]");

		T = eq.lookupDDRM("T");
		E = eq.lookupDDRM("E");
		
		System.out.println("========== viterbi ============");
		
		Map<Integer, Double> starts = new HashMap<Integer, Double>(); 
		starts.put(0, 0.6d);
		starts.put(1, 0.4d);
	
		viterbi(starts, SEQUENCE);
		
		System.out.println("1b -> 1a -> 2d");
		
		eq.process("V1 = Eb * S");
		eq.process("V2 = Ea * T * max(V1) * [ 1; 0 ]"); // [1; 0] <- state 0 has larger value, pick state 0
		eq.process("V3 = Ed * T * max(V2) * [ 1; 0 ]"); // [1; 0] <- state 0 has larger value, pick state 0
			
		System.out.print("V1: ");
		DMatrixRMaj V1 = eq.lookupDDRM("V1");
		V1.print("%2.2f");
		System.out.print("V2: ");
		DMatrixRMaj V2 = eq.lookupDDRM("V2");
		V2.print("%2.3f");
		DMatrixRMaj V3 = eq.lookupDDRM("V3");
		System.out.print("V3: ");
		V3.print("%2.5f");
		
		System.out.println("1b -> 1a -> 2d");
		
		System.out.println("========== forward ============");
		forward(starts, SEQUENCE);
		
		eq.process("F1 = Eb * S");
		eq.process("F2 = Ea * T * F1");
		eq.process("F3 = Ed * T * F2");
		System.out.print("F1: ");
		DMatrixRMaj F1 = eq.lookupDDRM("F1");
		F1.print("%2.2f");
		System.out.print("F2: ");
		DMatrixRMaj F2 = eq.lookupDDRM("F2");
		F2.print("%2.4f");
		System.out.print("F3: ");
		DMatrixRMaj F3 = eq.lookupDDRM("F3");
		F3.print("%2.6f");
		
		
		System.out.println("========== backward ============");
		
		Map<Integer, Double> ends = new HashMap<Integer, Double>(); 
		ends.put(0, 1d);
		ends.put(1, 1d);
		backward(ends, SEQUENCE);
		
		eq.process("B3 = [1; 1]");
		eq.process("B2 = T' * Ed * B3");
		eq.process("B1 = T' * Ea * B2");
		
		System.out.print("B3: ");
		DMatrixRMaj B3 = eq.lookupDDRM("B3");
		B3.print("%2.0f");
		System.out.print("B2: ");
		DMatrixRMaj B2 = eq.lookupDDRM("B2");
		B2.print("%2.2f");
		System.out.print("B1: ");
		DMatrixRMaj B1 = eq.lookupDDRM("B1");
		B1.print("%2.4f");
		
		
		System.out.println("========== Verification =========");
		System.out.println("P(b,a,d) = F(d1) + F(d2) = 0.003594 + 0.010692 = 0.014286");
		System.out.println("P(b,a,d) at position #1 = F(b1) * B(b1) + F(b2) * B(b2) = 0.18 * 0.0555 + 0.08 * 0.0537 = 0.014286");
		System.out.println("P(b,a,d) at position #2 = F(a1) * B(a1) + F(a2) * B(a2) = 0.0456 * 0.15 + 0.0438 * 0.17 = 0.014286");
		System.out.println("P(b,a,d) at position #3 = F(d1) * B(d1) + F(d2) * B(d2) = 0.003594 * 1 + 0.010692 * 1 = 0.014286");
		System.out.println("=========== Posterior Probability of each state ==========");
		System.out.println("PP(b1) = F(b1) * B(b1) = 0.18 * 0.0555 = 0.010008");
		System.out.println("PP(b2) = F(b2) * B(b2) = 0.08 * 0.0537 = 0.004296");
		System.out.println("PP(a1) = F(a1) * B(a1) = 0.0456 * 0.15 = 0.00684");
		System.out.println("PP(a2) = F(a2) * B(a2) = 0.0438 * 0.17 = 0.007446");
		System.out.println("PP(d1) = F(d1) * B(d1) = 0.003594 * 1 = 0.003594");
		System.out.println("PP(d2) = F(d2) * B(d2) = 0.010692 * 1 = 0.010692");
		
	}
	
	@SuppressWarnings("deprecation")
	public static void forward(Map<Integer, Double> starts, String [] sequence) {

		Map<String, Double> probs = new LinkedHashMap<String, Double>();
		final Map<String, Double> ss = probs;
		
		starts.entrySet().stream().forEach(p -> { 
				ss.put("0" + "#" + STATES[p.getKey()], p.getValue() * E.get(CONVERT[0], p.getKey())); 
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
							if (probs.get(String.valueOf(step - 1) + "#" + STATES[from]) != null)
								last = probs.get(String.valueOf(step - 1) + "#" + STATES[from]);
			
							sum += (double) last * T.get(to, from) * E.get(CONVERT[step], to);
						}
					}
					
					probs.put(String.valueOf(step) + "#" + STATES[to], sum);
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
		
		ends.entrySet().stream().forEach(p -> ss.put(String.valueOf(sequence.length - 1) + "#" + STATES[p.getKey()], p.getValue())); 
		
		Set<Integer> froms = new LinkedHashSet<Integer>(ends.keySet());
		
		for (int step = sequence.length - 2; step >= 0; step--) {
			
			Set<Integer> nexts = new LinkedHashSet<Integer>();
			
			for (int from : froms) {
				
				{
					double sum = 0d;				
					for (int to = 0; to < T.numRows; to++) {
				
						if (T.get(to, from) > 0 && E.get(CONVERT[step], to) > 0) {
						
							double last = 0d;
							if (probs.get(String.valueOf((step + 1) + "#" + STATES[to])) != null)
								last = probs.get(String.valueOf((step + 1) + "#" + STATES[to]));
						
							sum += (double) last * T.get(to, from) * E.get(CONVERT[step + 1], to);
						}
					}

					probs.put(String.valueOf(step) + "#" + STATES[from], sum);
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
