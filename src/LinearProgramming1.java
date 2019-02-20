import org.apache.commons.math3.fitting.leastsquares.LeastSquaresAdapter;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.optim.PointVectorValuePair;

public class LinearProgramming1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LevenbergMarquardtOptimizer optimizer = new LevenbergMarquardtOptimizer();

		 final double[] weights = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };

		 final double[] initialSolution = {1, 1, 1};

		 PointVectorValuePair optimum = optimizer.optimize(problem);

		 final double[] optimalValues = optimum.getPoint();

		 System.out.println("A: " + optimalValues[0]);
		 System.out.println("B: " + optimalValues[1]);
		 System.out.println("C: " + optimalValues[2]);
	}
	
	public static class QuadraticProblem extends LeastSquaresAdapter implements LeastSquaresProblem {


		public QuadraticProblem(LeastSquaresProblem problem) {
			super(problem);
			// TODO Auto-generated constructor stub
		}

		@Override
		public RealVector getStart() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getObservationSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getParameterSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Evaluation evaluate(RealVector point) {
			// TODO Auto-generated method stub
			return null;
		}	
	}

}
