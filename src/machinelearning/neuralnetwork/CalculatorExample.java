package machinelearning.neuralnetwork;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.layers.LSTMLayerSpace;
import org.deeplearning4j.arbiter.layers.RnnOutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.OptimizationResult;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.api.data.DataSource;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultReference;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxCandidatesCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.MaxTimeCondition;
import org.deeplearning4j.arbiter.optimize.api.termination.TerminationCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.generator.RandomSearchGenerator;
import org.deeplearning4j.arbiter.optimize.parameter.continuous.ContinuousParameterSpace;
import org.deeplearning4j.arbiter.optimize.parameter.integer.IntegerParameterSpace;
import org.deeplearning4j.arbiter.optimize.runner.IOptimizationRunner;
import org.deeplearning4j.arbiter.optimize.runner.LocalOptimizationRunner;
import org.deeplearning4j.arbiter.saver.local.FileModelSaver;
import org.deeplearning4j.arbiter.scoring.impl.TestSetLossScoreFunction;
import org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator;
import org.deeplearning4j.arbiter.ui.listener.ArbiterStatusListener;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.ClassificationScoreCalculator;
import org.deeplearning4j.earlystopping.termination.BestScoreEpochTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.CheckpointListener;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class CalculatorExample {
	
	private static final int EPOCHS = 35;
	private static final int BATCH_SIZE = 20;
	private static final int TOTAL_BATCH = 100;
	private static final int LAYER_SIZE = 64;
	private static final int TIME_STEP = 6;
	private static final int SEED = 83;
	
	
	private static final int [] INPUT_MASK = { 1, 1, 1, 1, 1, 1 };
	private static final int [] OUTPUT_MASK = { 0, 0, 0, 0, 1, 1 };
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		FileUtils.cleanDirectory(new File("out")); 

		if (args.length > 0 && args[0].equalsIgnoreCase("O")) {
			optimize();
		}
		else {
			MultiLayerNetwork network = train();		
			eval(network);
		}
	}
	
	public static void eval(MultiLayerNetwork network) throws Exception {
		
		System.err.println("========== MY OWN TEST ===========");
		INDArray inArr = Nd4j.zeros(new int[] { 1, MathIterator.INPUT_SIZE, TIME_STEP });
		INDArray outArr = Nd4j.zeros(new int[] { 1, MathIterator.OUTPUT_SIZE, TIME_STEP });
		
		INDArray in = MathIterator.toINDArray(MathIterator.INPUT_SIZE, "39+24=", INPUT_MASK);
		INDArray out = MathIterator.toINDArray(MathIterator.OUTPUT_SIZE, "63", OUTPUT_MASK);
		
		inArr.putRow(0, in);
		outArr.putRow(0, out);
		
		INDArray res = network.rnnTimeStep(inArr);
		
		System.out.println("INPUTS: " + MathIterator.toString(in, INPUT_MASK));
		System.out.println("EXPECTED: " + MathIterator.toString(out, OUTPUT_MASK));
		System.out.println("ACTUAL: " + MathIterator.toString(res, OUTPUT_MASK));
	}
	
	public static void optimize() throws Exception {
		
		MultiLayerSpace space = plan();
		
		TerminationCondition[] terminationConditions = { 
				new MaxTimeCondition(6, TimeUnit.HOURS),
				new MaxCandidatesCondition(6)
		};
	
		OptimizationConfiguration configuration = new OptimizationConfiguration.Builder()
				.candidateGenerator(new RandomSearchGenerator(space, null))
				.dataSource(CalculatorDataSource.class, new Properties())
				.modelSaver(new FileModelSaver("out"))
				.scoreFunction(new TestSetLossScoreFunction(true))
				.terminationConditions(terminationConditions)
				.build();
		
		IOptimizationRunner runner = new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());

		StatsStorage ss = new FileStatsStorage(new File("out/calculator.dl4j"));
		runner.addListeners(new ArbiterStatusListener(ss));
		UIServer.getInstance().attach(ss);

		runner.execute();

		String s = "Best score: " + runner.bestScore() + "\n" + "Index of model with best score: "
				+ runner.bestScoreCandidateIndex() + "\n" + "Number of configurations evaluated: "
				+ runner.numCandidatesCompleted() + "\n";
		System.out.println(s);

		int indexOfBestResult = runner.bestScoreCandidateIndex();
		List<ResultReference> allResults = runner.getResults();

		OptimizationResult bestResult = allResults.get(indexOfBestResult).getResult();
		MultiLayerNetwork bestModel = (MultiLayerNetwork) bestResult.getResultReference().getResultModel();

		System.out.println("\n\nConfiguration of best model:\n");
		System.out.println(bestModel.getLayerWiseConfigurations());

		UIServer.getInstance().stop();
	}
	
	public static MultiLayerNetwork train() {
		
		MultiLayerNetwork network = build();
		
		MathIterator trainIter = new MathIterator(BATCH_SIZE, TOTAL_BATCH, SEED);
		MathIterator testIter = new MathIterator(20, 1, SEED + 5);
		
		network.setListeners(new ScoreIterationListener(1000), 
				new PerformanceListener(1000, true),
				new EvaluativeListener(testIter, 5, InvocationType.EPOCH_END),
				new CheckpointListener.Builder("out").keepAll().saveEveryNEpochs(1).build()); 
		
		System.out.println(network.summary());
			
		
		EarlyStoppingConfiguration<MultiLayerNetwork> eac = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>()
				.epochTerminationConditions(new BestScoreEpochTerminationCondition(0.975),
										new ScoreImprovementEpochTerminationCondition(3))
				.scoreCalculator(new ClassificationScoreCalculator(Evaluation.Metric.ACCURACY, testIter))
				.evaluateEveryNEpochs(1)
				.modelSaver(new LocalFileModelSaver("out"))
				.build();	
	
		EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(eac, network, trainIter);
		
		System.out.println("Training model....");
		EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
		
		System.out.println("Termination reason: " + result.getTerminationReason());
        System.out.println("Termination details: " + result.getTerminationDetails());
        System.out.println("Total epochs: " + result.getTotalEpochs());
        System.out.println("Best epoch number: " + result.getBestModelEpoch());
        System.out.println("Best Accuracy at epoch: " + result.getBestModelScore());
        
        Map<Integer,Double> epochVsScore = result.getScoreVsEpoch();
        List<Integer> list = new ArrayList<Integer>(epochVsScore.keySet());
        Collections.sort(list);
        System.out.println("Epoch\tAccuracy");
        for( Integer i : list){
            System.out.println(i + "\t" + epochVsScore.get(i));
        }
        
        return network;
	}
	
	public static MultiLayerSpace plan() {
		
		ParameterSpace<Double> l2Hyperparam = new ContinuousParameterSpace(0.00001, 0.9);
        ParameterSpace<Integer> layer1Hyperparam = new IntegerParameterSpace(16, 1024); 
        ParameterSpace<Integer> layer2Hyperparam = new IntegerParameterSpace(16, 1024);
		
		return new MultiLayerSpace.Builder()
				.seed(SEED)
				.weightInit(WeightInit.XAVIER)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Nadam())
				.activation(Activation.TANH)
				.l2(l2Hyperparam)
				.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
				.trainingWorkspaceMode(WorkspaceMode.ENABLED)
				.numEpochs(EPOCHS)
				
				.layer(new LSTMLayerSpace.Builder()
								.nIn(MathIterator.INPUT_SIZE)
								.nOut(layer1Hyperparam).build())
				.layer(new LSTMLayerSpace.Builder()
								.nIn(layer1Hyperparam).nOut(layer2Hyperparam).build())
				.layer(new RnnOutputLayerSpace.Builder()
								.activation(Activation.SOFTMAX)  
								.lossFunction(LossFunctions.LossFunction.MCXENT)
								.nIn(layer2Hyperparam).nOut(MathIterator.OUTPUT_SIZE).build())
				.build();
	}
	
	public static MultiLayerNetwork build() {
				
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
				
				.layer(0, new LSTM.Builder()
								.nIn(MathIterator.INPUT_SIZE)
								.nOut(LAYER_SIZE).build())
				.layer(1, new LSTM.Builder()
								.nIn(LAYER_SIZE).nOut(LAYER_SIZE).build())
				.layer(2, new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
								.activation(Activation.SOFTMAX)        
								.nIn(LAYER_SIZE).nOut(MathIterator.OUTPUT_SIZE).build())
				.build();
				
								
		return new MultiLayerNetwork(conf);
	}
	
	
	
	public static class MathIterator implements DataSetIterator {
		
		private static final long serialVersionUID = 1L;
			
		private static Map<String, Integer> str2Idx = new HashMap<String, Integer>();
		private static Map<Integer, String> idx2Str = new HashMap<Integer, String>();
		
		
		static {
			
			for (int i = 0; i <= 9; i++) {
				str2Idx.put(String.valueOf(i), i);
				idx2Str.put(i, String.valueOf(i));
			}
			
			str2Idx.put("+", 10);
			idx2Str.put(10, "+");
			
			str2Idx.put("=", 11);
			idx2Str.put(11, "=");
		}
		
		public static final int INPUT_SIZE = str2Idx.size();
		public static final int OUTPUT_SIZE = INPUT_SIZE - 2;
		
		private int current;
		private int batchSize;
		private int totalBatch;
		private int seed;
		private Random rand;

		public MathIterator(int batchSize, int totalBatch, int seed) {
			this.batchSize = batchSize;
			this.totalBatch = totalBatch;
			this.current = 0;
			this.seed = seed;
			this.rand = new Random(seed);
		}

		@Override
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return current < totalBatch;
		}

		@Override
		public DataSet next() {
			// TODO Auto-generated method stub
			return next(current);
		}

		@Override
		public DataSet next(int num) {
			// TODO Auto-generated method stub
			current++;		
			try {
				return generate(batchSize, rand);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		public static INDArray toINDArray(int size, String stmt, int [] mask) throws Exception  {
			
			INDArray arr = Nd4j.zeros(new int[] { size, TIME_STEP });
			
			for (int i = 0, j = 0; i < TIME_STEP; i++) {
				
				if (mask[i] == 1 || mask == null) {

					int idx = toIndex(stmt.charAt(j));		
					arr.putScalar(new int[] { idx, i }, 1.0);	
					j++;
				}
			}
			
			return arr;
		}
			
		public static String toString(INDArray arr, int [] mask) throws Exception {
			
			if (arr.rank() == 3) {
				arr = arr.get(NDArrayIndex.point(0));
			}
			
					
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < TIME_STEP; i++) {
				
				if (mask[i] == 1 || mask == null) {
					
					int idx = Nd4j.getExecutioner().exec(new IMax(
							arr.get(NDArrayIndex.all(), NDArrayIndex.point(i)))).getInt(0);
	
					builder.append(toChar(idx));
				}
			}
		
			return builder.toString();
		}
		
		public static char toChar(int index) throws Exception {
			
			String tmp = idx2Str.get(index);
			
			if (tmp != null)
				return tmp.charAt(0);
						
			throw new RuntimeException("<< toChar >> Invalid Index: " + index);		
		}
		
		public static int toIndex(char ch) throws Exception {
		
			Integer num = str2Idx.get(String.valueOf(ch));
			
			if (num != null)
				return num.intValue();
			
			throw new RuntimeException("<< toIndex >> Invalid char: " + ch);
		}
		
		public static DataSet generate(int sampleSize, Random rand) throws Exception {
				
			INDArray input = Nd4j.zeros(new int[] { sampleSize, INPUT_SIZE, TIME_STEP });
			INDArray label = Nd4j.zeros(new int[] { sampleSize, OUTPUT_SIZE, TIME_STEP });
			
			INDArray fmask = Nd4j.ones(new int [] { sampleSize, TIME_STEP });
			INDArray lmask = Nd4j.zeros(new int[] { sampleSize, TIME_STEP });
			
			for (int i = 0; i < sampleSize; i++) {
				lmask.putScalar(new int [] { i, TIME_STEP - 2 }, 1.0);
				lmask.putScalar(new int [] { i, TIME_STEP - 1 }, 1.0);
			}

			for (int i = 0; i < sampleSize; i++) {
				
				int num1 = redo(rand, rand.nextInt(100), 100);
				int num2 = rand.nextInt(99 - num1);
				
				String in = String.format("%02d", num1) + "+" + String.format("%02d", num2) + "=";
				String out = String.format("%02d", (num1 + num2));
				
				INDArray arrIn = toINDArray(INPUT_SIZE, in, INPUT_MASK);
				INDArray arrOut = toINDArray(OUTPUT_SIZE, out, OUTPUT_MASK);
							
				input.putRow(i, arrIn);
				label.putRow(i, arrOut);
				
			}	
			
			return new DataSet(input, label, fmask, lmask);
		}
		
		private static int redo(Random rand, int num, int limit) {
			
			switch(num) {
			case 95:
			case 96:
			case 97:
			case 98:
			case 99:
				num = num - rand.nextInt(50);
			break;
			default:
			}
			
			return num < 99 ? num : 97;
		}

		@Override
		public int inputColumns() {
			// TODO Auto-generated method stub
			return str2Idx.size();
		}

		@Override
		public int totalOutcomes() {
			// TODO Auto-generated method stub
			return str2Idx.size() - 1;
		}

		@Override
		public boolean resetSupported() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean asyncSupported() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			this.current = 0;
			this.rand = new Random(seed);
		}

		@Override
		public int batch() {
			// TODO Auto-generated method stub
			return batchSize;
		}

		@Override
		public void setPreProcessor(DataSetPreProcessor preProcessor) {
			throw new UnsupportedOperationException("Not implemented");
			
		}

		@Override
		public DataSetPreProcessor getPreProcessor() {
			throw new UnsupportedOperationException("Not implemented");
		}

		@Override
		public List<String> getLabels() {
			// TODO Auto-generated method stub
			throw new UnsupportedOperationException("Not implemented");
		}
	}
	
	public static class CalculatorDataSource implements DataSource {
		
		private static final long serialVersionUID = 1L;
		
		private DataSetIterator training = null;
		private DataSetIterator testing = null;
		
		public CalculatorDataSource () {
			training = new MathIterator(BATCH_SIZE, TOTAL_BATCH, SEED + 14);
			testing = new MathIterator(20, 1, SEED + 6);
		}

		@Override
		public void configure(Properties properties) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object trainData() {
			// TODO Auto-generated method stub
			return training;
		}

		@Override
		public Object testData() {
			// TODO Auto-generated method stub
			return testing;
		}

		@Override
		public Class<?> getDataType() {
			// TODO Auto-generated method stub
			return DataSetIterator.class;
		}		
	}
}
