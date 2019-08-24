import java.text.DecimalFormat;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

import machinelearning.hmm.HMMAlgothrim.UnderFlowStrategy;
import machinelearning.hmm.HMMAlgothrim.VirterbiAlgorithm;
import machinelearning.hmm.Viterbi;

public class TestMe {
	
	private static final DecimalFormat ff = new DecimalFormat("0.0000000");

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("T = [ " +
					   /* A,    B */
			/* A */ "   0.7, 0.3;" +
			/* B */ "   0.3, 0.7 " +
						"]");

		eq.process("E = [" +
				 /*  0,   1 */
		/* A */  " 0.9, 0.1;" +
		/* B */  " 0.2, 0.8 " +
					"]");
		
		eq.process("S = [ 0.5; 0.5 ]");
		
		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		DMatrixRMaj S = eq.lookupDDRM("S");
		
		int [] converter = { 0, 0, 1, 0, 0 };
		String [] output = { "0", "1" };
		
		{
			Viterbi v = new Viterbi(VirterbiAlgorithm.BAYES_RULES_ALGO, UnderFlowStrategy.NONE);
			System.out.println(display(v.fit(converter, S, T, E)));
			System.out.println("PROB: " + ff.format(v.probability(v.fit(converter, S, T, E))));
		}
		System.out.println();

		{
			Viterbi v = new Viterbi(VirterbiAlgorithm.WIKI_PROPOSED_ALGO, UnderFlowStrategy.ENABLED);
			System.out.println(display(v.fit(converter, S, T, E)));
			System.out.println("PROB: " + ff.format(v.probability(v.fit(converter, S, T, E))));
		}
		System.out.println();
		{
			Viterbi v = new Viterbi(VirterbiAlgorithm.BAYES_RULES_ALGO, UnderFlowStrategy.ENABLED);
			System.out.println(display(v.fit(converter, S, T, E)));
			System.out.println("PROB: " + ff.format(v.probability(v.fit(converter, S, T, E))));
		}
		
	}
	
	private static String display(List<Pair<Integer, Double>> paths) {
		
		final String [] states = { "A", "B" };
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < paths.size(); i++) {

			if (i > 0)
				builder.append(", ");

			Pair<Integer, Double> pair = paths.get(i);

			builder.append(states[pair.getFirst()] + " : " + ff.format(pair.getSecond()));
		}
		
		return builder.toString();
	}
}
