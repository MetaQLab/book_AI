package unsharp;

import java.awt.*;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utils.ImageProcessor;

public class Unsharp {
    private JLabel imageView;
    private JLabel originalImageLabel;
    private String windowName;
    private Mat image, originalImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();

    public Unsharp(String windowName, Mat newImage) {
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
        applyUnsharpMasking();
    }

    /**
     * 언샤프 마스킹 적용
     */
    protected void applyUnsharpMasking() {
        Mat blurred = new Mat();
        Mat sharpened = new Mat();

        // 가우시안 블러 적용 (5x5 커널)
        Imgproc.GaussianBlur(originalImage, blurred, new Size(5, 5), 0);

        // 언샤프 마스크 생성 (원본 - 블러)
        Core.subtract(originalImage, blurred, sharpened);

        // 샤프닝 적용 (원본 + 마스크 * α) → α=1.5 적용
        Core.addWeighted(originalImage, 1.5, sharpened, -0.5, 0, image);

        updateView();
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
