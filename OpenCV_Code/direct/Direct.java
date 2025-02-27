package direct;

import java.awt.*;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utils.ImageProcessor;

public class Direct {
    private JLabel imageView;
    private JLabel originalImageLabel;
    private String windowName;
    private Mat image, originalImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();

    public Direct(String windowName, Mat newImage) {
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
        applyDirectionalFilter();
    }

    /**
     * 방향성 필터 적용
     */
    protected void applyDirectionalFilter() {
        Mat kernel = new Mat(3, 3, CvType.CV_32F);

        // 수직 방향 검출 필터 (Vertical Edge Detection)
        kernel.put(0, 0, -1, 0, 1);
        kernel.put(1, 0, -2, 0, 2);
        kernel.put(2, 0, -1, 0, 1);

        Imgproc.filter2D(originalImage, image, -1, kernel);
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
