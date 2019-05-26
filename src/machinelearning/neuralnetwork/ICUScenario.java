package machinelearning.neuralnetwork;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.arbiter.ComputationGraphSpace;
import org.deeplearning4j.arbiter.conf.updater.SgdSpace;
import org.deeplearning4j.arbiter.layers.LSTMLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
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
import org.deeplearning4j.arbiter.scoring.impl.EvaluationScoreFunction;
import org.deeplearning4j.arbiter.task.ComputationGraphTaskCreator;
import org.deeplearning4j.arbiter.ui.listener.ArbiterStatusListener;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Sgd;

public class ICUScenario {

	private static final long SEED = 83;
	private static final int NB_INPUTS = 86;
	private static final int NB_EPOCHS = 10;
	private static final double LEARNING_RATE = 0.005;
	private static final int BATCH_SIZE = 32;
	private static final int NUM_LABELS = 2;
	
	private static final int NB_TRAIN_EXAMPLES = 2000;
	private static final int NB_TEST_EXAMPLES = 800;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		FileUtils.cleanDirectory(new File("out")); 
		
		if (args.length > 0 && args[0].equals("O"))
			process(optimize());
		else
			process(build());	
	}
	
	public static void process(ComputationGraph graph) throws Exception {
		
		graph.init();
	}
	
	public static void process(ComputationGraphSpace space) throws Exception {
		
		TerminationCondition[] terminationConditions = { 
				new MaxTimeCondition(96, TimeUnit.HOURS),
				new MaxCandidatesCondition(6)
		};
		
		@SuppressWarnings("deprecation")
		OptimizationConfiguration configuration = new OptimizationConfiguration.Builder()
				.candidateGenerator(new RandomSearchGenerator(space, null))
				.dataSource(ICUDataSource.class, new Properties())
				.modelSaver(new FileModelSaver("out"))
				.scoreFunction(new EvaluationScoreFunction(
								org.deeplearning4j.eval.Evaluation.Metric.ACCURACY))
				.terminationConditions(terminationConditions)
				.build();
		
		IOptimizationRunner runner = new LocalOptimizationRunner(configuration, new ComputationGraphTaskCreator());

		StatsStorage ss = new FileStatsStorage(new File("out/icuStats.dl4j"));
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

	public static ComputationGraph build() {

		ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.l2(0.01)
				.cacheMode(CacheMode.DEVICE)
				.weightInit(WeightInit.XAVIER)
				.activation(Activation.TANH)
				.updater(new Sgd(LEARNING_RATE))
				.graphBuilder()
				
				.addInputs("in")
				
				.addLayer("lstm", new LSTM.Builder()
										.nIn(NB_INPUTS).nOut(30).build(), "in")
				.addVertex("lastStep", new LastTimeStepVertex("in"), "lstm")
				
				.addLayer("out", new OutputLayer.Builder()
									.activation(Activation.SOFTMAX).nIn(30).nOut(NUM_LABELS).build(), "lastStep")
				
				.setOutputs("out").build();

		return new ComputationGraph(conf);
	}
	
	public static ComputationGraphSpace optimize() {
		
		ParameterSpace<Double> l2Param = new ContinuousParameterSpace(0.0001, 0.09);
		ParameterSpace<Double> learnParam = new ContinuousParameterSpace(0.001, 0.09);
        ParameterSpace<Integer> layer1Param = new IntegerParameterSpace(8, 256); 
        ParameterSpace<Integer> layer2Param = new IntegerParameterSpace(8, 256);
		
		return new ComputationGraphSpace.Builder() 
				.seed(SEED)
				.l2(l2Param)
				.weightInit(WeightInit.XAVIER)
				.activation(Activation.TANH)
				.updater(new SgdSpace(learnParam))
				.numEpochs(NB_EPOCHS)
						
				.addInputs("in")
				
				.addLayer("lstm", new LSTMLayerSpace.Builder()
										.nIn(layer1Param).nOut(layer2Param).build(), "in")
				.addVertex("lastStep", new LastTimeStepVertex("in"), "lstm")
				
				.addLayer("out", new OutputLayerSpace.Builder()
									.activation(Activation.SOFTMAX).
									nIn(layer2Param).nOut(2).build(), "lastStep")
				
				.setOutputs("out").build();
	}
	
	public static class ICUDataSource implements DataSource {
		
		private static final long serialVersionUID = 1L;
		
		private static final String ROOT = "data/physionet";
		
		private SequenceRecordReaderDataSetIterator training = null;
		private SequenceRecordReaderDataSetIterator testing = null;
		
		public ICUDataSource() throws Exception {
			
			String featuresPath = Paths.get(ROOT, "sequence", "%d.csv").toString();
			String labelsPath = Paths.get(ROOT, "mortality", "%d.csv").toString();
			
			SequenceRecordReader trainData = new CSVSequenceRecordReader(1, ",");
			trainData.initialize( new NumberedFileInputSplit(
									featuresPath, 0, NB_TRAIN_EXAMPLES - 1));
			
			SequenceRecordReader trainLabels = new CSVSequenceRecordReader();
			trainLabels.initialize(new NumberedFileInputSplit(
									labelsPath, 0, NB_TRAIN_EXAMPLES - 1));
			
			training = new SequenceRecordReaderDataSetIterator(trainData, trainLabels,
	                1, 2, false, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END);
			
			
			SequenceRecordReader testData = new CSVSequenceRecordReader(1, ",");
			testData.initialize(new NumberedFileInputSplit(
									featuresPath, NB_TRAIN_EXAMPLES, NB_TRAIN_EXAMPLES + 50));
			
			SequenceRecordReader testLabels = new CSVSequenceRecordReader();
			testLabels.initialize(new NumberedFileInputSplit(
									labelsPath, NB_TRAIN_EXAMPLES, NB_TRAIN_EXAMPLES  + 50));
			
			testing = new SequenceRecordReaderDataSetIterator(testData, testLabels,
	                1, 2, false, SequenceRecordReaderDataSetIterator.AlignmentMode.ALIGN_END);
		}
		
		@Override
		public void configure(Properties properties) {
			// TODO Auto-generated method stub
		}

		@Override
		public Class<?> getDataType() {
			// TODO Auto-generated method stub
			return DataSetIterator.class;
		}

		@Override
		public Object testData() {
			// TODO Auto-generated method stub
			return testing;
		}

		@Override
		public Object trainData() {
			// TODO Auto-generated method stub
			return training;
		}
		 
		 
	}


}
