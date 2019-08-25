package machinelearning.hmm;

import java.text.DecimalFormat;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

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
		
		Printer p = new Printer(ff);

		{
			ForwardBackward fb = new ForwardBackward.Builder().build();
			fb.fit(converter, S, T, E);
		
			System.out.println("Viterbi    : " + p.display(states, fb.viterbi()) + "   || Prob(U3,U3,U2,U1,U3,U3,U1,U3|R,R,G,G,B,R,G,R): " + ff.format(fb.viterbi(fb.viterbi())));
			System.out.println("Forward    : " + p.display(characters, fb.forward()) + "   || Posterior: " + ff.format(fb.forward(fb.forward())));
			System.out.println("Backward   : " + p.display(characters, fb.backward()));
			System.out.println("FB         : " + p.display(characters, fb.forwardBackward()));
			System.out.println("Posterior  : " + p.display(characters, fb.posterior()));
		}
		System.out.println();
		{
			ForwardBackward fb = new ForwardBackward.Builder()
									.setUnderFlowStrategy(true).build();
			fb.fit(converter, S, T, E);
		
			System.out.println("Viterbi    : " + p.display(states, fb.viterbi()) + "   || Weight(U3,U3,U2,U1,U3,U3,U1,U3|R,R,G,G,B,R,G,R): " + ff.format(fb.viterbi(fb.viterbi())));
			System.out.println("Forward    : " + p.display(characters, fb.forward()) + "   || Posterior: " + ff.format(fb.forward(fb.forward())));
			System.out.println("Backward   : " + p.display(characters, fb.backward()));
			System.out.println("FB         : " + p.display(characters, fb.forwardBackward()));
			System.out.println("Posterior  : " + p.display(characters, fb.posterior()));
		}
	}
	
}
