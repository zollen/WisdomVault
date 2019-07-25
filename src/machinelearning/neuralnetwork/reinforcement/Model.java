package machinelearning.neuralnetwork.reinforcement;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.EmbeddingLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.conf.preprocessor.FeedForwardToRnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.RnnToFeedForwardPreProcessor;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class Model {
	
	private static final int SEED = 83;
	private static final int MAX_GAMES = 1;
	private static final int OUTCOMES = 2;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MultiLayerNetwork network = network();
		
		System.out.println(network.summary());
		
		List<MazeGame> games = new ArrayList<MazeGame>();
		
		for (int i = 0; i < MAX_GAMES; i++) {
			MazeGame game = new MazeGame();
			game.run();
			games.add(game);
		}
		
		DataSet data = getData(games);
		
		System.out.println(data.getFeatures());
		
		System.out.println(games.get(0));
		
		

	}
	
	public static DataSet getData(List<MazeGame> games) {
		
		INDArray input = Nd4j.zeros(new int[] { MAX_GAMES, 1, MazeGame.MAX_ROUNDS }, 'f');
		INDArray label = Nd4j.zeros(new int[] { MAX_GAMES, OUTCOMES, MazeGame.MAX_ROUNDS }, 'f');
		
		INDArray inputMask = Nd4j.zeros(new int [] { MAX_GAMES, MazeGame.MAX_ROUNDS });
		INDArray labelMask = Nd4j.zeros(new int[] { MAX_GAMES, MazeGame.MAX_ROUNDS });
		
		for (int gth = 0; gth < MAX_GAMES; gth++) {
			
			MazeGame game = games.get(gth);
			List<MazeGame.Move> moves = game.moves();
		
			for (int i = 0; i < moves.size(); i++) {
				
				MazeGame.Move move = moves.get(i);
				
				input.putScalar(new int [] { gth, 0, i }, move.getRow() * 10 + move.getCol());
				inputMask.putScalar(new int[] { gth, moves.size() }, 1.0);
			}
			
			MazeGame.Move last = moves.get(moves.size() - 1);
		
			int outcome = moves.size() <= 10 && 
					last.getRow() == MazeGame.END_ROW && 
					last.getCol() == MazeGame.END_COLUMN ? 1 : 0;
			
			label.putScalar(new int[] { gth, outcome, moves.size() - 1 }, 1.0);
			labelMask.putScalar(new int[] { gth, moves.size() - 1 }, 1.0);
		
		}
		
		return new DataSet(input, label, inputMask, labelMask);
	}

	public static MultiLayerNetwork network() {
		
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(SEED)
				.cacheMode(CacheMode.HOST)
				.weightInit(WeightInit.XAVIER)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Nadam())
				.activation(Activation.TANH)
				.l2(0.0001)
				.cudnnAlgoMode(ConvolutionLayer.AlgoMode.NO_WORKSPACE)
				.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
				.trainingWorkspaceMode(WorkspaceMode.ENABLED)
				.list()
				

				.layer(0, new EmbeddingLayer.Builder()
								.nIn(MazeGame.TOTAL_SLOTS)
								.nOut(32)
								.activation(Activation.TANH).build())
				
				.layer(1, new LSTM.Builder()
						.nIn(32).nOut(64)
						.activation(Activation.TANH).build())
				 
				.layer(2, new LSTM.Builder()
								.nIn(64).nOut(32)
								.activation(Activation.TANH).build())
				.layer(3, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
								.activation(Activation.SOFTMAX)        
								.nIn(32).nOut(OUTCOMES).build())
				
				.inputPreProcessor(0, new RnnToFeedForwardPreProcessor())
	            .inputPreProcessor(1, new FeedForwardToRnnPreProcessor())
	            
				.build();
	
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(
							new PerformanceListener.Builder()
								.reportSample(true)
								.reportScore(true)
								.reportTime(true)
								.reportETL(true)
								.reportBatch(true)
								.reportIteration(true)
								.setFrequency(100).build());
		
		return network;
	}
}
