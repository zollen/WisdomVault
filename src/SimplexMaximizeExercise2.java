import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

public class SimplexMaximizeExercise2 {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub		
		//describe the optimization problem
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 3, 5}, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] { 2, 8}, Relationship.LEQ, 13));
        constraints.add(new LinearConstraint(new double[] { 5, -1}, Relationship.LEQ, 11));

        constraints.add(new LinearConstraint(new double[] { 1, 0}, Relationship.GEQ, 0));
        constraints.add(new LinearConstraint(new double[] { 0, 1}, Relationship.GEQ, 0));

        //create and run solver
        PointValuePair solution = new SimplexSolver().optimize(
        		f, new LinearConstraintSet(constraints), GoalType.MAXIMIZE);

        if (solution != null) {
            //get solution
            double max = solution.getValue();
            System.out.println("Opt: " + max);

            //print decision variables
            System.out.print("[");
            for (int i = 0; i < 2; i++) {
            	if (i > 0)
            		System.out.print(", ");
                System.out.print(ff.format(solution.getPoint()[i]));
            }
            
            System.out.println("]");
        }
   
	}	
	
	

}
