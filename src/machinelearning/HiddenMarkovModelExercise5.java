package machinelearning;
import java.text.DecimalFormat;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise5 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.########");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/**
		 * states = ('A', 'V', 'N', 'D' ) observations = ('C', 'T')
		 * 
		 * start_probability = {'A': 0.4, 'V': 0.4, 'N': 0.2}
		 * 
		 * transition_probability = { 
		 * 				'A' : {'A': 0.3, 'V': 0.0, 'N': 0.5, 'D': 0.2}, 
		 * 				'V' : {'A': 0.3, 'V': 0.0, 'N': 0.4, 'D': 0.3},
		 * 				'N' : {'A': 0.0, 'V': 0.3, 'N': 0.5, 'D': 0.2},
		 * 				'D' : {'A': 0.0, 'V': 0.0, 'N': 0.0, 'D': 0.0}
		 * }
		 * 
		 * emission_probability = { 
		 * 				'A' : {'C': 0.2, 'T': 0.0},
		 * 				'V' : {'C': 0.0, 'T': 0.4},
		 * 				'N' : {'C': 0.2, 'T': 0.3},
		 * 				'D' : {'C': 0.0, 'T': 0.0}
		 * }
		 */	
		
		Equation eq = new Equation();	

		eq.process("T = [" +
					/*      A,   V,   N,   D     */
				/* A */	" 0.3, 0.3, 0.0, 0.0;" +
				/* V */	" 0.0, 0.0, 0.3, 0.0;" +
				/* N */ " 0.5, 0.4, 0.4, 0.0;" +
				/* D */ " 0.2, 0.3, 0.2, 0.0 " +
						"]");
		
		
		eq.process("Ec = [" +			
						" 0.2, 0.0, 0.0, 0.0;" +
						" 0.0, 0.0, 0.0, 0.0;" +
						" 0.0, 0.0, 0.2, 0.0;" +
						" 0.0  0.0, 0.0, 0.0 " +		
					"]");
		
		eq.process("Et = [" +
						" 0.0, 0.0, 0.0, 0.0;" +
						" 0.0, 0.4, 0.0, 0.0;" +
						" 0.0, 0.0, 0.3, 0.0;" +
						" 0.0, 0.0, 0.0, 0.0 " +
				"]");
		
		eq.process("S = [0.4; 0.4; 0.2; 0.0]"); 
		{			
			System.out.println("========== forward ============");
			eq.process("F1 = Ec * S");
			eq.process("F2 = Et * T * F1");
			
			DMatrixRMaj F1 = eq.lookupDDRM("F1");
			System.out.println(String.format("C(A): %-6s  C(V): %-6s  C(N): %-6s", 
							ff.format(F1.get(0, 0)), 
							ff.format(F1.get(1, 0)), 
							ff.format(F1.get(2, 0))));
			
			DMatrixRMaj F2 = eq.lookupDDRM("F2");
			System.out.println(String.format("T(A): %-6s  T(V): %-6s  T(N): %-6s", 
							ff.format(F2.get(0, 0)), 
							ff.format(F2.get(1, 0)), 
							ff.format(F2.get(2, 0))));
			
			System.out.println("========== backward ============");
			
			eq.process("B2 = [1; 1; 1; 1]");
			eq.process("B1 = T' * Et * B2");
			
			DMatrixRMaj B2 = eq.lookupDDRM("B2");
			System.out.println(String.format("T(A): %-6s  T(V): %-6s  T(N): %-6s", 
							ff.format(B2.get(0, 0)), 
							ff.format(B2.get(1, 0)), 
							ff.format(B2.get(2, 0))));
			
			DMatrixRMaj B1 = eq.lookupDDRM("B1");
			System.out.println(String.format("C(A): %-6s  C(V): %-6s  C(N): %-6s", 
							ff.format(B1.get(0, 0)), 
							ff.format(B1.get(1, 0)), 
							ff.format(B1.get(2, 0))));
			
			System.out.println("1 * 0.0048 + 1 * 0.0168 = 0.0216");
			System.out.println("0.08 * 0.15 + 0.04 * 0.24 = 0.0216");
		}
		
		{
			System.out.println("========== viterbi ============");
		
			eq.process("V1 = Ec * S");
			eq.process("V2 = Et * T * max(V1) * [1;0;0;0]"); // [1;0;0;0]: pick the first element of V1 because it is the biggest
			
			DMatrixRMaj V1 = eq.lookupDDRM("V1");
			System.out.println(String.format("C(A): %-6s  C(V): %-6s  C(N): %-6s", 
					ff.format(V1.get(0, 0)), 
					ff.format(V1.get(1, 0)), 
					ff.format(V1.get(2, 0))));
			
			DMatrixRMaj V2 = eq.lookupDDRM("V2");
			System.out.println(String.format("T(A): %-6s  T(V): %-6s  T(N): %-6s", 
					ff.format(V2.get(0, 0)), 
					ff.format(V2.get(1, 0)), 
					ff.format(V2.get(2, 0))));	
			
			System.out.println("C(A) -> T(N)");
		}
	}
}
