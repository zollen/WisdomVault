package machinelearning.classifier;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.function.Logit;
import org.apache.commons.math3.analysis.function.Sigmoid;

/**
 * Logistic Weights updates explaination
 * https://ml-cheatsheet.readthedocs.io/en/latest/logistic_regression.html
 * 
 * For example:
 * Training Data: 4 features (x1, x2, x3, x4), 10 records: (4, 10)
 * Weight: (w1, w2, w3, w4): initial values (1, 1, 1, 1)
 * 
 * prediction: sigmoid: 1 / ( 1 + e^(-(w1 * x1 + w2 * x2 + w3 * x3 + w4 * x4))
 * 
 * decision boundary: 0.5
 * 
 * Computing cost
 * ==============
 * predictions = predict(training_data, weights)
 * cost1: If label = 1, then -log(predictions)
 * cost0: If label = 0, then -log(1 - predictions)
 * cost = cost1 + cost0
 * cost = cost.sum() / len(cost)     <-- average out all training data costs
 *  
 * 
 *  Updating Weights
 *  ================
 *  predictions = predict(training_data, weights)
 *  gradient = dot_product(transpose(training_data), predictions - labels)
 *  gradient = gradient / len(predictions)  <-- average all the gradient array of 4 features 
 *  gradient = gradient * learning_rate
 *  weights = weight - gradient
 *  
 *  Training
 *  ========
 *  for i in 1 to MAX_EPOCHS
 *     new_weights = update_weight(training_data, labels, weights, learning_rate)
 *     cost = cost_function(features, labels, new_weight)
 *     print(cost)
 *     weights = new_weights
 * 
 */



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