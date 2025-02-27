package dither;

import java.awt.*;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utils.ImageProcessor;

public class Dither {
    private JLabel imageView;
    private JLabel originalImageLabel;
    private String windowName;
    private Mat image, originalImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();

    public Dither(String windowName, Mat newImage) {
        this.windowName = windowName;
        this.image = newImage.clone();
        this.originalImage = newImage.clone();
    }

    public void init() {
        setSystemLookAndFeel();
        initGUI();
    }

    private void initGUI() {
        JFrame frame = createJFrame(windowName);
        updateView();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JFrame createJFrame(String windowName) {
        JFrame frame = new JFrame(windowName);
        frame.setLayout(new GridBagLayout());
        setupImage(frame);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private void setupImage(JFrame frame) {
        JPanel imagesPanel = new JPanel();
        imageView = new JLabel();
        imageView.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;

        originalImageLabel = new JLabel();
        Image originalAWTImage = imageProcessor.toBufferedImage(originalImage);
        originalImageLabel.setIcon(new ImageIcon(originalAWTImage));

        imagesPanel.add(originalImageLabel);
        imagesPanel.add(imageView);

        frame.add(imagesPanel, c);
        applyDitheringEffect();
    }

    /**
     * 디더링 (Floyd-Steinberg Dithering) 적용
     */
    protected void applyDitheringEffect() {
        Mat grayImage = new Mat();
        Imgproc.cvtColor(originalImage, grayImage, Imgproc.COLOR_BGR2GRAY); // 흑백 변환

        Mat ditheredImage = floydSteinbergDithering(grayImage);
        image = ditheredImage;

        updateView();
    }

    /**
     * Floyd-Steinberg 오차 확산 디더링 적용
     */
    private Mat floydSteinbergDithering(Mat grayImage) {
        Mat dithered = grayImage.clone();
        int rows = dithered.rows();
        int cols = dithered.cols();

        for (int y = 0; y < rows - 1; y++) {
            for (int x = 1; x < cols - 1; x++) {
                double oldPixel = dithered.get(y, x)[0];
                double newPixel = (oldPixel < 128) ? 0 : 255;
                double quantError = oldPixel - newPixel;
                
                dithered.put(y, x, newPixel);

                // Floyd-Steinberg 오차 확산
                spreadError(dithered, x + 1, y, quantError * 7 / 16);
                spreadError(dithered, x - 1, y + 1, quantError * 3 / 16);
                spreadError(dithered, x, y + 1, quantError * 5 / 16);
                spreadError(dithered, x + 1, y + 1, quantError * 1 / 16);
            }
        }
        return dithered;
    }

    /**
     * 특정 픽셀에 오차 확산
     */
    private void spreadError(Mat image, int x, int y, double error) {
        if (x >= 0 && x < image.cols() && y >= 0 && y < image.rows()) {
            double[] pixel = image.get(y, x);
            double newValue = pixel[0] + error;
            newValue = Math.max(0, Math.min(255, newValue)); // 값 범위 제한
            image.put(y, x, newValue);
        }
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        Image outputImage = imageProcessor.toBufferedImage(image);
        imageView.setIcon(new ImageIcon(outputImage));

        Image originalAnnotated = imageProcessor.toBufferedImage(originalImage);
        originalImageLabel.setIcon(new ImageIcon(originalAnnotated));
    }
}
