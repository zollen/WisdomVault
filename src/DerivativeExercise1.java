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
		
		x = new DerivativeStructure(3, 2, 0, 2d);
		DerivativeStructure y = new DerivativeStructure(3, 2, 1, 3d);
		DerivativeStructure z = new DerivativeStructure(3, 2, 2, 4d);
		
		// k = 2 x^3 + 3 y^2 + z^4 + 5, x = 2, y = 3, z = 4
		
		DerivativeStructure k = new DerivativeStructure(2, x.pow(3), 3, y.pow(2), 1, z.pow(4)).add(5);
		System.out.println("k        = " + k.getValue());
		
		// dk/dx = 6 x^2
		System.out.println("dk/dx    = " + k.getPartialDerivative(1, 0, 0));
		// dk/dy = 6 y
		System.out.println("dk/dy    = " + k.getPartialDerivative(0, 1, 0));
		// dk/dz = 4 x^3
		System.out.println("dk/dy    = " + k.getPartialDerivative(0, 0, 1));
		
		
		DerivativeStructure _a = new DerivativeStructure(3, 1, 0, 2);
		DerivativeStructure _b = new DerivativeStructure(3, 1, 1, 3);
		DerivativeStructure _c = new DerivativeStructure(3, 1, 2, 4);
		
		DerivativeStructure _k = _a.multiply(5).multiply(5);
		DerivativeStructure _j = _b.multiply(5);
		k = _k.add(_j).add(_c);
		
		System.out.println("k        = " + k.getValue());
		
		System.out.println("dk/da    = " + k.getPartialDerivative(1, 0, 0));
		// dk/dy = 6 y
		System.out.println("dk/db    = " + k.getPartialDerivative(0, 1, 0));
		// dk/dz = 4 x^3
		System.out.println("dk/dc    = " + k.getPartialDerivative(0, 0, 1));
		
	}

}
