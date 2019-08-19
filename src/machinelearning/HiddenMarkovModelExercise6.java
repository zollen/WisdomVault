package machinelearning;

import java.text.DecimalFormat;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class HiddenMarkovModelExercise6 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.00000000");
	
	private static String[] states = { "U1", "U2", "U3" };
	private static String[] observations = { "R", "R", "G", "G", "B", "R", "G", "R" };
	private static int [] converter = { 0, 0, 1, 1, 2, 0, 1, 0 };
	private static double[] start_probability = { 1.0/3.0, 1.0/3.0, 1.0/3.0 };

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
				
		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		
		{
			Viterbi1 v = new Viterbi1();
			List<Integer> paths = v.compute(observations, converter, states, 
									start_probability, T, E, ff);
		
			System.out.print("Viterbi path: [");
			for (int i = 0; i < paths.size(); i++) {
			
				if (i > 0)
					System.out.print(", ");
			
				System.out.print(states[paths.get(i)]);
			}
			System.out.println("]");
		}
		
		{
			CommonOps_DDRM.transpose(E);
			
			Viterbi2 virtebi = new Viterbi2();
			virtebi.compute(observations, converter, states, start_probability, T, E, ff);

			System.out.println();
		}
		
		

	}

}
