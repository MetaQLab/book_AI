package depth;

import java.awt.*;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.calib3d.StereoBM;
import org.opencv.imgcodecs.Imgcodecs;
import utils.ImageProcessor;

public class Depth {
    private JLabel imageView;
    private JLabel originalImageLabel;
    private String windowName;
    private Mat depthMap, leftImage, rightImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();

    public Depth(String windowName, Mat left, Mat right) {
        this.windowName = windowName;
        this.leftImage = left.clone();
        this.rightImage = right.clone();
        this.depthMap = new Mat();
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
        Image originalAWTImage = imageProcessor.toBufferedImage(leftImage);
        originalImageLabel.setIcon(new ImageIcon(originalAWTImage));

        imagesPanel.add(originalImageLabel);
        imagesPanel.add(imageView);

        frame.add(imagesPanel, c);
        applyDepthMap();
    }

    /**
     * 깊이 맵 생성
     */
    protected void applyDepthMap() {
        Mat leftGray = new Mat(), rightGray = new Mat();
        Imgproc.cvtColor(leftImage, leftGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(rightImage, rightGray, Imgproc.COLOR_BGR2GRAY);

        StereoBM stereo = StereoBM.create(16, 15);
        stereo.compute(leftGray, rightGray, depthMap);

        // 깊이 맵을 8비트로 변환
        Core.normalize(depthMap, depthMap, 0, 255, Core.NORM_MINMAX, CvType.CV_8U);
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
        Image outputImage = imageProcessor.toBufferedImage(depthMap);
        imageView.setIcon(new ImageIcon(outputImage));

        Image originalAnnotated = imageProcessor.toBufferedImage(leftImage);
        originalImageLabel.setIcon(new ImageIcon(originalAnnotated));
    }
}
