import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise2 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.######");
	
	private static DMatrixRMaj T = null;
	private static DMatrixRMaj E = null;	
	
	
	private static final String [] STATES = { "1", "2" };
	
	private static final String [] SEQUENCE = { "b", "a", "d" };
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * states = ('1', '2') observations = ('a', 'b', 'c', 'd')
		 * 
		 * start_probability = {'1': 0.6, '2': 0.4}
		 * 
		 * transition_probability = { 
		 * 				'1' : {'start': 0.6, '1': 0.5, '2': 0.5}, 
		 * 				'2' : {'start': 0.4, '1': 0.3, '2': 0.7}
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
		
		Map<Integer, Double> starts = new HashMap<Integer, Double>(); 
		starts.put(0, 0.6d);
		starts.put(1, 0.4d);
	
		viterbi(starts, SEQUENCE);
		
		System.out.println("1b -> 1a -> 2d");
	}
	
	public static void viterbi(Map<Integer, Double> starts, String [] sequence) {
		
		Map<String, Double> probs = new LinkedHashMap<String, Double>();
		final Map<String, Double> ss = probs;
		
		starts.entrySet().stream().forEach(
				p -> { 
					ss.put("0" + "#" + STATES[p.getKey()] + sequence[0], p.getValue() * E.get(0, p.getKey()));
				}
		);
		
		Set<Integer> froms = new LinkedHashSet<Integer>(starts.keySet());
		Set<Integer> nexts = new LinkedHashSet<Integer>();
		
		for (int step = 1; step < sequence.length; step++) {
			
			for (int from : froms) {
				
				for (int to = 0; to < T.numRows; to++) {
					
					if (T.get(to, from) > 0 && E.get(step, to) > 0) {
						
						double left = 0.0d;
						double right = 0.0d;
						if (ss.get((step - 1) + "#" + STATES[from] + sequence[step - 1]) != null) {
							left = ss.get((step - 1) + "#" + STATES[from] + sequence[step - 1]);
						}
						
						if (ss.get(step + "#" + STATES[to] + sequence[step]) != null) {
							right = ss.get(step + "#" + STATES[to] + sequence[step]);
						}
						
						if (left > 0) {							
							left = left * T.get(to, from) * E.get(step, to);
							
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
