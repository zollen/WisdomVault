package linearalgebra;
import org.ejml.data.DMatrixRMaj;

public class TestMe13 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(trigBasis(3));
	}
	
	public static DMatrixRMaj trigBasis(int k) {
		
		int n = k * 2;
		DMatrixRMaj dft = new DMatrixRMaj(n, n);
		
		for (int row = 0; row < n; row++)
			dft.set(row, 0, 1);
		
		for (int col = 1; col <= k; col++) {
			
			for (int row = 1; row <= n; row++) {
				
				dft.set(row - 1, 2 * col - 1, Math.cos(col * row * Math.PI / k));
				if (2 * col < n) {
					dft.set(row - 1, 2 * col, Math.sin(col * row  * Math.PI / k));
				}
			}
		}
		
		return dft;
	}

}
