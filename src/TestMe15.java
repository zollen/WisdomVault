import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class TestMe15 {
	
	private static DecimalFormat formatter = new DecimalFormat("0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double [] inputs = {
				0,
				Math.PI/6,
				Math.PI/3,
				Math.PI/2,
				2 * Math.PI/3,
				5 * Math.PI/6,
				Math.PI,
				4 * Math.PI/3
		};
		
		double [] outputs = {
				Math.sin(inputs[0]),
				Math.sin(inputs[1]),
				Math.sin(inputs[2]),
				Math.sin(inputs[3]),
				Math.sin(inputs[4]),
				Math.sin(inputs[5]),
				Math.sin(inputs[6]),
				Math.sin(inputs[7])
		};
		
		
		System.out.println("Input:     [ " + Arrays.stream(inputs).mapToObj( formatter::format ).collect(Collectors.joining(", ")) + " ]");
		
		System.out.println("Amplitude: [ " +  Arrays.stream(outputs).mapToObj( formatter::format ).collect(Collectors.joining(", ")) + " ]");
	   
		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex [] res = transformer.transform(inputs, TransformType.FORWARD);
		
		System.out.println("           [ " +  Arrays.stream(res).mapToDouble( Complex::getReal ).mapToObj( formatter::format ).collect(Collectors.joining(", ")) + " ]");
		System.out.println("           [ " +  Arrays.stream(res).mapToDouble( Complex::getImaginary ).mapToObj( formatter::format ).collect(Collectors.joining(", ")) + " ]");
		

	}
	
	

}
