package depth;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class App {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String leftImagePath = "src/img/fruit.jpg"; // 좌측 이미지 경로
        String rightImagePath = "src/img/fruit.jpg"; // 우측 이미지 경로

        Mat leftImage = Imgcodecs.imread(leftImagePath);
        Mat rightImage = Imgcodecs.imread(rightImagePath);

        if (leftImage.empty() || rightImage.empty()) {
            System.out.println("Couldn't open one of the files: " + leftImagePath + " or " + rightImagePath);
            return;
        }

        Depth gui = new Depth("Depth Map Generation", leftImage, rightImage);
        gui.init();
    }
}
