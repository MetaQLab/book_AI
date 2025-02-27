package nonLinear;

import java.awt.*;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utils.ImageProcessor;

public class NonLinear {
    private JLabel imageView;
    private JLabel originalImageLabel;
    private String windowName;
    private Mat image, originalImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();

    public NonLinear(String windowName, Mat newImage) {
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
        applyNonLinearDistortion();
    }

    /**
     * 비선형 왜곡 변환 적용
     */
    protected void applyNonLinearDistortion() {
        int width = originalImage.cols();
        int height = originalImage.rows();

        Mat distorted = new Mat(height, width, originalImage.type());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double newX = x + 10 * Math.sin(2 * Math.PI * y / 50);
                double newY = y + 10 * Math.cos(2 * Math.PI * x / 50);

                int intNewX = Math.min(Math.max((int) newX, 0), width - 1);
                int intNewY = Math.min(Math.max((int) newY, 0), height - 1);

                double[] pixel = originalImage.get(intNewY, intNewX);
                distorted.put(y, x, pixel);
            }
        }

        image = distorted;
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
