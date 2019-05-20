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
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.InvocationType;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;


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

        MultiLayerNetwork network = skynetModel();
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

   
    public MultiLayerNetwork skynetModel() {
    	
    	
      
        return null;
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