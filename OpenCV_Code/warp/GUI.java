package warp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import javax.swing.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import utils.ImageProcessor;

public class GUI {
    private static final String FIRST = "First";
    private static final String SECOND = "Second";
    private static final String THIRD = "Third";
    private static final String FOURTH = "Fourth";

    private String selectedPoint = FIRST;
    private final ImageProcessor imageProcessor = new ImageProcessor();
    private Mat image, originalImage;
    private JLabel imageView;

    private Point[] points = new Point[4]; // 선택된 4개의 좌표 저장

    public GUI(String windowName, Mat newImage) {
        this.image = newImage;
        this.originalImage = newImage.clone();
        initGUI(windowName);
    }

    public void init() {
        setSystemLookAndFeel();
    }


    private void initGUI(String windowName) {
        setSystemLookAndFeel();
        JFrame frame = new JFrame(windowName);
        frame.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setupPointSelection(frame);
        setupImageView(frame);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupPointSelection(JFrame frame) {
        JRadioButton firstButton = createRadioButton(FIRST, true);
        JRadioButton secondButton = createRadioButton(SECOND, false);
        JRadioButton thirdButton = createRadioButton(THIRD, false);
        JRadioButton fourthButton = createRadioButton(FOURTH, false);

        ButtonGroup group = new ButtonGroup();
        group.add(firstButton);
        group.add(secondButton);
        group.add(thirdButton);
        group.add(fourthButton);

        JPanel panel = new JPanel(new GridLayout(1, 0));
        panel.add(firstButton);
        panel.add(secondButton);
        panel.add(thirdButton);
        panel.add(fourthButton);

        addComponent(frame, new JLabel("Select Point:"), 0, 0);
        addComponent(frame, panel, 1, 0);

        JButton applyButton = new JButton("Apply Perspective Transform");
        applyButton.addActionListener(e -> applyPerspectiveTransform());
        addComponent(frame, applyButton, 0, 1, 2, 1);
    }

    private JRadioButton createRadioButton(String name, boolean selected) {
        JRadioButton button = new JRadioButton(name);
        button.setActionCommand(name);
        button.setSelected(selected);
        button.addActionListener(e -> selectedPoint = e.getActionCommand());
        return button;
    }

    private void setupImageView(JFrame frame) {
        imageView = new JLabel();
        imageView.setHorizontalAlignment(SwingConstants.CENTER);
        addComponent(frame, imageView, 0, 2);
        updateView(image);

        // 마우스 클릭 이벤트 추가 (좌표 저장)
        imageView.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setPoint(e.getX(), e.getY());  // 마우스 클릭한 위치를 저장
            }
        });
    }
    
    private void setPoint(int x, int y) {
        int index = -1;

        // 선택된 버튼에 따라 인덱스 결정
        switch (selectedPoint) {
            case FIRST:
                index = 0;
                break;
            case SECOND:
                index = 1;
                break;
            case THIRD:
                index = 2;
                break;
            case FOURTH:
                index = 3;
                break;
        }

        // 선택한 인덱스에 좌표 저장
        if (index != -1) {
            points[index] = new Point(x, y);
            System.out.println("Point " + index + " set: " + points[index]);
        }
    }


    private void applyPerspectiveTransform() {
        // points 배열에 null 값이 있는지 확인
        if (points[0] == null || points[1] == null || points[2] == null || points[3] == null) {
        	System.out.println("Point 0: " + points[0]);
        	System.out.println("Point 1: " + points[1]);
        	System.out.println("Point 2: " + points[2]);
        	System.out.println("Point 3: " + points[3]);

            JOptionPane.showMessageDialog(null, "Please select all 4 points first!");
            return;
        }

        // 원본 좌표 설정
        MatOfPoint2f srcMat = new MatOfPoint2f(
            points[0], points[1], points[2], points[3]
        );

        // 목표로 하는 좌표 (변환 후 위치) 설정
        MatOfPoint2f dstMat = new MatOfPoint2f(
            new Point(0, 0),
            new Point(300, 0),
            new Point(0, 300),
            new Point(300, 300)
        );

        // 원근 변환 행렬 계산
        Mat perspectiveMatrix = Imgproc.getPerspectiveTransform(srcMat, dstMat);
        Mat output = new Mat();

        // 새로 추가한 warpPerspective 메서드 호출
        warpPerspective(originalImage, output, perspectiveMatrix, new Size(300, 300));

        // 결과 이미지를 화면에 업데이트
        updateView(output);
    }

    /**
     * 원근 변환을 수행하는 메서드
     * @param src 원본 이미지
     * @param dst 변환된 결과 이미지
     * @param M 원근 변환 행렬
     * @param dsize 출력 이미지 크기
     */
    public static void warpPerspective(Mat src, Mat dst, Mat M, Size dsize) {
        Imgproc.warpPerspective(src, dst, M, dsize);
    }

    private void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addComponent(JFrame frame, JComponent component, int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.WEST;
        frame.add(component, gbc);
    }

    private void addComponent(JFrame frame, JComponent component, int x, int y, int width, int height) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        frame.add(component, gbc);
    }
    

    private void updateView(Mat newMat) {
        imageView.setIcon(new ImageIcon(imageProcessor.toBufferedImage(newMat)));
    }
}

