package histogram;

import java.util.*;
import java.awt.*;
import javax.swing.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import utils.ImageProcessor;

public class GUI {
    private JLabel imageView, originalImageLabel, statsLabel;
    private String windowName;
    private Mat image, originalImage, grayImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();

    public GUI(String windowName, Mat newImage) {
        this.windowName = windowName;
        this.image = newImage;
        this.originalImage = newImage.clone();
        this.grayImage = newImage.clone();
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
        JPanel imagesPanel = new JPanel(new GridLayout(1, 2));
        imageView = new JLabel();
        originalImageLabel = new JLabel();
        statsLabel = new JLabel("Statistics: ");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        imagesPanel.add(originalImageLabel);
        imagesPanel.add(imageView);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 6;
        c.gridwidth = 2;
        frame.add(imagesPanel, c);
        
        c.gridy = 7;
        frame.add(statsLabel, c);
        
        processOperation();
    }

    protected void processOperation() {
        Imgproc.cvtColor(originalImage, grayImage, Imgproc.COLOR_RGB2GRAY);
        Imgproc.equalizeHist(grayImage, image);
        calculateAndDisplayStats(image);
        updateView();
    }

    private void calculateAndDisplayStats(Mat img) {
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble stdDev = new MatOfDouble();
        Core.meanStdDev(img, mean, stdDev);
        
        Core.MinMaxLocResult minMax = Core.minMaxLoc(img);
        double min = minMax.minVal;
        double max = minMax.maxVal;
        
        int[] histogram = new int[256];
        for (int i = 0; i < img.rows(); i++) {
            for (int j = 0; j < img.cols(); j++) {
                int value = (int) img.get(i, j)[0];
                histogram[value]++;
            }
        }
        
        int mode = 0, modeCount = 0;
        for (int i = 0; i < histogram.length; i++) {
            if (histogram[i] > modeCount) {
                modeCount = histogram[i];
                mode = i;
            }
        }
        
        statsLabel.setText(String.format("<html><b>Statistics</b><br>Count: %d &nbsp&nbsp Mean: %.2f &nbsp&nbsp StdDev: %.2f<br>Min: %.2f &nbsp&nbsp Max: %.2f &nbsp&nbsp Mode: %d</html>", 
                                         img.rows() * img.cols(), mean.get(0,0)[0], stdDev.get(0,0)[0], min, max, mode));
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        Image originalAWTImage = imageProcessor.toBufferedImage(originalImage);
        originalImageLabel.setIcon(new ImageIcon(originalAWTImage));
        
        Image outputImage = imageProcessor.toBufferedImage(image);
        imageView.setIcon(new ImageIcon(outputImage));
    }
}
