import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

public class Test11 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("P = [ 2; 0; 1 ]");
		eq.process("Q = [-1; 1; 1 ]");
		eq.process("R = [ 1; 1; 0 ]");
		
		eq.process("U = Q - P");
		eq.process("V = R - P");
		
		DMatrixRMaj U = eq.lookupMatrix("U");
		DMatrixRMaj V = eq.lookupMatrix("V");
		
		DMatrixRMaj W = CommonOps.crossProduct(U, V);
		
		eq.alias(W, "W");
		
		System.out.println("[U]: " + U);
		System.out.println("[V]: " + V);
		System.out.println("[W]: " + W);
		
		eq.process("c = dot(P, W)");
		System.out.println(eq.lookupDouble("c"));
		
		//Ans:  x + 3y +2z = 4
		
		eq.process("D = [ 6./9; 6./9; 6./9 ]");  
		eq.process("C = [ 3; 3; 3 ]");
		eq.process("L = C - D");
		eq.process("K = dot(L,W)/normF(W)^2 * W");
		eq.process("LEN1 = normF(L)");
		eq.process("LEN2 = normF(K)");
		
		System.out.println("Perpendicular Vector: " + eq.lookupMatrix("K"));
		System.out.println("LEN(Projecting Vector): " + eq.lookupDouble("LEN1"));
		System.out.println("LEN(Perpendicular Vector): " + eq.lookupDouble("LEN2"));
		
		eq.process("A = U");
		eq.process("B = [ 2; 6; 10 ]");
		System.out.println("B =====> " + eq.lookupMatrix("B"));
		
		eq.process("RES = dot(A, B)");
		System.out.println("RES: " + (int) eq.lookupDouble("RES"));
		
		DMatrixRMaj A = eq.lookupMatrix("A");
		DMatrixRMaj B = eq.lookupMatrix("B");
		DMatrixRMaj C = CommonOps.crossProduct(A, B);
		eq.alias(C, "C");
		eq.process("Q = [ A/normF(A), B/normF(B), C/normF(C) ]");
		DMatrixRMaj Q = eq.lookupMatrix("Q");
		System.out.println("Q: " + Q);
		System.out.println("Rank(Q): " + MatrixFeatures.rank(Q));
		System.out.println("Orth(Q): " + MatrixFeatures.isOrthogonal(Q, 0.00000001d));
		
		// ANS6.4: [ 1; 1; 0 ]
		
		eq.process("A = [" +
							" 1, 3, 2, -4;" +
							" 1, 1, 2, -4 " +
						"]");
		
		A = eq.lookupMatrix("A");
		CommonOps_DDRM.rref(A.copy(), 4, A);
		
		System.out.println(A);
	}
	
	
}
