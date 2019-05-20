package machinelearning.neuralnetwork;


import java.io.File;
import java.util.Arrays;
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
import org.datavec.image.transform.PipelineImageTransform;
import org.datavec.image.transform.RotateImageTransform;
import org.datavec.image.transform.WarpImageTransform;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.Distribution;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.inputs.InvalidInputTypeException;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.LocalResponseNormalization;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.primitives.Pair;

/**
 * Animal Classification
 *
 * Example classification of photos from 4 different animals (bear, duck, deer, turtle).
 *
 * References:
 *  - U.S. Fish and Wildlife Service (animal sample dataset): http://digitalmedia.fws.gov/cdm/
 *  - Tiny ImageNet Classification with CNN: http://cs231n.stanford.edu/reports/2015/pdfs/leonyao_final.pdf
 *
 * CHALLENGE: Current setup gets low score results. Can you improve the scores? Some approaches:
 *  - Add additional images to the dataset
 *  - Apply more transforms to dataset
 *  - Increase epochs
 *  - Try different model configurations
 *  - Tune by adjusting learning rate, updaters, activation & loss functions, regularization, ...
 */

public class AnimalsClassification {
    
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

    // LeNet, AlexNet or Custom but you need to fill it out
    protected static String modelType = "LeNet"; 
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

        MultiLayerNetwork network;
        switch (modelType) {
            case "LeNet":
                network = lenetModel();
                break;
            case "AlexNet":
                network = alexnetModel();
                break;
            case "custom":
                network = customModel();
                break;
            default:
                throw new InvalidInputTypeException("Incorrect model provided.");
        }
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
            network.save(new File("data/lenet.model"));
        }
        System.out.println("****************Example finished********************");
    }
    
    private void eval(MultiLayerNetwork network) throws Exception {
    	
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
            int[] predictedClasses = network.predict(data.getFeatures());
            String expectedResult = allClassLabels.get(labelIndex);
            String modelPrediction = allClassLabels.get(predictedClasses[0]);
            
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

    private ConvolutionLayer convInit(String name, int in, int out, int[] kernel, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).biasInit(bias).build();
    }

    private ConvolutionLayer conv3x3(String name, int out, double bias) {
        return new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {1,1}).name(name).nOut(out).biasInit(bias).build();
    }

    private ConvolutionLayer conv5x5(String name, int out, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(new int[]{5,5}, stride, pad).name(name).nOut(out).biasInit(bias).build();
    }

    private SubsamplingLayer maxPool(String name,  int[] kernel) {
        return new SubsamplingLayer.Builder(kernel, new int[]{2,2}).name(name).build();
    }

    private DenseLayer fullyConnected(String name, int out, double bias, double dropOut, Distribution dist) {
        return new DenseLayer.Builder().name(name).nOut(out).biasInit(bias).dropOut(dropOut).build();
    }

    public MultiLayerNetwork lenetModel() {
        /**
         * Revisde Lenet Model approach developed by ramgo2 achieves slightly above random
         * Reference: https://gist.github.com/ramgo2/833f12e92359a2da9e5c2fb6333351c5
         **/
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .l2(0.005)
            .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
            .activation(Activation.RELU)
            .weightInit(WeightInit.XAVIER)
//            .updater(new Nadam(1e-4))
            .updater(new AdaDelta())
            .list()
            .layer(0, convInit("cnn1", channels, 50 ,  new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0}, 0))
            .layer(1, maxPool("maxpool1", new int[]{2,2}))
            .layer(2, conv5x5("cnn2", 100, new int[]{5, 5}, new int[]{1, 1}, 0))
            .layer(3, maxPool("maxool2", new int[]{2,2}))
            .layer(4, new DenseLayer.Builder().nOut(500).build())
            .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(numLabels)
                .activation(Activation.SOFTMAX)
                .build())
            .setInputType(InputType.convolutional(height, width, channels))
            .build();

        return new MultiLayerNetwork(conf);

    }

    public MultiLayerNetwork alexnetModel() {
        /**
         * AlexNet model interpretation based on the original paper ImageNet Classification with Deep Convolutional Neural Networks
         * and the imagenetExample code referenced.
         * http://papers.nips.cc/paper/4824-imagenet-classification-with-deep-convolutional-neural-networks.pdf
         **/

        double nonZeroBias = 1;
        double dropOut = 0.5;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .weightInit(new NormalDistribution(0.0, 0.01))
            .activation(Activation.RELU)
            .updater(new AdaDelta())
            .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer) // normalize to prevent vanishing or exploding gradients
            .l2(5 * 1e-4)
            .list()
            .layer(convInit("cnn1", channels, 96, new int[]{11, 11}, new int[]{4, 4}, new int[]{3, 3}, 0))
            .layer(new LocalResponseNormalization.Builder().name("lrn1").build())
            .layer(maxPool("maxpool1", new int[]{3,3}))
            .layer(conv5x5("cnn2", 256, new int[] {1,1}, new int[] {2,2}, nonZeroBias))
            .layer(new LocalResponseNormalization.Builder().name("lrn2").build())
            .layer(maxPool("maxpool2", new int[]{3,3}))
            .layer(conv3x3("cnn3", 384, 0))
            .layer(conv3x3("cnn4", 384, nonZeroBias))
            .layer(conv3x3("cnn5", 256, nonZeroBias))
            .layer(maxPool("maxpool3", new int[]{3,3}))
            .layer(fullyConnected("ffn1", 4096, nonZeroBias, dropOut, new NormalDistribution(0, 0.005)))
            .layer(fullyConnected("ffn2", 4096, nonZeroBias, dropOut, new NormalDistribution(0, 0.005)))
            .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .name("output")
                .nOut(numLabels)
                .activation(Activation.SOFTMAX)
                .build())
            .setInputType(InputType.convolutional(height, width, channels))
            .build();

        return new MultiLayerNetwork(conf);

    }

    public MultiLayerNetwork customModel() {
        /**
         * Use this method to build your own custom model.
         **/
        return null;
    }
    
    public DataSetIterator getIterator(ImageRecordReader trainRR, InputSplit trainData) throws Exception {
    	
    	DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        ImageTransform vFlipTransform = new FlipImageTransform(1);
        ImageTransform warpTransform = new WarpImageTransform(rng, 30);
        ImageTransform rotateTransform = new RotateImageTransform(rng, 90);     
   
        List<Pair<ImageTransform, Double>> pipeline = Arrays.asList(
        															new Pair<>(vFlipTransform, 0.5),       														 
        															new Pair<>(rotateTransform, 0.5),       													
                                                                    new Pair<>(warpTransform, 0.5)
        															);

        ImageTransform transform = new PipelineImageTransform(pipeline, true);
        
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
        new AnimalsClassification().run(args);
    }
}