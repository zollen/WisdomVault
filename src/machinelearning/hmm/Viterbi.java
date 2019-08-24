package machinelearning.hmm;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

public class Viterbi implements HMMAlgothrim<Double> {
	
	private VirterbiAlgorithm algorithm = VirterbiAlgorithm.BAYES_RULES_ALGO;
	private UnderFlowStrategy strategy = UnderFlowStrategy.NONE;
	
	public Viterbi() {}
	
	public Viterbi(VirterbiAlgorithm algorithm) {
		this.setAlgorithm(algorithm);
	}
	
	public Viterbi(UnderFlowStrategy strategy) {
		this.setStrategy(strategy);
	}
	
	public Viterbi(VirterbiAlgorithm algorithm, UnderFlowStrategy strategy) {
		this.setAlgorithm(algorithm);
		this.setStrategy(strategy);
	}
	
	public VirterbiAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(VirterbiAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
	
	public UnderFlowStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(UnderFlowStrategy strategy) {
		this.strategy = strategy;
	}
	
	@Override
	public List<Pair<Integer, Double>> fit(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		if (this.algorithm == VirterbiAlgorithm.WIKI_PROPOSED_ALGO) {
			return wiki(converter, S, T, E);
		}
		else {
			return bayes(converter, S, T, E);
		}
	}
	
	@Override
	public double posterior(List<Pair<Integer, Double>> list) {
		
		throw new RuntimeException("Not supported!");
	}
	

	private static class TNode {
		public List<Pair<Integer, Double>> v_path;
		public double v_prob;

		public TNode(List<Pair<Integer, Double>> v_path, double v_prob) {
			this.v_path = new ArrayList<Pair<Integer, Double>>(v_path);
			this.v_prob = v_prob;
		}
	}

	public List<Pair<Integer, Double>> wiki(int [] converter, DMatrixRMaj sp, DMatrixRMaj tp, DMatrixRMaj ep) {
		
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
	
	public List<Pair<Integer, Double>> bayes(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		List<Pair<Integer, Double>> list = new ArrayList<Pair<Integer, Double>>();
		
		list.add(max(S, 0, E, converter[0]));
		
		return _compute(converter, T, E, list);
	}
	
	private List<Pair<Integer, Double>> _compute(int [] converter, DMatrixRMaj T, DMatrixRMaj E, List<Pair<Integer, Double>> list) {
		
		Pair<Integer, Double> last = list.get(list.size() - 1);
		
		if (list.size() == converter.length) {
			return list;
		}
		
		
		
		double maxProb = Double.MIN_VALUE;
		List<Pair<Integer, Double>> desirable = null;
		
		for (int col = 0; col < T.numCols; col++ ) {
		
			double prob = last.getSecond() *  T.get(last.getFirst(), col) * E.get(col, converter[list.size()]);
		
			List<Pair<Integer, Double>> tmp = new ArrayList<Pair<Integer, Double>>(list);
			tmp.add(new Pair<Integer, Double>(col, prob));
			
			tmp = _compute(converter, T, E, tmp);
			double pp = tmp.get(tmp.size() - 1).getSecond();
			if (pp > maxProb) {
				maxProb = pp; 
				desirable = tmp;
			}
		}
		
		return desirable;
	}
	
	private Pair<Integer, Double> max(DMatrixRMaj T, int col1, DMatrixRMaj E, int col2) {
		
		double maxProb = Double.MIN_VALUE;
		int maxRow = -1;
		for (int row = 0; row < T.numRows; row++) {
			double val = T.get(row, col1) * E.get(row, col2);
			if (val > maxProb) {
				maxProb = val;
				maxRow = row;
			}
		}
		
		return new Pair<Integer, Double>(maxRow, maxProb);
	}


	public static void main(String[] args) throws Exception {
		
		DecimalFormat ff = new DecimalFormat("0.0000");
		
		String[] states = { "#", "NN", "VB" };
		String[] observations = { "I", "write", "a letter" };
		int [] converter = { 0, 1, 2 };
		double[] start_probability = { 0.3, 0.4, 0.3 };
		
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
		
		{
			System.out.print("Wiki Proposed ALGO: [");
			
			double start = System.nanoTime();
			
			Viterbi v1 = new Viterbi(VirterbiAlgorithm.WIKI_PROPOSED_ALGO);
			List<Pair<Integer, Double>> paths = v1.fit(converter, S, T, E);
			
			double end = System.nanoTime();
	
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < paths.size(); i++) {

				if (i > 0)
					builder.append(", ");

				Pair<Integer, Double> pair = paths.get(i);

				builder.append(states[pair.getFirst()] + " : " + ff.format(pair.getSecond()));
			}
			
			System.out.println(builder.toString() + "]    Performance: " + ((end - start) / 1000000.0) + " ms");
		}

		{
			System.out.print("Bayes Rules ALGO: [");
			
			double start = System.nanoTime();
			
			Viterbi v2 = new Viterbi(VirterbiAlgorithm.BAYES_RULES_ALGO);
			List<Pair<Integer, Double>> paths = v2.fit(converter, S, T, E);
			
			double end = System.nanoTime();

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < paths.size(); i++) {

				if (i > 0)
					builder.append(", ");

				Pair<Integer, Double> pair = paths.get(i);

				builder.append(states[pair.getFirst()] + " : " + ff.format(pair.getSecond()));
			}
			
			System.out.println(builder.toString() + "]    Performance: " + ((end - start) / 1000000.0) + " ms");
		}

	}
}