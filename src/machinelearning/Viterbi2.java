package machinelearning;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class Viterbi2 {

	private static final DecimalFormat ff = new DecimalFormat("0.0000");

	private static final String[] SEQUENCE = { "I", "write", "a letter" };
	private static final String[] STATES = { "#", "NN", "VB" };
	private static final int [] CONVERTER = { 0, 1, 2 };
	private static final double[] STARTS = { 0.3, 0.4, 0.3 };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("T = [" +
						/* #,   NN,  VB */
		/* # */ 		" 0.2, 0.2, 0.6;" +
		/* NN */ 		" 0.4, 0.1, 0.5;" +
		/* VB */ 		" 0.1, 0.8, 0.1 " + 
					"]");

		eq.process("E = [" +
						  /* #,    NN,    VB */
		/* I */ 		" 0.01,	  0.8,   0.19;" +
		/* write */ 	" 0.02,  0.01,   0.97;" +
		/* a letter */ 	" 0.02,   0.5,   0.48 " + 
					"]");
		
		eq.process("S = [ 0.3; 0.4; 0.3 ]");

		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");

		{
			Viterbi2 virtebi = new Viterbi2();
			virtebi.compute(SEQUENCE, CONVERTER, STATES, STARTS, T, E, ff);

			System.out.println();
		}

		{
			eq.process("Ei = diag(E(0,0:))");
			eq.process("Ew = diag(E(1,0:))");
			eq.process("Ea = diag(E(2,0:))");

			eq.process("V1 = Ei * S");
			eq.process("V2 = Ew * T * max(V1) * [ 0; 1; 0 ]");
			eq.process("V3 = Ea * T * max(V2) * [ 0; 0; 1 ]");

			System.out.print("V1: ");
			DMatrixRMaj V1 = eq.lookupDDRM("V1");
			V1.print("%2.4f");
			System.out.print("V2: ");
			DMatrixRMaj V2 = eq.lookupDDRM("V2");
			V2.print("%2.4f");
			DMatrixRMaj V3 = eq.lookupDDRM("V3");
			System.out.print("V3: ");
			V3.print("%2.4f");

		}
	}

	public void compute(String[] sequence, int [] converter, String[] states, double[] starts, DMatrixRMaj T, DMatrixRMaj E, DecimalFormat ff) {

		Map<String, Double> probs = new LinkedHashMap<String, Double>();
		final Map<String, Double> ss = probs;

		Set<Integer> froms = new LinkedHashSet<Integer>();

		for (int i = 0; i < starts.length; i++) {
			ss.put("0" + ":" + states[i] + "{" + sequence[0] + "}", starts[i] * E.get(converter[0], i));
			froms.add(i);
		}

		Set<Integer> nexts = new LinkedHashSet<Integer>();

		for (int step = 1; step < converter.length; step++) {

			for (int from : froms) {

				for (int to = 0; to < T.numRows; to++) {

					if (T.get(to, from) > 0 && E.get(converter[step], to) > 0) {

						double left = 0.0d;
						double right = 0.0d;
						if (ss.get((step - 1) + ":" + states[from] + "{" + sequence[step - 1] + "}") != null) {
							left = ss.get((step - 1) + ":" + states[from] + "{" + sequence[step - 1] + "}");
						}

						if (ss.get(step + ":" + states[to] + "{" + sequence[step] + "}") != null) {
							right = ss.get(step + ":" + states[to] + "{" + sequence[step] + "}");
						}

						if (left > 0) {
							left = left * T.get(to, from) * E.get(converter[step], to);

							if (left > right)
								ss.put(step + ":" + states[to] + "{" + sequence[step] + "}", left);
						}

						nexts.add(to);
					}
				}
			}

			froms = new LinkedHashSet<Integer>(nexts);
			nexts.clear();
		}

		ss.entrySet().stream().forEach(p -> System.out.println(p.getKey() + " ==> " + ff.format(p.getValue())));
	}

}
