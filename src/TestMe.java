import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

import machinelearning.hmm.ForwardBackward;

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
		
		ForwardBackward fb = new ForwardBackward.Builder()
				.setUnderFlowStrategy(true).build();
		fb.fit(converter, S, T, E);
		
		System.out.println("Forward    : " + display(output, converter, fb.forward()));
		System.out.println("Backward   : " + display(output, converter, fb.backward()));
		System.out.println("Prob(state): " + display(output, converter, fb.forwardBackward()));
		
	}
	
	private static String display(String [] characters, int [] converter, 
						List<Pair<Integer, DMatrixRMaj>> list) {

		return list
				.stream().map(p -> "{" + characters[converter[p.getFirst()]] + "}: " +
						"[" +
							ff.format(p.getSecond().get(0, 0)) + ", " + 
							ff.format(p.getSecond().get(1, 0)) + 
						"]"
						)
				.collect(Collectors.joining(", "));
	}
}
