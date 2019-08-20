package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
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
			List<Pair<Integer, Double>> list = virtebi.compute(SEQUENCE, CONVERTER, STATES, S, T, E, ff);

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

	public List<Pair<Integer, Double>> compute(String[] sequence, int [] converter, String[] states, DMatrixRMaj starts, DMatrixRMaj T, DMatrixRMaj E, DecimalFormat ff) {
		
		double lastVal = 0;
		int lastRow = 0;
		List<Pair<Integer, Double>> list = new ArrayList<Pair<Integer, Double>>();
		
		for (int index = 0; index < sequence.length; index++) {
			
			DMatrixRMaj ee = diag(E, converter[index]);
			
			if (index == 0) {
				
				DMatrixRMaj res = new DMatrixRMaj(E.numRows, 1);
						
				CommonOps_DDRM.mult(ee, starts, res);
				
				double maxVal = Double.MIN_VALUE;
				int maxRow = -1;
				for (int row = 0; row < E.numRows; row++) {
					
					if (res.get(row, 0) > maxVal) {
						maxVal = res.get(row, 0);
						maxRow = row;
					}				
				}
				
				lastVal = maxVal;
				lastRow = maxRow;
				
				list.add(new Pair<>(lastRow, lastVal));
				
			}
			else {
				
				DMatrixRMaj res = new DMatrixRMaj(T.numRows, ee.numCols);
				CommonOps_DDRM.mult(lastVal, T, ee, res);
				
				double maxVal = Double.MIN_VALUE;
				int maxCol = -1;
				for (int col = 0; col < res.numCols; col++) {
					
					if (res.get(lastRow, col) > maxVal) {
						maxVal = res.get(lastRow, col);
						maxCol = col; 
					}
				}
				
				lastVal = maxVal;
				lastRow = maxCol;
				
				list.add(new Pair<>(lastRow, lastVal));
			}			
		}	
		
		return list;
	}
	
	private DMatrixRMaj diag(DMatrixRMaj A, int col) {
		
		double [] args = new double[A.numCols];
		
		for (int i = 0; i < A.numRows; i++) {
			args[i] = A.get(i, col);
		}

		return CommonOps_DDRM.diagR(A.numRows, A.numCols, args);
	}

}
