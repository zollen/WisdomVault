import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;

public class RightInverse {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();

		eq.process("A = [" + 
							" 1, 2, 3;" + 
							" 4, 5, 6 " + 
						"]");

		DMatrixRMaj A = eq.lookupDDRM("A");
		System.out.println("Rank(A): " + MatrixFeatures.rank(A));
		
		{
			// YES RIGHT INVERSE: Rank(A) = Rank(AA') = 2 fill the rowpsace
			eq.process("K = A * A'");
		//	System.out.println("AA': " + eq.lookupDDRM("K"));
			eq.process("RIGHT = A' * inv(A * A')");
			eq.process("K = A * RIGHT");
			System.out.println("Right Inverse: " + eq.lookupDDRM("RIGHT"));

			// NO LEFT INVERSE: Rank(A) = Rank(A'A) = 2 does not fill colspace
			eq.process("K = A' * A");
		//	System.out.println("A'A: " + eq.lookupDDRM("K"));
			eq.process("LEFT = inv(A' * A) * A'");
			eq.process("K = LEFT * A");
			System.out.println("Left Inverse: " + eq.lookupDDRM("LEFT"));
		}
	}

	public static DMatrixRMaj crossProducts(DMatrixRMaj v1, DMatrixRMaj v2) {

		Vector3D vec1 = new Vector3D(v1.getData());
		Vector3D vec2 = new Vector3D(v2.getData());

		Vector3D vec3 = Vector3D.crossProduct(vec1, vec2);

		double[] arr = vec3.toArray();
		double[][] data = new double[arr.length][1];

		for (int i = 0; arr != null && i < arr.length; i++) {
			data[i][0] = arr[i];
		}

		return new DMatrixRMaj(data);

	}

}
