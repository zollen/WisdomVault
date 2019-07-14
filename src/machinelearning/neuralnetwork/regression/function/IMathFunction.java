package machinelearning.neuralnetwork.regression.function;

import org.nd4j.linalg.api.ndarray.INDArray;

public interface IMathFunction {

    INDArray getFunctionValues(INDArray x);

    String getName();
}