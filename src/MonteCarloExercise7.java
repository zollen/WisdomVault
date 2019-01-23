import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class MonteCarloExercise7 {
	
	private static final String [] RULES = { "U1", "U2", "U3"};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// (a, b, c, d) -> (a && b && !c && !d) || (!a && !b) || (!b && c);
		
		
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
	
		System.out.println("Number of Valuations: " + countCNF(F, 10000) + " +/- E");
	}
	
	public static double countCNF(DMatrixRMaj F, int N) {
		
		List<Integer> c = new ArrayList<Integer>();
		int numOfAtoms = F.numCols;
		int numOfRules = F.numRows;
		
		int Q = 0;
		for (int i = 0; i < numOfRules; i++) {
			int val = (int) Math.pow(2, (numOfAtoms - numOfAtoms(F, i)));
			c.add(val);
			
			System.out.println("Freq(" + RULES[i] + ") = " + (double) val);
			
			Q += val;
		}
		
		System.out.println("|Q|: " + Q);
		
		List<Double> p = new ArrayList<Double>();
		for (int i = 0; i < numOfRules; i++) {
			p.add((double) c.get(i) / Q);
			
			System.out.println("Prob(" + RULES[i] + ") = " + (double) c.get(i) / Q);
		}

		
		
		int count = 0;
		Random rand = new Random(System.nanoTime());
		for (int i = 0; i < N; i++) {
			
			int pos = rand.nextInt(numOfRules);
			
			// setting up parameters based on the random u
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
			
			
			// evaluate the parameters with other u's
			boolean duplicated = false;
			for (int j = 0; j < F.numRows; j++) {
				if (j != pos) {
					if (check(F, j, args)) {
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
	
	public static boolean check(DMatrixRMaj F, int row, Boolean [] args) {
		
		boolean res = true;
		
		for (int i = 0; i < F.numCols; i++) {
			
			if (F.get(row, i) == 1 && args[i] != true) {
				res = false;
				break;
			}

			if (F.get(row, i) == -1 && args[i] != false) {
				res = false;
				break;
			}
		}
		
		return res;
	}
	
	public static int numOfAtoms(DMatrixRMaj F, int row) {
				
		int numOfAtoms = 0;
		
		for (int i = 0; i < F.numCols; i++) {
			
			if (F.get(row, i) != 0)
				numOfAtoms++;
		}
			
		return numOfAtoms;
	}

}
