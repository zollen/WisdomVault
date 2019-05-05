package machinelearning.neuralnetwork;

public class NeuralNetworkInfo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Activation
		// tanh has a stronger gradient than sigmoid 
		// sigmoid: 1/(1+e^-x)                                           0.5 <= y < 1
		// df/dx( 1/(1+e^-x) ) = e^x/(1+e^x)^2  center at x = 0            0 < y <= 0.25
		// tanh: tanh(x)                        center at x = 0           -1 < y < 1
		// df/dx( tanh(x) ) = sech(x)^2         center at x = 0            0 < y <= 1
		
		// 1. Regression Loss Functions
		//		1.1 Mean Squared Error Loss
		//		1.2 Mean Squared Logarithmic Error Loss
		//		1.3 Mean Absolute Error Loss
		// 2. Binary Classification Loss Functions
		//		2.1 Binary Cross-Entropy
		//		2.2 Hinge Loss
		//		2.3 Squared Hinge Loss
		// 3. Multi-Class Classification Loss Functions
		//		3.1 Multi-Class Cross-Entropy Loss
		//		3.2 Sparse Multiclass Cross-Entropy Loss
		//		3.3 Kullback Leibler Divergence Loss
	 
		
		// hyper-parameters
		// η - learning rate
		// α - learning decay rate

		// org.nd4j.linalg.learning.config.Sgd
		// Standard Gradient Decent
		// θ(t+1) = θ(t) - η dg(t)
		
		// org.nd4j.linalg.learning.config.NoOp
		// Making no update to the gradient
		
		// org.nd4j.linalg.learning.config.Nesterovs
		//  ** Momentum method **
		// If α > η, then v(t) would dominate the decent and will not change the current direction quickly (overcome noises)
		// If α < η, then η dg(t) would dominate the decent and become a smoothing factor of the decent.
		// v(t+1) = α v(t) - η dg(x(t), y(t), θ(t))
		// θ(t+1) = θ(t) + v(t+1)
		// ** Nesterovs - Improved momentum method ** 
		// Using the average decent rate of all x and y at t
		// Φ(t) = θ(t) + α v(t)
		// gNag = 1 / n * Σ dg(x(t), y(t), Φ(t))  <-- sum all (x,y) of dg(x(t), y(t), Φ(t)) at t
		// v(t+1) = α v(t) - η gNag
		// θ(t+1) = θ(t) + v(t+1)
		
		// org.nd4j.linalg.learning.config.AdaGrad
		// High dimension non-convex model, each dimension may have a very different gradient and may require 
		// very different learning rates
		// G(t) = dg(t) x dg(t)'
		// ε is a very small value to unsure the denominator is bigger than zero
		// I is an identity matrix
		// Both θ(t+1) and θ(t) are matrices containing all dimensions dg(t)
		// θ(t+1) = θ(t) - η / sqrt( εI + diag(G(t)) ) * dg(t)
			
		// org.nd4j.linalg.learning.config.AdaDelta
		// High dimension non-convex model, Adagrad may decrease the effective learning rate too fast so 
		// it was trapped before it reaches a good locally convex structure and achieves a bad local minimum.
		// Another problem with Adagrad is that if the initial gradients are large, the consequent effective 
		// learning rate will be low for the rest of the training.
		// One algorithm that was created to combat these problems is Adadelta, that requires no initial 
		// learning rate setting and insensitive to hyper-parameters. 
		// G(t) = dg(t) x dg(t)'
		// ρ is decaying constant = α
		// ε is a very small value to prevent sqrt(0)
		// dg(t) = dg(x(t), y(t), Φ(t)) = 1 / n * Σ dg(x(t), y(t), Φ(t))  <-- sum all (x,y) of dg(x(t), y(t), Φ(t)) at t
		// E(G(t)) is an exponentially decaying average of the squared gradients
		// E(G(t)) = ρ E(G(t-1))  + (1 - ρ)g(t) * g(t)
		// RMS(g(t)) = sqrt( εI + E(G(t)) )      <-- no learning rate required
		
		// org.nd4j.linalg.learning.config.RmsProp
		// θ(t+1) = θ(t) - η / RMS(g(t)) * g(t)  <-- learning rate required
		
	}

}
