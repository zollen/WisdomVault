package machinelearning.hmm;

import java.text.DecimalFormat;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

import machinelearning.hmm.HMMComposite.HMMResult;

public class HiddenMarkovModel2 {

	// Reference: http://danushka.net/lect/dm/TextMining.pdf
	private static final DecimalFormat ff = new DecimalFormat("0.00000000");

	private static String [] states = { "U1", "U2", "U3" };
	private static String[] characters = { "R", "G", "B" };
	
	// 					sequence: "R", "R", "G", "G", "B", "R", "G", "R" 
	private static int[] converter = { 0, 0, 1, 1, 2, 0, 1, 0 };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();

		eq.process("T = [" +
			 /* TO: U1,  U2,  U3 */
  /* FROM: U1 */ " 0.1, 0.4, 0.5;" +
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
			HMMComposite fb = new HMMComposite.Builder().build();
			HMMResult h = fb.fit(converter, S, T, E);
		
			System.out.println("Viterbi    : " + p.display(states, h.vlist()) + "   => P(U3,U3,U2,U1,U3,U3,U1,U3|R,R,G,G,B,R,G,R): " + ff.format(h.viterbi().probability(h.vlist())));
			System.out.println("Forward    : " + p.display(characters, h.flist()) + "   => P(U3,R,R,G,G,B,R,G,R): " + ff.format(h.forward().probability(h.flist())));
			System.out.println("Backward   : " + p.display(characters, h.blist()));
			System.out.println("FB         : " + p.display(characters, h.fblist()));
			System.out.println("Posterior  : " + p.display(characters, h.plist()));
		}
		System.out.println();
		{
			HMMComposite fb = new HMMComposite.Builder()
									.setUnderFlowStrategy(true).build();
			HMMResult h = fb.fit(converter, S, T, E);
		
			System.out.println("Viterbi    : " + p.display(states, h.vlist()) + "   => P(U3,U3,U2,U1,U3,U3,U1,U3|R,R,G,G,B,R,G,R): " + ff.format(h.viterbi().probability(h.vlist())));
			System.out.println("Forward    : " + p.display(characters, h.flist()) + "   => P(U3,R,R,G,G,B,R,G,R): " + ff.format(h.forward().probability(h.flist())));
			System.out.println("Backward   : " + p.display(characters, h.blist()));
			System.out.println("FB         : " + p.display(characters, h.fblist()));
			System.out.println("Posterior  : " + p.display(characters, h.plist()));
		}
	}
	
}
