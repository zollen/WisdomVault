import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class FastFourierTransformer1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double [] input = new double[8];
	    input[0] = 0.0;
	    input[1] = 162.6345596729059;
	    input[2] = 230.0;
	    input[3] = 162.63455967290594;
	    input[4] = 2.8166876380389125E-14;
	    input[5] = -162.6345596729059;
	    input[6] = -230.0;
	    input[7] = -162.63455967290597;
	    
	    String [] title = new String[8];
	    title[0] = "0(DC)";
	    title[1] = "Fs/8";
	    title[2] = "Fs/4";
	    title[3] = "3Fs/8";
	    title[4] = "Fs/2";
	    title[5] = "3Fs/8";
	    title[6] = "Fs/4";
	    title[7] = "Fs/8";
	   
																							// =

		FastFourierTransformer transformer = new FastFourierTransformer(DftNormalization.STANDARD);
		
		double[] Magnitude = new double[input.length];
		
		try {           
	        Complex[] complx = transformer.transform(input, TransformType.FORWARD);

	        for (int i = 0; i < complx.length; i++) {               
	            double rr = (complx[i].getReal());
	            double ri = (complx[i].getImaginary());

	            Magnitude[i] = Math.sqrt((rr * rr) + (ri * ri));
	            
	            System.out.println(i + "   " + title[i] + "      " + Magnitude[i]);
	        }

	    } catch (IllegalArgumentException e) {
	        System.out.println(e);
	    }
		
		

	}
	
}
