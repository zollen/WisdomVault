package machinelearning;

import java.text.DecimalFormat;
import java.util.Random;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.RandomMatrices_DDRM;
import org.ejml.equation.Equation;

public class NeuralNetworkBasic {
	
	private static DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final Random rand = new Random(0);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//https://medium.com/coinmonks/implementing-an-artificial-neural-network-in-pure-java-no-external-dependencies-975749a38114
		// Using Nerual Network to simulate the following XOR logic
		// x1  |  x2   |  y
		// ----|-------|---
		//  0  |   0   |  0
		//  0  |   1   |  1
		//  1  |   0   |  1
		//  1  |   1   |  0

		Equation eq = new Equation();		
		eq.process("X = [ " +
							" 0,  0;" +
							" 0,  1;" +
							" 1,  0;" +
							" 1,  1 " +
						"]");
		
		eq.process("Y = [ 0; 1; 1; 0 ]");
		
		DMatrixRMaj X = eq.lookupDDRM("X");	
		DMatrixRMaj Y = eq.lookupDDRM("Y");	
		
		int m = 4;
		int nodes = 400;
		
		// X = X'
		// {2,4}
		CommonOps_DDRM.transpose(X);
		// Y = Y'
		// {1,4}
		CommonOps_DDRM.transpose(Y);
		
		DMatrixRMaj W1 = RandomMatrices_DDRM.rectangle(nodes, 2, 0.0, 1.0, rand);
		DMatrixRMaj b1 = new DMatrixRMaj(nodes, m);
		
		DMatrixRMaj W2 = RandomMatrices_DDRM.rectangle(1, nodes, 0.0, 1.0, rand);
		DMatrixRMaj b2 = new DMatrixRMaj(1, m);
		
