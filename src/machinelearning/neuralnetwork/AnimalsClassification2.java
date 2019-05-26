package machinelearning.neuralnetwork;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.io.labels.PathLabelGenerator;
import org.datavec.api.io.labels.PatternPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.FlipImageTransform;
import org.datavec.image.transform.ImageTransform;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.arbiter.ComputationGraphSpace;
import org.deeplearning4j.arbiter.layers.BatchNormalizationSpace;
import org.deeplearning4j.arbiter.layers.DenseLayerSpace;
import org.deeplearning4j.arbiter.layers.OutputLayerSpace;
import org.deeplearning4j.arbiter.layers.SeparableConvolution2DLayerSpace;
import org.deeplearning4j.arbiter.layers.SubsamplingLayerSpace;
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
import org.deeplearning4j.arbiter.task.ComputationGraphTaskCreator;
import org.deeplearning4j.arbiter.ui.listener.ArbiterStatusListener;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileGraphSaver;
import org.deeplearning4j.earlystopping.scorecalc.ClassificationScoreCalculator;
import org.deeplearning4j.earlystopping.termination.BestScoreEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingGraphTrainer;
import org.deeplearning4j.earlystopping.trainer.IEarlyStoppingTrainer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.CacheMode;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SeparableConvolution2D;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.PerformanceListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.deeplearning4j.ui.weights.ConvolutionalIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IAMax;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.lossfunctions.LossFunctions;


public class AnimalsClassification2 {
    
	// hyper-parameters
    protected static int batchSize = 30;
    protected static int epochs = 20;
    protected static int embedding = 128;
    
    protected static int height = 100;
    protected static int width = 100;
    protected static int channels = 3;
    protected static int numLabels = 4;
    
    protected static boolean save = false;
    protected static int maxPathsPerLabel = 90;
    protected static long seed = 83;
    protected static Random rng = new Random(seed);


