import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;

public class TestMe3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 1, 0, -1, 2; 0, 3, 1, -1; 2, 4, 0, 3; -3, 1, -1, 2 ]");
		
		DMatrixRMaj A = eq.lookupMatrix("A");
		DMatrixRMaj B = new DMatrixRMaj(4, 2);
		B.set(0, 0, 1); B.set(0, 1, 2);
		B.set(1, 0, 3); B.set(1, 1, -1);
		B.set(2, 0, 0); B.set(2, 1, -2);
		B.set(3, 0, 4); B.set(3, 1, 1);
		
		double [] [] data = { { 3, -2, 0, 5 }, {1, 0, -3, 4} };
		
		SimpleMatrix C = new SimpleMatrix(data);
		
		eq.alias(B, "B");
		eq.alias(C, "C");
		
		
		eq.process("M = 3 * (A * A * A) - 5 * ((B * C) * (B * C))");
		
		System.out.println(A);
		System.out.println(B);
		System.out.println(C);
		System.out.println(eq.lookupMatrix("M"));
		
		
		DMatrixRMaj K = new DMatrixRMaj(3, 3);
		CommonOps_DDRM.fill(K, 3);
		
		DMatrixRMaj I = CommonOps_DDRM.identity(3, 3);
		
		System.out.println(K);
		System.out.println(I);
		System.out.println("DET: " + CommonOps_DDRM.det(K));
		
		DMatrixRMaj reduced = new DMatrixRMaj(4, 4);
		System.out.println("RREF: " + CommonOps_DDRM.rref(A, 3, reduced));
		
		DMatrixRMaj transposed = new DMatrixRMaj(4, 4);
		CommonOps_DDRM.transpose(A, transposed);
		System.out.println(transposed);
		
		eq.process("J = A * A * A");
		
		System.out.println(eq.lookupMatrix("J"));
		
		DMatrixRMaj multi = new DMatrixRMaj(4, 4);
		ppow(A, 3, multi);
		System.out.println(multi);
		
		System.out.println(K);
		System.out.println("TRACE: " + CommonOps_DDRM.trace(K));
		
	
		eq.process("DAMN = [ 1, 0, 0, 0; 1./4, 1, 0, 0; 1./3, 1./3, 0, 0; 1./2, 1./2, 1./2, 1 ]");
		DMatrixRMaj damn = eq.lookupMatrix("DAMN");
		DMatrixRMaj inv = new DMatrixRMaj(4, 4);
		CommonOps_DDRM.invert(damn, inv);
		System.out.println(CommonOps_DDRM.det(damn));
	}
	
	private static void ppow(DMatrixRMaj a, int power, DMatrixRMaj f) {
		
		DMatrixRMaj b = new DMatrixRMaj(a.numRows, a.numCols);
		b.set(a);
	
		for (int i = 0; i < power - 1; i++) {
			CommonOps_DDRM.mult(a, b, f);
			b.set(f);
		}
		
	}

}
