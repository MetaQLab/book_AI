package gabor;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class App {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String filePath = "src/img/sample.jpg"; 
        Mat newImage = Imgcodecs.imread(filePath);

        if (newImage.empty()) {
            System.out.println("Couldn't open file: " + filePath);
            return;
        }

        Gabor gui = new Gabor("Gabor Filter Example", newImage);
        gui.init();
    }
}
