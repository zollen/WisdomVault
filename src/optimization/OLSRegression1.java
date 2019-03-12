package optimization;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.regression.GLSMultipleLinearRegression;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class OLSRegression1 {

	private static final DecimalFormat ff = new DecimalFormat("#,##0.000");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Review TestUtils for chi-square test, t-test, g-test, ANOVA test, KS-test
		System.err.println("Review TestUtils for chi-square test, t-test, g-test, ANOVA test, KS-test");
				
		{
			double[] y = new double[] { 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };

			double[][] x = new double[6][];
			x[0] = new double[] { 0, 0, 0, 0, 0 };
			x[1] = new double[] { 2.0, 0, 0, 0, 0 };
			x[2] = new double[] { 0, 3.0, 0, 0, 0 };
			x[3] = new double[] { 0, 0, 4.0, 0, 0 };
			x[4] = new double[] { 0, 0, 0, 5.0, 0 };
			x[5] = new double[] { 0, 0, 0, 0, 6.0 };
			// Ordinary Least Squares Regression
			OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
			regression.newSampleData(y, x);

			double[] beta = regression.estimateRegressionParameters();
			double[] residuals = regression.estimateResiduals();
			double[][] parametersVariance = regression.estimateRegressionParametersVariance();
			double regressandVariance = regression.estimateRegressandVariance();
			double rSquared = regression.calculateRSquared();
			double sigma = regression.estimateRegressionStandardError();

			System.out.println(
					"Beta: " + Arrays.stream(beta).mapToObj(p -> ff.format(p)).collect(Collectors.joining(", ")));
			System.out.println("Residuals: "
					+ Arrays.stream(residuals).mapToObj(p -> ff.format(p)).collect(Collectors.joining(", ")));

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < parametersVariance.length; i++) {
				for (int j = 0; j < parametersVariance[i].length; j++) {
					if (j > 0)
						builder.append(", ");
					builder.append("Var[" + i + "][" + j + "] ==> " + ff.format(parametersVariance[i][j]));
				}
				builder.append("\n");
			}

			System.out.println(builder.toString());

			System.out.println("Regression Var: " + ff.format(regressandVariance));
			System.out.println("R^2: " + ff.format(rSquared));
			System.out.println("Signa: " + ff.format(sigma));
		}

		System.out.println("==================================================");
		{
			// Generalized Least Squares
			GLSMultipleLinearRegression regression = new GLSMultipleLinearRegression();
			double[] y = new double[] { 11.0, 12.0, 13.0, 14.0, 15.0, 16.0 };
			double[][] x = new double[6][];
			x[0] = new double[] { 0, 0, 0, 0, 0 };
			x[1] = new double[] { 2.0, 0, 0, 0, 0 };
			x[2] = new double[] { 0, 3.0, 0, 0, 0 };
			x[3] = new double[] { 0, 0, 4.0, 0, 0 };
			x[4] = new double[] { 0, 0, 0, 5.0, 0 };
			x[5] = new double[] { 0, 0, 0, 0, 6.0 };
			double[][] omega = new double[6][];
			omega[0] = new double[] { 1.1, 0, 0, 0, 0, 0 };
			omega[1] = new double[] { 0, 2.2, 0, 0, 0, 0 };
			omega[2] = new double[] { 0, 0, 3.3, 0, 0, 0 };
			omega[3] = new double[] { 0, 0, 0, 4.4, 0, 0 };
			omega[4] = new double[] { 0, 0, 0, 0, 5.5, 0 };
			omega[5] = new double[] { 0, 0, 0, 0, 0, 6.6 };
			regression.newSampleData(y, x, omega);
			
			// or computing covariance matrix with..
			// new Covariance().computeCovarianceMatrix(data);
			
			double[] beta = regression.estimateRegressionParameters();
			double[] residuals = regression.estimateResiduals();
			double[][] parametersVariance = regression.estimateRegressionParametersVariance();
			double regressandVariance = regression.estimateRegressandVariance();
			double sigma = regression.estimateRegressionStandardError();

			System.out.println(
					"Beta: " + Arrays.stream(beta).mapToObj(p -> ff.format(p)).collect(Collectors.joining(", ")));
			System.out.println("Residuals: "
					+ Arrays.stream(residuals).mapToObj(p -> ff.format(p)).collect(Collectors.joining(", ")));

			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < parametersVariance.length; i++) {
				for (int j = 0; j < parametersVariance[i].length; j++) {
					if (j > 0)
						builder.append(", ");
					builder.append("Var[" + i + "][" + j + "] ==> " + ff.format(parametersVariance[i][j]));
				}
				builder.append("\n");
			}

			System.out.println(builder.toString());
			System.out.println("Regression Var: " + ff.format(regressandVariance));
			System.out.println("Signa: " + ff.format(sigma));
		}

	}

}
