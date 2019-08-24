package machinelearning.hmm;

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

public class Backward implements HMMAlgothrim<DMatrixRMaj> {
	
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
	
	public static void main(String ...args) {		
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
		
//		eq.process("Er = E(0:,0)");
//		eq.process("Ew = E(0:,1)");
//		eq.process("Eb = E(0:,2)");
		
		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		DMatrixRMaj S = eq.lookupDDRM("S");
		
		DecimalFormat ff = new DecimalFormat("0.0000");
		
		// SEQUENCE: R W B B
		
		// 'B'  time = 4
//		eq.process("B4 = [ 1; 1]");
		
		// 'BB'  time = 3
		// Long form
		// B3(0) = T[0,0] * E[0,B] * 1 + T[0,1] * E[1,B] * 1
		// B3(0) = 0.6 * 0.3 * 1 + 0.4 * 0.3 * 1
//		eq.process("B30 = T(0,0) * E(0,2) * B4(0,0) + T(0,1) * E(1,2) * B4(0,0)");
//		System.out.println("B3(0): " + ff.format(eq.lookupDouble("B30")));
		// B3(1) = T[1,0] * E[0,B] * 1 + T[1,1] * E[1,B] * 1
		// B3(1) = 0.3 * 0.3 * 1 + 0.7 * 0.3 * 1
//		eq.process("B31 = T(1,0) * E(0,2) * B4(1,0) + T(1,1) * E(1,2) * B4(1,0)");
//		System.out.println("B3(1): " + ff.format(eq.lookupDouble("B31")));
		
		// Matrix form
//		eq.process("B3 = T * (Eb .* B4)");
//		eq.lookupDDRM("B3").print("%2.4f");
		
		// 'WBB'  time = 2
		// Long form
		// B2(0) = T[0,0] * E[0,B] * B3(0) + T[0,1] * E[1,B] * B3(1)
		// B2(0) = 0.6 * 0.3 * 0.3 + 0.4 * 0.3 * 0.3
//		eq.process("B20 = T(0,0) * E(0,2) * B30 + T(0,1) * E(1,2) * B31");
//		System.out.println("B2(0): " + ff.format(eq.lookupDouble("B20")));
		// B2(1) = T[1,0] * E[0,B] * B3(0) + T[1,1] * E[1,B] * B3(1)
		// B2(1) = 0.3 * 0.3 * 0.3 + 0.7 * 0.3 * 0.3
//		eq.process("B21 = T(1,0) * E(0,2) * B30 + T(1,1) * E(1,2) * B31");
//		System.out.println("B2(1): " + ff.format(eq.lookupDouble("B21")));
		
		// Matrix form
//		eq.process("B2 = T * (Eb .* B3)");
//		eq.lookupDDRM("B2").print("%2.4f");
		
		// 'RWBB' time = 1
		// Long form
		// B1(0) = T[0,0] * E[0,W] * B2(0) + T[0,1] * E[1,W] * B2(1)
		// B1(0) = 0.6 * 0.4 * 0.09 + 0.4 * 0.3 * 0.09
//		eq.process("B10 = T(0,0) * E(0,1) * B20 + T(0,1) * E(1,1) * B21");
//		System.out.println("B1(0): " + ff.format(eq.lookupDouble("B10")));
		// B1(1) = T[1,0] * E[0,W] * B2(0) + T[1,1] * E[1,W] * B2(1)
		// B1(1) = 0.3 * 0.4 * 0.09 + 0.7 * 0.3 * 0.09
//		eq.process("B11 = T(1,0) * E(0,1) * B20 + T(1,1) * E(1,1) * B21");
//		System.out.println("B1(1): " + ff.format(eq.lookupDouble("B11")));
		
		// Matrix form
//		eq.process("B1 = T * (Ew .* B2)");
//		eq.lookupDDRM("B1").print("%2.4f");
		
		// 'RWBB'  time = 0  (optional)
		// Long form
		// B0(0) = S[0,0] * E[0,R] * B1(0)
		// B0(0) = 0.8 * 0.3 * 0.0324
//		eq.process("B00 = S(0,0) * E(0,0) * B10");
//		System.out.println("B0(0): " + ff.format(eq.lookupDouble("B00")));
		// B0(1) = S[1,0] * E[1,R] * B1(1)
		// B0(1) = 0.2 * 0.4 * 0.0297
//		eq.process("B01 = S(1,0) * E(1,0) * B11");
//		System.out.println("B0(1): " + ff.format(eq.lookupDouble("B01")));
		
		// Matrix form       (optional)
//		eq.process("B0 = S .* Er .* B1");
//		eq.lookupDDRM("B0").print(%2.4f");
		
		int [] converter = { 0, 1, 2, 2 };
		String [] output = { "R", "W", "B"};
		
		Backward backward = new Backward();
		List<Pair<Integer, DMatrixRMaj>> list = backward.fit(converter, S, T, E);
		
		System.out.println(list.stream().map(p -> 
					"{" + output[p.getFirst()] + "} : " + "[" +
					ff.format(p.getSecond().get(0, 0)) + ", " +
					ff.format(p.getSecond().get(1, 0)) + "]"
				).collect(Collectors.joining(", ")));
		
	}
	
	private UnderFlowStrategy strategy = UnderFlowStrategy.NONE;
	
	public Backward() {}
	
	public Backward(UnderFlowStrategy strategy) {
		this.strategy = strategy;
	}
	
	@Override
	public List<Pair<Integer, DMatrixRMaj>> fit(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		Map<Integer, DMatrixRMaj> map = new HashMap<Integer, DMatrixRMaj>();
		
		for (int index = 0; index < E.numCols; index++) {
			map.put(index, CommonOps_DDRM.extractColumn(E, index, null));
		}
		
		
		List<Pair<Integer, DMatrixRMaj>> backward = new ArrayList<Pair<Integer, DMatrixRMaj>>();
		DMatrixRMaj res = new DMatrixRMaj(T.numRows, 1);
		for (int index = converter.length - 1; index >= 0; index--) {
			
			if (index == converter.length - 1) {
				CommonOps_DDRM.fill(res, 1.0);
				
				if (strategy.equals(UnderFlowStrategy.ENABLED)) {
					CommonOps_DDRM.scale(1.0 / CommonOps_DDRM.elementSum(res), res);
				}
			}
			else {
				DMatrixRMaj tmp = new DMatrixRMaj(T.numRows, 1);
				
				CommonOps_DDRM.elementMult(map.get(converter[index + 1]), res, tmp);
				CommonOps_DDRM.mult(T, tmp.copy(), tmp);
					
				if (strategy.equals(UnderFlowStrategy.ENABLED)) {
						double sums = CommonOps_DDRM.elementSum(tmp);
						CommonOps_DDRM.scale(1.0 / sums, tmp);
				}
			
				res = tmp;
			}	
			
			backward.add(0, new Pair<>(converter[index], res));
		}
		
		return backward;
		
	}
	
	@Override
	public double posterior(List<Pair<Integer, DMatrixRMaj>> list) {
		
		throw new RuntimeException("Not supported!");
	}

}
