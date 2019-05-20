package machinelearning.neuralnetwork;


import java.io.File;
import java.util.List;
import java.util.Random;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.io.labels.PatternPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.FlipImageTransform;
import org.datavec.image.transform.ImageTransform;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.graph.MergeVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ActivationLayer;
import org.deeplearning4j.nn.conf.layers.BatchNormalization;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.lossfunctions.LossFunctions;


public class AnimalsClassification2 {
    
    protected static int height = 100;
    protected static int width = 100;
    protected static int channels = 3;
    protected static int batchSize = 30;
  
    protected static long seed = 83;
    protected static Random rng = new Random(seed);
    protected static int epochs = 32;
    protected static double splitTrainTest = 0.9;
    protected static boolean save = true;
    protected static int maxPathsPerLabel = 90;

    private int numLabels;

    public void run(String[] args) throws Exception {
        System.out.println("Load data....");
        /**
         * Data Setup -> organize and limit data file paths:
         *  - mainPath = path to image files
         *  - fileSplit = define basic dataset split with limits on format
         *  - pathFilter = define additional file load filter to limit size and balance batch content
         **/
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
        FileSplit fileSplit = new FileSplit(new File("img/animals"), 
        							NativeImageLoader.ALLOWED_FORMATS, rng);
        int numExamples = Math.toIntExact(fileSplit.length());
        numLabels = fileSplit.getRootDir().listFiles(File::isDirectory).length;
        BalancedPathFilter pathFilter = new BalancedPathFilter(rng, labelMaker, numExamples, numLabels, maxPathsPerLabel);

        /**
         * Data Setup -> train test split
         *  - inputSplit = define train and test split
         **/
        InputSplit[] inputSplit = fileSplit.sample(pathFilter, splitTrainTest, 1 - splitTrainTest);
        InputSplit trainData = inputSplit[0];
        InputSplit testData = inputSplit[1];
        
        
        /**
         * Data Setup -> normalization
         *  - how to normalize images and generate large dataset to train on
         **/
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);

        System.out.println("Build model....");

        // Uncomment below to try AlexNet. Note change height and width to at least 100
        // MultiLayerNetwork network = new AlexNet(height, width, channels, numLabels, 
        // seed, iterations).init();

        ComputationGraph network = skynetModel();
        network.init();
       
        /**
         * Data Setup -> define how to load data into net:
         *  - recordReader = the reader that loads and converts image data pass in inputSplit to initialize
         *  - dataIter = a generator that only loads one batch at a time into memory to save memory
         *  - trainIter = uses MultipleEpochsIterator to ensure model runs through the data for all epochs
         **/
        ImageRecordReader trainRR = new ImageRecordReader(height, width, channels, labelMaker);
        DataSetIterator trainIter;


        System.out.println("Train model....");
        // test iterator
        ImageRecordReader testRR = new ImageRecordReader(height, width, channels, labelMaker);
        testRR.initialize(testData);
        DataSetIterator testIter = new RecordReaderDataSetIterator(testRR, batchSize, 1, numLabels);
        scaler.fit(testIter);
        testIter.setPreProcessor(scaler);

        // listeners
        network.setListeners(new ScoreIterationListener(10), 
        					new EvaluativeListener(testIter, 10, InvocationType.EPOCH_END));
        
        System.out.println(network.summary());

        // Train without transformations
        trainRR.initialize(trainData);
        trainIter = new RecordReaderDataSetIterator(trainRR, batchSize, 1, numLabels);
        scaler.fit(trainIter);
        trainIter.setPreProcessor(scaler);
        network.fit(trainIter, epochs);
        
        // Train without transformations #10
       
