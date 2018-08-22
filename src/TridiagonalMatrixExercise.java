import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;


public class TridiagonalMatrixExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 1, 1, 0, 0; 1, 1, 1, 0; 0, 1, 1, 1; 0, 0, 1, 1 ]");
	
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		System.out.println("<=== A: " + CommonOps_DDRM.det(A) + "===>");
		System.out.println(A);
		
		for (int i = 1; i <= 3; i++) {
			
			int [] rows = create(i);
			int [] cols = create(i);
			DMatrixRMaj a = new DMatrixRMaj(i, i);
			CommonOps_DDRM.extract(A, rows, i, cols, i, a);
		
			System.out.println("<=== A" + i + ": " + CommonOps_DDRM.det(a) + " ===>");
			System.out.println(a);
		}
	
	}
	
	private static int [] create(int count) {
		int [] counts = new int[count];
		
		for (int i = 0; i < counts.length; i++)
			counts[i] = i;
		
		return counts;
	}

}
