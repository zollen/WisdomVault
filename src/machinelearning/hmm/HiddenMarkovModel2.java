package machinelearning.hmm;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

public class HiddenMarkovModel2 {

	private static final DecimalFormat ff = new DecimalFormat("0.00000000");

	private static String [] states = { "U1", "U2", "U3" };
	private static String[] characters = { "R", "G", "B" };
	private static String[] observations = { "R", "R", "G", "G", "B", "R", "G", "R" };
	private static int[] converter = { 0, 0, 1, 1, 2, 0, 1, 0 };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();

		eq.process("T = [" +
				 /* U1, U2, U3 */
		/* U1 */ " 0.1, 0.4, 0.5;" +
		/* U2 */ " 0.6, 0.2, 0.2;" +
		/* U3 */ " 0.3, 0.4, 0.3 " + "]");

		eq.process("E = [" +
				  /* R,   G,   B */
		/* U1 */ " 0.3, 0.5, 0.2;" +
		/* U2 */ " 0.1, 0.4, 0.5;" +
		/* U3 */ " 0.6, 0.1, 0.3 " + "]");

		eq.process("S = [ 1./3; 1./3; 1./3 ]");

		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		DMatrixRMaj S = eq.lookupDDRM("S");

		ForwardBackward fb = new ForwardBackward();
		fb.fit(converter, S, T, E);
		
		System.out.println("Viterbi    : " + display(states, fb.viterbi()));
		System.out.println("Forward    : " + display(fb.forward()));
		System.out.println("Backward   : " + display(fb.backward()));
		System.out.println("Prob(state): " + display(fb.forwardBackward()));
		System.out.println("PP(R,G,B)  : " + display(characters, converter, fb.posteriorProb()));
	}

	private static String display(String [] output, List<Pair<Integer, Double>> list) {

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < observations.length; i++) {

			Pair<Integer, Double> pair = list.get(i);

			if (i > 0)
				builder.append(", ");

			builder.append("{" + output[pair.getFirst()] + "}: " + ff.format(pair.getSecond()));

		}

		return builder.toString();
	}
	
	private static String display(String [] output, int [] converter, List<Pair<Integer, Double>> list) {

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < observations.length; i++) {

			Pair<Integer, Double> pair = list.get(i);

			if (i > 0)
				builder.append(", ");

			builder.append("{" + output[converter[pair.getFirst()]] + "}: " + ff.format(pair.getSecond()));

		}

		return builder.toString();
	}

	private static String display(List<Pair<Integer, DMatrixRMaj>> list) {

		return list
				.stream().map(p -> "{" + observations[converter[p.getFirst()]] + "}: " + 
						"[" +
							ff.format(p.getSecond().get(0, 0)) + ", " + 
							ff.format(p.getSecond().get(1, 0)) + ", " +
							ff.format(p.getSecond().get(2, 0)) + 
						"]")
				.collect(Collectors.joining(", "));
	}

}
