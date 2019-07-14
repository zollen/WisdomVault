package machinelearning.neuralnetwork.regression.function;


import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

/**
 * Calculate function value of sine of x.
 */
public class CosMathFunction implements IMathFunction {

    @Override
    public INDArray getFunctionValues(final INDArray x) {
        return Transforms.cos(x, true);
    }

    @Override
    public String getName() {
        return "Cos";
    }
}