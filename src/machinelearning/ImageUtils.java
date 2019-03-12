package machinelearning;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageUtils {
	
	public static BufferedImage image(String imageName) throws Exception {

		return ImageIO.read(new File(imageName));
	}

	public static double[] array(BufferedImage image) throws Exception {

		double[] t = null;
		return image.getRaster().getPixels(0, 0, image.getWidth(), image.getHeight(), t);
	}

	public static void write(BufferedImage image, double[] pixels, String fileName) throws Exception {
	
		BufferedImage bImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());	
		bImage.getRaster().setPixels(0, 0, image.getWidth(), image.getHeight(), pixels);	
		ImageIO.write(bImage, "jpg", new File(fileName));
	}
	
	public static BufferedImage grey(BufferedImage image) {
		
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		return op.filter(image, null);
	}
	
	public static BufferedImage resize(BufferedImage image, int width, int height) throws Exception {

        BufferedImage resized = new BufferedImage(width, height, image.getType());
        Graphics2D g2d = resized.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return resized;
	}

}
