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
	
		
		T = eq.lookupDDRM("T");
		E = eq.lookupDDRM("E");
		
		System.out.println("========== viterbi ============");
		
		Map<Integer, Double> starts = new HashMap<Integer, Double>(); 
		starts.put(0, 0.6d);
		starts.put(1, 0.4d);
	
		viterbi(starts, SEQUENCE);
		
		System.out.println("1b -> 1a -> 2d");
		
		
		System.out.println("========== forward ============");
		forward(starts, SEQUENCE);
		
		
		System.out.println("========== backward ============");
		
		Map<Integer, Double> ends = new HashMap<Integer, Double>(); 
		ends.put(0, 1d);
		ends.put(1, 1d);
		backward(ends, SEQUENCE);
	}
	
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
							if (probs.get(String.valueOf((step + 1) + "#" + String.valueOf(from))) != null)
								last = probs.get(String.valueOf((step + 1) + "#" + String.valueOf(from)));
						
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
