package machinelearning.neuralnetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.iterator.SamplingDataSetIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.InMemoryModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;

public class BasicClassification {
	
	private static final String VALUE_FLOWER_SETOSA = "Iris-setosa";
	private static final String VALUE_QUALITY_VERCOLOR = "Iris-versicolor";
	private static final String VALUE_QUALITY_VIGRINICA = "Iris-virginica";
	

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//Initialize the user interface backend
	    UIServer uiServer = UIServer.getInstance();

	    //Configure where the network information (gradients, score vs. time etc) is to be stored. Here: store in memory.
	    StatsStorage statsStorage = new InMemoryStatsStorage();         //Alternative: new FileStatsStorage(File), for saving and loading later

	    //Attach the StatsStorage instance to the UI: this allows the contents of the StatsStorage to be visualized
	    uiServer.attach(statsStorage);
	    
		System.out.println("Preparing data");
		DataSet data = getData("data/iris.arff.txt");
		
		data.shuffle();
		SplitTestAndTrain samples = data.splitTestAndTrain(0.85);
		
		DataSet training = samples.getTrain();
		DataSet test = samples.getTest();
		
		// Normalize data so one attribute with much larger range 
		// would not dominate the other attributes
		NormalizerStandardize normalizer = new NormalizerStandardize();
		normalizer.fit(training);
		normalizer.transform(training);
		normalizer.transform(test);
		
		DataSetIterator testIter = new SamplingDataSetIterator(training, 10, 15);
		DataSetIterator trainIter = new SamplingDataSetIterator(test, 10, 15);
		
		
		
		System.out.println("Building model....");
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.weightInit(WeightInit.XAVIER)
				.biasInit(0.0)
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.updater(new Sgd(0.1))
				.l2(0.0001)
				.list()
				.layer(0, new DenseLayer.Builder().nIn(4).nOut(3).activation(Activation.TANH).build())
				.layer(1, new DenseLayer.Builder().nIn(3).nOut(3).activation(Activation.LEAKYRELU).build())
				.layer(2, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
								.nIn(3)
								.nOut(3)
								.activation(Activation.SOFTMAX).build())
				.build();
		
		
		MultiLayerNetwork network = new MultiLayerNetwork(conf);
		network.init();
		network.setListeners(new ScoreIterationListener(1));
		//Then add the StatsListener to collect this information from the network, as it trains
		network.setListeners(new StatsListener(statsStorage));
		
		System.out.println(network.summary());
		
		EarlyStoppingModelSaver<MultiLayerNetwork> saver = new InMemoryModelSaver<MultiLayerNetwork>();
		
		EarlyStoppingConfiguration<MultiLayerNetwork> eac = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>()
				.epochTerminationConditions(new MaxEpochsTerminationCondition(100))
				.iterationTerminationConditions(new MaxTimeIterationTerminationCondition(1, TimeUnit.MINUTES))
				.scoreCalculator(new DataSetLossCalculator(testIter, true))
				.evaluateEveryNEpochs(1)
				.modelSaver(saver)
				.build();	
		
		EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(eac, conf, trainIter);
		
		System.out.println("Training model....");
		EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();
		
		System.out.println("Termination reason: " + result.getTerminationReason());
        System.out.println("Termination details: " + result.getTerminationDetails());
        System.out.println("Total epochs: " + result.getTotalEpochs());
        System.out.println("Best epoch number: " + result.getBestModelEpoch());
        System.out.println("Score at best epoch: " + result.getBestModelScore());
        
        Map<Integer,Double> scoreVsEpoch = result.getScoreVsEpoch();
        List<Integer> list = new ArrayList<Integer>(scoreVsEpoch.keySet());
        Collections.sort(list);
        System.out.println("Score vs. Epoch:");
        for( Integer i : list){
            System.out.println(i + "\t" + scoreVsEpoch.get(i));
        }
	}
	
	private static DataSet getData(String fileName) throws Exception {
		
		BufferedReader reader = null;
		
		Map<String,Integer> map = new HashMap<String, Integer>();
		map.put(VALUE_FLOWER_SETOSA, 0);
		map.put(VALUE_QUALITY_VERCOLOR, 1);
		map.put(VALUE_QUALITY_VIGRINICA, 2);
		
		try {
			reader = new BufferedReader(new FileReader(fileName));
			ArffReader arff = new ArffReader(reader);
			Instances instances = arff.getData();
			instances.setClassIndex(instances.numAttributes() - 1);
			
			INDArray inputs = Nd4j.zeros(instances.numInstances(), instances.numAttributes() - 1);
			INDArray labels = Nd4j.zeros(instances.numInstances(), 3);

			for (int row = 0; row < instances.numInstances(); row++) {
				
				Instance instance = instances.get(row);
				
				for (int col = 0; col < instances.numAttributes() - 1; col++) {
					inputs.putScalar(row, col, instance.value(col));
				}
				
				int position = map.get(instance.stringValue(instances.numAttributes() - 1));
				
				labels.putScalar(row, position, 1);
			}
			
			return new DataSet(inputs, labels);
		} 
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception ex) {}
		}
	}

}
