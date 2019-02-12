import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class Experiment1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [ " + 
							"  1,  1,  0;" + 
							"  0,  1,  1 " +
						"]");
	
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		
		SingularValueDecomposition svd = new SingularValueDecomposition(
				MatrixUtils.createRealMatrix(MatrixFeatures.array(A)));
		
	
		DMatrixRMaj U = new DMatrixRMaj(svd.getU().getData());
	
		System.out.println(variance(A));
	
		
		// The slope of PC1 is [ sqrt(2)/2; -sqrt(2)/2 ]
		// The slope of PC2 is [ sqrt(2)/2;  sqrt(2)/2 ]
		
		// since AVG(A) = [ 2/3; 2/3 ]
		
		// PC1: ax + b = y
		// -1x + b = y
		// -1(2/3) + b = 2/3,  so b = 4/3
		// The axis of PC1: -x + 4/3 = y
		
		// PC2: ax + b = y
		// 1x + b = y
		// 1(2/3) + b = 2/3, so b = 0
		// The axis of PC2: x = y
		
		// A=(0,1)
		// L: x + y = 4/3
		// ⊥(L): x = y
		// Point A lies on ⊥(L): ax + b = c, (0) + b = 1, b = 1
		// L1: x + 1 = y
		// The intersection point between L and L1: x + 1 = 4/3 − x
		// The intersection point:(1/6, 7/6) 

		// B=(1,1)
		// Point B lies on ⊥(L): ax + b = c, (1) + b = 1, b = 0
		// L2: x = y
		// The intersection point between L and L2: x = 4/3 − x
		// The intersection point:(2/3, 2/3)

		// C=(1,0)
		// Point C lies on ⊥(L): ax + b = c, (1) + b = 0, b = −1
		// L3: x − 1 = y
		// The intersection point between L and L3: x − 1 = 4/3 − x
		// The intersection point:(7/6, 1/6)
		
	}
	
	public static double variance(DMatrixRMaj A) {
		
		double sum = 0d;
		
		DMatrixRMaj avg = avg(A);
		
		for (int col = 0; col < A.numCols; col++) {
				
			for (int row = 0; row < A.numRows; row++) {
			
				double diff = A.get(row, col) - avg.get(row, 0);
				
				sum += Math.abs(diff * diff);
			}
		}
		
		return sum / A.numCols;
	}
	
	public static DMatrixRMaj proj(DMatrixRMaj U, DMatrixRMaj A) {
		
		DMatrixRMaj p = new DMatrixRMaj(A.numRows, A.numCols);
				
		
		
		
		return p;
	}
	
	public static DMatrixRMaj avg(DMatrixRMaj A) {
		
		DMatrixRMaj avg = new DMatrixRMaj(A.numRows, 1);
		
		for (int row = 0; row < A.numRows; row++) {
			
			double val = 0d;
			for (int col = 0; col < A.numCols; col++) {
				val += A.get(row, col);
			}
			
			avg.set(row, 0, val / A.numCols);
		}
		
		
		return avg;
				
	}

}
