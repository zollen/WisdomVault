package machinelearning.neuralnetwork;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.graph.rnn.DuplicateToTimeSeriesVertex;
import org.deeplearning4j.nn.conf.graph.rnn.LastTimeStepVertex;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class AutoEncoder3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Starting Neural Network - AutoEncoder");

		ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
				.seed(0)
				.weightInit(WeightInit.XAVIER)
				.updater(new AdaDelta())
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
				.l2(0.001)
				.inferenceWorkspaceMode(WorkspaceMode.ENABLED)
				.trainingWorkspaceMode(WorkspaceMode.ENABLED)
				.graphBuilder()
				.addInputs("encoderInput", "decoderInput")
				.setInputTypes(InputType.recurrent(2), InputType.recurrent(3))
				.addLayer("encoder", new LSTM.Builder()
									.nOut(96)
									.activation(Activation.TANH).build(),
							"encoderInput")
				.addLayer("encoder2", new LSTM.Builder()
									.nOut(48)
									.activation(Activation.TANH).build(), 
							"encoder")
				.addVertex("laststep", new LastTimeStepVertex("encoderInput"), "encoder2")
				.addVertex("dup", new DuplicateToTimeSeriesVertex("decoderInput"), "laststep")
				.addLayer("decoder", new LSTM.Builder()
									.nOut(48)
									.activation(Activation.TANH).build(),
							"decoderInput", "dup")
				.addLayer("decoder2", new LSTM.Builder()
									.nOut(96)
									.activation(Activation.TANH).build(), 
							"decoder")
				.addLayer("output", new RnnOutputLayer.Builder()
								.lossFunction(LossFunctions.LossFunction.MSE)
								.activation(Activation.SIGMOID)
								.nOut(3).build(),
							"decoder2")
				.setOutputs("output").build();

		ComputationGraph graph = new ComputationGraph(conf);
		graph.init();
		graph.setListeners(new ScoreIterationListener(1));
	}

}
