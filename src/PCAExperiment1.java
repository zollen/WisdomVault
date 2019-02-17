import java.text.DecimalFormat;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class PCAExperiment1 {
	
	private static DecimalFormat formatter = new DecimalFormat("0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("D = [ " + 
							"  1,  1,  0;" + 
							"  0,  1,  1 " +
						"]");
	
		
		DMatrixRMaj D = eq.lookupDDRM("D");
		
		SingularValueDecomposition svd = new SingularValueDecomposition(
				MatrixUtils.createRealMatrix(MatrixFeatures.array(D)));
		
	
		DMatrixRMaj U = new DMatrixRMaj(svd.getU().getData());
		System.out.println("The Axis with the correct slope: " + U);
		
		// The slope of PC1(U) is [ sqrt(2)/2; -sqrt(2)/2 ]
		// The slope of PC2(U) is [ sqrt(2)/2;  sqrt(2)/2 ]
		
		// since AVG(D) = [ 2/3; 2/3 ]
		
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
		
		// L1: x - 4/3 = y
		DMatrixRMaj line1 = new DMatrixRMaj(2, 1);
		line1.set(0, 0, (double) 4/3);
		line1.set(1, 0, -1);
		DMatrixRMaj p1 = proj(line1, D);
		double v1 = variance(p1);
		System.out.println("Var(Proj(D, L1): " + formatter.format(v1) + "\nProj(D, L1): " + p1);

		// L2: x = y
		DMatrixRMaj line2 = new DMatrixRMaj(2, 1);
		line2.set(0, 0, 0);
		line2.set(1, 0, 1);
		DMatrixRMaj p2 = proj(line2, D);
		double v2 = variance(p2);
		System.out.println("Var(Proj(D, L2): " +  formatter.format(v2) + "\nProj(D, L2): " + p2);
		
		// Var(D) = Var(Proj(D), L1) + Var(Proj(D), L2)
		System.out.println("Var(Proj(D) =  Var(Proj(D), L1) + Var(Proj(D), L2): " + formatter.format(v1 + v2));
		
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
		
		return sum / (A.numCols - 1);
	}
	
	public static DMatrixRMaj proj(DMatrixRMaj line, DMatrixRMaj points) {
		
		// line:  ax + b = y
		// line: [ b; a ]
		
		DMatrixRMaj p = new DMatrixRMaj(points.numRows, points.numCols);
		
		DMatrixRMaj perp = perp(line);
					
		for (int col = 0; col < points.numCols; col++) {	
			// d = y - ax 
			double d = points.get(1, col) - (points.get(0, col) * perp.get(1, 0));
			
			double x = (line.get(0, 0) - d) / (perp.get(1, 0) - line.get(1, 0));
			
			// new y = a (new x) + b
			double y = line.get(1, 0) * x + line.get(0, 0);
			
			p.set(0, col, x);
			p.set(1, col, y);
		}
		
		return p;
	}
	
	public static DMatrixRMaj perp(DMatrixRMaj A) {
		
		DMatrixRMaj p = new DMatrixRMaj(A.numRows, A.numCols);
		
		p.set(0, 0, 0);
		p.set(1, 0, -1 * A.get(1, 0));
		
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
