import java.util.Random;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


public class TestMe {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		INDArray arr = Nd4j.zeros(2, 3, 4);
		Random rand = new Random(0);
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				for (int k = 0; k < 4; k++) {
					arr.putScalar(new int[] { i, j, k }, Math.abs(rand.nextInt()) % 1000); 
				}
			}
		}
		
		System.out.println(arr);
		System.out.println("======================");
		INDArray me = arr.tensorAlongDimension(0, 2, 0);
		System.out.println(me);
		System.out.println("======================");
		INDArray you = arr.tensorAlongDimension(0, 0, 2);
		System.out.println(you);

		
	
		
		
	}
}
