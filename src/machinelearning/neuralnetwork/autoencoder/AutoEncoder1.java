package machinelearning.neuralnetwork.autoencoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.AutoEncoder;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.AdaGrad;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.primitives.ImmutablePair;

public class AutoEncoder1 {
	
	private static DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final Random rand = new Random(0);
	
	// This "stacked" autoEncoder that performs anomaly detection on MNIST digits without preTraining. 
	// The goal is to identify outlier digits; i.e. digits that are unusual and atypical. Identification of items, 
	// events or observations that “stand out” from the norm of a given dataset is broadly known as anomaly detection. 
	// Anomaly detection does not require a labeled dataset, and can be undertaken with unSupervised learning, which 
	// is helpful because most of the world’s data is not labeled.

	// This type of anomaly detection uses reconstruction error to measure how well the decoder is performing. 
	// Stereotypical examples should have low reconstruction error, whereas outLiers should have high reconstruction error.
	
	// This AutoEncoder downloads 50000 images of (0..9) digits from MNIST database (http://yann.lecun.com/exdb/mnist/). 
	// Among these images there are a lot of junk/noise/garbage images. This AutoEncoder performs 
	// anomaly detection and finds out all noisy/junk/garbage images.
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub	
		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.weightInit(WeightInit.XAVIER)
				.updater(new AdaGrad(0.05))
				.activation(Activation.RELU)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.l2(0.0001)
				.list()
				.layer(0, new AutoEncoder.Builder().nIn(784).nOut(250).build())
				.layer(1, new AutoEncoder.Builder().nIn(250).nOut(10).build())
				.layer(2, new AutoEncoder.Builder().nIn(10).nOut(250).build())
				.layer(3, new OutputLayer.Builder().nIn(250).nOut(784)
								.lossFunction(LossFunctions.LossFunction.MSE).build())
				.build();
		
		
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(1));
		
		System.out.println("Downloading Data...");
		DataSetIterator itr = new MnistDataSetIterator(10000, 50000, false);
		
		
		List<INDArray> featuresTrain = new ArrayList<INDArray>();
		List<INDArray> featuresTest = new ArrayList<INDArray>();
		List<INDArray> labelsTest = new ArrayList<INDArray>();
		
		System.out.println("Preprocessing Data...");
		while (itr.hasNext()) {
			
			DataSet data = itr.next();
			
			SplitTestAndTrain samples = data.splitTestAndTrain(80, rand);
			
			DataSet training = samples.getTrain();
			DataSet testing = samples.getTest();
			
			featuresTrain.add(training.getFeatures());
			featuresTest.add(testing.getFeatures());
			
			labelsTest.add(Nd4j.argMax(testing.getLabels(), 1));
		}
		
		System.out.println("Begin Unsupervised Training...");
		for (int nEpochs = 0; nEpochs < 30; nEpochs++) {
			
			for (int i = 0; i < featuresTrain.size(); i++) {
				
				INDArray data = featuresTrain.get(i);
				network.fit(data, data);
			}
			
			System.out.println("  --> Epoch " + nEpochs + " complete");
		}
		
		
	
		System.out.println("Begin Testing...");
		
		Map<Integer, List<ImmutablePair<Double, INDArray>>> map = 
				new HashMap<Integer, List<ImmutablePair<Double, INDArray>>>();
		
		for (int i = 0; i < 10; i++)
			map.put(i, new ArrayList<ImmutablePair<Double, INDArray>>());
		
		for (int sample = 0; sample < featuresTest.size(); sample++) {
			
			INDArray tests = featuresTest.get(sample);
			INDArray labels = labelsTest.get(sample);
			
			for (int row = 0; row < tests.rows(); row++) {
				
				INDArray test = tests.getRow(row);
				int index = labels.getInt(row);
				
				double score = network.score(new DataSet(test, test));
				
				List<ImmutablePair<Double, INDArray>> list = map.get(index);
				list.add(new ImmutablePair<Double, INDArray>(score, test));
			}
		}
			
		System.out.println("RESULT: Re-Construction Error score");
		Map<String, INDArray> bests = new LinkedHashMap<String, INDArray>();
		Map<String, INDArray> worsts = new LinkedHashMap<String, INDArray>();
		
		AtomicInteger count = new AtomicInteger();
		map.entrySet().stream().forEach(p -> {
			
			count.incrementAndGet();
			
			p.getValue().stream().sorted(new PairComparator(1)).limit(4).forEach(
					k -> bests.put("BEST_" + p.getKey() + "_" + ff.format(k.getKey()) + count.get(), k.getRight()));
			p.getValue().stream().sorted(new PairComparator(-1)).limit(4).forEach(
					k -> worsts.put("WORST_" + p.getKey() + "_" + ff.format(k.getKey()) + count.get(), k.getRight()));
			
			StringBuilder builder = new StringBuilder();
			builder.append("Digit: [" + p.getKey() + "], Size: [" + p.getValue().size() + "] ==> ");
			builder.append("Best: [" + p.getValue().stream().sorted(new PairComparator(1)).limit(4)
							.map(k -> ff.format(k.getKey())).collect(Collectors.joining(", ")) + "]");
			builder.append(", Worst: [" + p.getValue().stream().sorted(new PairComparator(-1)).limit(4)
							.map(k -> ff.format(k.getKey())).collect(Collectors.joining(", ")) + "]");
			System.out.println(builder.toString());
		});
		
		System.out.println("Creating Image Files at out directory...");		
		bests.entrySet().stream().forEach(p -> {			
			createImage("out/" + p.getKey() + ".png", p.getValue());
		});
		
		worsts.entrySet().stream().forEach(p -> {
			createImage("out/" + p.getKey() + ".png", p.getValue());
		});
		
		System.out.println("Please review the out directory");
		System.out.println("Done!!");
	}
	
	public static void createImage(String fileName, INDArray arr) {
		
		OutputStream out = null;
		
		try {
		
			BufferedImage image = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
			
			for (int i = 0; i < 784; i++) {
				image.getRaster().setSample(i % 28, i / 28, 0, (255 * arr.getInt(i)));
			}
		
			out = new FileOutputStream(new File(fileName));
			
			ImageIO.write(image, "PNG", out);		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (out != null)
					out.close();
			}
			catch (Exception ex) {}
		}	
	}
	
	
	private static class PairComparator implements Comparator<ImmutablePair<Double, INDArray>> {
		
		private int sign = 1;
		
		public PairComparator(int sign) {
			this.sign = sign;
		}

		@Override
		public int compare(ImmutablePair<Double, INDArray> o1, ImmutablePair<Double, INDArray> o2) {
			// TODO Auto-generated method stub
			return o1.getKey().compareTo(o2.getKey()) * sign;
		}
		
	}
	
}
