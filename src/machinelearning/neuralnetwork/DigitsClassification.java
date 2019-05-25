package machinelearning.neuralnetwork;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.arbiter.MultiLayerSpace;
import org.deeplearning4j.arbiter.conf.updater.NadamSpace;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.optimize.api.CandidateGenerator;
import org.deeplearning4j.arbiter.optimize.api.OptimizationResult;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.api.data.DataSource;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultReference;
import org.deeplearning4j.arbiter.optimize.api.saving.ResultSaver;
import org.deeplearning4j.arbiter.optimize.api.score.ScoreFunction;
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
import org.deeplearning4j.arbiter.task.MultiLayerNetworkTaskCreator;
import org.deeplearning4j.arbiter.ui.listener.ArbiterStatusListener;
import org.deeplearning4j.datasets.iterator.SamplingDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.CheckpointListener;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nadam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import graphics.ImageUtils;

public class DigitsClassification {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static int totalSamples = 0;
	private static int batchSize = 30;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Loading Data....");
		DataSet data = getData("img/digits");
		
		System.out.println("Optimizing model...."); 
		MultiLayerSpace space = network();
		
		CandidateGenerator candidateGenerator = new RandomSearchGenerator(space, null); 
		Class<? extends DataSource> dataSourceClass = DigitsDataSource.class;
        Properties dataSourceProperties = new Properties();
        dataSourceProperties.setProperty("minibatchSize", String.valueOf(batchSize));
        
        @SuppressWarnings("deprecation")
		ScoreFunction scoreFunction = new EvaluationScoreFunction(org.deeplearning4j.eval.Evaluation.Metric.ACCURACY);
		
        TerminationCondition[] terminationConditions = {
                new MaxTimeCondition(15, TimeUnit.MINUTES),
                new MaxCandidatesCondition(10)
        };
        
        ResultSaver modelSaver = new FileModelSaver("out");
        OptimizationConfiguration configuration = new OptimizationConfiguration.Builder()
                .candidateGenerator(candidateGenerator)
                .dataSource(dataSourceClass, dataSourceProperties)
                .modelSaver(modelSaver)
                .scoreFunction(scoreFunction)
                .terminationConditions(terminationConditions)
                .build();
        
        IOptimizationRunner runner = new LocalOptimizationRunner(configuration, new MultiLayerNetworkTaskCreator());
        
        StatsStorage ss = new FileStatsStorage(new File("out/arbiterExampleUiStats.dl4j"));
        runner.addListeners(new ArbiterStatusListener(ss));
        UIServer.getInstance().attach(ss);
        
        runner.execute();
        
        String s = "Best score: " + runner.bestScore() + "\n" +
                "Index of model with best score: " + runner.bestScoreCandidateIndex() + "\n" +
                "Number of configurations evaluated: " + runner.numCandidatesCompleted() + "\n";
        System.out.println(s);
        
        int indexOfBestResult = runner.bestScoreCandidateIndex();
        List<ResultReference> allResults = runner.getResults();

        OptimizationResult bestResult = allResults.get(indexOfBestResult).getResult();
        MultiLayerNetwork bestModel = (MultiLayerNetwork) bestResult.getResultReference().getResultModel();

        System.out.println("\n\nConfiguration of best model:\n");
        System.out.println(bestModel.getLayerWiseConfigurations().toJson());
        
   
            

		System.out.println("Building model with optimal hyper-parameters....");
		// My own hyper-parameters: 
		// ========================
		// 	double learningRate = 0.001;
		// 	double beta1 = 0.9;
		// 	double beta2 = 0.999; 
		// 	double epsilon = 0;
		// 	double l2 = 0.0001;
		// 	int layer1 = 250;
		// 	int layer2 = 250;
		// -----------------
		// Accuracy: 0.954
		// Precision: 0.954
		// Recall: 0.953
		// F1: 0.953
		
		// Above optimization: 
		// ========================
			double learningRate = 0.05736747513235539;
			double beta1 = 0.5726747513235538;
			double beta2 = 0.5726747513235538;
			double epsilon = 0.5726747513235538;
			double l2 = 0.002756262663063505;
			int layer1 = 335;
			int layer2 = 440;
		// ------------------
		// Accuracy: 0.981
		// Precision: 0.981
		// Recall: 0.981
		// F1: 0.981
		MultiLayerConfiguration conf = network(learningRate, beta1, beta2, epsilon, l2, layer1, layer2);
		
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.setListeners(new ScoreIterationListener(1), 
						new PerformanceListener(1, true),
						new CheckpointListener.Builder("out").keepAll().saveEveryNEpochs(1).build()); 
		
		System.out.println(network.summary());
		
		
		double accuracy = 0.0;
		double recall = 0.0;
		double precision = 0.0;
		double f1 = 0.0;
		double numOfTimes = 15.0;
		
		
		for (int i = 0; i < numOfTimes; i++) {
			
			MultiLayerNetwork model = new MultiLayerNetwork(conf.clone());
			model.init();
			
			data.shuffle();
			SplitTestAndTrain samples = data.splitTestAndTrain(0.8);
		
			DataSet training = samples.getTrain();
			DataSet testing = samples.getTest();
			
	
			for (int nEpochs = 0; nEpochs < 30; nEpochs++) {
				network.fit(training);
			}
			
			Evaluation eval = new Evaluation();
			eval.eval(testing.getLabels(), network.output(testing.getFeatures()));
	
			accuracy += eval.accuracy();
			precision += eval.precision();
			recall += eval.recall();
			f1 += eval.f1();
			
			System.out.println("Step " + (i + 1) + " Accuracy: [" + ff.format(eval.accuracy()) +
					"], Precision: [" + ff.format(eval.precision()) + 
					"], Recall: [" + ff.format(eval.recall()) +
					"], F1: [" + ff.format(eval.f1())+ "]");
		}
		