	public void run(String[] args) throws Exception {
		
		FileUtils.cleanDirectory(new File("out")); 
		
		if (args.length > 0 && args[0].indexOf("O") >= 0) {

			System.out.println("Optimize model....");

			ComputationGraphSpace space = optimizeModel();

			CandidateGenerator candidateGenerator = new RandomSearchGenerator(space, null);
			Class<? extends DataSource> dataSourceClass = AnimalsDataSource.class;

			@SuppressWarnings("deprecation")
			ScoreFunction scoreFunction = new EvaluationScoreFunction(
					org.deeplearning4j.eval.Evaluation.Metric.ACCURACY);

			TerminationCondition[] terminationConditions = { 
					new MaxTimeCondition(8, TimeUnit.HOURS),
					new MaxCandidatesCondition(5) };

			ResultSaver modelSaver = new FileModelSaver("out");
			OptimizationConfiguration configuration = new OptimizationConfiguration.Builder()
					.candidateGenerator(candidateGenerator)
					.dataSource(dataSourceClass, new Properties())
					.modelSaver(modelSaver)
					.scoreFunction(scoreFunction)
					.terminationConditions(terminationConditions)
					.build();

			IOptimizationRunner runner = new LocalOptimizationRunner(configuration, new ComputationGraphTaskCreator());

			StatsStorage ss = new FileStatsStorage(new File("out/animalsStats.dl4j"));
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
			
		} else {
			
			// My own hyper-parameters: 
			// ========================
			// 	double learningRate = NA (AdaDelta);
			// 	double beta1 = NA (AdaDelta);
			// 	double beta2 = NA (AdaDelta); 
			// 	double epsilon = NA (AdaDelta);
			// 	double l2 = 0.005;
			// 	int layer1 = 64;
			// 	int layer4 = 128;
			//  int layer7 = 64;
			//  int layer10 = 80;
			// -----------------
			// Accuracy: 0.825
			// Precision: 0.800
			// Recall: 0.825
			// F1: 0.800
			
			System.out.println("Build model....");

			ComputationGraph network = playModel();
			network.init();

			// test iterator
			DataSetIterator testIter = getIterator("img/test_animals", new PatternPathLabelGenerator("_", 0));

			// listeners
			network.setListeners(new ScoreIterationListener(10), 
					new PerformanceListener(10, false),
					new ConvolutionalIterationListener(10, true),
					new EvaluativeListener(testIter, 1, InvocationType.EPOCH_END));

			System.out.println(network.summary());

			// Train without transformations
			DataSetIterator trainIter1 = getIterator("img/animals", new ParentPathLabelGenerator());
			DataSetIterator trainIter2 = getIterator("img/animals", new ParentPathLabelGenerator(),
					new FlipImageTransform(1));
			
			DataSetIterator trainIter = new MultIteratorsIterator(trainIter1, trainIter2);

			/*
			 * List<Pair<ImageTransform,Double>> pipeline = Arrays.asList( 
			 * 					new Pair<>(new FlipImageTransform(1), 0.6), 
			 * 					new Pair<>(new ScaleImageTransform(rng, 30), 0.7), 
			 * 					new Pair<>(new CropImageTransform(rng, 20), 0.7), 
			 * 					new Pair<>(new EqualizeHistTransform(rng, 39), 0.7), 
			 * 					new Pair<>(new ColorConversionTransform(rng, 1), 0.7), 
			 * 					new Pair<>(new WarpImageTransform(rng, 70), 0.7));
			 * 
			 * DataSetIterator trainIter3 = getIterator("img/animals", 
			 * 									new ParentPathLabelGenerator(), 
			 * 										new PipelineImageTransform(pipeline, true));
			 */

			System.out.println("Train model....");
			
			testIter.reset();
			
			EarlyStoppingModelSaver<ComputationGraph> saver = new LocalFileGraphSaver("out");
			
			EarlyStoppingConfiguration<ComputationGraph> eac = new EarlyStoppingConfiguration.Builder<ComputationGraph>()
					.epochTerminationConditions(new BestScoreEpochTerminationCondition(0.875))
					.scoreCalculator(new ClassificationScoreCalculator(
							org.nd4j.evaluation.classification.Evaluation.Metric.ACCURACY, testIter))
					.evaluateEveryNEpochs(1)
					.modelSaver(saver)
					.build();	
		
			IEarlyStoppingTrainer<ComputationGraph> trainer = new EarlyStoppingGraphTrainer(eac, network, trainIter);
			
			System.out.println("Training model....");
			EarlyStoppingResult<ComputationGraph> result = trainer.fit();
			
			System.out.println("Termination reason: " + result.getTerminationReason());
	        System.out.println("Termination details: " + result.getTerminationDetails());
	        System.out.println("Total epochs: " + result.getTotalEpochs());
	        System.out.println("Best epoch number: " + result.getBestModelEpoch());
	        System.out.println("Score at best epoch: " + result.getBestModelScore());
	        
	        Map<Integer,Double> epochVsScore = result.getScoreVsEpoch();
	        List<Integer> list = new ArrayList<Integer>(epochVsScore.keySet());
	        Collections.sort(list);
	        System.out.println("Epoch\tScore");
	        for( Integer i : list){
	            System.out.println(i + "\t" + epochVsScore.get(i));
	        }
			
					
			// Evaluation test samples
			System.out.println("My Own Evaluation....");
			eval(network);

			if (save) {
				System.out.println("Save model....");
				network.save(new File("out/playnet.model"));
			}
			System.out.println("****************Example finished********************");
		}
	}
    
    private void eval(ComputationGraph network) throws Exception {
    	
    	// Evaluation test samples
    	DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
    	RecordReaderDataSetIterator iter = getTestData("img/test_animals");
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        
        double total = 0.0;
        double correct = 0.0;
        
        while (iter.hasNext()) {
        	
        	DataSet data = iter.next();
        	ImageRecordReader reader = (ImageRecordReader) iter.getRecordReader();
        	File file = reader.getCurrentFile();
        	
        	List<String> allClassLabels = iter.getLabels();
            int labelIndex = data.getLabels().argMax(1).getInt(0);
            INDArray [] predictedClasses = network.output(false, data.getFeatures());
            
            int expected = Nd4j.getExecutioner().exec(new IAMax(predictedClasses[0])).getInt(0);
            		
            String expectedResult = allClassLabels.get(labelIndex);
            String modelPrediction = allClassLabels.get(expected);
            
            if (expectedResult.equals(modelPrediction)) {
            	correct++;
            	System.out.println(file.getName() + " labeled [" + expectedResult + "], and the model predicted [" + modelPrediction + "]");
            }
            else {
            	System.err.println(file.getName() + " labeled [" + expectedResult + "], but the model predicted [" + modelPrediction + "]");
            }
            total++;
        }
        
        System.out.println("Accuracy: " + correct / total);
    }
    
