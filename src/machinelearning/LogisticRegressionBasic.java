package machinelearning;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.function.Logit;
import org.apache.commons.math3.analysis.function.Sigmoid;

public class LogisticRegressionBasic {

	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");

	private static final double[] x = { 0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9 };


	public static void main(String... args) throws FileNotFoundException {

		// y = b0 + b1 x
		// |
		// ---------------------------|
		//                           \|/
		// Sigmoid: p = 1 / ( 1 + e^(-y))
		//          |
		//          --|--------|
		//           \|/      \|/
		// Logit: ln( p /( 1 - p)) = b0 + b1x

		// inverse(Sigmoid) = Logit

		Logit logit = new Logit();
		Sigmoid sigmoid = new Sigmoid();
		
		Arrays.stream(x).forEach(p -> System.out.println(p + " => func(" + p + ") = " +
				ff.format(func(p).getValue()) + " ==> "
					+ ff.format(logit.value(sigmoid.value(func(p).getValue())))));
	}

	public static DerivativeStructure func(double val) {

		// y = 0.2 x^2 - 0.3 x + 0.04
		DerivativeStructure x = new DerivativeStructure(2, 2, 0, val);

		return new DerivativeStructure(0.2d, x.pow(2), -0.3d, x).add(0.04);
	}

}