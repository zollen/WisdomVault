import java.util.Random;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerMinMaxScaler;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.factory.Nd4j;

public class TestMe {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		INDArray arr1 = Nd4j.zeros(new int[] { 1, 10 });
		INDArray arr2 = Nd4j.zeros(new int[] { 1, 10 });
		INDArray labels1 = Nd4j.zeros(new int[] { 1, 10 });
		INDArray labels2 = Nd4j.ones(new int[] { 1, 10 });
		Random rand = new Random(0);
		
		for (int i = 0; i < 10; i++) {
			arr1.putScalar(new int[] { 0, i }, rand.nextInt(10));
		}
		for (int i = 0; i < 10; i++) {
			arr2.putScalar(new int[] { 0, i }, rand.nextInt(10));
		}
		for (int i = 0; i < 10; i++) {
			labels1.putScalar(new int[] { 0, i }, (i < 6 ? 1 : 0));
		}
		
		
/*	
		DataNormalization processor = new CombinedPreProcessor.Builder()
								.addPreProcessor(new NormalizerStandardize())
								.addPreProcessor(new NormalizerMinMaxScaler())
								.build();
*/		
		DataNormalization processor1 = new NormalizerStandardize();
		DataNormalization processor2 = new NormalizerMinMaxScaler(0, 100);
		
		DataSet data1 = new DataSet(arr1, labels1);
		DataSet data2 = new DataSet(arr2, labels2);
		
		System.out.println(data2);
		
		processor2.fit(data1);
		processor2.transform(data2);
		
		
		System.out.println(data2);
		
	}
}
