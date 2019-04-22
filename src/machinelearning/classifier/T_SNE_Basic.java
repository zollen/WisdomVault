package machinelearning.classifier;

import java.util.Random;

import org.apache.commons.math3.stat.StatUtils;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

import com.jujutsu.tsne.PrincipalComponentAnalysis;

public class T_SNE_Basic {

//	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	public static final Random rand = new Random(0);
	
	public static final int SIZE = 50;
		
	public static void main(String ...args) {
		
		double [][] X = randomPoint(SIZE, 4);
				
		// FORMAT: X[record][attribute]
		PrincipalComponentAnalysis pca = new PrincipalComponentAnalysis();
    	double [][] Y = pca.pca(X, 2);
		
    	// Full multi-dimensional data
    	DMatrixRMaj X1 = new DMatrixRMaj(X);
    	// X1[attribute][record]
    	CommonOps_DDRM.transpose(X1);
    	    	
    	// Two dimensional data
    	DMatrixRMaj Y1 = new DMatrixRMaj(Y);
    	// Y1[attribute][record]
    	CommonOps_DDRM.transpose(Y1);
    		
    	
    	// Algorithm
    	// data set X = { x1, x2, x3 ... xn etc }
    	// cost function parameter: perplexity (perp)
    	// optimization parameter:
    	//		1. number of iteration T
    	//		2. learning rate η
    	//		3. momentum α(t)
    	// Output: low dimensional data representation Y = { y1, y2, y3 ..yn etc }
    	// begin
    	//		compute pairwise affinities Pji with perplexity (perp)
    	//		set Pji = (Pji + Pij) / (2n)
    	// for t = 1 to T do
    	//    Compute low-dimensional affinities Qij
    	// 	  Compute gradient ∂C/∂y 
    	//	  Compute Y(t) = Y(t-1) + η * ∂C/∂y * α(t)(Y(t-1) - Y(t-2))
    	// end
    	
    	// perp(Pji) = 2^(H(Pji)) where H(Pji) = - Σ Pi Log(Pi) is the entropy
    	// Compute affinities Qij = (1 + (Yi - Yj)^2)^-1 / ( Σ Σ (1 + (Yi - Yk)^2)^-1 )
    	// Compute gradient ∂C/∂yi = Σ (Pij - Qij) * (Yi - Yj) * (1 + (Yi - Yj)^2)^-1
    	
    	// Perplexity
    	// The perplexity is related to the number of nearest neighbors that is used in 
    	// other manifold learning algorithms. Larger datasets usually require a larger 
    	// perplexity. Consider selecting a value between 5 and 50. The choice is not 
    	// extremely critical since t-SNE is quite insensitive to this parameter.
    	
    	// Learning rate
    	// The learning rate for t-SNE is usually in the range [10.0, 1000.0]. 
    	// If the learning rate is too high, the data may look like a ‘ball’ with any 
    	// point approximately equidistant from its nearest neighbors. If the learning rate 
    	// is too low, most points may look compressed in a dense cloud with few outliers. 
    	// If the cost function gets stuck in a bad local minimum increasing the learning rate 
    	// may help.
	}
	
	
	public static void kld(DMatrixRMaj samples) {
		// Kullback-Leibler divergence (KLD)
		// Xi - points in high dimensional space
		// Yi - equivalent point of Xi in low dimensional space
		// Pji - conditional probability between J and I in high dimensional space
		// Qji - conditional probability between J and I in low dimensional space
		// Cost function (C) = Σ Σ Pji * log(Pji/Qji)
		
		// The cost function calculates the total overhead of 'encoding' Pji to Qji
		// total overhead = the sum of overhead of all data points.
		
		// SNE/t-SNE minimizes the sum of KLD of all data points using gradient descent method.
	}
	
	
	public static void gradient(DMatrixRMaj samples) {
		// t-SNE technique invented back in 2008. This is the state of the art most advance
		// technique.
		
		// Xi - points in high dimensional space
		// Yi - equivalent point of Xi in low dimensional space
		// Pji - conditional probability between J and I in high dimensional space
		// Qji - conditional probability between J and I in low dimensional space
		// Cost function (C) = Σ Σ Pji * log(Pji/Qij)
		
		// SNE use asymmetric cost function which is expensive to compute
		// t-SNE use symmetric cost function where Pij = Pji and Qij = Qji
		
		// SNE Qji = exp(-(Yi - Yj)^2) / ( Σ exp(-(Yi - Yk)^2) )
		// t-SNE Qji = (1 + (Yi - Yj)^2)^-1 / ( Σ (1 + (Yi - Yk)^2)^-1 )
		
		// t-SNE use t-distribution to overcome the the crowing problem
		// i.e. Imagine projecting a hypercube onto a lower dimension or a line
		// the distance between neighbors would be impossible to preserve.
		// Therefore t-distribution is used instead of gaussian distribution
		
		// Computing Gradient:
		// Minimize the gradient of the cost function
		// SNE gradient: ∂C/∂yi = 4 Σ (Pij - Qij)(Yi - Yj)
		// t-SNE gradient: ∂C/∂yi = 4 Σ (Pij - Qij) * 1 / ((Yi - Yj)(1 + (Yi - Yj)^2))
		
		// Gradient Decent formula
		// η = learning rate
		// α(t) = momentum (how fast does it moves) - overcome local minimum
		// Y(t) = Y(t-1) + η * ∂C/∂y * α(t)(Y(t-1) - Y(t-2))
		
	}

	
	public static DMatrixRMaj probabilites(DMatrixRMaj samples, boolean withVariance) {
		
		DMatrixRMaj mat = new DMatrixRMaj(samples.numCols, samples.numCols);
		
		double variance = 1.0;
		
		for (int center = 0; center < samples.numCols; center++) {
			
			if (withVariance) {
				variance = 2 * variance(samples, center);
			}
			
			for (int other = 0; other < samples.numCols; other++) {
				mat.set(other, center, probability(samples, center, other, variance));
			}
		}
		
		// symmetrized: Pij = 1/2N * (Pij + Pji)
		for (int center = 0; center < samples.numCols; center++) {			
			for (int other = 0; other < samples.numCols; other++) {
				if (center != other) {
					
					double first = mat.get(other, center);
					double second = mat.get(center, other);
					double avg = (first + second) / (2 * samples.numCols);
					
					mat.set(other, center, avg);
					mat.set(center, other, avg);
				}
			}
		}
		
		
				
		return mat;
	}
	
