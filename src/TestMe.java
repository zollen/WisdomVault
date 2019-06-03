import java.util.Random;

import org.deeplearning4j.datasets.iterator.CombinedPreProcessor;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;

public class TestMe {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		INDArray arr = Nd4j.zeros(new int[] { 1, 10 });
		INDArray mask = Nd4j.ones(new int[] { 1, 10 });
		Random rand = new Random(0);
		
		for (int i = 0; i < 10; i++) {
			arr.putScalar(new int[] { 0, i }, rand.nextInt(500));
		}
		
	
		DataSetPreProcessor processor = new CombinedPreProcessor.Builder()
								.addPreProcessor(new NormalizerStandardize())
								.addPreProcessor(new NormalizerMinMaxScaler())
								.build();
		
		System.out.println(arr);
		
		processor.preProcess(new DataSet(arr, mask));
		
		System.out.println(arr);
	}
}
