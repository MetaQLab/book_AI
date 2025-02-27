package kernels;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class App {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) throws Exception {
		String filePath = "src/img/fruit.jpg";
		Mat newImage = Imgcodecs.imread(filePath);

		if (newImage.dataAddr() == 0) {
			System.out.println("Couldn't open file " + filePath);
		} else {
			GUI gui = new GUI("kernels Example", newImage);
			gui.init();
		}
		return;
	}
}