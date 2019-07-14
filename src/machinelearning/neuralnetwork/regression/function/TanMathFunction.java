package machinelearning.neuralnetwork.regression.function;


import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

/**
 * Calculate function value of sine of x.
 */
public class TanMathFunction implements IMathFunction {

    @Override
    public INDArray getFunctionValues(final INDArray x) {
        return Transforms.tan(x, true);
    }

    @Override
    public String getName() {
        return "Tan";
    }
}