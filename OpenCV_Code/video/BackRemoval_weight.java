package video;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import utils.ImageProcessor;
import utils.VideoProcessor;
import utils.VideoProcessor.AbsDifferenceBackground;

public class BackRemoval_weight {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private JFrame frame;
    private JLabel imageLabel;
    private VideoProcessor backgroundRemover;

    public static void main(String[] args) {
        BackRemoval_weight app = new BackRemoval_weight();
        app.initGUI();
        app.runMainLoop();
    }

    private void initGUI() {
        frame = new JFrame("Background Removal_weight Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        imageLabel = new JLabel();
        frame.add(imageLabel);
        frame.setVisible(true);
    }

    private void runMainLoop() {
        ImageProcessor imageProcessor = new ImageProcessor();
        Mat webcamMatImage = new Mat();
        Image tempImage;
        VideoCapture capture = new VideoCapture(0);
        capture.set(Videoio.CAP_PROP_FRAME_WIDTH, 320);
        capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, 240);

        if (!capture.isOpened()) {
            System.out.println("캡쳐할 수 없습니다!");
            return;
        }

        // 첫 번째 프레임을 배경 이미지로 설정
        Mat backgroundImage = new Mat();
        capture.read(backgroundImage);
        backgroundRemover = new AbsDifferenceBackground(backgroundImage);

        while (true) {
            capture.read(webcamMatImage);

            if (!webcamMatImage.empty()) {
                // 배경 제거 적용
                Mat processedImage = backgroundRemover.process(webcamMatImage);
                tempImage = imageProcessor.toBufferedImage(processedImage);
                ImageIcon imageIcon = new ImageIcon(tempImage, "Processed Video");
                imageLabel.setIcon(imageIcon);
                frame.pack();
            } else {
                System.out.println("프레임 캡쳐 실패 --중지됨!!");
                break;
            }
        }
    }
}
