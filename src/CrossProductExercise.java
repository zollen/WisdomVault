import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class CrossProductExercise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		
		eq.process("A = [ 1;  2;  3 ]");
		eq.process("B = [-1;  3; -2 ]");
		
		DMatrixRMaj A = eq.lookupMatrix("A");
		DMatrixRMaj B = eq.lookupMatrix("B");
		
		DMatrixRMaj C = crossProducts(A, B);
		
		System.out.println(C);
		
		C = CommonOps.crossProduct(A, B);
		
		System.out.println(C);
		
		eq.process("A = A'");
		eq.process("B = B'");
		
		
		C = CommonOps.crossProduct(A,  B);
		
		System.out.println(C);
		
		
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
