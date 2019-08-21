package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

public class Viterbi2 {

	private static final DecimalFormat ff = new DecimalFormat("0.0000");

	private static final String[] SEQUENCE = { "I", "write", "a letter" };
	private static final String[] STATES = { "#", "NN", "VB" };
	private static final int [] CONVERTER = { 0, 1, 2 };

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
				/*       I,   write,   a letter      */
		/* # */		" 0.01,	   0.02,     0.02;" +
		/* NN */	"  0.8,    0.01,      0.5;" +
		/* VB */	" 0.19,    0.97,     0.48 " +
					"]");
		
		eq.process("S = [ 0.3; 0.4; 0.3 ]");

		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		DMatrixRMaj S = eq.lookupDDRM("S");

		{
			Viterbi2 virtebi = new Viterbi2();
			List<Pair<Integer, Double>> list = virtebi.compute(SEQUENCE, CONVERTER, S, T, E);

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < SEQUENCE.length; i++) {
				
				Pair<Integer, Double> pair = list.get(i);
				
				if (i > 0)
					builder.append(", ");
				
				builder.append(SEQUENCE[i] + "{" + STATES[pair.getFirst()] + "}: " + ff.format(pair.getSecond()));
				
			}
			
			System.out.println(builder.toString());
		}

		{
			eq.process("Ei = diag(E(0:,0))");
			eq.process("Ew = diag(E(0:,1))");
			eq.process("Ea = diag(E(0:,2))");

			eq.process("V1 = Ei * S");
			eq.process("V1 = V1'");
			eq.process("V2 = [0, 1, 0] * T * Ew * max(V1) ");
			eq.process("V3 = [0, 0, 1] * T * Ea * max(V2) ");

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

	public List<Pair<Integer, Double>> compute(String[] sequence, int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		List<Pair<Integer, Double>> list = new ArrayList<Pair<Integer, Double>>();
		
		list.add(max(S, 0, E, converter[0]));
		
		return _compute(converter, T, E, list);
	}
	
	private List<Pair<Integer, Double>> _compute(int [] converter, DMatrixRMaj T, DMatrixRMaj E, List<Pair<Integer, Double>> list) {
		
		Pair<Integer, Double> last = list.get(list.size() - 1);
		
		if (list.size() == converter.length) {
			return list;
		}
		
		List<List<Pair<Integer, Double>>> repo = new ArrayList<List<Pair<Integer, Double>>>();
		
		for (int col = 0; col < T.numCols; col++ ) {
			
			double prob = last.getSecond() *  T.get(last.getFirst(), col) * E.get(last.getFirst(), converter[list.size()]);
		
			List<Pair<Integer, Double>> tmp = new ArrayList<Pair<Integer, Double>>(list);
			tmp.add(new Pair<Integer, Double>(col, prob));
			
			repo.add(_compute(converter, T, E, tmp));
		}
		
		
		
		List<Pair<Integer, Double>> desirable = null;
		double maxProb = Double.MIN_VALUE;
		for (List<Pair<Integer, Double>> target : repo) {
			
			if (target.get(target.size() - 1).getSecond() > maxProb) {
				desirable = target;
				maxProb = target.get(target.size() - 1).getSecond();
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

}
