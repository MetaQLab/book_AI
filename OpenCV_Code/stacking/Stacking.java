package stacking;

import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Core;
import org.opencv.imgcodecs.Imgcodecs;
import utils.ImageProcessor;

public class Stacking {
    private JLabel imageView;
    private JLabel originalImageLabel;
    private String windowName;
    private Mat stackedImage, originalImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();
    private List<Mat> imageList = new ArrayList<>();

    public Stacking(String windowName, Mat newImage) {
        this.windowName = windowName;
        this.originalImage = newImage.clone();
        this.stackedImage = newImage.clone();
        loadMultipleImages(); // 여러 장의 이미지 로드
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
        applyStackingEffect();
    }

    /**
     * 스태킹 효과 적용
     */
    protected void applyStackingEffect() {
        if (imageList.isEmpty()) {
            System.out.println("No additional images loaded for stacking.");
            return;
        }

        Mat result = new Mat(originalImage.size(), originalImage.type(), new Scalar(0, 0, 0));

        for (Mat img : imageList) {
            Core.addWeighted(result, 0.5, img, 0.5, 0, result); // 이미지 블렌딩
        }

        stackedImage = result;
        updateView();
    }

    /**
     * 여러 장의 이미지 로드
     */
    private void loadMultipleImages() {
        String[] imagePaths = {
            "src/img/fruit.jpg",  // 원본 이미지
            "src/img/human.jpg", // 다른 이미지들
            "src/img/sample.jpg"
        };

        for (String path : imagePaths) {
            Mat img = Imgcodecs.imread(path);
            if (!img.empty()) {
                Imgproc.resize(img, img, originalImage.size()); // 동일 크기로 조정
                imageList.add(img);
            } else {
                System.out.println("Failed to load: " + path);
            }
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
        Image outputImage = imageProcessor.toBufferedImage(stackedImage);
        imageView.setIcon(new ImageIcon(outputImage));

        Image originalAnnotated = imageProcessor.toBufferedImage(originalImage);
        originalImageLabel.setIcon(new ImageIcon(originalAnnotated));
    }
}