		for (int i = 0; i < 32000; i++) {
					
			// Forward Prop
            // LAYER 1
			
			// Z1       =  W1      x X     +  b1
			// {400,4}  = {400,2}  x {2,4} + {400,4}
			DMatrixRMaj Z1 = new DMatrixRMaj(nodes, m);	
			DMatrixRMaj W1X = new DMatrixRMaj(nodes, m);
			CommonOps_DDRM.mult(W1, X, W1X);		
			CommonOps_DDRM.add(W1X, b1, Z1);	
			// A1 = σ(Z1) 
			// {400,4}
			DMatrixRMaj A1 = sigmoid(Z1);
		
		
			// LAYER 2
			
			// Z2    =  W2     x A1      + b2
			// {1,4} = {1,400} x {400,4} + {1,4}
			DMatrixRMaj Z2 = new DMatrixRMaj(1, m);
			DMatrixRMaj W2A1 = new DMatrixRMaj(1, m);
			CommonOps_DDRM.mult(W2, A1, W2A1);		
			CommonOps_DDRM.add(W2A1, b2, Z2);
			// A2 = σ(Z2)
			// {1,4}
			DMatrixRMaj A2 = sigmoid(Z2);
			
		
			
			double cost = entropy(m, Y, A2);

			
			
			// Back Prop
            // LAYER 2
			
			// DZ2   = A2    - Y
			// {1,4} = {1,4} - {1,4}
			DMatrixRMaj DZ2 = new DMatrixRMaj(1, m);
			CommonOps_DDRM.subtract(A2, Y, DZ2);
		
			// DW2     = (DZ2   x A1'    ) / m
			// {1,400} = ({1,4} x {4,400}) / 4
			DMatrixRMaj DZ2A1 = new DMatrixRMaj(1, nodes);
			CommonOps_DDRM.multTransB(DZ2, A1, DZ2A1);
			
			DMatrixRMaj DW2 = DZ2A1.copy();
			CommonOps_DDRM.scale((double) 1.0 / m, DW2);
			
			// DB2   = DZ2   / m
			// {1,4} = {1,4} / 4
			DMatrixRMaj DB2 = DZ2.copy();
			CommonOps_DDRM.scale((double) 1.0 / m, DB2);
			
			
			// LAYER 1
			
			DMatrixRMaj W2DZ2 = new DMatrixRMaj(nodes, m);
			CommonOps_DDRM.multTransA(W2, DZ2, W2DZ2);
			
			DMatrixRMaj A1A1 = new DMatrixRMaj(nodes, m);	
			CommonOps_DDRM.elementPower(A1, 2, A1A1);
			
			DMatrixRMaj MA1A1 = new DMatrixRMaj(nodes, m);
			CommonOps_DDRM.subtract(1.0, A1A1, MA1A1);
			
			// DZ1     = (W2'     x DZ2  ) * (1 - (A1      * A1     ))
			// {400,4} = ({400,1} x {1,4}) * (1 - ({400,4} * {400,4}))
			DMatrixRMaj DZ1 = new DMatrixRMaj(nodes, m);
			CommonOps_DDRM.elementMult(W2DZ2, MA1A1, DZ1);
				
			// DW1     = (DZ1     x X'    ) / m
			// {400,2} = ({400,4} x {4,2]}) / 4
			DMatrixRMaj DW1 = new DMatrixRMaj(nodes, 2);
			CommonOps_DDRM.multTransB(DZ1, X, DW1);
			
			// DB1     = DZ1     / m
			// {400,4} = {400,4} / 4
			DMatrixRMaj DB1 = DZ1.copy();
			CommonOps_DDRM.scale((double) 1.0 / m, DB1);

			
			// Gradient Decent
			
			// W1      = W1      - DW1     * 0.01
			// {400,2} = {400,2} - {400,2} * 0.01
			DMatrixRMaj W1W = DW1.copy();
			CommonOps_DDRM.scale(0.01, W1W);
			CommonOps_DDRM.subtractEquals(W1, W1W);
			
			// b1      = b1      - DB1     * 0.01
			// {400,4} = {400,4} - {400,4} * 0.01
			DMatrixRMaj b1b = DB1.copy();
			CommonOps_DDRM.scale(0.01, b1b);
			CommonOps_DDRM.subtractEquals(b1, b1b);
			
			// W2      =  W2     - DW2     * 0.01
			// {1,400} = {1,400} - {1,400} * 0.01
			DMatrixRMaj W2W = DW2.copy();
			CommonOps_DDRM.scale(0.01, W2W);
			CommonOps_DDRM.subtractEquals(W2, W2W);
			
			// b2    = b2    - DB2   * 0.01
			// {1,4} = {1,4} - {1,4} * 0.01
			DMatrixRMaj b2b = DB2.copy();
			CommonOps_DDRM.scale(0.01, b2b);
			CommonOps_DDRM.subtractEquals(b2, b2b);
			
			if (i % 400 == 0) {
                StringBuilder builder = new StringBuilder();
                for (int k = 0; k < A2.numCols; k++) {
                	if (k > 0)
                		builder.append(", ");
                	
                	builder.append(ff.format(A2.get(0, k)));
                }
                System.out.println("Cost = " + ff.format(cost) + " >> Prediction: " + builder.toString());
            }
			
		}
		
		
	}
	
	public static double entropy(int inputs, DMatrixRMaj Y, DMatrixRMaj A) {
		// This is the cost function. The general cost function for Nerual network
		// is Σ (Actual ouput_i - Expect output_i)^2
		// The goal is to minimize the cost function
		
		DMatrixRMaj out = new DMatrixRMaj(A.numRows, A.numCols);
		double sum = 0.0;
		
		for (int col = 0; col < out.numCols; col++) {
			for (int row = 0; row < out.numRows; row++) {
				out.set(row, col, Y.get(row, col) * Math.log(A.get(row, col)) + 
									(1 - Y.get(row, col)) * Math.log(1 - A.get(row, col)));
			}
		}
		
		for (int col = 0; col < out.numCols; col++) {
			for (int row = 0; row < out.numRows; row++) {
				sum += out.get(row, col);
			}
		}
		
		return -1.0 * sum / inputs;
	}
	
	public static DMatrixRMaj sigmoid(DMatrixRMaj mat) {
		// Nowadays Neural network use ReLU activation function, instead of the
		// old school sigmoid. ReLU is much simple and more effective than sigmoid.
		// ReLU return 0 if the input is negative
		// ReLU return 1 if the input is positive
		
		DMatrixRMaj out = new DMatrixRMaj(mat.numRows, mat.numCols);
		Sigmoid sigmoid = new Sigmoid();
		
		for (int col = 0; col < mat.numCols; col++) {
			
			for (int row = 0; row < mat.numRows; row++) {	
				out.set(row, col, sigmoid.value(mat.get(row, col)));
			}
		}
		
		return out;	
	}

}
