package machinelearning;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;

import linearalgebra.MatrixFeatures;

public class PCAExperiment2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Consider the following set of three-dimensional points
		// D = {(0,2,1), (0,4,3), (1,4,5), (1,8,6), (1,8,10), (4,8,14), (5,9,13)}

		// Perform a PCA with PC1 and PC2
		
		Equation eq = new Equation();
		// (0, 2, 1), (0, 4, 3), (1, 4, 5), (1, 8, 6), (1, 8, 10), (4, 8, 14), (5, 9, 13)
		eq.process("A = [" +
						" 0, 0, 1, 1,  1,  4,  5;" +
						" 2, 4, 4, 8,  8,  8,  9;" +
						" 1, 3, 5, 6, 10, 14, 13 " +
					"]");
		
		// scrattor plot SP(x, y), SP(x, z) and SP(y, z)
		// SP(y, z) has more correlation between y and z than other SP's.
		// Let's use the information of y and z!
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		int [] rows = { 1, 2 };
		int [] cols = { 0, 1, 2, 3, 4, 5, 6  };
		A = copy(A, rows, cols);
		
		System.out.println("SP(y, z): " + A);
		
		A = translate(avg(A), A);
		
		System.out.println("Translate(avg(A), SP(y, z)): " + A);

		// convert both A and U to 2 dimension
		SingularValueDecomposition svd = new SingularValueDecomposition(
		
				MatrixUtils.createRealMatrix(MatrixFeatures.array(A)));
		
		DMatrixRMaj U = new DMatrixRMaj(svd.getU().getData());
		
		System.out.println("EigenVectors: " + U);
		
		DMatrixRMaj PC1 = copy(U, 0);
		DMatrixRMaj PC2 = copy(U, 1);
		
		DMatrixRMaj projpc1 = proj(PC1, A);
		DMatrixRMaj projpc2 = proj(PC2, A);
		
		System.out.println("Proj(PC1, A): " + projpc1);
		System.out.println("Proj(PC2, A): " + projpc2);
		
		DMatrixRMaj center = new DMatrixRMaj(2, 1);
		// Caution: calculating SS and Variance only make sense with projected lines, *not* projected planes.
		// Projected planes uses the normal as the plane's equation, which may confuse user
		// perspective. The EignVectors are always vectors(lines), not planes!
		
		System.out.println("SS(proj1): " + ss(center, projpc1));
		System.out.println("SS(proj2): " + ss(center, projpc2));
		
		System.out.println("Var(proj1): " + variance(center, projpc1));
		System.out.println("Var(proj2): " + variance(center, projpc2));

		System.out.println("Normalized Coords: " + 
						combine(dists(center, 0, projpc1), dists(center, 1, projpc2)));
		
	}
	
	public static DMatrixRMaj combine(DMatrixRMaj x, DMatrixRMaj y) {

		DMatrixRMaj merged = new DMatrixRMaj(2, x.numRows);
		
		for (int col = 0; col < x.numRows && col < y.numRows; col++) {
			
			merged.set(0, col, x.get(col, 0));
			merged.set(1, col, y.get(col, 0));
		}
		
		return merged;
	}
	
	public static DMatrixRMaj dists(DMatrixRMaj center, int checked, DMatrixRMaj projected) {
		
		DMatrixRMaj B = new DMatrixRMaj(projected.numCols, 1);
		
		for (int col = 0; col < projected.numCols; col++) {
			DMatrixRMaj T = copy(projected, col);
			B.set(col, 0, dist(center, T) * (projected.get(checked, col) >= 0 ? 1 : -1));
		}
		
		return B;
	}
	
	public static double ss(DMatrixRMaj center, DMatrixRMaj A) {
		
		double sum = 0d;
		
		for (int col = 0; col < A.numCols; col++) {
			
			DMatrixRMaj target = copy(A, col);
				
			double dist = dist(center, target);
			
			sum += dist * dist;
		}
		
		return sum;
	}
	
	public static DMatrixRMaj proj(DMatrixRMaj projected, DMatrixRMaj points) {
		
		if (points.numRows > 2)
			return proj3D(projected, points);
		
		return proj2D(projected, points);
	}
	
	public static DMatrixRMaj proj3D(DMatrixRMaj plane, DMatrixRMaj points) {
		
		DMatrixRMaj pts = new DMatrixRMaj(points.numRows, points.numCols);
		// Plane: ax + by + cz + d
		// Point: (a, b, c)
		
		for (int col = 0; col < points.numCols; col++) {
			
			double d = plane.numRows > 3 ? plane.get(3, col) : 0;
		
			double t = -1 * (plane.get(0, 0) * points.get(0, col) + 
					plane.get(1, 0) * points.get(1, col) +
					plane.get(2, 0) * points.get(2, col) + d) /
						(plane.get(0, 0) * plane.get(0, 0) + 
							plane.get(1, 0) * plane.get(1, 0) +
							plane.get(2, 0) * plane.get(2, 0));
		
		
			pts.set(0, col, points.get(0, col) + plane.get(0, 0) * t); 
			pts.set(1, col, points.get(1, col) + plane.get(1, 0) * t); 
			pts.set(2, col, points.get(2, col) + plane.get(2, 0) * t); 
		}
		
		return pts;
	}
	
	public static DMatrixRMaj proj2D(DMatrixRMaj line, DMatrixRMaj points) {
		
		// line:  ax + b = y
		// line: [ x(a); y(a) ]
		
		DMatrixRMaj p = new DMatrixRMaj(points.numRows, points.numCols);
		
		DMatrixRMaj perp = perp2D(line);
	
		double slope1 = line.get(1, 0) / line.get(0, 0);
		double slope2 = perp.get(1, 0) / perp.get(0, 0); 
			
		for (int col = 0; col < points.numCols; col++) {	
			// b = y - ax 
			double b = points.get(1, col) - (points.get(0, col) * slope2);

			// a_perp * x + b = a_orig * x, x = b / (a_orig - a_perp)
			double x = b / (slope1 - slope2);
			
			// new y = a_orig (new x)
			double y = slope1 * x;
			
			p.set(0, col, x);
			p.set(1, col, y);
		}
		
		return p;
	}
	
	public static DMatrixRMaj perp2D(DMatrixRMaj A) {
		
		DMatrixRMaj c = new DMatrixRMaj(A.numRows, A.numCols);
		DMatrixRMaj pp = new DMatrixRMaj(2, 2);
		pp.set(0, 0, 0);
		pp.set(1, 0, 1);
		pp.set(0, 1, -1);
		pp.set(1, 1, 0);
		
		CommonOps_DDRM.mult(pp, A, c);
		
		return c;
	}
	
	public static DMatrixRMaj translate(DMatrixRMaj T, DMatrixRMaj A) {
		
		DMatrixRMaj B = new DMatrixRMaj(A.numRows, A.numCols);
		
		for (int col = 0; col < A.numCols; col++) {
			
			for (int row = 0; row < A.numRows; row++) {
				B.set(row, col, A.get(row, col) - T.get(row, 0));		
			}
		}
		
		return B;
	}
	
	public static double variance(DMatrixRMaj avg, DMatrixRMaj A) {
		
		return ss(avg, A) / (A.numCols - 1);
	}
	
	public static double dist(DMatrixRMaj a, DMatrixRMaj b) {
		
		return Math.sqrt(
				(b.get(0, 0) - a.get(0, 0)) * (b.get(0, 0) - a.get(0, 0)) + 
				(b.get(1, 0) - a.get(1, 0)) * (b.get(1, 0) - a.get(1, 0)) 
				);	
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
	
	public static DMatrixRMaj copy (DMatrixRMaj A, int col) {
		
		DMatrixRMaj B = new DMatrixRMaj(A.numRows, 1);
		
		for (int i = 0; i < A.numRows; i++)
			B.set(i, 0, A.get(i, col));
		
		return B;
	}
	
	public static DMatrixRMaj copy(DMatrixRMaj A, int [] rows, int [] cols) {
		
		DMatrixRMaj B = new DMatrixRMaj(rows.length, cols.length);
		
		for (int col = 0; col < cols.length; col++) {
			for (int row = 0; row < rows.length; row++) {
				B.set(row, col, A.get(rows[row], cols[col]));
			}
		}
		
		return B;
	}
	
}
