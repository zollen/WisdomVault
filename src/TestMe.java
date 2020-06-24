import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class TestMe {
	
	// (a) Al + (b) N H4 Cl O4 ==> (c) Al2 O3 + (d) AL Cl3 + (e) N2 + (f) H2O
	
	// a = 2c + d
	// b = 2e
	// 4b = 2f
	// b = 3d
	// 4b = 3c + f
	
	// 10 Al + 6 H N4 Cl O4 ==> 4 Al2 O3 + 2 Al Cl3 + 3 N2 + 12 H2O
	
	public static void main(String[] args) throws Exception {
		
		Equation eq = new Equation();
		eq.process("A = [" +
					//    a|  b|  c|  d|  e|  f
						"-1,  0,  2,  1,  0,  0,   0;" +
						" 0, -1,  0,  0,  2,  0,   0;" +
						" 0, -4,  0,  0,  0,  2,   0;" +
						" 0, -1,  0,  3,  0,  0,   0;" +
						" 0, -4,  3,  0,  0,  1,   0 " +
						"]");
		
		{
			DMatrixRMaj A = eq.lookupDDRM("A");
			DMatrixRMaj reduced = new DMatrixRMaj(A.numRows, A.numCols);
			CommonOps_DDRM.rref(A, 6, reduced).print("%2f");
		}
		
		// | a |   | 5/6 |
		// | b |   | 1/2 |
		// | c |   | 1/3 |
		// | d | = | 1/6 | f
		// | e |   | 1/4 |
		// | f |   | 1   |
		
		// The solution space is a vector in a 6th dimension space.
		// Let f = 12, so we can have all integer values
		// then
		// | a |   | 5/6 |      | 10 |
		// | b |   | 1/2 |      | 6  |
		// | c |   | 1/3 |      | 4  |
		// | d | = | 1/6 | 12 = | 2  | 
		// | e |   | 1/4 |      | 3  |
		// | f |   | 1   |      | 12 |
	}
	
}
