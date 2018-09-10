import org.ejml.equation.Equation;

public class FourierMatrixDefinition {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Equation eq = new Equation();
		// w = e^(i * k * 2pi/n)
		// w = [ 1,   1,    1,    1,    1,    1 ]
		//	   [ 1,   w,  w^2,  w^3,  w^4,  w^5 ]
		// 	   [ 1, w^2,  w^4,  w^6,  w^8, w^10 ]
		//     [ 1, w^3,  w^6,  w^9, w^12, w^15 ]
		//     [ 1, w^4,  w^8, w^12, w^16, w^20 ]
		//     [ 1, w^5, w^10, w^15, w^20, w^25 ]
		//
		
		
		eq.process("F4 = [" +
						"  1,  1,  1,  1;" +
						"  1, -i, -1,  i;" +
						"  1, -1,  1, -1;" +
						"  1,  i, -1, -i " +
					"]");
		
		eq.process("F6 = [" +
						" 1,          1,          1,  1,          1,          1;" +
						" 1,  e^(ipi/3), e^(i2pi/3), -1, e^(i4pi/3), e^(i5pi/3);" +
						" 1, e^(i2pi/3), e^(i4pi/3),  1, e^(i2pi/3), e^(i4pi/3);" +
						" 1,         -1,          1, -1, 		  1,      	 -1;" +
						" 1, e^(i4pi/3), e^(i2pi/3),  1, e^(i4pi/3), e^(i2pi/3);" +
						" 1, e^(i5pi/3), e^(i4pi/3), -1, e^(i2pi/3),  e^(ipi/3) " +
					"]");
		
		
		
	}
	
	

}
