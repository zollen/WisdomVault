import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

public class DerivativeExercise1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 3 x^3 - 2 x^2 + 4 x + 5
		
		DerivativeStructure x = new DerivativeStructure(2, 2, 0, 2d);
		DerivativeStructure c1 = x.pow(3);
		DerivativeStructure c2 = x.pow(2);
		
		DerivativeStructure f = new DerivativeStructure(3d, c1, -2d, c2, 4d, x).add(5);
		DerivativeStructure g = c1.multiply(3).add(c2.multiply(-2)).add(x.multiply(4)).add(5);
		
		
		System.out.println("f        = " + f.getValue());
		System.out.println("g        = " + g.getValue());
		System.out.println("df/dx    = " + f.getPartialDerivative(1, 0));
		System.out.println("df/dy    = " + f.getPartialDerivative(0, 1));
		System.out.println("d2f/dx2  = " + f.getPartialDerivative(2, 0));
		System.out.println("d2f/dxdy = " + f.getPartialDerivative(1, 1));
		System.out.println("d2f/dy2  = " + f.getPartialDerivative(0, 2));
			
	}

}
