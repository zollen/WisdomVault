import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.ejml.simple.ops.SimpleOperations_DDRM;

public class PseudoInverseExercise2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		
		eq.process("A = [ " +
							"  2,  1;" +
							"  0,  1;" +
							"  4,  1 " +
						"]");

/*		eq.process("A = [ " +
  							" 2,  1,  3;" +
  							" 1,  0, -1 " +
 						" ]");  */
		
		DMatrixRMaj A = eq.lookupDDRM("A");
		System.out.println("Rank(A): " + MatrixFeatures.rank(A));

		{
			eq.process("K1 = A' * A");
			EigenDecomposition eigenV = new EigenDecomposition(MatrixUtils.createRealMatrix(MatrixFeatures.array(eq.lookupDDRM("K1"))));
			DMatrixRMaj V = new DMatrixRMaj(eigenV.getV().getData());
			DMatrixRMaj W = new DMatrixRMaj(eigenV.getD().getData());
			
			for (int i = 0; i < (W.numRows < W.numCols ? W.numRows : W.numCols); i++) {
				W.set(i, i, Math.sqrt(W.get(i, i)));
			}
			
			eq.alias(V, "V");
			eq.alias(W, "W");
				
			eq.process("V1 = V(0:1, 0)");
			eq.process("V2 = V(0:1, 1)");
		
			eq.process("U1 = 1./" + W.get(0, 0) + " * A * V1");
			eq.process("U2 = 1./" + W.get(1, 1) + " * A * V2");
			
			
			DMatrixRMaj U1 = eq.lookupDDRM("U1");
			DMatrixRMaj U2 = eq.lookupDDRM("U2");
			DMatrixRMaj U3 = crossProducts(U1, U2);
			eq.alias(U3, "U3");
			
			eq.process("U = [ U1, U2, U3 ]");
				
			for (int i = 0; i < (W.numRows < W.numCols ? W.numRows : W.numCols); i++) {
				W.set(i, i, 1 / W.get(i, i));
			}
			
			eq.process("W = [ W , [ 0; 0 ] ]"); 
			eq.process("K = V * W * U'");
			
			System.out.println("A' A: " + eq.lookupDDRM("K"));

		}
		
		{
			
			eq.process("K2 = A * A'");
			EigenDecomposition eigenU = new EigenDecomposition(MatrixUtils.createRealMatrix(MatrixFeatures.array(eq.lookupDDRM("K2"))));
			DMatrixRMaj U = new DMatrixRMaj(eigenU.getV().getData());
			DMatrixRMaj W = new DMatrixRMaj(eigenU.getD().getData());
			eq.alias(U, "U");
			eq.alias(W, "W");
			
			eq.process("W = W(0:1,0:2)");
			
			for (int i = 0; i < (W.numRows < W.numCols ? W.numRows : W.numCols); i++) {
				W.set(i, i, Math.sqrt(W.get(i, i)));
			}
			
			eq.process("U1 = U(0:2,0)");
			eq.process("U2 = U(0:2,1)");
			eq.process("U3 = U(0:2,2)");
			
			eq.process("V1 = 1./" + W.get(0, 0) + " * A' * U1");
			eq.process("V2 = 1./" + W.get(1, 1) + " * A' * U2");
			
			eq.process("V = [ V1, V2 ]");
			
			for (int i = 0; i < (W.numRows < W.numCols ? W.numRows : W.numCols); i++) {
				W.set(i, i, 1 / W.get(i, i));
			}
			
			eq.process("K = V * W * U'");
			
			System.out.println("A * A': " + eq.lookupDDRM("K"));

		}

		
		{
			SimpleOperations_DDRM op = new SimpleOperations_DDRM();
			DMatrixRMaj B = new DMatrixRMaj(A.numRows, A.numCols);
			op.pseudoInverse(A, B);
			
			System.out.println("Direct: " + B);
			eq.alias(B, "B");
			
			eq.process("K = B * A");
			
			System.out.println("Pseudo(A) * A: " + eq.lookupDDRM("K"));
		}

	}
	
	public static DMatrixRMaj crossProducts(DMatrixRMaj v1, DMatrixRMaj v2) {
		
		Vector3D vec1 = new Vector3D(v1.getData());
		Vector3D vec2 = new Vector3D(v2.getData());
		
		Vector3D vec3 = Vector3D.crossProduct(vec1, vec2);
		
		double [] arr = vec3.toArray();
		double [][] data = new double[arr.length][1];
		
		for (int i = 0; arr != null && i < arr.length; i++) {
			data[i][0] = arr[i];
		}
		
		return new DMatrixRMaj(data);
		
	}

}
