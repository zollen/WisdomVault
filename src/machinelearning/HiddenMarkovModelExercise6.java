package machinelearning;

import java.text.DecimalFormat;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

public class HiddenMarkovModelExercise6 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.00000000");
	
	private static String[] states = { "U1", "U2", "U3" };
	private static String[] observations = { "R", "R", "G", "G", "B", "R", "G", "R" };
	private static int [] converter = { 0, 0, 1, 1, 2, 0, 1, 0 };
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();	

		eq.process("T = [" +
				   /*      U1,  U2,  U3      */
	/* U1 */			" 0.1, 0.4, 0.5;" +
	/* U2 */			" 0.6, 0.2, 0.2;" +
	/* U3 */			" 0.3, 0.4, 0.3 " +
						"]");
		
		eq.process("E = [" +
					/*      R,   G,   B      */
	/* U1 */			" 0.3, 0.5, 0.2;" +
	/* U2 */			" 0.1, 0.4, 0.5;" +
	/* U3 */			" 0.6, 0.1, 0.3 " +
						"]");
		
		eq.process("S = [ 1./3; 1./3; 1./3 ]");
				
		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		DMatrixRMaj S = eq.lookupDDRM("S");
		
		{
			System.out.println("Using Wiki Proposed Viterbi");
			Viterbi1 v = new Viterbi1();
			List<Pair<Integer, Double>> paths = v.compute(converter, S, T, E);
		
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < observations.length; i++) {
				
				Pair<Integer, Double> pair = paths.get(i);
				
				if (i > 0)
					builder.append(", ");
				
				builder.append(observations[i] + "{" + states[pair.getFirst()] + "}: " + ff.format(pair.getSecond()));
				
			}
			
			System.out.println(builder.toString());
		}
		
		{
			System.out.println("Using Bayes Rules calculation");
			Viterbi2 virtebi = new Viterbi2();
			List<Pair<Integer, Double>> list = virtebi.compute(converter, S, T, E);

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < observations.length; i++) {
				
				Pair<Integer, Double> pair = list.get(i);
				
				if (i > 0)
					builder.append(", ");
				
				builder.append(observations[i] + "{" + states[pair.getFirst()] + "}: " + ff.format(pair.getSecond()));
				
			}
			
			System.out.println(builder.toString());
		}
		
		

	}

}