    public ComputationGraphSpace optimizeModel() {
    	
		ParameterSpace<Double> l2Hyperparam = new ContinuousParameterSpace(0.0001, 0.09);
        ParameterSpace<Integer> layer1BandwidthHyperparam = new IntegerParameterSpace(32, 756); 
        ParameterSpace<Integer> layer4BandwidthHyperparam = new IntegerParameterSpace(32, 756); 
        ParameterSpace<Integer> layer7BandwidthHyperparam = new IntegerParameterSpace(32, 756);
        ParameterSpace<Integer> layer10BandwidthHyperparam = new IntegerParameterSpace(32, 2048);
        
        return new ComputationGraphSpace.Builder() 
			.seed(seed)
			.l2(l2Hyperparam)
			.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
			.weightInit(WeightInit.RELU)
			.activation(Activation.RELU)
			.updater(new AdaDelta())
			.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
			.convolutionMode(ConvolutionMode.Same)
			.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
			.trainingWorkspaceMode(WorkspaceMode.ENABLED)
			.numEpochs(epochs)
			
			.addInputs("inputs1").setInputTypes(InputType.convolutional(height, width, channels))
			
			.addLayer("1.1-5x5", convolspace(channels, layer1BandwidthHyperparam, new int[] { 5, 5 }), 
					"inputs1")	
			.addLayer("1.2-batch", batchspace(), 
					"1.1-5x5")
			.addLayer("1.3-maxpool", poolspace(new int[] { 2, 2 }, new int[] { 2, 2 }), 
					"1.2-batch")
			
			.addLayer("1.4-3x3", convolspace(layer1BandwidthHyperparam, layer4BandwidthHyperparam, new int[] { 3, 3 }),
					"1.3-maxpool")
			.addLayer("1.5-batch", batchspace(), 
					"1.4-3x3")
			.addLayer("1.6-maxpool", poolspace(new int[] { 2, 2 }, new int[] { 2, 2 }),
					"1.5-batch")
			
			.addLayer("1.7-3x3", convolspace(layer4BandwidthHyperparam, layer7BandwidthHyperparam, new int[] { 3, 3 }),
					"1.6-maxpool")
			.addLayer("1.8-batch", batchspace(), 
					"1.7-3x3")
			.addLayer("1.9-maxpool", poolspace(new int[] { 2, 2 }, new int[] { 2, 2 }),
					"1.8-batch")
	
			.addLayer("1.10-dense", new DenseLayerSpace.Builder()
					.nOut(layer10BandwidthHyperparam)
					.dropOut(0.5)
					.build(), 
					"1.9-maxpool")
			.addLayer("1.11-output", new OutputLayerSpace.Builder()
					.lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
					.nIn(layer10BandwidthHyperparam)
					.nOut(numLabels)
	                .activation(Activation.SOFTMAX)
					.build(), 
					"1.10-dense")
			
			.setOutputs("1.11-output")
            .build();
		
    }
    
    public ComputationGraph playModel() {
    	
    	ComputationGraphConfiguration.GraphBuilder graph = new NeuralNetConfiguration.Builder()
				.seed(seed)
				.l2(0.005)
				.cacheMode(CacheMode.HOST)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.weightInit(WeightInit.RELU)
				.activation(Activation.RELU)
				.updater(new AdaDelta())
				.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
				.convolutionMode(ConvolutionMode.Same)
				.cudnnAlgoMode(ConvolutionLayer.AlgoMode.PREFER_FASTEST)
				.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
				.trainingWorkspaceMode(WorkspaceMode.ENABLED)
				.graphBuilder();

		ComputationGraphConfiguration conf = graph
				
			.addInputs("inputs1").setInputTypes(InputType.convolutional(height, width, channels))
			
			.addLayer("1.1-5x5", convolution("5x5c", channels, 64, new int[] { 5, 5 }), 
					"inputs1")	
			.addLayer("1.2-batch", batchNormalization("batch1"), 
					"1.1-5x5")
			.addLayer("1.3-maxpool", maxPooling("maxpool1", new int[] { 2, 2 }, new int[] { 2, 2 }), 
					"1.2-batch")
			
			.addLayer("1.4-3x3", convolution("3x3c1", 64, 128, new int[] { 3, 3 }),
					"1.3-maxpool")
			.addLayer("1.5-batch", batchNormalization("batch2"), 
					"1.4-3x3")
			.addLayer("1.6-maxpool", maxPooling("maxpool2", new int[] { 2, 2 }, new int[] { 2, 2 }),
					"1.5-batch")
			
			.addLayer("1.7-3x3", convolution("3x3c2", 128, 64, new int[] { 3, 3 }),
					"1.6-maxpool")
			.addLayer("1.8-batch", batchNormalization("batch3"), 
					"1.7-3x3")
			.addLayer("1.9-maxpool", maxPooling("maxpool3", new int[] { 2, 2 }, new int[] { 2, 2 }),
					"1.8-batch")
	
			.addLayer("1.10-dense", new DenseLayer.Builder().nOut(80).dropOut(0.5).build(), 
					"1.9-maxpool")
			.addLayer("1.11-output", new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
					.nOut(numLabels)
	                .activation(Activation.SOFTMAX)
					.build(), 
					"1.10-dense")
			
			.setOutputs("1.11-output")
            .build();
		
		
		
		return new ComputationGraph(conf);
    }
   
