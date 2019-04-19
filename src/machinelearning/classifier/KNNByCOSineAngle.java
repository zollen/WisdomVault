package machinelearning.classifier;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

public class KNNByCOSineAngle {
	
	private static final DecimalFormat ff = new DecimalFormat("0.000");
	
	private static final int K = 3;

	
	private static final double [][] TRAINING = { 
						{7, 8}, {9, 9}, {8, 8}, {10, 10}, {6, 7},  
						{8, 1}, {9, 2}, {10, 1}, {11, 2}, {12, 3} 
											};
	private static final int [] CLS = { 1,  1,  1,  1,  1,  0,  0,  0,  0,  0 };
	
	public static final String [] LABELS = { "N", "Y" };
	
	// We must first observe the data pattern before we decide the classification method
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double [] test1 = { 2.0, 3.0 };	
		System.out.println(test1[0] + ", " + test1[1] + " --> " + knnsearch(test1));
		
		double [] test2 = { 2.0, 1.0 };	
		System.out.println(test2[0] + ", " + test2[1] + " --> " + knnsearch(test2));
		
		double [] test3 = { 4.0, 3.0 };	
		System.out.println(test3[0] + ", " + test3[1] + " --> " + knnsearch(test3));
		
		double [] test4 = { 5.0, 2.0 };	
		System.out.println(test4[0] + ", " + test4[1] + " --> " + knnsearch(test4));
		
		double [] test5 = { 5.0, 3.0 };	
		System.out.println(test5[0] + ", " + test5[1] + " --> " + knnsearch(test5));
		
		// Now we can reuse the results of these points
		// substitute these results as one of the attribute of the dataset for 
		// classification

	}
	
	public static int knnsearch(double [] data) {
		
		DMatrixRMaj test = new DMatrixRMaj(data.length, 1);
		
		Map<Double, String> map = new TreeMap<Double, String>(new Comparator<Double>() {

			@Override
			public int compare(Double o1, Double o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2) * -1;
			}
		});
		
		
		{
			double dist = 0.0;
			
			for (int i = 0; i < data.length; i++)
				dist += Math.pow(data[i], 2);
			
			dist = Math.sqrt(dist);
				
			for (int i = 0; i < data.length; i++) 
				test.set(i, 0, data[i] / dist);
		}
		
		for (int i = 0; i < CLS.length; i++) {
			DMatrixRMaj sample = new DMatrixRMaj(2, 1);
			
			double dist = 0.0;
			
			for (int j = 0; j < data.length; j++)
				dist += Math.pow(TRAINING[i][j], 2);
			
			dist = Math.sqrt(dist);
			
			for (int j = 0; j < data.length; j++)
				sample.set(j, 0, TRAINING[i][j] / dist);
			
			// using cos() or dot product, instead of euclidean distance
			map.put(CommonOps_DDRM.dot(sample, test), LABELS[CLS[i]]);		
		}
		
		// choosing the K points that cos() angle is nearest to the cos() angle of the test point
		map.entrySet().stream().limit(K).forEach(p -> System.out.println(ff.format(p.getKey()) + " --> " + p.getValue()));
		long count = map.entrySet().stream().limit(K).filter(p -> "Y".equals(p.getValue())).count();
	
		return K - count <= (double) K / 2 ? 1 : 0;	
	}
	
}
