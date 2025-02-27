package morphologyexample;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import utils.ImageProcessor;

public class GUI {
    private JLabel imageView;
    private Mat image, originalImage;
    private final ImageProcessor imageProcessor = new ImageProcessor();
    private JRadioButton erodeButton, dilateButton, openButton, closeButton;
    private JRadioButton rectButton, ellipseButton, crossButton;
    private JSlider kernelSlider;

    public GUI(String windowName, Mat newImage) {
        this.image = newImage;
        this.originalImage = newImage.clone();
        initGUI(windowName);
    }
    
    public void init() {
        setSystemLookAndFeel();
        init();
    }


    private void initGUI(String windowName) {
        JFrame frame = new JFrame(windowName);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        setupOperationButtons(frame);
        setupKernelSlider(frame);
        setupShapeButtons(frame);
        setupImage(frame);
        setupButtons(frame);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void setupOperationButtons(JFrame frame) {
        JLabel opLabel = new JLabel("Operation:");
        erodeButton = new JRadioButton("Erode", true);
        dilateButton = new JRadioButton("Dilate");
        openButton = new JRadioButton("Open");
        closeButton = new JRadioButton("Close");
        ButtonGroup opGroup = new ButtonGroup();
        opGroup.add(erodeButton);
        opGroup.add(dilateButton);
        opGroup.add(openButton);
        opGroup.add(closeButton);
        JPanel opPanel = new JPanel();
        opPanel.add(opLabel);
        opPanel.add(erodeButton);
        opPanel.add(dilateButton);
        opPanel.add(openButton);
        opPanel.add(closeButton);
        frame.add(opPanel);
    }

    private void setupKernelSlider(JFrame frame) {
        JLabel sliderLabel = new JLabel("Kernel Size: 0");
        kernelSlider = new JSlider(0, 20, 3);
        kernelSlider.setMajorTickSpacing(5);
        kernelSlider.setMinorTickSpacing(1);
        kernelSlider.setPaintTicks(true);
        kernelSlider.setPaintLabels(true);
        kernelSlider.addChangeListener(e -> 
            sliderLabel.setText("Kernel Size: " + kernelSlider.getValue()));
        frame.add(sliderLabel);
        frame.add(kernelSlider);
    }

    private void setupShapeButtons(JFrame frame) {
        JLabel shapeLabel = new JLabel("Kernel Shape:");
        rectButton = new JRadioButton("Rectangle", true);
        ellipseButton = new JRadioButton("Ellipse");
        crossButton = new JRadioButton("Cross");
        ButtonGroup shapeGroup = new ButtonGroup();
        shapeGroup.add(rectButton);
        shapeGroup.add(ellipseButton);
        shapeGroup.add(crossButton);
        JPanel shapePanel = new JPanel();
        shapePanel.add(shapeLabel);
        shapePanel.add(rectButton);
        shapePanel.add(ellipseButton);
        shapePanel.add(crossButton);
        frame.add(shapePanel);
    }

    private void setupImage(JFrame frame) {
        imageView = new JLabel();
        JScrollPane imageScrollPane = new JScrollPane(imageView);
        imageScrollPane.setPreferredSize(new Dimension(640, 480));
        frame.add(imageScrollPane);
        updateView(image);
    }

    private void setupButtons(JFrame frame) {
        JPanel buttonPanel = new JPanel();
        JButton applyButton = new JButton("Apply Operation");
        applyButton.addActionListener(e -> applyMorphologicalOperation());
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            image = originalImage.clone();
            updateView(image);
        });
        buttonPanel.add(applyButton);
        buttonPanel.add(resetButton);
        frame.add(buttonPanel);
    }

    private void applyMorphologicalOperation() {
        int kernelSize = kernelSlider.getValue();
        if (kernelSize == 0) return;
        int shape = rectButton.isSelected() ? Imgproc.MORPH_RECT : 
                    ellipseButton.isSelected() ? Imgproc.MORPH_ELLIPSE : Imgproc.MORPH_CROSS;
        Mat kernel = Imgproc.getStructuringElement(shape, new Size(kernelSize, kernelSize));
        Mat result = new Mat();
        if (erodeButton.isSelected()) {
            Imgproc.erode(image, result, kernel);
        } else if (dilateButton.isSelected()) {
            Imgproc.dilate(image, result, kernel);
        } else if (openButton.isSelected()) {
            Imgproc.morphologyEx(image, result, Imgproc.MORPH_OPEN, kernel);
        } else if (closeButton.isSelected()) {
            Imgproc.morphologyEx(image, result, Imgproc.MORPH_CLOSE, kernel);
        }
        updateView(result);
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
