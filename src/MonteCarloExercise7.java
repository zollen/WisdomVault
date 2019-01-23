import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class MonteCarloExercise7 {
	
	private static final String [] RULES = { "U1", "U2", "U3"};
	
	
	@FunctionalInterface
	interface Function<A, B, C, D, E> {
	    public E apply(A a, B b, C c, D d);
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Function<Boolean, Boolean, Boolean, Boolean, Boolean> df = 
				(a, b, c, d) -> (a && b && !c && !d) || (!a && !b) || (!b && c);
		df.apply(true, true, true, true);
		
		// F[i,j]=1 if j is a conjunct in ui
		// F[i,j]=-1 if not(j) is a conjunt in ui
		// F[i,j]=0 if j does not appear in ui
			
		Equation eq = new Equation();
		eq.process("F = [ " + 
						/*  a,  b,  c,  d */
		     /* u1 */	"   1,  1, -1, -1;" + 
			 /* u2 */	"  -1, -1,  0,  0;" +
		     /* u3 */	"   0, -1,  1,  0 " +
						"]");
		
		DMatrixRMaj F = eq.lookupDDRM("F");
		
		List<Function<Boolean, Boolean, Boolean, Boolean, Boolean>> u =
				new ArrayList<Function<Boolean, Boolean, Boolean, Boolean, Boolean>>();
		u.add((a, b, c, d) -> (a && b && !c && !d)); 
		u.add((a, b, c, d) -> (!a && !b)); 
		u.add((a, b, c, d) -> (!b && c));
		
		List<Integer> r = new ArrayList<Integer>();
		r.add(4); // 4 atoms
		r.add(2); // 2 atoms
		r.add(2); // 2 atoms		
		
		System.out.println("Number of Valuations: " + countCNF(u, r, F, 10000) + " +/- E");
	}
	
	public static double countCNF(List<Function<Boolean, Boolean, Boolean, Boolean, Boolean>> u, 
			List<Integer> r, DMatrixRMaj F, int N) {
		
		List<Integer> c = new ArrayList<Integer>();
		int numOfAtoms = F.numCols;
		
		int Q = 0;
		for (int i = 0; i < r.size(); i++) {
			int val = (int) Math.pow(2, (numOfAtoms - r.get(i)));
			c.add(val);
			
			Q += val;
		}
		
		
		List<Double> p = new ArrayList<Double>();
		for (int i = 0; i < r.size(); i++) {
			p.add((double) c.get(i) / Q);
		}

		
		
		int count = 0;
		Random rand = new Random(System.nanoTime());
		for (int i = 0; i < N; i++) {
			
			int pos = rand.nextInt(r.size());
			
			// setting up parameters based on the random DNF(u)
			Boolean [] args = new Boolean[F.numCols];
			for (int j = 0; j < F.numCols; j++) {
				if (F.get(pos, j) != 0) {
					if (F.get(pos, j) == 1) {
						args[j] = true;
					}
					else
					if (F.get(pos, j) == -1) {
						args[j] = false;
					}
				}
				else {
					args[j] = rand.nextBoolean();
				}
			}
			
			
			// evaluate the parameters with other DNF(others)
			boolean duplicated = false;
			for (int j = 0; j < F.numRows; j++) {
				if (j != pos) {
					if (u.get(j).apply(args[0], args[1], args[2], args[3])) {
						duplicated = true;
						break;
					}
				}
			}
			
			if (!duplicated) {
				count++;
			}
			
		}
		

		return (double) count / N * Q;
	}
	
	public static int myAttempt(DMatrixRMaj F) {
		
		DMatrixRMaj Q = new DMatrixRMaj(100, F.numCols);
	
		for (int row = 0, trow = 0; row < F.numRows; row++) {
			
			int numOfZeros = numOfZeros(F, row);
			int size = (int) Math.pow(2, numOfZeros);
			
			for (int comb = 0; comb < size; comb++) {
				String bin = Integer.toBinaryString(comb);
				while (bin.length() < numOfZeros)
					bin = "0" + bin;
				
				if (numOfZeros == 0) 
					bin = "";
				
				copyRow(row, F, trow, Q);
				
				for (int col = 0, pos = 0; col < Q.numCols && pos < bin.length(); col++) {

					if (Q.get(trow, col) == 0) {
						char flag = bin.charAt(pos++);
						Q.set(trow, col, (flag == '0') ? -1 : 1);
					}
				}
				
				trow++;
			}	
		}
		
		Set<String> set = new HashSet<String>();
		for (int row = 0; row < Q.numRows; row++) {
			
			StringBuilder builder = new StringBuilder();
			for (int col = 0; col < Q.numCols; col++) {
				if (Q.get(row, col) == 1) {
					builder.append("1");
				}
				else
				if (Q.get(row, col) == -1) {
					builder.append("0");
				}
			}
			
			if (builder.length() > 0) {
				set.add(builder.toString());
			}
		}
		
		Map<String, List<Integer>> R = new HashMap<String, List<Integer>>();
		
		for (String flags : set) {
			
			for (int row = 0; row < F.numRows; row++) {
				if (check(F, row, flags)) {
					
					List<Integer> list = R.get(flags);
					if (list == null) {
						list = new ArrayList<Integer>();
						R.put(flags,  list);
					}
					list.add(row);
				}
			}
		}
		
		for (String flags : R.keySet()) {
			
			List<Integer> nums = R.get(flags);
			String all = nums.stream().map(p -> RULES[p]).collect(Collectors.joining(", "));
			System.out.println(flags + " ===> " + all);			
		}
		
		
		return R.size();
	}
	
	public static boolean check(DMatrixRMaj F, int row, String data) {
		
		for (int i = 0; i < F.numCols; i++) {
			
			char flag = data.charAt(i);
			
			if (F.get(row, i) == 1 && flag != '1') {
				return false;
			}
			else
			if (F.get(row, i) == -1 && flag != '0') {
				return false;
			}
		}
		
		return true;
	}
	
	public static void copyRow(int row, DMatrixRMaj src, int trow, DMatrixRMaj dest) {
		
		for (int i = 0; i < src.numCols; i++) {
			dest.set(trow, i, src.get(row, i));
		}
	}
	
	public static int numOfZeros(DMatrixRMaj F, int row) {
				
		int numOfZeros = 0;
		
		for (int i = 0; i < F.numCols; i++) {
			
			if (F.get(row, i) == 0)
				numOfZeros++;
		}
			
		return numOfZeros;
	}

}
