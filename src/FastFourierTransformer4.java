import java.text.NumberFormat;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexFormat;
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
		
		double[] inputs = new double[8];
		inputs[0] = 2 * Math.cos(0 * 5 * 2 * Math.PI / 8);
		inputs[1] = 2 * Math.cos(1 * 5 * 2 * Math.PI / 8);
		inputs[2] = 2 * Math.cos(2 * 5 * 2 * Math.PI / 8);
		inputs[3] = 2 * Math.cos(3 * 5 * 2 * Math.PI / 8);
		inputs[4] = 2 * Math.cos(4 * 5 * 2 * Math.PI / 8);
		inputs[5] = 2 * Math.cos(5 * 5 * 2 * Math.PI / 8);
		inputs[6] = 2 * Math.cos(6 * 5 * 2 * Math.PI / 8);
		inputs[7] = 2 * Math.cos(7 * 5 * 2 * Math.PI / 8);

	
		System.out.println("f(x)=2cos(5x)+sin(x)");
		System.out.println("==================================");

		
		Complex [] lres = LowPass(inputs, 2);
		Arrays.stream(lres).forEach(p -> { System.out.println("LowPass ==> " + cf.format(p)); });
		
		System.out.println("====================================");
		
		Complex [] hres = HighPass(inputs, 2);
		Arrays.stream(hres).forEach(p -> { System.out.println("HighPass ==> " + cf.format(p)); });

	}

	public static Complex[] LowPass(double[] signals, int threshold) {

		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] complx = transformer.transform(signals, TransformType.FORWARD);

		for (int i = 0; i < complx.length; i++) {
		
			if (i > complx.length / threshold) {
				complx[i] = new Complex(0, 0);
			}
		}

		return transformer.transform(complx, TransformType.INVERSE);
	}

	public static Complex[] HighPass(double[] signals, int threshold) {

		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] complx = transformer.transform(signals, TransformType.FORWARD);
		
		for (int i = 0; i < complx.length; i++) {
			
			if (i <= complx.length / threshold) {
				complx[i] = new Complex(0, 0);
			}

		}

		return transformer.transform(complx, TransformType.INVERSE);
	}

}