	public static double probability(DMatrixRMaj samples, int center, int other, double variance) {
		
		double denom = 0.0;
		double nom = 0.0;
		
		// Closeted neighbors would have a very small distance, the negative value of the
		// small distance then Math.exp() would have a high value close to 1.0
		for (int i = 0; i < samples.numCols; i++) {						
			if (center != i) {
				denom += Math.exp( 
						-1.0 * Math.pow(distance(samples, center, i), 2) / variance);						
			}
		}
		
		nom = Math.exp(-1.0 * Math.pow(distance(samples, center, other), 2) / variance);
		
		return nom / denom;
	}
	
	public static double variance(DMatrixRMaj samples, int center) {
		
		double [] values = new double[samples.numCols];
		for (int i = 0; i < samples.numCols; i++) {		
			values[i] = distance(samples, center, i);		
		}
		
		return StatUtils.variance(values);
	}
	
	public static double distance(DMatrixRMaj samples, int center, int other) {
		
		double dist = 0.0;
		for (int i = 0; i < samples.numRows; i++) {
			dist += Math.pow(samples.get(i, center) - samples.get(i, other), 2);
		}
		
		return Math.sqrt(dist);
	}
	
	public static double [][] randomPoint(int total, int fields) {
		
		double [][] samples = new double[total][fields];
		
		final double [] primes = { 31, 67, 101, 137, 199 };
		
		for (int record = 0; record < total; record++) {
			for (int attr = 0; attr < fields; attr++) {
				samples[record][attr] = rand.nextDouble() * primes[attr];
			}
		}
		
		return samples;
	}
	

}
