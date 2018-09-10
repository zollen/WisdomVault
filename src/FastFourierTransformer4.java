import java.text.NumberFormat;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexFormat;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FastFourierTransformer4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(3);
		nf.setMaximumFractionDigits(3);

		// create complex format with custom number format
		// when one number format is used, both real and
		// imaginary parts are formatted the same
		ComplexFormat cf = new ComplexFormat(nf);
		
		
		double[] inputs = new double[4];
		inputs[0] = 2 * Math.cos(0 * 5 * 2 * Math.PI / 8) + Math.sin(0 * 1 * 2 * Math.PI / 8);
		inputs[1] = 2 * Math.cos(1 * 5 * 2 * Math.PI / 8) + Math.sin(1 * 1 * 2 * Math.PI / 8);
		inputs[2] = 2 * Math.cos(2 * 5 * 2 * Math.PI / 8) + Math.sin(2 * 1 * 2 * Math.PI / 8);
		inputs[3] = 2 * Math.cos(3 * 5 * 2 * Math.PI / 8) + Math.sin(3 * 1 * 2 * Math.PI / 8);
		
	
		System.out.println("f(x)=2cos(5x)+sin(x)");
		System.out.println("==================================");

		
		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] complx = transformer.transform(inputs, TransformType.FORWARD);
		
		Arrays.stream(complx).forEach(p -> { System.out.println("FFT ==> " + cf.format(p)); });
		
		// F4 DFT matrix
		// F4 = 
		//		[ 1,  1,  1,  1 ] 
		//		[ 1, -i, -1,  i ] 
		//		[ 1, -1,  1, -1 ] 
		//		[ 1,  i, -1, -i ] 
		Complex [][] data = new Complex[][] {
					 { new Complex(1, 0),  new Complex(1, 0),  new Complex(1, 0),  new Complex(1, 0) },
					 { new Complex(1, 0), new Complex(0, -1), new Complex(-1, 0), new Complex(0, 1)  },
					 { new Complex(1, 0), new Complex(-1, 0),  new Complex(1, 0), new Complex(-1, 0) },
					 { new Complex(1, 0),  new Complex(0, 1), new Complex(-1, 0), new Complex(0, -1) }
		};
		
		// sample inputs in Complex form
		Complex [][] in = new Complex[][] {
			{ new Complex(inputs[0], 0) },
			{ new Complex(inputs[1], 0) },
			{ new Complex(inputs[2], 0) },
			{ new Complex(inputs[3], 0) }		
		};
	
		FieldMatrix<Complex> m = MatrixUtils.createFieldMatrix(data);
		FieldMatrix<Complex> n = new Array2DRowFieldMatrix<Complex>(in);
		
		
		FieldMatrix<Complex> k = m.multiply(n);
		Complex [][] res = k.getData();
		
		for (int i = 0; i < 4; i++)
			System.out.println("DFT ==> " + cf.format(res[i][0]));
	}
}
