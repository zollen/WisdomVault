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
	
	// 					sequence: "R", "R", "G", "G", "B", "R", "G", "R" 
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

		{
			ForwardBackward fb = new ForwardBackward.Builder().build();
			fb.fit(converter, S, T, E);
		
			List<Pair<Integer, Double>> vl = fb.viterbi();
			List<Pair<Integer, DMatrixRMaj>> ffl = fb.forward();
			List<Pair<Integer, DMatrixRMaj>> bbl = fb.backward();
			System.out.println("Viterbi    : " + display(states, vl) + "   || Prob(U3,U3,U2,U1,U3,U3,U1,U3|R,R,G,G,B,R,G,R): " + ff.format(fb.viterbi(vl)));
			System.out.println("Forward    : " + display(ffl) + "   || Posterior: " + ff.format(fb.forward(ffl)));
			System.out.println("Backward   : " + display(bbl));
			System.out.println("FB         : " + display(fb.forwardBackward()));
			System.out.println("Posterior  : " + display(characters, fb.posterior()));
		}
		System.out.println();
		{
			ForwardBackward fb = new ForwardBackward.Builder()
									.setUnderFlowStrategy(true).build();
			fb.fit(converter, S, T, E);
		
			List<Pair<Integer, Double>> vl = fb.viterbi();
			List<Pair<Integer, DMatrixRMaj>> ffl = fb.forward();
			List<Pair<Integer, DMatrixRMaj>> bbl = fb.backward();
			System.out.println("Viterbi    : " + display(states, vl) + "   || Weight(U3,U3,U2,U1,U3,U3,U1,U3|R,R,G,G,B,R,G,R): " + ff.format(fb.viterbi(vl)));
			System.out.println("Forward    : " + display(ffl) + "   || Posterior: " + ff.format(fb.forward(ffl)));
			System.out.println("Backward   : " + display(bbl));
			System.out.println("FB         : " + display(fb.forwardBackward()));
			System.out.println("Posterior  : " + display(characters, fb.posterior()));
		}
	}

	private static String display(String [] output, List<Pair<Integer, Double>> list) {

		return list.stream().map(p -> "{" + output[p.getFirst()] + "}: " + 
					ff.format(p.getSecond())).collect(Collectors.joining(", "));
	}

	private static String display(List<Pair<Integer, DMatrixRMaj>> list) {

		return list
				.stream().map(p -> "{" + characters[p.getFirst()] + "}: " + 
						"[" +
							ff.format(p.getSecond().get(0, 0)) + ", " + 
							ff.format(p.getSecond().get(1, 0)) + ", " +
							ff.format(p.getSecond().get(2, 0)) + 
						"]")
				.collect(Collectors.joining(", "));
	}

}
