import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.linsol.LinearSolverDense;

public class TestMe1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 2, 3, -4 ; 5, 4, -4  ; -1, 7, 0 ]");
		eq.process("B = [5; 8; 1]");
		DMatrixRMaj A = eq.lookupDDRM("A");
		DMatrixRMaj x = new DMatrixRMaj(3, 1);
        DMatrixRMaj y = eq.lookupDDRM("B");
	//	CommonOps_DDRM.mult(A,x,y);
        
       
        System.out.println("======== A ============");
        System.out.println(A);
        System.out.println("======== X ============");
        System.out.println(x);
        System.out.println("======== Y ============");
        System.out.println(y);
       
		LinearSolverDense<DMatrixRMaj> solver = LinearSolverFactory_DDRM.general(A.numRows, A.numCols);
		solver.setA(A);
		solver.solve(y, x);
		
		System.out.println("======== RESULT ============");
		System.out.println(x);
		
		CommonOps_DDRM.fill(x, 0);
		CommonOps_DDRM.solve(A, y, x);  // quick & simple but not as good as above Factory
		
		System.out.println("======== RESULT ============");
		System.out.println(x);
		
	
	}

}
