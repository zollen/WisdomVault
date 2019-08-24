package machinelearning.hmm;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

public class HiddenMarkovModel1 {

	private static final DecimalFormat ff = new DecimalFormat("0.######");
	
	private static final String [] STATES = { "0", "1" };
	
	private static final String[] SEQUENCE = { "A", "C", "T", "G" };
	
	private static final int [] CONVERTER = { 0, 1, 2, 3 };

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
				  /*  A,    C,    T,    G */
		/* 0 */  " 0.45, 0.05, 0.05, 0.45;" +
		/* 1 */  " 0.05, 0.45, 0.45, 0.05 " +
					"]");
		
		eq.process("S = [0.5; 0.5]");
		
		eq.process("Ea = diag(E(0:,0))");
		eq.process("Ec = diag(E(0:,1))");
		eq.process("Et = diag(E(0:,2))");
		eq.process("Eg = diag(E(0:,3))");
		
		

		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		DMatrixRMaj S = eq.lookupDDRM("S");
		

		{
			System.out.println("===================Forward===================");
			
			Forward forward = new Forward();
			System.out.println(display1(forward.fit(CONVERTER, S, T, E)));			
		}
		
		{
			System.out.println("==================Viterbi====================");


			Viterbi v1 = new Viterbi(Viterbi.Algorithm.WIKI_PROPOSED_ALGO);
			System.out.println("Wiki Proposed Viterbi Algo: " + display2(v1.fit(CONVERTER, S, T, E)));
								
			Viterbi v2 = new Viterbi(Viterbi.Algorithm.BAYES_RULES_ALGO);
			System.out.println("Bayes Calculation Viterbi : " + display2(v2.fit(CONVERTER, S, T, E)));
		}
		
		{
			System.out.println("===================Backward===================");
			
			Backward backward = new Backward();
			System.out.println(display1(backward.fit(CONVERTER, S, T, E)));
		}
		
		System.out.println();
		System.out.println("Posterior Probability Of Position #2");
		System.out.println("PP(0) = F(C0) * B(C0) = 0.00875 * 0.03 = " + ff.format(0.00875 * 0.03));
		System.out.println("PP(1) = F(C1) * B(C1) = 0.03375 * 0.055 = " + ff.format(0.03375 * 0.055));
		System.out.println("PP(#2) = PP(0) + PP(1) = " + ff.format(0.002118));
		System.out.println("Posterior Probability Of Position #3");
		System.out.println("PP(#3) = F(T0) * B(T0) + F(T1) * B(T1) = " + ff.format(0.002118));
		System.out.println("Verifying the Probability with Forward(A,C,T,G)");
		System.out.println("PP(ACTG) = F(G0) + F(G1) = " + ff.format(0.002118));
	}
	
	private static String display2(List<Pair<Integer, Double>> list) {
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < SEQUENCE.length; i++) {
			
			Pair<Integer, Double> pair = list.get(i);
			
			if (i > 0)
				builder.append(", ");
			
			builder.append(SEQUENCE[i] + "{" + STATES[pair.getFirst()] + "}: " + ff.format(pair.getSecond()));
			
		}
		
		return builder.toString();
	}
	
	private static String display1(List<Pair<Integer, DMatrixRMaj>> list) {
		
		return list.stream().map(p -> 
			"{" + SEQUENCE[CONVERTER[p.getFirst()]] + "}: " + "[" +
			ff.format(p.getSecond().get(0, 0)) + ", " +
			ff.format(p.getSecond().get(1, 0)) + "]"
				).collect(Collectors.joining(", "));
	}
}
