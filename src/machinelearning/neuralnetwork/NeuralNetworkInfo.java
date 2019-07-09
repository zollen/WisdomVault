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
		
		// org.nd4j.linalg.learning.config.Adam
		// Adam can be looked at as a combination of RMSprop and Stochastic Gradient Descent with 
		// momentum. It uses the squared gradients to scale the learning rate like RMSprop and 
		// it takes advantage of momentum by using moving average of the gradient instead of 
		// gradient itself like SGD with momentum. 
		// m_t = b1 * m_t-1 + (1 - b1) * g_t
		// v_t = b2 * v_t-1 + (1 - b2) * (g_t)^2
		// m and v are moving average.
		// Expected Value(m_t) = Expected Value(g_t)
		// Expected Value(v_t) = Expected Value((g_t)^2)
		
		// m_0 = 0
		// m_1 = b1 * m_0 + (1 - b1) * g_1 = (1 - b1) * g_1
		// m_2 = b1 * m_1 + (1 - b1) * g_2 = b1 * (1 - b1) * g_1 + (1 - b1) * g_2
		// m_3 = b1 * m_2 + (1 - b1) * g_3 = b1^2 * (1 - b1) * g_1 + b1 * (1 - b1) * g_2 + (1- b1) g_3
		
		// m_t = (1 - b1) Σ (b1)^(t-i) * g_i
		
		// In the first row, we use the above formula for moving average to expand m:
		// Expected Value(m_t) = Expected Value( (1 - b1) Σ (b1)^(t-i) * g_i )
		
		// Next, we approximate g[i] with g[t]. Now we can take it out of sum, since it does not 
		// now depend on i. Because the approximation is taking place, the error C emerge in the 
		// formula:
		// Expected Value(m_t) = Expected Value(g_t) * (1 - b1) * Σ (b1)^(t-i) + K
		
		// In the last line we just use the formula for the sum of a finite geometric series:
		// Bias correction for the first momentum estimator
		// Expected Value(m_t) = Expected Value(g_t) * (1 - b1) + K
		
		// Now we need to correct the estimator, so that the expected value is the one we want. 
		// This step is usually referred to as bias correction. The final formulas for our 
		// estimator will be as follows:
		// M_t = m_t / (1 - b1)
		// V_t = v_t / (1 - b1)
		
		// w_t = w_t-1 + η * M_t / (sqrt(V_t) + e
	}

}
