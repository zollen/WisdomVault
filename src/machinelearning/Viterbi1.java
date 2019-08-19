package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class Viterbi1 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.0000");
	
	private static String[] states = { "#", "NN", "VB" };
	private static String[] observations = { "I", "write", "a letter" };
	private static int [] converter = { 0, 1, 2 };
	private static double[] start_probability = { 0.3, 0.4, 0.3 };
	

	private static class TNode {
		public List<Integer> v_path;
		public double v_prob;

		public TNode(List<Integer> v_path, double v_prob) {
			this.v_path = new ArrayList<Integer>(v_path);
			this.v_prob = v_prob;
		}
	}

	public List<Integer> compute(String[] observables, int [] converter, String[] states, double[] sp, DMatrixRMaj tp, DMatrixRMaj ep, DecimalFormat ff) {
		
		TNode[] T = new TNode[states.length];
		for (int state = 0; state < states.length; state++) {
			List<Integer> intArray = new ArrayList<Integer>();
			intArray.add(state);
			T[state] = new TNode(intArray, sp[state] * ep.get(state, converter[0]));
			System.err.println(debug(observables, states, intArray, sp[state] * ep.get(state, 0), ff));
		}

		
		for (int output = 1; output < converter.length; output++) {
			
			TNode[] U = new TNode[states.length];
			for (int next_state = 0; next_state < states.length; next_state++) {
				
				List<Integer> argmax = null;
				double valmax = 0;			
				for (int current_state = 0; current_state < states.length; current_state++) {
					
					List<Integer> v_path = new ArrayList<Integer>(T[current_state].v_path);
					double v_prob = T[current_state].v_prob;
					double p = ep.get(next_state, converter[output]) * tp.get(current_state, next_state);
					v_prob *= p;
					
					if (v_prob > valmax) {
						
						if (v_path.size() == converter.length) {
							argmax = v_path;
						} else {
							argmax = v_path;
							argmax.add(next_state);
						}
						
						valmax = v_prob;
					}
				}
				
				System.err.println(debug(observables, states, argmax, valmax, ff));
				U[next_state] = new TNode(argmax, valmax);
			}
			
			T = U;
		}
		
		// apply sum/max to the final states:
		List<Integer> argmax = null;
		double valmax = 0;
		for (int state = 0; state < states.length; state++) {
			
			List<Integer> v_path = new ArrayList<Integer>(T[state].v_path);
			double v_prob = T[state].v_prob;
			if (v_prob > valmax) {
				argmax = v_path;
				valmax = v_prob;
			}
		}
	
		return argmax;
	}
	
	private String debug(String[] observables, String[] states, List<Integer> tokens, double prob, DecimalFormat ff) {
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < tokens.size(); i++) {
			
			if (builder.length() > 0)
				builder.append(", ");
			
			builder.append(states[tokens.get(i)] + " {" + observables[i] + "}");
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
		List<Integer> paths = v.compute(observations, converter, states, start_probability, T, E, ff);
		
		System.out.print("Viterbi path: [");
		for (int i = 0; i < paths.size(); i++) {
			
			if (i > 0)
				System.out.print(", ");
			
			System.out.print(states[paths.get(i)]);
		}
		System.out.println("]");

	}
}