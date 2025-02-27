package holo;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class App {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        String filePath = "src/img/fruit.jpg"; // 이미지 파일 경로
        Mat newImage = Imgcodecs.imread(filePath);

        if (newImage.empty()) {
            System.out.println("Couldn't open file: " + filePath);
            return;
        }

        Holo gui = new Holo("Holographic Effect Example", newImage);
        gui.init();
    }
}
