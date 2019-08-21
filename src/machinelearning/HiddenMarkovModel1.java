package machinelearning;
import java.text.DecimalFormat;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

public class HiddenMarkovModel1 {

	private static final DecimalFormat ff = new DecimalFormat("0.######");
	
	private static final String [] STATES = { "0", "1" };
	
	private static final String[] SEQUENCE = { "A", "C", "T", "G" };
	
	private static final int [] CONVERT = { 0, 1, 2, 3 };

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


			Viterbi1 v1 = new Viterbi1();
			System.out.println("Wiki Proposed Viterbi Algo: " + display(v1.fit(CONVERT, S, T, E)));
								
			Viterbi2 v2 = new Viterbi2();
			System.out.println("Bayes Calculation Viterbi : " + display(v2.fit(CONVERT, S, T, E)));
			
			
			eq.process("V1 = Ea * S");
			eq.process("V2 = [1, 0] * T * Ec * max(V1)");
			eq.process("V3 = [0, 1] * T * Et * max(V2)");
			eq.process("V4 = [0, 1] * T * Eg * max(V3)");
			
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
		System.out.println("PP(#2) = PP(0) + PP(1) = " + ff.format(0.002118));
		System.out.println("Posterior Probability Of Position #3");
		System.out.println("PP(#3) = F(T0) * B(T0) + F(T1) * B(T1) = " + ff.format(0.002118));
		System.out.println("Verifying the Probability with Forward(A,C,T,G)");
		System.out.println("PP(ACTG) = F(G0) + F(G1) = " + ff.format(0.002118));
	}
	
	private static String display(List<Pair<Integer, Double>> list) {
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < SEQUENCE.length; i++) {
			
			Pair<Integer, Double> pair = list.get(i);
			
			if (i > 0)
				builder.append(", ");
			
			builder.append(SEQUENCE[i] + "{" + STATES[pair.getFirst()] + "}: " + ff.format(pair.getSecond()));
			
		}
		
		return builder.toString();
	}
}
