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
		// A candy company makes three types of candy, solid, fruit, and cream filled, 
		// and packages these candies in three different assortments. 
		// A box of assortment I contains 4 solid, 4 fruit, and 12 cream and sells for $9.40. 
		// A box of assortment II contains 12 solid, 4 fruit, and 4 cream and sells for $7.60. 
		// A box of assortment III contains 8 solid, 8 fruit, and 8 cream and sells for $11.00. 
		//
		// The manufacturing costs per piece of candy are $0.20 for solid, $0.25 for fruit, and 
		// $0.30 for cream. 
		
		// The company can manufacture up to 4800 solid, 4000 fruit, and 5600 cream candies weekly. 
		
		// How many boxes of each type should the company produce in order to maximize profit? 
		// What is their maximum profit?
		
		
		// Linear Model: Maximize 4x + 3y + 5z = P
		// Subject To:
		//		4x + 12y + 8z <= 4800
		// 		4x + 4y + 8z  <= 4000
		//	   12x + 4y + 8z  <= 5600
		// 		x, y, z >= 0
		
        LinearObjectiveFunction f = new LinearObjectiveFunction(new double[] { 4, 3, 5 }, 0);

        Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
        constraints.add(new LinearConstraint(new double[] { 4, 12, 8 }, Relationship.LEQ, 4800));
        constraints.add(new LinearConstraint(new double[] { 4,  4, 8 }, Relationship.LEQ, 4000));
        constraints.add(new LinearConstraint(new double[] { 12, 4, 8 }, Relationship.LEQ, 5600));

   //    constraints.add(new LinearConstraint(new double[] { 1, 0, 0}, Relationship.GEQ, 0));
   //    constraints.add(new LinearConstraint(new double[] { 0, 1, 0}, Relationship.GEQ, 0));

        //create and run solver
        PointValuePair solution = new SimplexSolver().optimize(
        		f, new LinearConstraintSet(constraints), GoalType.MAXIMIZE);

        if (solution != null) {
            //get solution
            double max = solution.getValue();
            System.out.println("Opt: " + max);

            //print decision variables
          
                System.out.println("[" + 
                		"x = " +
                		ff.format(solution.getPoint()[0]) + ", " +
                		"y = " +
                		ff.format(solution.getPoint()[1]) + ", " +
                		"z = " +
                		ff.format(solution.getPoint()[2]) + 
                		"]");
           
        }
   
	}	
	
	

}
