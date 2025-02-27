package distance;

import java.util.List;
import java.util.ArrayList;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import javax.swing.*;

import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import utils.ImageProcessor;

public class GUI {
    private JLabel imageView;
    private JLabel originalImageLabel; 
    private String windowName;
    private Mat image, originalImage,grayImage;

    private final ImageProcessor imageProcessor = new ImageProcessor();

    public GUI(String windowName, Mat newImage) {
        super();
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

        frame.add(imagesPanel,c);
        processOperation();
    }

    protected void processOperation() {
        Imgproc.Canny(originalImage, image, 220, 255, 3, false);
        Imgproc.threshold(image, image, 100, 255, Imgproc.THRESH_BINARY_INV);
        Imgproc.distanceTransform(image, image, Imgproc.CV_DIST_L2, 3);
        image.convertTo(image, CvType.CV_8UC1);
        Core.multiply(image, new Scalar(20), image);
        
        updateView();
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        Mat newMat = image;
        Image outputImage = imageProcessor.toBufferedImage(newMat);
        imageView.setIcon(new ImageIcon(outputImage));
        
        Mat originalAnnotatedMat = grayImage;
        Image originalAnnotated = imageProcessor.toBufferedImage(originalAnnotatedMat);
        originalImageLabel.setIcon(new ImageIcon(originalAnnotated));
        
    }
}