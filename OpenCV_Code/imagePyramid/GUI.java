package imagePyramid;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;
import utils.ImageProcessor;

public class GUI {
    private JLabel imageView;
    private String windowName;
    private Mat image, originalImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();

    public GUI(String windowName, Mat newImage) {
        this.windowName = windowName;
        this.image = newImage;
        this.originalImage = newImage.clone();
    }

    public void init() {
        setSystemLookAndFeel();
        initGUI();
    }

    private void initGUI() {
        JFrame frame = createJFrame(windowName);
        updateView(image);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JFrame createJFrame(String windowName) {
        JFrame frame = new JFrame(windowName);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.PAGE_AXIS));
        setupButton(frame);
        setupImage(frame);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private void setupImage(JFrame frame) {
        imageView = new JLabel();
        imageView.setHorizontalAlignment(SwingConstants.CENTER);
        JScrollPane imageScrollPane = new JScrollPane(imageView);
        imageScrollPane.setPreferredSize(new Dimension(640, 480));
        frame.add(imageScrollPane);
    }

    private void setupButton(JFrame frame) {
        JPanel buttonsPanel = new JPanel(new FlowLayout());

        JButton restoreButton = new JButton("Restore");
        restoreButton.addActionListener(e -> {
            image = originalImage.clone();
            updateView(originalImage);
        });

        JButton pyramidDown = new JButton("Pyramid Down");
        pyramidDown.addActionListener(e -> {
            Mat temp = new Mat();
            Imgproc.pyrDown(image, temp);
            image = temp.clone();
            updateView(image);
        });

        JButton pyramidUp = new JButton("Pyramid Up");
        pyramidUp.addActionListener(e -> {
            Mat temp = new Mat();
            Imgproc.pyrUp(image, temp);
            image = temp.clone();
            updateView(image);
        });

        JButton laplacian = new JButton("Laplacian");
        laplacian.addActionListener(e -> {
            Mat gp1 = new Mat();
            Imgproc.pyrDown(image, gp1);
            Imgproc.pyrUp(gp1, gp1, image.size());
            
            Mat laplacianImage = new Mat();
            Core.subtract(image, gp1, laplacianImage);
            updateView(laplacianImage);
        });

        buttonsPanel.add(restoreButton);
        buttonsPanel.add(pyramidDown);
        buttonsPanel.add(pyramidUp);
        buttonsPanel.add(laplacian);
        frame.add(buttonsPanel);
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateView(Mat newMat) {
        Image outputImage = imageProcessor.toBufferedImage(newMat);
        imageView.setIcon(new ImageIcon(outputImage));
    }
}
