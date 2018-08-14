import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.LinearSolverFactory_DDRM;
import org.ejml.equation.Equation;
import org.ejml.interfaces.linsol.LinearSolverDense;


public class TestMe4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ 1, -2, -4; 2, -5, -3; 3, -7, -7 ]");
		DMatrixRMaj A = eq.lookupMatrix("A");	
		DMatrixRMaj cloned = A.copy();
		
		System.out.println("RANK: " + MatrixFeatures.rank(A));
				
		RrefGaussJordanRowPivot processor = new RrefGaussJordanRowPivot();
		processor.reduce(A, A.numCols - 1);
		System.out.println(A);
		
		DMatrixRMaj reduced = new DMatrixRMaj(A.numRows, A.numCols);
		CommonOps_DDRM.rref(cloned, A.numCols - 1, reduced);	
		System.out.println(reduced);
		
		DMatrixRMaj x = new DMatrixRMaj(cloned.numRows, 1);
		CommonOps_DDRM.fill(x, 0);
		DMatrixRMaj y = new DMatrixRMaj(cloned.numRows, 1);
		CommonOps_DDRM.fill(y, 0);
	
		CommonOps_DDRM.extractColumn(cloned, cloned.numCols - 1, y);
		DMatrixRMaj AA = CommonOps_DDRM.extract(cloned, 0, cloned.numCols - 2, 0, cloned.numRows);
		
		LinearSolverDense<DMatrixRMaj> solver = LinearSolverFactory_DDRM.general(AA.numRows, AA.numCols);
		solver.setA(AA);
		solver.solve(y, x);
		
		System.out.println("======== X ============");
		System.out.println(x);
		
		DMatrixRMaj res = new DMatrixRMaj(cloned.numRows, 1);
		CommonOps_DDRM.mult(AA, x, res);
	
	//	System.out.println(y);
	//	System.out.println(res);
		
		if (MatrixFeatures.isEquals(y, res))
			System.out.println("===> Matched");
		else
			System.out.println("===> Not Matched");
	}

}
