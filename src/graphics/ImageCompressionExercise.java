package graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;

import linearalgebra.MatrixFeatures;

public class ImageCompressionExercise {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedImage bImage = getImage("img/sample1.jpg");
		
		DMatrixRMaj m = compress(convert(bImage));

		writeImage(bImage, m.getData());

		System.out.println("DONE");
	}

	public static DMatrixRMaj compress(DMatrixRMaj m) throws Exception {

		SingularValueDecomposition<DMatrixRMaj> svd = DecompositionFactory_DDRM.svd(m.numRows, m.numCols, true, true,
				false);
		System.out.println("svd(m): " + svd.decompose(m));

		DMatrixRMaj S = svd.getW(null);
		DMatrixRMaj U = svd.getU(null, false);
		DMatrixRMaj V = svd.getV(null, true);
		
		System.out.println("Diag+(S): " + MatrixFeatures.isDiagonalPositive(S));
		
	//	S = filterByLargestEigenValues(S, 600);  // 600/900
		S = filterByThreshold(S, 80);
		
	

		DMatrixRMaj tmp1 = new DMatrixRMaj(U.numRows, S.numCols);
		CommonOps_DDRM.mult(U, S, tmp1);

		DMatrixRMaj tmp2 = new DMatrixRMaj(U.numRows, V.numCols);
		CommonOps_DDRM.mult(tmp1, V, tmp2);

		return tmp2;
	}

	public static DMatrixRMaj convert(BufferedImage image) throws Exception {

		DMatrixRMaj m = new DMatrixRMaj(image.getHeight(), image.getWidth());
		double [] pixels = getByteArray(image);
		
		System.out.println("Size: " + pixels.length);
		System.out.println("Height: " + image.getHeight());
		System.out.println("Width: " + image.getWidth());

		int x = 0;
		for (int row = 0; row < image.getHeight(); row++) {

			for (int col = 0; col < image.getWidth(); col++) {

				m.set(row, col, pixels[x]);
				x++;
			}
		}

		return m;
	}

	public static BufferedImage getImage(String imageName) throws Exception {

		return ImageIO.read(new File(imageName));
	}

	public static double[] getByteArray(BufferedImage image) throws Exception {
		Raster raster = image.getRaster();
		byte[] arr = ((DataBufferByte) raster.getDataBuffer()).getData();

		double[] pixels = new double[arr.length];
		for (int i = 0; i < arr.length; i++)
			pixels[i] = arr[i];

		return pixels;
	}

	public static void writeImage(BufferedImage image, double[] pixels) throws Exception {

		image.getRaster().setPixels(0, 0, image.getWidth(), image.getHeight(), pixels);
		ImageIO.write(image, "jpg", new File("img/output.jpg"));
	}

	public static DMatrixRMaj filterByLargestEigenValues(DMatrixRMaj m, int col) {

		DMatrixRMaj subject = new DMatrixRMaj(m.numRows, m.numCols);

		for (int i = 0; i < col && i < m.numCols; i++) {

			for (int j = 0; j < m.numRows; j++) {

				subject.set(j, i, m.get(j, i));
			}
		}

		return subject;
	}
	
	public static DMatrixRMaj filterByThreshold(DMatrixRMaj m, int threshold) {
		
		DMatrixRMaj subject = new DMatrixRMaj(m.numRows, m.numCols);

		for (int i = 0; i < m.numCols; i++) {

			for (int j = 0; j < m.numRows; j++) {

				if (m.get(j, i) > threshold)
					subject.set(j, i, m.get(j, i));
			}
		}

		return subject;
	}
	
	public static void printS(DMatrixRMaj m) {
		
		Set<Integer> vals = new HashSet<Integer>();
		
		for (int i = 0; i < m.numRows; i++) {
			
			for (int j = 0; j < m.numCols; j++) {
				
				vals.add((int) m.get(i, j));
			}
		}
		
		String all = vals.stream().sorted().map( String::valueOf ).collect(Collectors.joining(", "));
		System.out.println(all);
	}

}
