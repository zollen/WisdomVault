package machinelearning.neuralnetwork.regression.function;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

/**
 * Sign(x) or Sign of a real number, x, is -1 if x is negative, 0 if x is zero and 1 if x is positive.
 *
 * Calculate function value of Sign of Sine of x, which can be -1, 0 or 1.
 * The three possible outputs of Sign(sin) will form a line that resembles "squares" in the graph.
 */
public class SquareWaveMathFunction implements IMathFunction {

    @Override
    public INDArray getFunctionValues(final INDArray x) {
        INDArray sin = Transforms.sin(x, true);
        return Transforms.sign(sin, false);
    }

    @Override
    public String getName() {
        return "SquareWave";
    }
}