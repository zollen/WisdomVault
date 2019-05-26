package machinelearning.neuralnetwork;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.datavec.api.io.labels.PatternPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.indexaccum.IAMax;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;

public class MultiLayerGraphClassifier {
	
	public static final int height = 100;
	public static final int width = 100;
	public static final int channels = 3;
	public static final int batchSize = 320;
	public static final int numLabels = 4;
    
	public static final long seed = 83;
	public static final Random rng = new Random(seed);

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ComputationGraph network = ComputationGraph.load(new File("data/lenet.model"), false);
		
		System.out.println(network.summary());
		
		System.out.println("Begin examining testset...");
		eval(network);
	}
	
    public static RecordReaderDataSetIterator getTestData(String path) throws Exception {
    	
    	FileSplit fileSplit = new FileSplit(new File(path), NativeImageLoader.ALLOWED_FORMATS);
    
    	InputSplit [] inputSplit = fileSplit.sample(null);
        InputSplit data = inputSplit[0];
        
        PatternPathLabelGenerator labelDecider = new PatternPathLabelGenerator("_", 0);
        ImageRecordReader reader = new ImageRecordReader(height, width, channels, labelDecider);
        reader.initialize(data);
    	
 	
    	return new RecordReaderDataSetIterator(reader, 1, 1, 4);
    }
    
    public static void eval(ComputationGraph network) throws Exception {
    	
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
    
}
