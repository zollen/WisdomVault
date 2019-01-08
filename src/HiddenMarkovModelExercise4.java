import java.text.DecimalFormat;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise4 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.########");
	
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
			eq.process("F2 = El * T * F1");
			DMatrixRMaj F1 = eq.lookupDDRM("F1");
			DMatrixRMaj F2 = eq.lookupDDRM("F2");
			
			System.out.println(String.format("Forward  D(P): %-8s  D(A): %-8s  D(C): %-8s  D(B): %-8s", 
					ff.format(F1.get(0, 0)),
					ff.format(F1.get(1, 0)),
					ff.format(F1.get(2, 0)),
					ff.format(F1.get(3, 0))));
			
			System.out.println(String.format("Forward  L(P): %-8s  L(A): %-8s  L(C): %-8s  L(B): %-8s", 
					ff.format(F2.get(0, 0)),
					ff.format(F2.get(1, 0)),
					ff.format(F2.get(2, 0)),
					ff.format(F2.get(3, 0))));
		
			eq.process("B3 = [1; 1; 1; 1]"); 
			eq.process("B2 = T' * El * B3");
			DMatrixRMaj B3 = eq.lookupDDRM("B3");
			DMatrixRMaj B2 = eq.lookupDDRM("B2");
			
			System.out.println(String.format("Backward D(P): %-8s  D(A): %-8s  D(C): %-8s  D(B): %-8s", 
					ff.format(B3.get(0, 0)),
					ff.format(B3.get(1, 0)),
					ff.format(B3.get(2, 0)),
					ff.format(B3.get(3, 0))));
			
			System.out.println(String.format("Backward L(P): %-8s  L(A): %-8s  L(C): %-8s  L(B): %-8s", 
					ff.format(B2.get(0, 0)),
					ff.format(B2.get(1, 0)),
					ff.format(B2.get(2, 0)),
					ff.format(B2.get(3, 0))));
			
			eq.process("V1 = Ed * S");
			eq.process("V2 = El * T * max(V1) * [ 1; 0; 0; 0 ]"); // [1; 0; 0; 0] <- state P has larger value, pick state P
			
			DMatrixRMaj V1 = eq.lookupDDRM("V1");
			DMatrixRMaj V2 = eq.lookupDDRM("V2");
			
			System.out.println(String.format("Viterbi  D(P): %-8s  D(A): %-8s  D(C): %-8s  D(B): %-8s", 
					ff.format(V1.get(0, 0)),
					ff.format(V1.get(1, 0)),
					ff.format(V1.get(2, 0)),
					ff.format(V1.get(3, 0))));
			
			System.out.println(String.format("Viterbi  L(P): %-8s  L(A): %-8s  L(C): %-8s  L(B): %-8s", 
					ff.format(V2.get(0, 0)),
					ff.format(V2.get(1, 0)),
					ff.format(V2.get(2, 0)),
					ff.format(V2.get(3, 0))));
			
		
		}
		
		{
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
}