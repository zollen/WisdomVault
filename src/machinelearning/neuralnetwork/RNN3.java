package machinelearning.neuralnetwork;

import java.io.File;

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
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class RNN3 {
	
	public static void main( String[] args ) throws Exception {
		int miniBatchSize = 1;						//Size of mini batch to use when  training
		int exampleLength = 5;					//Length of each training example sequence to use. This could certainly be increased
		
		
		// Above is Used to 'prime' the LSTM with a character sequence to continue/complete.
		// Initialization characters must all be in CharacterIterator.getMinimalCharacterSet() by default
		
		//Get a DataSetIterator that handles vectorization of text into something we can use to train
		// our LSTM network.
		WordDataSetIterator iter = getWordsIterator("data/testme.txt", miniBatchSize, exampleLength);
		int nOut = iter.totalOutcomes();
		

		//Set up network configuration:
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
			.seed(12345)
			.l2(0.001)
            .weightInit(WeightInit.XAVIER)
            .updater(new Nesterovs())
			.list()
			.layer(new LSTM.Builder().nIn(nOut).nOut(nOut)
					.activation(Activation.TANH).build())
			.layer(new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
					.activation(Activation.SOFTMAX)        //MCXENT + softmax for classification
					.nIn(nOut).nOut(nOut).build())
            .backpropType(BackpropType.TruncatedBPTT)
            .tBPTTForwardLength(10)
            .tBPTTBackwardLength(10)
			.build();

		MultiLayerNetwork net = new MultiLayerNetwork(conf);
		net.init();
		net.setListeners(new ScoreIterationListener(1));

		//Print the  number of parameters in the network (and for each layer)
        System.out.println(net.summary());

		//Do training, and then generate and print samples from network
		while (iter.hasNext()) {
			DataSet ds = iter.next();
			net.fit(ds);
		}
		iter.reset();

		
		net.rnnClearPreviousState();
		String [] stmts = { "My name is Stephen"};
		INDArray arr = iter.toINDArray(stmts);
     
		INDArray output = net.rnnTimeStep(arr);
		output = output.tensorAlongDimension(output.size(2) - 1, 1, 0);
		
		iter.getIdx().entrySet().stream().forEach(
				p -> System.out.println(String.format("[%10s] -> [%d]", p.getKey(), p.getValue())));
		
		int target = Nd4j.getExecutioner().exec(new IMax(output, 1)).getInt(0);
		System.out.println("===============================");
		System.out.println("NEXT: " + iter.toWord(target));

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

}
