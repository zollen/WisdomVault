package machinelearning.neuralnetwork;

import java.io.File;
import java.util.Random;

import org.deeplearning4j.nn.conf.BackpropType;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class RNN3 {
	
	private static final Random rand = new Random(0);

	public static void main( String[] args ) throws Exception {
		int lstmLayerSize = 200;					//Number of units in each LSTM layer
		int miniBatchSize = 32;						//Size of mini batch to use when  training
		int exampleLength = 1000;					//Length of each training example sequence to use. This could certainly be increased
        int tbpttLength = 50;                       //Length for truncated backpropagation through time. i.e., do parameter updates ever 50 characters
		int numEpochs = 10;							//Total number of training epochs
		int nSamplesToGenerate = 4;					//Number of samples to generate after each training epoch
		int nCharactersToSample = 300;				//Length of each sample to generate
		String generationInitialization = null;		//Optional character initialization; a random character is used if null
		// Above is Used to 'prime' the LSTM with a character sequence to continue/complete.
		// Initialization characters must all be in CharacterIterator.getMinimalCharacterSet() by default
		
		//Get a DataSetIterator that handles vectorization of text into something we can use to train
		// our LSTM network.
		WordDataSetIterator iter = getWordsIterator("data/book.txt", miniBatchSize, exampleLength);
		int nOut = iter.totalOutcomes();
		

		//Set up network configuration:
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
			.seed(12345)
			.l2(0.0001)
            .weightInit(WeightInit.XAVIER)
            .updater(new Adam(0.005))
			.list()
			.layer(new LSTM.Builder().nIn(iter.inputColumns()).nOut(lstmLayerSize)
					.activation(Activation.TANH).build())
			.layer(new LSTM.Builder().nIn(lstmLayerSize).nOut(lstmLayerSize)
					.activation(Activation.TANH).build())
			.layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT).activation(Activation.SOFTMAX)        //MCXENT + softmax for classification
					.nIn(lstmLayerSize).nOut(nOut).build())
            .backpropType(BackpropType.TruncatedBPTT)
            .tBPTTForwardLength(tbpttLength)
            .tBPTTBackwardLength(tbpttLength)
			.build();

		MultiLayerNetwork net = new MultiLayerNetwork(conf);
		net.init();
		net.setListeners(new ScoreIterationListener(100));

		//Print the  number of parameters in the network (and for each layer)
        System.out.println(net.summary());

		//Do training, and then generate and print samples from network
        int miniBatchNumber = 0;
		for( int i=0; i<numEpochs; i++ ){
            while(iter.hasNext()){
                DataSet ds = iter.next();
                net.fit(ds);
            }
            iter.reset();
		}
     
		System.out.println("--------------------");
		System.out.println("Completed " + miniBatchNumber + " minibatches of size " + miniBatchSize + "x"
				+ exampleLength + " characters");
		System.out.println("Sampling characters from network given initialization \""
				+ (generationInitialization == null ? "" : generationInitialization) + "\"");
		String[] samples = sampleCharactersFromNetwork("For", net, iter, 
												nCharactersToSample, nSamplesToGenerate);
		for (int j = 0; j < samples.length; j++) {
			System.out.println("----- Sample " + j + " -----");
			System.out.println(samples[j]);
			System.out.println();
		}

		System.out.println("\n\nExample complete");
	}

	/** Downloads Shakespeare training data and stores it locally (temp directory). Then set up and return a simple
	 * DataSetIterator that does vectorization based on the text.
	 * @param miniBatchSize Number of text segments in each training mini-batch
	 * @param sequenceLength Number of characters in each text segment.
	 */
	public static WordDataSetIterator getWordsIterator(String fileName, int miniBatchSize, int sequenceLength) throws Exception{
		return new WordDataSetIterator(new File(fileName), miniBatchSize, sequenceLength);
	}

	/** Generate a sample from the network, given an (optional, possibly null) initialization. Initialization
	 * can be used to 'prime' the RNN with a sequence you want to extend/continue.<br>
	 * Note that the initalization is used for all samples
	 * @param initialization String, may be null. If null, select a random character as initialization for all samples
	 * @param charactersToSample Number of characters to sample from network (excluding initialization)
	 * @param net MultiLayerNetwork with one or more LSTM/RNN layers and a softmax output layer
	 * @param iter CharacterIterator. Used for going from indexes back to characters
	 */
	private static String[] sampleCharactersFromNetwork(String initialization, MultiLayerNetwork net,
				WordDataSetIterator iter, int charactersToSample, int numSamples ){
		
		//Create input for initialization
		INDArray initializationInput = Nd4j.zeros(numSamples, iter.inputColumns(), initialization.length());
		
		int idx = iter.toIdx(initialization);
		for( int j=0; j<numSamples; j++ ){
			initializationInput.putScalar(new int[]{j,idx, 0}, 1.0f);
		}

		StringBuilder[] sb = new StringBuilder[numSamples];
		for( int i = 0; i < numSamples; i++ ) {
			sb[i] = new StringBuilder(initialization);
			sb[i].append(" ");
		}

		//Sample from network (and feed samples back into input) one character at a time (for all samples)
		//Sampling is done in parallel here
		net.rnnClearPreviousState();
		INDArray output = net.rnnTimeStep(initializationInput);
		output = output.tensorAlongDimension((int)output.size(2) - 1, 1, 0);	//Gets the last time step output

		for( int i = 0; i < charactersToSample; i++ ) {
			//Set up next input (single time step) by sampling from previous output
			INDArray nextInput = Nd4j.zeros(numSamples,iter.inputColumns());
			//Output is a probability distribution. Sample from this for each example we want to generate, and add it to the new input
			for( int s = 0; s < numSamples; s++ ) {
				
				double[] outputProbDistribution = new double[iter.totalOutcomes()];
				for( int j = 0; j < outputProbDistribution.length; j++ ) 
					outputProbDistribution[j] = output.getDouble(s,j);
				
				int sampledCharacterIdx = sampleFromDistribution(outputProbDistribution, rand);

				nextInput.putScalar(new int[]{ s, sampledCharacterIdx }, 1.0);		//Prepare next time step input
				sb[s].append(iter.toWord(sampledCharacterIdx) + " ");	//Add sampled character to StringBuilder (human readable output)
			}

			output = net.rnnTimeStep(nextInput);	//Do one time step of forward pass
		}

		String[] out = new String[numSamples];
		for( int i=0; i<numSamples; i++ ) out[i] = sb[i].toString();
		return out;
	}

	/** Given a probability distribution over discrete classes, sample from the distribution
	 * and return the generated class index.
	 * @param distribution Probability distribution over classes. Must sum to 1.0
	 */
	public static int sampleFromDistribution( double[] distribution, Random rng ){
	    double d = 0.0;
	    double sum = 0.0;
	    for( int t=0; t<10; t++ ) {
            d = rng.nextDouble();
            sum = 0.0;
            for( int i=0; i<distribution.length; i++ ){
                sum += distribution[i];
                if( d <= sum ) return i;
            }
            //If we haven't found the right index yet, maybe the sum is slightly
            //lower than 1 due to rounding error, so try again.
        }
		//Should be extremely unlikely to happen if distribution is a valid probability distribution
		throw new IllegalArgumentException("Distribution is invalid? d="+d+", sum="+sum);
	}

}