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

public class Forward implements HMMAlgothrim<DMatrixRMaj> {

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
		
//		eq.process("Er = E(0:,0)");
//		eq.process("Ew = E(0:,1)");
//		eq.process("Eb = E(0:,2)");
		
		DMatrixRMaj T = eq.lookupDDRM("T");
		DMatrixRMaj E = eq.lookupDDRM("E");
		DMatrixRMaj S = eq.lookupDDRM("S");
		
		DecimalFormat ff = new DecimalFormat("0.0000");
		
		// 'R'
//		eq.process("F1 = Er .* S");
//		eq.process("F10 = F1(0,0)");
//		eq.process("F11 = F1(1,0)");
//		eq.lookupDDRM("F1").print("%2.4f");
//		System.out.println("F1(0): " + ff.format(eq.lookupDouble("F10")));
//		System.out.println("F1(1): " + ff.format(eq.lookupDouble("F11")));
		
		// 'RW'                                         
		// Long form
		// F2(0) = E[0,W] * (T[0,0] * F1(0) + T[1,0] * F1(1))
		// F2(0) = 0.4 * (0.6 * 0.24 + 0.3 * 0.08)
//		eq.process("F20 = E(0,1) * (T(0,0) * F10 + T(1,0) * F11)");
//		System.out.println("F2(0): " + ff.format(eq.lookupDouble("F20")));
		// F2(1) = E[1,W] * (T[0,1] * F1(0) + T[1,1] * F1(1))
		// F2(1) = 0.3 * (0.4 * 0.24 + 0.7 * 0.08)
//		eq.process("F21 = E(1,1) * (T(0,1) * F10 + T(1,1) * F11)");
//		System.out.println("F2(1): " + ff.format(eq.lookupDouble("F21")));
		
		// Matrix form
		// Transposing T switch from -> to, to -> from  
//		eq.process("F2 = Ew .* (T' * F1)"); 
//		eq.lookupDDRM("F2").print("%2.4f");
		
		// 'RWB'                                          
		// Long form
		// F3(0) = E[0,B] * (T[0,0] * F2(0) + T[1,0] * F2(1))
		// F3(0) = 0.3 * (0.6 * 0.0672 + 0.3 * 0.0456)
//		eq.process("F30 = E(0,2) * (T(0,0) * F20 + T(1,0) * F21)");
//		System.out.println("F3(0): " + ff.format(eq.lookupDouble("F30")));
		// F3(1) = E[1,B] * (T[0,1] * F2(0) + T[1,1] * F2(1))
		// F3(1) = 0.3 * (0.4 * 0.0672 + 0.7 * 0.0456)
//		eq.process("F31 = E(1,2) * (T(0,1) * F20 + T(1,1) * F21)");
//		System.out.println("F3(1): " + ff.format(eq.lookupDouble("F31")));
		
		// Matrix form
		// Transposing T switch from -> to, to -> from   (Matrix form)
//		eq.process("F3 = Eb .* (T' * F2)"); 
//		eq.lookupDDRM("F3").print("%2.4f");
		
		// 'RWBB'                                            
		// Long form
		// F4(0) = E[0,B] * (T[0,0] * F3(0) + T[1,0] * F3(1))
		// F4(0) = 0.3 * (0.6 * 0.0162 + 0.3 * 0.0176)
//		eq.process("F40 = E(0,2) * (T(0,0) * F30 + T(1,0) * F31)");
//		System.out.println("F4(0): " + ff.format(eq.lookupDouble("F40")));
		// F4(1) = E[1,B] * (T[0,1] * F3(0) + T[1,1] * F3(1))
		// F4(1) = 0.3 * (0.4 * 0.0162 + 0.7 * 0.0176)
//		eq.process("F41 = E(1,2) * (T(0,1) * F30 + T(1,1) * F31)");
//		System.out.println("F4(1): " + ff.format(eq.lookupDouble("F41")));
		
		// Matrix form
		// Transposing T switch from -> to, to -> from    
//		eq.process("F4 = Eb .* (T' * F3)"); 
//		eq.lookupDDRM("F4").print("%2.4f");
		
		int [] converter = { 0, 1, 2, 2 };
		String [] output = { "R", "W", "B" };
		
		Forward forward = new Forward();
		List<Pair<Integer, DMatrixRMaj>> list = forward.fit(converter, S, T, E);
		
		System.out.println(list.stream().map(p -> 
					"{" + output[p.getKey()] + "} : " + "[" +
					ff.format(p.getValue().get(0, 0)) + ", " +
					ff.format(p.getValue().get(1, 0)) + "]"
				).collect(Collectors.joining(", ")));
	}
	
	private UnderFlowStrategy strategy = UnderFlowStrategy.NONE;
	
	public Forward() {}
	
	public Forward(UnderFlowStrategy strategy) {
		this.strategy = strategy;
	}
	
	@Override
	public List<Pair<Integer, DMatrixRMaj>> fit(int [] converter, DMatrixRMaj S, DMatrixRMaj T, DMatrixRMaj E) {
		
		Map<Integer, DMatrixRMaj> map = new HashMap<Integer, DMatrixRMaj>();
		
		for (int index = 0; index < E.numCols; index++) {
			map.put(index, CommonOps_DDRM.extractColumn(E, index, null));
		}
		
				
		List<Pair<Integer, DMatrixRMaj>> forward = new ArrayList<Pair<Integer, DMatrixRMaj>>();
		DMatrixRMaj res = new DMatrixRMaj(T.numRows, 1);
		for (int index = 0; index < converter.length; index++) {
			
			if (index == 0) {
				
				CommonOps_DDRM.elementMult(map.get(converter[index]), S, res);	
				
				if (strategy.equals(UnderFlowStrategy.ENABLED)) {
					CommonOps_DDRM.scale(1.0 / CommonOps_DDRM.elementSum(res), res);
				}
			}
			else {
				DMatrixRMaj tmp = new DMatrixRMaj(T.numRows, res.numCols);
				
				CommonOps_DDRM.multTransA(T, res, tmp);
				CommonOps_DDRM.elementMult(tmp, map.get(converter[index]));
				
				if (strategy.equals(UnderFlowStrategy.ENABLED)) {
					CommonOps_DDRM.scale(1.0 / CommonOps_DDRM.elementSum(tmp), tmp);
				}
				
				res = tmp;
			}	
			
			forward.add(new Pair<>(converter[index], res));
		}
		
		return forward;
	}
	
	@Override
	public double posterior(List<Pair<Integer, DMatrixRMaj>> list) {
		
		DMatrixRMaj last = list.get(list.size() - 1).getSecond();
		return CommonOps_DDRM.elementSum(last);
	}
}