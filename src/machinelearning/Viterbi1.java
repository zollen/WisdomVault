package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

public class Viterbi1 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.0000");
	
	private static String[] states = { "#", "NN", "VB" };
	private static String[] observations = { "I", "write", "a letter" };
	private static int [] converter = { 0, 1, 2 };
	private static double[] start_probability = { 0.3, 0.4, 0.3 };
	

	private static class TNode {
		public List<Pair<Integer, Double>> v_path;
		public double v_prob;

		public TNode(List<Pair<Integer, Double>> v_path, double v_prob) {
			this.v_path = new ArrayList<Pair<Integer, Double>>(v_path);
			this.v_prob = v_prob;
		}
	}

	public List<Pair<Integer, Double>> compute(int [] converter, DMatrixRMaj sp, DMatrixRMaj tp, DMatrixRMaj ep) {
		
		TNode[] T = new TNode[tp.numRows];
		for (int state = 0; state < tp.numRows; state++) {
			List<Pair<Integer, Double>> intArray = new ArrayList<Pair<Integer, Double>>();
			double v_prob = sp.get(state, 0) * ep.get(state, converter[0]);
			intArray.add(new Pair<>(state, v_prob));
			T[state] = new TNode(intArray, v_prob);
		}

		
		for (int output = 1; output < converter.length; output++) {
			
			TNode[] U = new TNode[tp.numRows];
			for (int next_state = 0; next_state < tp.numRows; next_state++) {
				
				List<Pair<Integer, Double>> argmax = null;
				double valmax = 0;			
				for (int current_state = 0; current_state < tp.numRows; current_state++) {
					
					List<Pair<Integer, Double>> v_path = new ArrayList<Pair<Integer, Double>>(T[current_state].v_path);
					double v_prob = T[current_state].v_prob;
					double p = ep.get(next_state, converter[output]) * tp.get(current_state, next_state);
					v_prob *= p;
					
					if (v_prob > valmax) {
						
						if (v_path.size() == converter.length) {
							argmax = v_path;
						} else {
							argmax = v_path;
							argmax.add(new Pair<>(next_state, v_prob));
						}
						
						valmax = v_prob;
					}
				}

				U[next_state] = new TNode(argmax, valmax);
			}
			
			T = U;
		}
		
		// apply sum/max to the final states:
		List<Pair<Integer, Double>> argmax = null;
		double valmax = 0;
		for (int state = 0; state < tp.numRows; state++) {
			
			List<Pair<Integer, Double>> v_path = new ArrayList<Pair<Integer, Double>>(T[state].v_path);
			double v_prob = T[state].v_prob;
			if (v_prob > valmax) {
				argmax = v_path;
				valmax = v_prob;
			}
		}
	
		return argmax;
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
		
		eq.process("S = [ 0.3; 0.4; 0.3 ]");
				
		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		DMatrixRMaj S = eq.lookupDDRM("S");
		
		Viterbi1 v = new Viterbi1();
		List<Pair<Integer, Double>> paths = v.compute(converter, S, T, E);
		
		System.out.print("Viterbi path: [");
		for (int i = 0; i < paths.size(); i++) {
			
			if (i > 0)
				System.out.print(", ");
			
			Pair<Integer, Double> pair = paths.get(i);
			
			System.out.print(states[pair.getFirst()] + " : " + ff.format(pair.getSecond()));
		}
		System.out.println("]");

	}
}