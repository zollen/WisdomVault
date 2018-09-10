import java.text.NumberFormat;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexFormat;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FastFourierTransformer5 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(3);
		nf.setMaximumFractionDigits(3);
		ComplexFormat cf = new ComplexFormat(nf);
		
		Complex[][] inputs = new Complex[][] {
			{ new Complex(2 * Math.cos(0 * 5 * 2 * Math.PI / 8) + Math.sin(0 * 1 * 2 * Math.PI / 8), 0) },
			{ new Complex(2 * Math.cos(1 * 5 * 2 * Math.PI / 8) + Math.sin(1 * 1 * 2 * Math.PI / 8), 0) },
			{ new Complex(2 * Math.cos(2 * 5 * 2 * Math.PI / 8) + Math.sin(2 * 1 * 2 * Math.PI / 8), 0) },
			{ new Complex(2 * Math.cos(3 * 5 * 2 * Math.PI / 8) + Math.sin(3 * 1 * 2 * Math.PI / 8), 0) },
			{ new Complex(2 * Math.cos(4 * 5 * 2 * Math.PI / 8) + Math.sin(4 * 1 * 2 * Math.PI / 8), 0) },
			{ new Complex(2 * Math.cos(5 * 5 * 2 * Math.PI / 8) + Math.sin(5 * 1 * 2 * Math.PI / 8), 0) },
			{ new Complex(2 * Math.cos(6 * 5 * 2 * Math.PI / 8) + Math.sin(6 * 1 * 2 * Math.PI / 8), 0) },
			{ new Complex(2 * Math.cos(7 * 5 * 2 * Math.PI / 8) + Math.sin(7 * 1 * 2 * Math.PI / 8), 0) }
		};
		
		Complex [] inps = new Complex[8];
		for (int i = 0; i < 8; i++) 
			inps[i] = inputs[i][0];
		
	
		System.out.println("f(x)=2cos(5x)+sin(x)");
		System.out.println("==================================");
		
		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] complx = transformer.transform(inps, TransformType.FORWARD);
		
		Arrays.stream(complx).forEach(p -> { System.out.println("FFT ==> " + cf.format(p)); });
		
		
		
		FieldMatrix<Complex> m = MatrixUtils.createFieldMatrix(inputs);
		FieldMatrix<Complex> p = dft(8);
		
		System.out.println(print(p.multiply(m)));
	}
	
	public static FieldMatrix<Complex> dft(int size) {
		
		Complex [][] data = new Complex[size][size];
		
		double w = -2 * Math.PI / size;
		
		for (int i = 0; i < size; i++) {
			
			for (int j = 0; j < size; j++) {
				
				if (i == 0 || j == 0) {
					data[i][j] = new Complex(1, 0);
					continue;
				}
				
				data[i][j] = ComplexUtils.polar2Complex(1, i * j * w);			
			}	
		}
		
		return MatrixUtils.createFieldMatrix(data);
	}
	
	public static String print(FieldMatrix<Complex> m) {
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(3);
		nf.setMaximumFractionDigits(3);
		ComplexFormat cf = new ComplexFormat(nf);
		
		Complex [][] arr = m.getData();
		int width = m.getColumnDimension();
		int height = m.getRowDimension();
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < height; i++) {
			
			builder.append("[");
			
			for (int j = 0; j < width; j++) {
				if (j > 0)
					builder.append(", ");
				
				builder.append(fill(cf.format(arr[i][j]), 16));
			}
			
			builder.append("]\n");
		}
		
		return builder.toString();
	}
	
	private static String fill(String data, int width) {
		
		int spaces = 0;
		if (width > data.length()) {
			spaces = width - data.length();
		}
		else {
			width = data.length();
		}
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < spaces; i++)
			builder.append(" ");
		
		return builder.toString() + data.trim();
	}	
}
