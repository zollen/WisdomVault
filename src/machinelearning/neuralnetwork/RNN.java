package machinelearning.neuralnetwork;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.split.NumberedFileInputSplit;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class RNN {
	
	private static final int numLabelClasses = 6;
	
	private static final int batchSize = 128;
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.weightInit(WeightInit.XAVIER)
				.biasInit(0.0)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Nesterovs(0.005, 0.5))
				.gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
				.gradientNormalizationThreshold(0.5)
				.list()
				.layer(0, new LSTM.Builder().nIn(1).nOut(10).activation(Activation.TANH).build())
				.layer(1,
						new RnnOutputLayer.Builder(LossFunctions.LossFunction.MCXENT)
								.nIn(10)
								.nOut(numLabelClasses)
								.activation(Activation.SOFTMAX)
								.dist(new UniformDistribution(0, 1)).build())
				.build();

		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(20)); 
		
		System.out.println(network.summary());
		
		
		File dir = new File("out");
		
		generate("data/synthetic_control.data");
		
		
		// Training data
		CSVSequenceRecordReader trainer = new CSVSequenceRecordReader(0, ", ");
		trainer.initialize(new NumberedFileInputSplit(dir.getAbsoluteFile() + File.separator + "testseq_%d.csv", 0, 499));
		DataSetIterator itr1 = new SequenceRecordReaderDataSetIterator(trainer, batchSize, numLabelClasses, 1);
		
		CSVSequenceRecordReader tester = new CSVSequenceRecordReader(0, ", ");
		tester.initialize(new NumberedFileInputSplit(dir.getAbsoluteFile() + File.separator + "testseq_%d.csv", 499, 599));
		DataSetIterator itr2 = new SequenceRecordReaderDataSetIterator(tester, batchSize, numLabelClasses, 1);

		
		System.out.println("Training model....");
		network.fit(itr1);
		
		System.out.println("Testing model....");
		Evaluation evaluation = network.evaluate(itr2);	
		System.out.println(evaluation.stats());
		
		cleanup();
	}
	
	public static void cleanup() throws Exception {
		
		File dir = new File("out");
		
		File [] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.toLowerCase().startsWith("testseq_") && name.toLowerCase().endsWith(".csv");
			}
			
		});
		
		for (int i = 0; files != null && i < files.length; i++) {
			files[i].delete();
		}
	}
	
	public static void generate(String fileName) throws Exception {
		
		cleanup();
		
		String content = IOUtils.toString(new FileReader(new File(fileName)));
		String [] lines = content.split("\n");
		
		int lineCount = 0;
		int index = 0;
		
		List<String> linesList = new ArrayList<String>();
		
		for (String line : lines) {
			
			Integer count = lineCount / 100;
			String newLine = line.replaceAll("\\s+", ", " + count.toString() + "\n");
			newLine = newLine + ", " + count.toString();
			linesList.add(newLine);
			lineCount++;
		}
		
		Collections.shuffle(linesList);
		
		for (String line : linesList) {
			File file = new File("out/testseq_" + index + ".csv");
			FileUtils.writeStringToFile(file, line, Charset.defaultCharset());
			index++;	
		}
	}
	
	
}
