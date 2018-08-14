import org.apache.commons.math3.linear.MatrixUtils;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;


public class TestMe10 {
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		eq.process("A = [" +
							" -3, 1;" +
							" -1, 0;" +
							"  0, 5;" +
							"  2, 0;" +
							"  4, 1 " +
						"]");
		
		DMatrixRMaj A = eq.lookupMatrix("A");
	
		System.out.println(sineIntepolate(A));
	}
	
	
	
	public static DMatrixRMaj sineIntepolate(DMatrixRMaj input) {
		
		int total = input.numRows;
		
		DMatrixRMaj curve = new DMatrixRMaj(total, total);
		
		for (int row = 0; row < total; row++) {
						
			for (int col = 0; col < total; col++) {
				
				double val = 1;
				if (col > 0) {
					if (col % 2 == 0)
						val = Math.sin(input.get(row, 0) * (Math.PI/(col * 2)));
					else
						val = Math.cos(input.get(row, 0) * (Math.PI/(col * 2)));
				}
				
				curve.set(row, col, val);

			}
		}
		
		System.out.println("Rank(curve): " + MatrixFeatures.rank(curve));
		
		DMatrixRMaj output = new DMatrixRMaj(input.numRows, 1);
		
		for (int row = 0; row < total; row++) {
			output.set(row, 0, input.get(row, 1));
		}
		
		DMatrixRMaj K = SimpleMatrix.wrap(curve).combine(0, curve.numCols, SimpleMatrix.wrap(output)).getDDRM();
		DMatrixRMaj RES = new DMatrixRMaj(K.numRows, K.numCols);
		
		CommonOps_DDRM.rref(K.copy(), K.numCols - 1, RES);
		
				
		DMatrixRMaj m = new DMatrixRMaj(total, 1);
		m.setData(SimpleMatrix.wrap(RES).extractMatrix(0, total, total, total + 1).getDDRM().getData());

		return m;
	}
	
	public static DMatrixRMaj polyIntepolate(DMatrixRMaj input) {
		
		int total = input.numRows;
		
		DMatrixRMaj curve = new DMatrixRMaj(total, total);
		
		for (int row = 0; row < total; row++) {
			
			int power = total - 1;
			
			for (int col = 0; col < total; col++) {
				
				double val = Math.pow(input.get(row, 0), power--);
				
				curve.set(row, col, val);				
			}
		}
		
		DMatrixRMaj output = new DMatrixRMaj(input.numRows, 1);
		
		for (int row = 0; row < total; row++) {
			output.set(row, 0, input.get(row, 1));
		}
		
		DMatrixRMaj K = SimpleMatrix.wrap(curve).combine(0, curve.numCols, SimpleMatrix.wrap(output)).getDDRM();
		DMatrixRMaj RES = new DMatrixRMaj(K.numRows, K.numCols);
		
		CommonOps_DDRM.rref(K.copy(), K.numCols - 1, RES);
				
		DMatrixRMaj m = new DMatrixRMaj(total, 1);
		m.setData(SimpleMatrix.wrap(RES).extractMatrix(0, total, total, total + 1).getDDRM().getData());
	
		return m;
	}
	
	public static DMatrixRMaj tempDist(double tl, double tr, double tt, double tb, int size) {
		
		int dim = size * size;
		DMatrixRMaj matrix = new DMatrixRMaj(MatrixUtils.createRealIdentityMatrix(dim).getData());
		
		for (int row = 0; row < dim; row++) {
			
			int ty = row / size;
			int tx = row % size;
			
			for (int col = 0; col < dim; col++) {
			
				double val = 0;
			
				if (row == col)
					continue;
					
				int y = getRow(col, size);
				int x = getCol(col, size);
				
				
				if (x - 1 == tx && y == ty)
					val = -0.25;
				if (x + 1 == tx && y == ty)
					val = -0.25;
				if (y - 1 == ty && x == tx)
					val = -0.25;
				if (y + 1 == ty && x == tx)
					val = -0.25;
				
				
			
				matrix.set(row, col, val);
			}
		}
		
		DMatrixRMaj b = new DMatrixRMaj(size * size, 1);
		
		for (int i = 0; i < size * size; i++) {
			
			int row = getRow(i, size);
			int col = getCol(i, size);
			
			if (row == 0 && col == 0)  // top left corner
				b.set(i, 0, (tt + tl) / 4);
			else
			if (row == 0 && col == size - 1) // top right corner
				b.set(i, 0, (tt + tr) / 4);
			else
			if (row == size - 1 && col == 0) // bottom left corner
				b.set(i, 0, (tb + tl) / 4);
			else
			if (row == size - 1 && col == size - 1)  // bottom right corner
				b.set(i, 0, (tb + tr) / 4);
			else
			if (row == 0 && col > 0 && col < size - 1) // top edge
				b.set(i, 0, tt / 4);
			else
			if (row == size - 1 && col > 0 && col < size - 1) // bottom edge
				b.set(i, 0, tb / 4);
			else
			if (row > 0 && row < size - 1 && col == 0) // left edge
				b.set(i, 0, tl / 4);
			else
			if (row > 0 && row < size - 1 && col == size - 1) // right edge
				b.set(i, 0, tr / 4);
			else
				b.set(i, 0, 0);
		}
		
		DMatrixRMaj K = SimpleMatrix.wrap(matrix).combine(0, matrix.numCols, SimpleMatrix.wrap(b)).getDDRM();
		DMatrixRMaj RES = new DMatrixRMaj(K.numRows, K.numCols);
	
		CommonOps_DDRM.rref(K.copy(), K.numCols - 1, RES);
		
		DMatrixRMaj m = new DMatrixRMaj(size, size);
		m.setData(SimpleMatrix.wrap(RES).extractMatrix(0, size * size, size * size, size * size + 1).getDDRM().getData());
		return m;
	}
	

	public static int getRow(int index, int size) {
		return index / size;
	}
	
	public static int getCol(int index, int size) {
		return index % size;
	}
}
