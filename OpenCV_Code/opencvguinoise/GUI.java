package opencvguinoise;

import java.awt.Image;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import utils.ImageProcessor;

public class GUI {
    private JLabel imageView;
    private String windowName;
    private Mat image, originalImage;
    
    private final ImageProcessor imageProcessor = new ImageProcessor();
    
    private JRadioButton noneButton, blurButton, bilateralButton, gaussianButton, medianButton;
    private JButton noiseButton;

    public GUI(String windowName, Mat newImage) {
        super();
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

        setupFilterOptions(frame);
        setupImage(frame);
        setupButtons(frame);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    private void setupFilterOptions(JFrame frame) {
        JLabel filterLabel = new JLabel("Select Filter:", JLabel.CENTER);
        filterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        frame.add(filterLabel);

        noneButton = new JRadioButton("None", true);
        blurButton = new JRadioButton("Blur");
        bilateralButton = new JRadioButton("Bilateral");
        gaussianButton = new JRadioButton("Gaussian");
        medianButton = new JRadioButton("Median");

        ButtonGroup group = new ButtonGroup();
        group.add(noneButton);
        group.add(blurButton);
        group.add(bilateralButton);
        group.add(gaussianButton);
        group.add(medianButton);

        JPanel radioPanel = new JPanel();
        radioPanel.add(noneButton);
        radioPanel.add(blurButton);
        radioPanel.add(bilateralButton);
        radioPanel.add(gaussianButton);
        radioPanel.add(medianButton);

        frame.add(radioPanel);
    }

    private void setupImage(JFrame frame) {
        JLabel mouseWarning = new JLabel("Try clicking on the image!", JLabel.CENTER);
        mouseWarning.setAlignmentX(Component.CENTER_ALIGNMENT);
        mouseWarning.setFont(new Font("Serif", Font.PLAIN, 18));
        frame.add(mouseWarning);

        imageView = new JLabel();
        
        final JScrollPane imageScrollPane = new JScrollPane(imageView);
        imageScrollPane.setPreferredSize(new Dimension(640, 480));
        
        imageView.addMouseListener(new MouseAdapter() {  
            public void mousePressed(MouseEvent e) {  
                Imgproc.circle(image, new Point(e.getX(), e.getY()), 20, new Scalar(0, 0, 255), 4);
                updateView(image);
            }  
        });

        frame.add(imageScrollPane);
    }

    private void setupButtons(JFrame frame) {
        JPanel buttonPanel = new JPanel();

        JButton clearButton = new JButton("Reset");
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                image = originalImage.clone();
                updateView(originalImage);
            }
        });
        buttonPanel.add(clearButton);

        noiseButton = new JButton("Add Noise");
        noiseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                applySelectedFilter();
            }
        });
        buttonPanel.add(noiseButton);

        frame.add(buttonPanel);
    }

    private void applySelectedFilter() {
        Mat processedImage = image.clone();
        
        if (blurButton.isSelected()) {
            Imgproc.blur(image, processedImage, new Size(15, 15));
        } else if (bilateralButton.isSelected()) {
            Imgproc.bilateralFilter(image, processedImage, 9, 75, 75);
        } else if (gaussianButton.isSelected()) {
            Imgproc.GaussianBlur(image, processedImage, new Size(3.0, 3.0), 0);
        } else if (medianButton.isSelected()) {
            Imgproc.medianBlur(image, processedImage, 15);
        }

        updateView(processedImage);
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private void updateView(Mat newMat) {
        Image outputImage = imageProcessor.toBufferedImage(newMat);
        imageView.setIcon(new ImageIcon(outputImage));
    }
}
