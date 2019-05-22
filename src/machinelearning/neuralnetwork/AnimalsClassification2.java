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
    protected static double learningRate = 0.05;
    protected static int epochs = 26;
    protected static int embedding = 128;
    
    protected static int height = 100;
    protected static int width = 100;
    protected static int channels = 3;
    
    protected static boolean save = false;
    protected static double splitTrainTest = 0.9;
    protected static int maxPathsPerLabel = 90;
    protected static long seed = 83;
    protected static Random rng = new Random(seed);

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

        ComputationGraph network = playModel();
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

    
    public ComputationGraph playModel() {
    	
    	ComputationGraphConfiguration.GraphBuilder graph = new NeuralNetConfiguration.Builder()
				.seed(seed)
				.l2(0.005)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.weightInit(WeightInit.XAVIER)
				.activation(Activation.RELU)
				.updater(new AdaDelta())
		//		.cudnnAlgoMode(ConvolutionLayer.AlgoMode.PREFER_FASTEST)
		//		.gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
				.convolutionMode(ConvolutionMode.Same)
		//		.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
		//		.trainingWorkspaceMode(WorkspaceMode.ENABLED)
				.graphBuilder();

		ComputationGraphConfiguration conf = graph
				
			.addInputs("inputs1").setInputTypes(InputType.convolutional(height, width, channels))
			
			.addLayer("1.1-5x5", convolution("5x5c", channels, 32, new int[] { 5, 5 }), 
					"inputs1")	
			.addLayer("1.2-maxpool", maxPooling("maxpool1", new int[] { 2, 2 }, new int[] { 2, 2 }), 
					"1.1-5x5")
			
			.addLayer("1.3-5x5", convolution("5x5c", 32, 64, new int[] { 5, 5 }),
					"1.2-maxpool")
			.addLayer("1.4-maxpool", maxPooling("maxpool2", new int[] { 2, 2 }, new int[] { 2, 2 }),
					"1.3-5x5")
	
			.addLayer("1.5-dense", new DenseLayer.Builder().nOut(80).build(), 
					"1.4-maxpool")
			.addLayer("1.6-output", new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
					.nOut(numLabels)
	                .activation(Activation.SOFTMAX)
					.build(), 
					"1.5-dense")
			
			.setOutputs("1.6-output")
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
    	return new ActivationLayer.Builder().name(name).activation(algo).dropOut(0.1).build();
    }
    
    public static BatchNormalization batchNormalization(String name) {
    	return new BatchNormalization.Builder(false).name(name).build();
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
    
    	return new ConvolutionLayer.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).activation(activation).build();   	
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