    public static SubsamplingLayer maxPooling(String name, int [] ... args) {
    	
    	int [] kernel = null;
    	int [] stride = null;
    	int [] pad = null;
    	
    	int index = 0;
    	for (int [] arg : args) {
    		switch(index) {
    		case 0:
    			kernel = arg;
    		break;
    		case 1:
    			stride = arg;
    		break;
    		default:
    			pad = arg;
    		}
    		index++;
    	}
    	
    	if (stride == null) {
    		stride = new int[] { 1, 1 };
    	}
    	
    	if (pad == null) {
    		pad = new int[] { 0, 0 };
    	}
    	
        return new SubsamplingLayer.Builder(kernel, stride, pad).name(name).build();
    }
    
    public static SubsamplingLayerSpace poolspace(int [] ... args) {
    	
    	int [] kernel = null;
    	int [] stride = null;
    	int [] pad = null;
    	
    	int index = 0;
    	for (int [] arg : args) {
    		switch(index) {
    		case 0:
    			kernel = arg;
    		break;
    		case 1:
    			stride = arg;
    		break;
    		default:
    			pad = arg;
    		}
    		index++;
    	}
    	
    	if (stride == null) {
    		stride = new int[] { 1, 1 };
    	}
    	
    	if (pad == null) {
    		pad = new int[] { 0, 0 };
    	}
    	
    	return new SubsamplingLayerSpace.Builder().kernelSize(kernel).stride(stride).padding(pad).build();
    }
  
    public static BatchNormalization batchNormalization(String name) {
    	return new BatchNormalization.Builder(false).name(name).build();
    }
    
    public static BatchNormalizationSpace batchspace() {
    	return new BatchNormalizationSpace.Builder().lockGammaBeta(false).build();
    }
    
    public static ConvolutionLayer convolution(String name, int in, int out, int [] ... args) {
    	return convolution(name, in, out, Activation.RELU, args);
    }
        
