import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class TestMe6 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("B = [ " + 
					" -1,  1,  2;" +
					" -1,  0, -1;" +
					" -1,  1,  3 " +
				"]"); 
		
		eq.process("C = [ " +
					"  1, -1,  2;" +
					"  0, -1, -1;" +
					"  1, -1,  3 " +
				"]");
		
		System.out.println("Rank(B): " + MatrixFeatures.rank(eq.lookupMatrix("B")));
		
		eq.process("Pbc = inv(C) * B");
		eq.process("Pcb = inv(B) * C");

		DMatrixRMaj Pbc = eq.lookupMatrix("Pbc");
		System.out.println("Pb->c: " + Pbc);
		
		DMatrixRMaj Pcb = eq.lookupMatrix("Pcb");
		System.out.println("Pc->b: " + Pcb);
	} 

}
