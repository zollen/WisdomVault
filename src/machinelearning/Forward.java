package machinelearning;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;
import org.nd4j.linalg.primitives.Pair;

public class Forward {

	/**
	 * http://www.cs.rochester.edu/u/james/CSC248/Lec11.pdf
	 * 
	 * states = ('0', '1') observations = ('R', 'W', 'B', 'B')
	 * 
	 * start_probability = {
	 * 			'0': 0.8, 
	 * 			'1': 0.2
	 * }
	 * 
	 * transition_probability = { 
	 * 			'0' : { '0': 0.6, '1': 0.4 }, 
	 * 			'1' : { '0': 0.3, '1': 0.7 }
	 * }
	 * 
	 * emission_probability = { 
	 * 			'0' : {'R': 0.3, 'W': 0.4, 'B': 0.3 },
	 * 			'1' : {'R': 0.4, 'W': 0.3, 'B': 0.3 }
	 * }
	 */

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("T = [ " +
						/* 0,   1 */
			/* 0 */ "   0.6,  0.4;" +
			/* 1 */ "   0.3,  0.7 " +
						"]");

		eq.process("E = [" +
				 /*  R,   W,   B */
		/* 0 */  " 0.3, 0.4, 0.3;" +
		/* 1 */  " 0.4, 0.3, 0.3 " +
					"]");
		
		eq.process("S = [ 0.8; 0.2 ]");
		
		eq.process("Er = [ 0.3; 0.4 ]");
		eq.process("Ew = [ 0.4; 0.3 ]");
		eq.process("Eb = [ 0.3; 0.3 ]");
		
		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		DMatrixRMaj S = eq.lookupDDRM("S");
		
		// 'R'
//		eq.process("F1 = Er .* S");
//		eq.lookupDDRM("F1").print("%2.4f");
//		System.out.println("F1(0): " + ff.format(eq.lookupDDRM("F1").get(0, 0)));
//		System.out.println("F1(1): " + ff.format(eq.lookupDDRM("F1").get(1, 0)));
		
		// 'W'                                         (Long form)
//		eq.process("F20 = 0.4 * (0.6 * 0.24 + 0.3 * 0.08)");
//		System.out.println("F2(0): " + ff.format(eq.lookupDouble("F20")));
//		eq.process("F21 = 0.3 * (0.4 * 0.24 + 0.7 * 0.08)");
//		System.out.println("F2(1): " + ff.format(eq.lookupDouble("F21")));
		
		// Transposing T switch from -> to, to -> from  (Matrix form)
//		eq.process("F2 = Ew .* (T' * F1)"); 
//		eq.lookupDDRM("F2").print("%2.4f");
		
		// 'B'                                          (Long form)
//		eq.process("F30 = 0.3 * (0.6 * 0.0672 + 0.3 * 0.0456)");
//		System.out.println("F3(0): " + ff.format(eq.lookupDouble("F30")));
//		eq.process("F31 = 0.3 * (0.4 * 0.0672 + 0.7 * 0.0456)");
//		System.out.println("F3(1): " + ff.format(eq.lookupDouble("F31")));
		
		// Transposing T switch from -> to, to -> from   (Matrix form)
//		eq.process("F3 = Eb .* (T' * F2)"); 
//		eq.lookupDDRM("F3").print("%2.4f");
		
		// 'B'                                            (Long form)
//		eq.process("F40 = 0.3 * (0.6 * 0.0162 + 0.3 * 0.0176)");
//		System.out.println("F4(0): " + ff.format(eq.lookupDouble("F40")));
//		eq.process("F41 = 0.3 * (0.4 * 0.0162 + 0.7 * 0.0176)");
//		System.out.println("F4(1): " + ff.format(eq.lookupDouble("F41")));
		
		// Transposing T switch from -> to, to -> from    (Matrix form)
//		eq.process("F4 = Eb .* (T' * F3)"); 
//		eq.lookupDDRM("F4").print("%2.4f");
		
		int [] converter = { 0, 1, 2, 2 };
		String [] states = { "R", "W", "B" };
		
		DecimalFormat ff = new DecimalFormat("0.0000");
		List<Pair<Integer, DMatrixRMaj>> list = fit(converter, S, T, E);
		
		System.out.println(list.stream().map(p -> 
					"{" + states[converter[p.getKey()]] + "} : " + "[" +
					ff.format(p.getValue().get(0, 0)) + ", " +
					ff.format(p.getValue().get(1, 0)) + "]"
				).collect(Collectors.joining(", ")));
	}
	
	public static List<Pair<Integer, DMatrixRMaj>> fit(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		Map<Integer, DMatrixRMaj> map = new HashMap<Integer, DMatrixRMaj>();
		
		for (int index = 0; index < E.numCols; index++) {
			map.put(index, CommonOps_DDRM.extractColumn(E, index, null));
		}
		
		
		
		List<Pair<Integer, DMatrixRMaj>> forward = new ArrayList<Pair<Integer, DMatrixRMaj>>();
		DMatrixRMaj res = new DMatrixRMaj(T.numRows, 1);
		for (int index = 0; index < converter.length; index++) {
			
			if (index == 0) {
				CommonOps_DDRM.elementMult(map.get(converter[index]), S, res);		
			}
			else {
				DMatrixRMaj tmp = new DMatrixRMaj(T.numRows, res.numCols);
				CommonOps_DDRM.multTransA(T, res, tmp);
				CommonOps_DDRM.elementMult(tmp, map.get(converter[index]));
				res = tmp;
			}	
			
			forward.add(new Pair<>(index, res));
		}
		
		return forward;
	}
}