        for (int i = 0; i < 1; i++) {       	
        	network.fit(getIterator(trainRR, trainData), epochs);
        }
       

        
        // Evaluation test samples
        eval(network);
        
       
        if (save) {
            System.out.println("Save model....");
            network.save(new File("data/skynet.model"));
        }
        System.out.println("****************Example finished********************");
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
            String expectedResult = allClassLabels.get(labelIndex);
            String modelPrediction = allClassLabels.get(predictedClasses[0].getInt(0));
            
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

   
    public ComputationGraph skynetModel() {
    	
		ComputationGraphConfiguration.GraphBuilder graph = new NeuralNetConfiguration.Builder()
				.seed(seed).l2(0.005)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.activation(Activation.RELU)
				.weightInit(WeightInit.XAVIER)
				.updater(new AdaDelta())
				.convolutionMode(ConvolutionMode.Same)
				.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
				.trainingWorkspaceMode(WorkspaceMode.ENABLED)
				.graphBuilder();

		ComputationGraphConfiguration conf = graph
			.addInputs("inputs1").setInputTypes(InputType.convolutional(height, width, channels))
			
			.addLayer("incept-1-1", convolution("1x1c", channels, 1, new int[] { 1, 1 }, new int[] { 1, 1 }), 
					"inputs1")
			.addLayer("incept-1-2", batchNormalization("batch1"), 
					"incept-1-1")
			.addLayer("incept-1-3", activation("activation1", Activation.RELU), 
					"incept-1-2")
		
			.addLayer("incept-2-1", convolution("1x1c", channels, 1, new int[] { 1, 1 }, new int[] { 1, 1 }), 
					"inputs1")
			.addLayer("incept-2-2", convolution("3x3c", 50, 50, new int[] { 3, 3 }, new int[] { 1, 1 }, new int [] { 0, 0 }), 
					"incept-2-1")	
			.addLayer("incept-2-3a", convolution("3x1c", 50, 50, new int[] { 3, 1 }, new int[] { 1, 1 }, new int [] { 0, 0 }), 
					"incept-2-2")
			.addLayer("incept-2-3b", convolution("1x3c", 50, 50, new int[] { 1, 3 }, new int[] { 1, 1 }, new int [] { 0, 0 }), 
					"incept-2-2")		
			.addVertex("vertex-2-4", new MergeVertex(), 
					"incept-2-3a", "incept-2-3b")
			.addLayer("incept-2-5", batchNormalization("batch1"), 
					"vertex-2-4")
			.addLayer("incept-2-6", activation("activation1", Activation.RELU), 
					"incept-2-5")
						
			.addLayer("incept-3-1", convolution("1x1c", channels, 1, new int[] { 1, 1 }, new int[] { 1, 1 }),
					"inputs1")
			.addLayer("incept-3-2a", convolution("3x1c", 50, 50, new int[] { 3, 1 }, new int[] { 1, 1 }), 
					"incept-3-1")
			.addLayer("incept-3-2b", convolution("1x3c", 50, 50, new int[] { 1, 3 }, new int[] { 1, 1 }), 
					"incept-3-1")			
			.addVertex("vertex-3-3", new MergeVertex(), 
					"incept-3-2a", "incept-3-2b")
			.addLayer("incept-3-4", batchNormalization("batch1"), 
					"vertex-3-3")
			.addLayer("incept-3-5", activation("activation1", Activation.RELU), 
					"incept-3-4")
		
			.addVertex("incept-123", new MergeVertex(),
					"incept-1-3", "incept-2-6", "incept-3-5")
			
			.addLayer("incept-4-1", maxPooling("maxpool4", new int[] { 2, 2 }), 
					"incept-123")
		
			.addLayer("incept-5", new DenseLayer.Builder().nIn(500).nOut(500).dropOut(0.5).build(), 
					"incept-4-1")
			
			.addLayer("incept-6", new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
					.nIn(500)
					.nOut(numLabels)
                .activation(Activation.SOFTMAX).build(), 
                "incept-5")
			
			.setOutputs("incept-6")
            .build();
		
		

        return new ComputationGraph(conf);
    }
    
    private static SubsamplingLayer maxPooling(String name, int [] ... args) {
    	
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
    
    public static ActivationLayer activation(String name, Activation algo) {
    	return new ActivationLayer.Builder().name(name).activation(algo).dropOut(0.2).build();
    }
    
    public static BatchNormalization batchNormalization(String name) {
    	return new BatchNormalization.Builder(false).name(name).build();
    }
    
    public static ConvolutionLayer convolution(String name, int in, int out, int [] ... args) {
    	
    	
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
    	
    	return new ConvolutionLayer.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).build();   	
    }

    public DataSetIterator getIterator(ImageRecordReader trainRR, InputSplit trainData) throws Exception {
    	
    	DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        ImageTransform vFlipTransform = new FlipImageTransform(1);
/*
        ImageTransform warpTransform = new WarpImageTransform(rng, 30);
        ImageTransform rotateTransform = new RotateImageTransform(rng, 90);     
   
        List<Pair<ImageTransform, Double>> pipeline = Arrays.asList(
        															new Pair<>(vFlipTransform, 0.5),       														 
        															new Pair<>(rotateTransform, 0.5),       													
                                                                    new Pair<>(warpTransform, 0.5)
        															);

        ImageTransform transform = new PipelineImageTransform(pipeline, true);
*/       
        trainRR.initialize(trainData, vFlipTransform);
        DataSetIterator trainIter = new RecordReaderDataSetIterator(trainRR, batchSize, 1, numLabels);
        scaler.fit(trainIter);
        trainIter.setPreProcessor(scaler);
        
        return trainIter;

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
}