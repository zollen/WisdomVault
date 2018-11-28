import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise1 {

	private static final DecimalFormat ff = new DecimalFormat("0.######");
	
	private static final String[] STATES = { "A", "C", "G", "T" };

	private static final int STATE_0 = 0;
	private static final int STATE_1 = 1;
	private static final int STATE_A = 0;
	private static final int STATE_C = 1;
	private static final int STATE_T = 2;
	private static final int STATE_G = 3;

	private static DMatrixRMaj T = null;
	private static DMatrixRMaj E = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * states = ('0', '1') observations = ('A', 'C', 'T', 'G')
		 * 
		 * start_probability = {'0': 0.5, '1': 0.5}
		 * 
		 * transition_probability = { '0' : {'0': 0.75, '1': 0.25}, '1' : {'0': 0.25,
		 * '1': 0.75}, }
		 * 
		 * emission_probability = { '0' : {'A': 0.45, 'C': 0.05, 'T': 0.05, 'G': 0.45},
		 * '1' : {'A': 0.05, 'C': 0.45, 'T': 0.45, 'G': 0.05}, }
		 */

		Equation eq = new Equation();
		eq.process("T = [ " +
						/* 0, 1 */
			/* 0 */ "   0.75,  0.25;" +
			/* 1 */ "   0.25,  0.75 " +
						"]");

		eq.process("E = [" +
					/* 0, 1 */
		/* A */ "   0.45,  0.05;" +
		/* C */ "   0.05,  0.45;" +
		/* T */ "   0.05,  0.45;" +
		/* G */ "   0.45,  0.05 " + 
					"]");

		T = eq.lookupDDRM("T");
		E = eq.lookupDDRM("E");

		// A0: 0.225,    A1: 0.025
		// C0: 0.00875,  C1: 0.03375
		// T0: 0.00075,  T1: 0.012375
		// G0: 0.001645, G1: 0.000473

		Map<Integer, Double> starts = new HashMap<Integer, Double>();
		starts.put(0, 0.5d);
		starts.put(1, 0.5d);

		forwarding(starts);
	}

	public static void forwarding(Map<Integer, Double> starts) {

		Map<Integer, Double> states = new HashMap<Integer, Double>();
		final Map<Integer, Double> ss = states;
		
		starts.entrySet().stream().forEach(p -> { 
				ss.put(p.getKey(), p.getValue() * E.get(0, p.getKey())); 
		});
		
		ss.entrySet().stream().forEach(p -> System.out.println(STATES[0] + p.getKey() + " ===> " + p.getValue()));
		
		
		for (int gene = 0 + 1; gene < E.numRows; gene++) {

			Map<Integer, Double> nexts = new HashMap<Integer, Double>();
			Set<Integer> tos = states.keySet();
		
			for (Integer to : tos) {
				
				double sum = 0d;
				
				for (int from = 0; from < T.numCols; from++) {
					
					if (T.get(to, from) > 0 && E.get(gene, to) > 0) {
					
						double last = 0d;
						if (states.get(from) != null)
							last = states.get(from);
					
						sum += (double) last * T.get(to, from) * E.get(gene, to);
					}
				}
				
				System.out.println(STATES[gene] + to + " ===> " + ff.format(sum));
				nexts.put(to, sum);
			}
			
			states = nexts;
		}
	}

}
