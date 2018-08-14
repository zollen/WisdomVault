import org.ejml.equation.Equation;

public class DeterminantsProperties {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 1, 2, 3; 3, 1, 5; 1, 1, 1 ]");
		eq.process("B = [ 1, 2, 3; 2, 1, 2; 1, 1, 1 ]");
		eq.process("C = [ 1, 2, 3; 5, 2, 7; 1, 1, 1 ]");
		eq.process("D = [ 1, 2, 1; 3, 1, 2; 1, 1, 8 ]");
		eq.process("E = [ 1, 2, 4; 3, 1, 7; 1, 1, 9 ]");
		eq.process("F = [ 3, 0, 0; 0, 2, 0; 0, 0, 5 ]");
		eq.process("G = [ 1, 2, 3; 15, 5, 25; 1, 1, 1 ]");
		eq.process("H = [ 3, 2, 3; 9, 1, 5; 3, 1, 1 ]");
		eq.process("I = [ 1, 0, 0; 0, 1, 0; 0, 0, 1 ]");
		
		eq.process("A1 = det(A)");
		eq.process("B1 = det(B)");
		eq.process("C1 = det(C)");
		eq.process("D1 = det(D)");
		eq.process("E1 = det(E)");
		System.out.println("det(A): " + eq.lookupDouble("A1"));
		System.out.println("det(B): " + eq.lookupDouble("B1"));
		System.out.println("det(C): " + eq.lookupDouble("C1"));
		System.out.println("det(C) = det(B) + det(A) where one_row(C) = one_row(A) + one_row(D)");
		
		System.out.println("++++++++++++++++++++++++++++");
		
		System.out.println("det(A): " + eq.lookupDouble("A1"));
		System.out.println("det(D): " + eq.lookupDouble("D1"));
		System.out.println("det(E): " + eq.lookupDouble("E1"));
		System.out.println("det(E) = det(A) + det(D) where one_column(E) = one_column(A) + one_column(D)");
		
		System.out.println("++++++++++++++++++++++++++++");
		
		eq.process("G1 = det(G)");
		System.out.println("det(A): " + eq.lookupDouble("A1"));
		System.out.println("det(G): " + eq.lookupDouble("G1"));
		System.out.println("det(G) = 5 * det(A) where one_row(E) = one_row(A) * 5");
		
		eq.process("H1 = det(H)");
		System.out.println("det(A): " + eq.lookupDouble("A1"));
		System.out.println("det(H): " + eq.lookupDouble("H1"));
		System.out.println("det(H) = 3 * det(A) where one_column(H) = one_column(A) * 3");
		
		System.out.println("++++++++++++++++++++++++++++");
		
		eq.process("AB = det(A * B)");
		System.out.println("det(A): " + eq.lookupDouble("A1"));
		System.out.println("det(B): " + eq.lookupDouble("B1"));
		System.out.println("det(A): " + eq.lookupDouble("AB"));
		System.out.println("det(AB) = det(A) * det(D)");
		
		System.out.println("++++++++++++++++++++++++++++");
		
		eq.process("AINV = det(inv(A))");
		System.out.println("1/det(A): " + (double) (1/eq.lookupDouble("A1")));
		System.out.println("det(inv(A)): " + eq.lookupDouble("AINV"));
		System.out.println("det(inv(A)) = 1 / det(A)");
		
		System.out.println("++++++++++++++++++++++++++++");
		
		eq.process("AT = det(A')");
		System.out.println("det(A): " + eq.lookupDouble("A1"));
		System.out.println("det(A'): " + eq.lookupDouble("AT"));
		System.out.println("det(A') = det(A)");

		
		System.out.println("++++++++++++++++++++++++++++");
		
		eq.process("A2 = det(2 * A)");
		eq.process("A3 = 8 * det(A)");
		System.out.println("det(2 * A): " + eq.lookupDouble("A2"));
		System.out.println("8 * det(A): " + eq.lookupDouble("A3"));
		System.out.println("det(k * A) = k^n * det(A)");
		
		System.out.println("++++++++++++++++++++++++++++");
		
		eq.process("F1 = det(F)");
		System.out.println("det(F): " + eq.lookupDouble("F1"));
		System.out.println("det(DiagMatrix(F)) = d_11 * d_22 * d_33 ... * d_nn");
		
		System.out.println("++++++++++++++++++++++++++++");
		
		eq.process("I1 = det(I)");
		System.out.println("det(I): " + eq.lookupDouble("I1"));
		System.out.println("det(I) = 1");
	}

}
