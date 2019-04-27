package machinelearning;

/** @Email: deusjeraldy@gmail.com
 * BSD License
 */

// np.java -> https://gist.github.com/Jeraldy/7d4262db0536d27906b1e397662512bc

import java.util.Arrays;

public class NeuralNetworkBasic1 {
  
    public static void main(String[] args) {
  
        double[][] X = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[][] Y = {{0}, {1}, {1}, {0}};

        int m = 4;
        int nodes = 400;

        X = MatrixCommon.T(X);
        Y = MatrixCommon.T(Y);

        double[][] W1 = MatrixCommon.random(nodes, 2);
        double[][] b1 = new double[nodes][m];

        double[][] W2 = MatrixCommon.random(1, nodes);
        double[][] b2 = new double[1][m];

        for (int i = 0; i < 4000; i++) {
            // Foward Prop
            // LAYER 1
            double[][] Z1 = MatrixCommon.add(MatrixCommon.dot(W1, X), b1);
            double[][] A1 = MatrixCommon.sigmoid(Z1);

            //LAYER 2
            double[][] Z2 = MatrixCommon.add(MatrixCommon.dot(W2, A1), b2);
            double[][] A2 = MatrixCommon.sigmoid(Z2);

            double cost = MatrixCommon.cross_entropy(m, Y, A2);
            //costs.getData().add(new XYChart.Data(i, cost));
         
            // Back Prop
            //LAYER 2
            double[][] dZ2 = MatrixCommon.subtract(A2, Y);
            double[][] dW2 = MatrixCommon.divide(MatrixCommon.dot(dZ2, MatrixCommon.T(A1)), m);
            double[][] db2 = MatrixCommon.divide(dZ2, m);

            //LAYER 1
            double[][] dZ1 = MatrixCommon.multiply(MatrixCommon.dot(MatrixCommon.T(W2), dZ2), MatrixCommon.subtract(1.0, MatrixCommon.power(A1, 2)));
            double[][] dW1 = MatrixCommon.divide(MatrixCommon.dot(dZ1, MatrixCommon.T(X)), m);
            double[][] db1 = MatrixCommon.divide(dZ1, m);

            // G.D
            W1 = MatrixCommon.subtract(W1, MatrixCommon.multiply(0.01, dW1));
            b1 = MatrixCommon.subtract(b1, MatrixCommon.multiply(0.01, db1));

            W2 = MatrixCommon.subtract(W2, MatrixCommon.multiply(0.01, dW2));
            b2 = MatrixCommon.subtract(b2, MatrixCommon.multiply(0.01, db2));

            if (i % 400 == 0) {
                System.out.println("==============");
                System.out.print("Cost = " + cost);
                System.out.println(" Predictions = " + Arrays.deepToString(A2));
            }
        }
    }
}