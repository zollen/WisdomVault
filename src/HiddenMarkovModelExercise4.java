import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise4 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.########");
	
	private static DMatrixRMaj T = null;
	private static DMatrixRMaj E = null;	
	
	
	private static final String [] STATES = { "P", "A", "C", "B" };
	
	private static final String [] SEQUENCE = { "D", "L" };
	
	private static final int [] CONVERT = { 0, 1 };
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * states = ('P', 'A', 'C', 'D') observations = ('D', 'L')
		 * 
		 * start_probability = {'P': 0.25, 'A': 0.25, 'C': 0.25, 'B': 0.25 }
		 * 
		 * transition_probability = { 
		 * 				'P' : {'P': 0.49, 'A': 0.21, 'C': 0.21, 'D': 0.09}, 
		 * 				'A' : {'P': 0.21, 'A': 0.49, 'C': 0.09, 'D': 0.21},
		 * 				'C' : {'P': 0.21, 'A': 0.09, 'C': 0.49, 'D': 0.21},
		 * 				'B' : {'P': 0.09, 'A': 0.21, 'C': 0.21, 'D': 0.49}
		 * }
		 * 
		 * emission_probability = { 
		 * 				'P' : {'L': 0.1, 'D': 0.9},
		 * 				'A' : {'L': 0.3, 'D': 0.7},
		 * 				'C' : {'L': 0.8, 'D': 0.2},
		 * 				'B' : {'L': 0.9, 'D': 0.1}
		 * }
		 */	
		
		Equation eq = new Equation();	

		eq.process("T = [" +
					/*       P,    A,    C,    B     */
				/* P */	" 0.49, 0.21, 0.21, 0.09;" +
				/* A */	" 0.21, 0.49, 0.09, 0.21;" +
				/* C */ " 0.21, 0.09, 0.49, 0.21;" +
				/* B */ " 0.09, 0.21, 0.21, 0.49 " +
						"]");
		
		eq.process("E = [" +
					/*      P,    A,    C,    B    */
				/* D */	" 0.9,  0.7,  0.2,  0.1;" +
				/* L */	" 0.1,  0.3   0.8,  0.9 " +
					"]");
		
		{
			// Using Matrix multiplication for solving Forward/Backward algos
			
			eq.process("S = [0.25; 0.25; 0.25; 0.25]");
		
			eq.process("Ed = [" +
						" 0.9,   0,   0,   0;" +
						"   0, 0.7,   0,   0;" +
						"   0,   0, 0.2,   0;" +
						"   0,   0,   0, 0.1 " +
					"]");
		
			eq.process("El = [" +
						" 0.1,   0,   0,   0;" +
						"   0, 0.3,   0,   0;" +
						"   0,   0, 0.8,   0;" +
						"   0,   0,   0, 0.9 " +
					"]");
		
			eq.process("F1 = Ed * S");
			System.out.println("F1: " + eq.lookupDDRM("F1"));
			eq.process("F2 = El * T * F1");
			System.out.println("F2: " + eq.lookupDDRM("F2"));
		
			eq.process("B3 = [1; 1; 1; 1]"); 
			eq.process("B2 = T * El * B3");
			System.out.println("B3: " + eq.lookupDDRM("B3"));
			System.out.println("B2: " + eq.lookupDDRM("B2"));
		}
		
		{
			// Using my own implementations for Forward/Backward/Viterbi algos
			
			T = eq.lookupDDRM("T");
			E = eq.lookupDDRM("E");
		
			System.out.println("========== viterbi ============");
		
			Map<Integer, Double> starts = new HashMap<Integer, Double>(); 
			starts.put(0, 0.25d);
			starts.put(1, 0.25d);
			starts.put(2, 0.25d);
			starts.put(3, 0.25d);
	
			viterbi(starts, SEQUENCE);
		
			System.out.println("PD -> CL");
		
			System.out.println("========== forward ============");
			forward(starts, SEQUENCE);
		
		
			System.out.println("========== backward ============");
		
			Map<Integer, Double> ends = new HashMap<Integer, Double>(); 
			ends.put(0, 1d);
			ends.put(1, 1d);
			ends.put(2, 1d);
			ends.put(3, 1d);
		
			backward(ends, SEQUENCE);
		
			System.out.println("========== Verification =========");
			System.out.println("P(E=D,E=L) = F(PL) + F(AL) + F(CL) + F(BL) = 0.015975 + 0.042825 + 0.0742 + 0.071775 = 0.204775");
			System.out.println("P(E=D,E=L) at position #1 = F(PD) * B(PD) + F(AD) * B(AD) + F(CD) * B(CD) + F(BD) * B(BD) = 0.204775");
			System.out.println("P(E=D,E=L) at position #2 = F(PL) * B(PL) + F(AL) * B(AL) + F(CL) * B(CL) + F(BL) * B(BL) = 0.204775");
		
		
			System.out.println("=========== Posterior Probability of each state ==========");
			System.out.println("PP(PD) = F(PD) * B(PD) = 0.225 * 0.361 = 0.081225");
			System.out.println("PP(AD) = F(AD) * B(AD) = 0.175 * 0.429 = 0.075075");
			System.out.println("PP(CD) = F(CD) * B(CD) = 0.05 * 0.629 = 0.03145");
			System.out.println("PP(BD) = F(BD) * B(BD) = 0.025 * 0.681 = 0.017025");
			System.out.println("	PP(S|D,L) = PP(PD) + PP(AD) + PP(CD) + PP(BD) = 0.204775");
			System.out.println("PP(PL) = F(PL) * B(PL) = 0.015975 * 1 = 0.015975");
			System.out.println("PP(AL) = F(AL) * B(AL) = 0.042825 * 1 = 0.042825");
			System.out.println("PP(CL) = F(CL) * B(CL) = 0.0742 * 1 = 0.0742");
			System.out.println("PP(BL) = F(BL) * B(BL) = 0.071775 * 1 = 0.071775");
			System.out.println("	PP(S|D,L) = PP(PL) + PP(AL) + PP(CL) + PP(BL) = 0.204775");
		}
	}
	
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
	
	public static void backward(Map<Integer, Double> ends, String [] sequence) {
		
		Map<String, Double> probs = new LinkedHashMap<String, Double>();
		final Map<String, Double> ss = probs;
		
		ends.entrySet().stream().forEach(p -> {
			ss.put(String.valueOf(sequence.length - 1) + "#" + STATES[p.getKey()], p.getValue()); 
		}); 
		
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
