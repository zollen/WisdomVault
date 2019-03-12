package linearalgebra;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class OrthogonalExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 1, 1, 1, 1; -1, 3, 2, 1; 3, 2, 1, 4 ]");
		eq.process("T = A'");
		DMatrixRMaj A = eq.lookupDDRM("A");
		DMatrixRMaj T = eq.lookupDDRM("T");
		
		System.out.println("<=== A ====>");
		System.out.println(A);
		System.out.println("<=== T ====>");
		System.out.println(T);
		
		eq.process("N = [ -4./5; -7./5; 6./5; 1 ]"); 
		DMatrixRMaj N = eq.lookupDDRM("N");
		
		System.out.println("<=== N(A) ===>");
		System.out.println(N);
		
		DMatrixRMaj c = new DMatrixRMaj(3, 1);
		CommonOps_DDRM.mult(A, N, c);
		
		System.out.println("<=== Verification of coefficient ===>");
		System.out.println(c);
		
	}

}
