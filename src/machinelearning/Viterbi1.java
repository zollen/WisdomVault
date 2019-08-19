package machinelearning;

import java.text.DecimalFormat;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class Viterbi1 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.0000");
	
	private static String[] states = { "#", "NN", "VB" };
	private static String[] observations = { "I", "write", "a letter" };
	private static double[] start_probability = { 0.3, 0.4, 0.3 };
	

	private static class TNode {
		public int[] v_path;
		public double v_prob;

		public TNode(int[] v_path, double v_prob) {
			this.v_path = copyIntArray(v_path);
			this.v_prob = v_prob;
		}
	}

	private static int[] copyIntArray(int[] ia) {
		int[] newIa = new int[ia.length];
		for (int i = 0; i < ia.length; i++) {
			newIa[i] = ia[i];
		}
		return newIa;
	}

	private static int[] copyIntArray(int[] ia, int newInt) {
		int[] newIa = new int[ia.length + 1];
		for (int i = 0; i < ia.length; i++) {
			newIa[i] = ia[i];
		}
		newIa[ia.length] = newInt;
		return newIa;
	}

	// forwardViterbi(observations, states, start_probability,
	// transition_probability, emission_probability)
	public int[] compute(String[] y, String[] X, double[] sp, DMatrixRMaj tp, DMatrixRMaj ep) {
		TNode[] T = new TNode[X.length];
		for (int state = 0; state < X.length; state++) {
			int[] intArray = new int[1];
			intArray[0] = state;
			T[state] = new TNode(intArray, sp[state] * ep.get(state, 0));
			System.err.println(debug(intArray, sp[state] * ep.get(state, 0)));
		}

		for (int output = 1; output < y.length; output++) {
			TNode[] U = new TNode[X.length];
			for (int next_state = 0; next_state < X.length; next_state++) {
				int[] argmax = new int[0];
				double valmax = 0;
				for (int state = 0; state < X.length; state++) {
					int[] v_path = copyIntArray(T[state].v_path);
					double v_prob = T[state].v_prob;
					double p = ep.get(next_state, output) * tp.get(next_state, state);
					v_prob *= p;
					if (v_prob > valmax) {
						if (v_path.length == y.length) {
							argmax = copyIntArray(v_path);
						} else {
							argmax = copyIntArray(v_path, next_state);
						}
						valmax = v_prob;

					}
				}
				System.err.println(debug(argmax, valmax));
				U[next_state] = new TNode(argmax, valmax);
			}
			T = U;
		}
		// apply sum/max to the final states:
		int[] argmax = new int[0];
		double valmax = 0;
		for (int state = 0; state < X.length; state++) {
			int[] v_path = copyIntArray(T[state].v_path);
			double v_prob = T[state].v_prob;
			if (v_prob > valmax) {
				argmax = copyIntArray(v_path);
				valmax = v_prob;
			}
		}
	
		System.out.print("Viterbi path: [");
		for (int i = 0; i < argmax.length; i++) {
			
			if (i > 0)
				System.out.print(", ");
			
			System.out.print(states[argmax[i]]);
		}
		System.out.println("].\n Probability of the whole system: " + ff.format(valmax));
		return argmax;
	}
	
	private String debug(int [] tokens, double prob) {
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < tokens.length; i++) {
			
			if (builder.length() > 0)
				builder.append(", ");
			
			builder.append(states[tokens[i]] + " {" + observations[i] + "}");
		}
		
		builder.append(" : " + ff.format(prob));
		
		return builder.toString();
	}

	public static void main(String[] args) throws Exception {
		
		System.out.print("States: ");
		for (int i = 0; i < states.length; i++) {
			System.out.print(states[i] + ", ");
		}
		System.out.print("\nObservations: ");
		for (int i = 0; i < observations.length; i++) {
			System.out.print(observations[i] + ", ");
		}
		System.out.print("\nStart probability: ");
		for (int i = 0; i < states.length; i++) {
			System.out.print(states[i] + ": " + start_probability[i] + ", ");
		}
		
		System.out.println();
		
		
		Equation eq = new Equation();	

		eq.process("T = [" +
					/*      #,   NN,   VB      */
	/* # */				" 0.2, 0.2, 0.6;" +
	/* NN */			" 0.4, 0.1, 0.5;" +
	/* VB */			" 0.1, 0.8, 0.1 " +
						"]");
		
		eq.process("E = [" +
					/*      I,    write,   a letter      */
	/* # */				" 0.01,	   0.02,     0.02;" +
	/* NN */			"  0.8,    0.01,      0.5;" +
	/* VB */			" 0.19,    0.97,     0.48 " +
						"]");
				
		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		
		Viterbi1 v = new Viterbi1();
		v.compute(observations, states, start_probability, T, E);

	}
}