		System.out.println();
		System.out.println("Cross-Validation After " + (int) numOfTimes + " iterations");
		System.out.println("======================================");
		System.out.println("Accuracy: " + ff.format(accuracy / numOfTimes));
		System.out.println("Precision: " + ff.format(precision / numOfTimes));
		System.out.println("Recall: " + ff.format(recall / numOfTimes));
		System.out.println("F1: " + ff.format(f1 / numOfTimes));
	
		UIServer.getInstance().stop();
	}
	
	public static DataSet getData(String path) throws Exception {
		
		File dir = new File(path);
		File [] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.toLowerCase().endsWith(".png");
			}			
		});
		
		double [][] data = new double[files.length][];
		double [][] labels = new double[files.length][10];
		
		totalSamples = files.length;
		
		int index = 0;
		for (File file : files) {
			
			data[index] = ImageUtils.array(ImageUtils.image(file.getAbsolutePath()));
			int result = Integer.valueOf(file.getName().charAt(0) - '0');

			
			for (int i = 0; i < 10; i++) {
				if (result == i)
					labels[index][i] = 1.0;
				else
					labels[index][i] = 0.0;
			}
			
			index++;
		}
		
		return new DataSet(Nd4j.create(data), Nd4j.create(labels));
	}
	
	public static MultiLayerSpace network() {
		
		ParameterSpace<Double> learningRateHyperparam = new ContinuousParameterSpace(0.0001, 0.1);  
		ParameterSpace<Double> beta1Hyperparam = new ContinuousParameterSpace(0.0, 0.999); 
		ParameterSpace<Double> beta2Hyperparam = new ContinuousParameterSpace(0.0, 0.999); 
		ParameterSpace<Double> epsilonHyperparam = new ContinuousParameterSpace(0.0, 0.999); 
		ParameterSpace<Double> l2Hyperparam = new ContinuousParameterSpace(0.0001, 0.09);
        ParameterSpace<Integer> layer1BandwidthHyperparam = new IntegerParameterSpace(16, 756); 
        ParameterSpace<Integer> layer2BandwidthHyperparam = new IntegerParameterSpace(16, 756); 


		return new MultiLayerSpace.Builder()
				.seed(83)
				.weightInit(WeightInit.XAVIER)
				.biasInit(0.0)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new NadamSpace(learningRateHyperparam, beta1Hyperparam, beta2Hyperparam, epsilonHyperparam))
				.l2(l2Hyperparam)
				.numEpochs(30)
				.addLayer(new DenseLayerSpace.Builder()
								.nIn(784)
								.nOut(layer1BandwidthHyperparam)
								.activation(Activation.TANH)
								.build())
				.addLayer(new DenseLayerSpace.Builder()
								.nIn(layer1BandwidthHyperparam)
								.nOut(layer2BandwidthHyperparam)
								.activation(Activation.LEAKYRELU)
								.build())
				.addLayer(new OutputLayerSpace.Builder()
								.nIn(layer2BandwidthHyperparam)
								.nOut(10)
								.lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
								.activation(Activation.SOFTMAX)
								.build())
				.build();
	}
	
	public static MultiLayerConfiguration network(double learningRate, double beta1, double beta2, 
			double epsilon, double l2, int layer1Bandwidth, int layer2Bandwidth) {

		return new NeuralNetConfiguration.Builder()
				.weightInit(WeightInit.XAVIER)
				.biasInit(l2)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Nadam(learningRate, beta1, beta2, epsilon))
				.l2(l2)
				.list()
				.layer(0,
						new DenseLayer.Builder()
								.nIn(784)
								.nOut(layer1Bandwidth)
								.activation(Activation.TANH)
								.build())
				.layer(1,
						new DenseLayer.Builder()
								.nIn(layer1Bandwidth)
								.nOut(layer2Bandwidth)
								.activation(Activation.LEAKYRELU)
								.build())
				.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
								.nIn(layer2Bandwidth)
								.nOut(10)
								.activation(Activation.SOFTMAX)
								.build())
				.build();

	}
	
	public static class DigitsDataSource implements DataSource {
		
		private static final long serialVersionUID = 1L;
		
		private DataSet training = null;
		private DataSet testing = null;
		private int batchSize = 0;
		
		public DigitsDataSource() throws Exception {
			
			DataSet data = getData("img/digits");
			
			data.shuffle();
			SplitTestAndTrain samples = data.splitTestAndTrain(0.8);
		
			this.training = samples.getTrain();
			this.testing = samples.getTest();
		}
		
		@Override
		public void configure(Properties properties) {
			// TODO Auto-generated method stub
			this.batchSize = Integer.parseInt(properties.getProperty("minibatchSize", "10"));
		}

		@Override
		public Class<?> getDataType() {
			// TODO Auto-generated method stub
			return DataSetIterator.class;
		}

		@Override
		public Object testData() {
			// TODO Auto-generated method stub
			return new SamplingDataSetIterator(testing, batchSize, totalSamples);
		}

		@Override
		public Object trainData() {
			// TODO Auto-generated method stub
			return new SamplingDataSetIterator(training, batchSize, totalSamples);
		}
		 
		 
	}

}
