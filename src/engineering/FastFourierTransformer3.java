package engineering;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexFormat;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FastFourierTransformer3 {
	
	private static DecimalFormat formatter = new DecimalFormat("0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumFractionDigits(3);
		nf.setMaximumFractionDigits(3);

		// create complex format with custom number format
		// when one number format is used, both real and
		// imaginary parts are formatted the same
		ComplexFormat cf = new ComplexFormat(nf);
		
		double [] input = new double[8];
	    input[0] = 2 * Math.cos(0 * 5 * 2 * Math.PI / 8) + Math.sin(0 * 2 * Math.PI / 8);
	    input[1] = 2 * Math.cos(1 * 5 * 2 * Math.PI / 8) + Math.sin(1 * 2 * Math.PI / 8);
	    input[2] = 2 * Math.cos(2 * 5 * 2 * Math.PI / 8) + Math.sin(2 * 2 * Math.PI / 8);
	    input[3] = 2 * Math.cos(3 * 5 * 2 * Math.PI / 8) + Math.sin(3 * 2 * Math.PI / 8);
	    input[4] = 2 * Math.cos(4 * 5 * 2 * Math.PI / 8) + Math.sin(4 * 2 * Math.PI / 8);
	    input[5] = 2 * Math.cos(5 * 5 * 2 * Math.PI / 8) + Math.sin(5 * 2 * Math.PI / 8);
	    input[6] = 2 * Math.cos(6 * 5 * 2 * Math.PI / 8) + Math.sin(6 * 2 * Math.PI / 8);
	    input[7] = 2 * Math.cos(7 * 5 * 2 * Math.PI / 8) + Math.sin(7 * 2 * Math.PI / 8);
	    
	    String [] title = new String[8];
	    title[0] = "2PI/8 * 0";
	    title[1] = "2PI/8 * 1";
	    title[2] = "2PI/8 * 2";
	    title[3] = "2PI/8 * 3";
	    title[4] = "2PI/8 * 4";
	    title[5] = "2PI/8 * 5";
	    title[6] = "2PI/8 * 6";
	    title[7] = "2PI/8 * 7";
	    
	    String [] Angles = new String[8];
	    Angles[0] = "0";
	    Angles[1] = "3PI/2";  // Magitude: 0 - 4j
	    Angles[2] = "0";
	    Angles[3] = "0";
	    Angles[4] = "ignore";
	    Angles[5] = "ignore";
	    Angles[6] = "ignore";
	    Angles[7] = "ignore";
	    
	    // The angle of the phase is based on cosine wave, 
	    // if we right shift the wave to 3PI/2, it become sin(x)
	    		
	    
	    System.out.println("f(x)=2cos(5x)+sin(x)");
	    System.out.println("==================================");
	   

		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		
		double[] Magnitude = new double[input.length];
	
		try {           
	        Complex[] complx = transformer.transform(input, TransformType.FORWARD);

	        for (int i = 0; i < complx.length; i++) {               
	            double rr = (complx[i].getReal());
	            double ri = (complx[i].getImaginary());

	            Magnitude[i] = Math.sqrt((rr * rr) + (ri * ri));
	                
	            System.out.println(String.format("%9s   %6s  ===>   %3s   %20s     %6s     %s", 
	            title[i],
	            formatter.format(input[i]),
	            String.valueOf(i + "Hz"),
	            cf.format(complx[i]),
	            (i < complx.length / 2) ? formatter.format( Magnitude[i] * 2 / complx.length ) : "ignore",
	            Angles[i]));
	        }
	        
	        System.out.println("Nyquist Limit: **For calculating Magnitude**, it is impossible to extract any upper half of the sampling frequencies (freq above): " + complx.length / 2 + "Hz");
	        System.out.println("Therefore we *double* each frequency within the lower half (freq below): " + complx.length / 2 + "Hz");
	        
	        System.out.println("=============== Removing 2cos(5x) with magitude of 2 =================");

	        complx[3] = complx[5] = new Complex(0, 0); // removing the 2cos(5x) component (with magitude of 2)
	        
	        Complex [] arr = transformer.transform(complx, TransformType.INVERSE);
	        
	        Arrays.stream(arr).forEach(p -> { System.out.println("AFTER ==> " + cf.format(p)); });
	        
	        double [] samples = new double[8];
	        samples[0] = Math.sin(0 * 2 * Math.PI / 8);
	        samples[1] = Math.sin(1 * 2 * Math.PI / 8);
	        samples[2] = Math.sin(2 * 2 * Math.PI / 8);
	        samples[3] = Math.sin(3 * 2 * Math.PI / 8);
	        samples[4] = Math.sin(4 * 2 * Math.PI / 8);
	        samples[5] = Math.sin(5 * 2 * Math.PI / 8);
	        samples[6] = Math.sin(6 * 2 * Math.PI / 8);
	        samples[7] = Math.sin(7 * 2 * Math.PI / 8);
	        
	        System.out.println("------------ sin(x) ----------------");
	        Arrays.stream(samples).forEach(p -> { System.out.println("COMPARE ==> " + formatter.format(p)); });
	        

	    } catch (IllegalArgumentException e) {
	        System.out.println(e);
	    }
		
		

	}
	
}
