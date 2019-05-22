package machinelearning.neuralnetwork;


import java.io.File;
import java.util.List;
import java.util.Random;

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
    protected static int epochs = 30;
    protected static int embedding = 128;
    
    protected static int height = 100;
    protected static int width = 100;
    protected static int channels = 3;
    protected static int numLabels = 4;
    
    protected static boolean save = false;
    protected static double splitTrainTest = 0.9;
    protected static int maxPathsPerLabel = 90;
    protected static long seed = 83;
    protected static Random rng = new Random(seed);


	public void run(String[] args) throws Exception {

		System.out.println("Build model....");

		// Uncomment below to try AlexNet. Note change height and width to at least 100
		// MultiLayerNetwork network = new AlexNet(height, width, channels, numLabels,
		// seed, iterations).init();

		ComputationGraph network = playModel();
		network.init();
		
		
		// test iterator
		DataSetIterator testIter = getIterator("img/test_animals", new PatternPathLabelGenerator("_", 0));


		// listeners
		network.setListeners(new ScoreIterationListener(10),
				new EvaluativeListener(testIter, 10, InvocationType.EPOCH_END));

		System.out.println(network.summary());

		// Train without transformations
		DataSetIterator trainIter1 = getIterator("img/animals", new ParentPathLabelGenerator());
		
		ImageTransform transform = new FlipImageTransform(1);
		DataSetIterator trainIter2 = getIterator("img/animals", new ParentPathLabelGenerator(), transform);
		

		System.out.println("Train model....");
		network.fit(trainIter1, epochs);
		network.fit(trainIter2, epochs);

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
			
			.addLayer("1.1-5x5", convolution("5x5c", channels, 64, new int[] { 5, 5 }), 
					"inputs1")	
			.addLayer("1.2-maxpool", maxPooling("maxpool1", new int[] { 2, 2 }, new int[] { 2, 2 }), 
					"1.1-5x5")
			
			.addLayer("1.3-3x3", convolution("3x3c", 64, 128, new int[] { 3, 3 }),
					"1.2-maxpool")
			.addLayer("1.4-maxpool", maxPooling("maxpool2", new int[] { 2, 2 }, new int[] { 2, 2 }),
					"1.3-3x3")
			
			.addLayer("1.5-3x3", convolution("3x3c", 128, 64, new int[] { 3, 3 }),
					"1.4-maxpool")
			.addLayer("1.6-maxpool", maxPooling("maxpool2", new int[] { 2, 2 }, new int[] { 2, 2 }),
					"1.5-3x3")
	
			.addLayer("1.7-dense", new DenseLayer.Builder().nOut(50).dropOut(0.5).build(), 
					"1.6-maxpool")
			.addLayer("1.8-output", new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
					.nOut(numLabels)
	                .activation(Activation.SOFTMAX)
					.build(), 
					"1.7-dense")
			
			.setOutputs("1.8-output")
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
    
    private DataSetIterator getIterator(String pathName, PathLabelGenerator labelMaker) throws Exception {
    	return getIterator(pathName, labelMaker, null);
    }
    
    private DataSetIterator getIterator(String pathName, PathLabelGenerator labelMaker, ImageTransform transform) throws Exception {

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
}