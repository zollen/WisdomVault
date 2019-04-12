package graphics;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Setting up opencv library in Eclipse
 * https://opencv-java-tutorials.readthedocs.io/en/latest/02-first-java-application-with-opencv.html
 */ 
public class OpenCVExerrcise {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
		System.out.println("mat = " + mat.dump());
	}

}
