package gabor;

import java.awt.*;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utils.ImageProcessor;

public class Gabor {
    private JLabel imageView;
    private JLabel originalImageLabel;
    private String windowName;
    private Mat image, originalImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();

    public Gabor(String windowName, Mat newImage) {
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
        applyGaborFilter();
    }

    /**
     * Gabor 필터 적용 메서드
     */
    protected void applyGaborFilter() {
        // Gabor 필터 파라미터 정의
        int ksize = 31;  // 커널 크기
        double sigma = 4.0;  // 가우시안 함수의 표준편차
        double theta = Math.PI / 4;  // 필터의 방향 (45도)
        double lambda = 10.0;  // 사인파의 파장
        double gamma = 0.5;  // 필터의 종횡비 (필터 모양 제어)
        double psi = 0;  // 위상 오프셋

        // OpenCV로 Gabor 필터 생성
        Mat kernel = Imgproc.getGaborKernel(new Size(ksize, ksize), sigma, theta, lambda, gamma, psi);
        Imgproc.filter2D(originalImage, image, -1, kernel);  // 이미지를 필터로 처리
        updateView();  // 필터가 적용된 이미지를 뷰에 업데이트
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
