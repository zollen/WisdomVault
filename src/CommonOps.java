import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

public class CommonOps {
	
	// Cross Product operation is for R^3 dimension only
	public static DMatrixRMaj crossProduct(DMatrixRMaj v1, DMatrixRMaj v2) {
		
		if (v1 != null && v2 != null && 
				v1.numRows == v2.numRows && 
				v1.numCols == v2.numCols &&
				((v1.numCols == 1 && v1.numRows == 3) || 
				 (v1.numCols == 3 && v1.numRows == 1))) {
			
			DMatrixRMaj vec1 = v1;
			DMatrixRMaj vec2 = v2;
			
			boolean transpose = false;
			if (vec1.numRows == 1 && vec1.numCols == 3) {
				transpose = true;
				DMatrixRMaj tmp1 = new DMatrixRMaj(3, 1);
				DMatrixRMaj tmp2 = new DMatrixRMaj(3, 1);
				CommonOps_DDRM.transpose(vec1, tmp1);
				CommonOps_DDRM.transpose(vec2, tmp2);
				vec1 = tmp1;
				vec2 = tmp2;
			}
		
			DMatrixRMaj v3 = new DMatrixRMaj(3, 1);
			
			v3.set(0, 0, (vec1.get(1, 0) * vec2.get(2, 0)) - (vec1.get(2, 0) * vec2.get(1, 0)));
			v3.set(1, 0, (vec1.get(2, 0) * vec2.get(0, 0)) - (vec1.get(0, 0) * vec2.get(2, 0)));
			v3.set(2, 0, (vec1.get(0, 0) * vec2.get(1, 0)) - (vec1.get(1, 0) * vec2.get(0, 0)));
			
			if (v3 != null && transpose)
				CommonOps_DDRM.transpose(v3);
			
			return v3;
		}
		
		return null;
	}
	
	public static double normA(DMatrixRMaj a) {
		
		double total = 0;
		
		for (int i = 0; i < a.numCols; i++) {
			
			for (int j = 0; j < a.numRows; j++) {
				
				double val = a.get(j, i);
				total += val * val;
			}
		}
		
		return Math.sqrt(total);
	}
	
	public static double normAbs(DMatrixRMaj a) {
		
		double total = 0;
		
		for (int i = 0; i < a.numCols; i++) {
			
			for (int j = 0; j < a.numRows; j++) {
				
				double val = a.get(j, i);
				total += Math.abs(val);
			}
		}
		
		return total;
	}

}