    public static ConvolutionLayer convolution(String name, int in, int out, Activation activation, int [] ... args) {
    	   	
    	int [] kernel = null;
    	int [] stride = null;
    	int [] pad = null;
    	
    	int index = 0;
    	for (int [] arg : args) {
    		switch(index) {
    		case 0:
    			kernel = arg;
    		break;
    		case 1:
    			stride = arg;
    		break;
    		default:
    			pad = arg;
    		}
    		index++;
    	}
    	
    	if (stride == null) {
    		stride = new int[] { 1, 1 };
    	}
    	
    	if (pad == null) {
    		pad = new int[] { 0, 0 };
    	}
    
    	return new SeparableConvolution2D.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).depthMultiplier(2).activation(activation).build();   	
    }
    
    public static SeparableConvolution2DLayerSpace convolspace(int in, int out, int [] ... args) {
    	return convolspace(in, out, null, null, Activation.RELU, args);
    }
    
    public static SeparableConvolution2DLayerSpace convolspace(int in, ParameterSpace<Integer> out, int [] ... args) {
    	return convolspace(in, null, null, out, Activation.RELU, args);
    }
    
    public static SeparableConvolution2DLayerSpace convolspace(ParameterSpace<Integer> in, int out, int [] ... args) {
    	return convolspace(null, out, in, null, Activation.RELU, args);
    }
    
    public static SeparableConvolution2DLayerSpace convolspace(ParameterSpace<Integer> in, ParameterSpace<Integer> out, int [] ... args) {
    	return convolspace(null, null, in, out, Activation.RELU, args);
    }
    
    public static SeparableConvolution2DLayerSpace convolspace(Integer in, Integer out, ParameterSpace<Integer> ins, ParameterSpace<Integer> outs, Activation activation, int [] ... args) {
    	
    	int [] kernel = null;
    	int [] stride = null;
    	int [] pad = null;
    	
    	int index = 0;
    	for (int [] arg : args) {
    		switch(index) {
    		case 0:
    			kernel = arg;
    		break;
    		case 1:
    			stride = arg;
    		break;
    		default:
    			pad = arg;
    		}
    		
    		index++;
    	}
    	
    	if (stride == null) {
    		stride = new int[] { 1, 1 };
    	}
    	
    	if (pad == null) {
    		pad = new int[] { 0, 0 };
    	}
    	
    	if (in != null && out != null && ins == null && outs == null) {
    		return new SeparableConvolution2DLayerSpace.Builder().nIn(in).nOut(out).kernelSize(kernel).stride(stride).padding(pad).depthMultiplier(2).activation(activation).build();   
    	}
    	else
    	if (in != null && out == null && ins == null && outs != null) {   		
    		return new SeparableConvolution2DLayerSpace.Builder().nIn(in).nOut(outs).kernelSize(kernel).stride(stride).padding(pad).depthMultiplier(2).activation(activation).build();  
    	}
    	else
    	if (in == null && out != null && ins != null && outs == null) {   		
    		return new SeparableConvolution2DLayerSpace.Builder().nIn(ins).nOut(out).kernelSize(kernel).stride(stride).padding(pad).depthMultiplier(2).activation(activation).build();
    	}
    	else {
    		return new SeparableConvolution2DLayerSpace.Builder().nIn(ins).nOut(outs).kernelSize(kernel).stride(stride).padding(pad).depthMultiplier(2).activation(activation).build(); 
    	}
    }
    
    private static DataSetIterator getIterator(String pathName, PathLabelGenerator labelMaker) throws Exception {
    	return getIterator(pathName, labelMaker, null);
    }
    
    private static DataSetIterator getIterator(String pathName, PathLabelGenerator labelMaker, ImageTransform transform) throws Exception {

		FileSplit fileSplit = new FileSplit(new File(pathName), NativeImageLoader.ALLOWED_FORMATS, rng);
		int numExamples = Math.toIntExact(fileSplit.length());
		BalancedPathFilter pathFilter = new BalancedPathFilter(rng, labelMaker, numExamples, numLabels,
				maxPathsPerLabel);

		/**
		 * Data Setup -> train test split - inputSplit = define train and test split
		 **/
		InputSplit[] inputSplit = fileSplit.sample(pathFilter, 1.0, 0.0);
		InputSplit data = inputSplit[0];

		ImageRecordReader reader = new ImageRecordReader(height, width, channels, labelMaker);
		reader.initialize(data, transform);
		
		DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
		DataSetIterator iter = new RecordReaderDataSetIterator(reader, batchSize, 1, numLabels);
        scaler.fit(iter);
        iter.setPreProcessor(scaler);
        
        return iter;
	}
  
    public RecordReaderDataSetIterator getTestData(String path) throws Exception {
    	
    	FileSplit fileSplit = new FileSplit(new File(path), NativeImageLoader.ALLOWED_FORMATS);
    
    	InputSplit [] inputSplit = fileSplit.sample(null);
        InputSplit data = inputSplit[0];
        
        PatternPathLabelGenerator labelDecider = new PatternPathLabelGenerator("_", 0);
        ImageRecordReader reader = new ImageRecordReader(height, width, channels, labelDecider);
        reader.initialize(data);
    	
 	
    	return new RecordReaderDataSetIterator(reader, 1, 1, 4);
    }

    public static void main(String[] args) throws Exception {
        new AnimalsClassification2().run(args);
    }
    
    
    public static class AnimalsDataSource implements DataSource {
		
		private static final long serialVersionUID = 1L;
		
		private DataSetIterator training = null;
		private DataSetIterator testing = null;
		
		public AnimalsDataSource() throws Exception {
			
			DataSetIterator trainIter1 = getIterator("img/animals", new ParentPathLabelGenerator());
			DataSetIterator trainIter2 = getIterator("img/animals", new ParentPathLabelGenerator(), new FlipImageTransform(1));
			DataSetIterator testIter1 = getIterator("img/test_animals", new PatternPathLabelGenerator("_", 0));
					
			this.training = new MultIteratorsIterator(trainIter1, trainIter2);
			this.testing = testIter1;
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