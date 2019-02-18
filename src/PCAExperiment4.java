import java.awt.image.BufferedImage;

public class PCAExperiment4 {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedImage image = ImageUtils.image("img/obama1.jpg");
		
		image = ImageUtils.resize(image, 200, 200);
		
		double[] arr1 = ImageUtils.array(image);
		
		ImageUtils.write(image, arr1, "img/output1.jpg");
		
		image = ImageUtils.grey(image);
	
		double[] arr2 = ImageUtils.array(image);
				
		ImageUtils.write(image, arr2, "img/output2.jpg");

		System.out.println("DONE");

	}

}
