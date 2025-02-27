package holo;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utils.ImageProcessor;

public class Holo {
    private JLabel imageView;
    private JLabel originalImageLabel;
    private String windowName;
    private Mat image, originalImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();

    public Holo(String windowName, Mat newImage) {
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
        applyHolographicEffect();
    }

    /**
     * 홀로그램 효과 적용
     */
    protected void applyHolographicEffect() {
        Mat holographicImage = new Mat();
        Core.addWeighted(originalImage, 1.2, applyColorShift(originalImage), 0.5, 10, holographicImage);
        applyInterferencePattern(holographicImage);
        applyBloomEffect(holographicImage);
        image = holographicImage;
        updateView();
    }

    /**
     * 색상 변환을 적용하여 홀로그램 느낌 강화
     */
    private Mat applyColorShift(Mat inputImage) {
        Mat shiftedImage = new Mat();
        List<Mat> channels = new ArrayList<>();

        // 채널 분리 (Mat[] 대신 List<Mat> 사용)
        Core.split(inputImage, channels);

        if (channels.size() == 3) {
            // Blue 채널 강조
            Core.add(channels.get(0), new Scalar(20), channels.get(0));
            // Red 채널 약화
            Core.add(channels.get(2), new Scalar(-20), channels.get(2));

            // 채널 병합
            Core.merge(channels, shiftedImage);
        } else {
            System.out.println("Error: Image does not have 3 channels.");
            return inputImage;
        }

        return shiftedImage;
    }

    /**
     * 간섭 패턴(Sine Wave Distortion) 적용
     */
    private void applyInterferencePattern(Mat image) {
        int width = image.cols();
        int height = image.rows();
        Mat distortedImage = image.clone();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double shift = 10 * Math.sin(2 * Math.PI * y / 50);
                int newX = Math.min(Math.max((int) (x + shift), 0), width - 1);

                double[] pixel = image.get(y, newX);
                distortedImage.put(y, x, pixel);
            }
        }

        image.assignTo(distortedImage);
    }

    /**
     * 블룸 효과(Bloom Effect) 적용 - 밝은 부분을 강조하여 빛의 확산 효과 추가
     */
    private void applyBloomEffect(Mat image) {
        Mat blur = new Mat();
        Imgproc.GaussianBlur(image, blur, new Size(15, 15), 0);

        Core.addWeighted(image, 0.8, blur, 0.3, 0, image);
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
