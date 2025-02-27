package video;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgcodecs.Imgcodecs;

public class Contour {

    private Mat image;
    private Mat binary;
    private double areaThreshold;
    private String fillFlag;
    private String onFillString;
    private String boundingBoxString;
    private String circleString;
    private String convexHullString;
    private String enclosingType;

    private JFrame frame;
    private JLabel imageLabel;
    
    public class Main {
        public static void main(String[] args) {
            // OpenCV 라이브러리 로드
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            // 이미지 로드
            String imagePath = "path/to/your/image.jpg"; // 사용할 이미지 경로 설정
            Mat image = Imgcodecs.imread(imagePath);
            
            if (image.empty()) {
                System.out.println("이미지를 불러올 수 없습니다.");
                return;
            }
            
            // 이미지를 그레이스케일로 변환
            Mat gray = new Mat();
            Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

            // 이진화
            Mat binary = new Mat();
            Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY);
            
            // Contour 객체 생성 및 윤곽선 검출 실행
            Contour contourProcessor = new Contour(image, binary, 500.0, "off", "box", "circle", "hull", "box");
            contourProcessor.drawContours();
        }
    }


    public Contour(Mat image, Mat binary, double areaThreshold, String fillFlag, String boundingBoxString,
            String circleString, String convexHullString, String enclosingType) {
        this.image = image;
        this.binary = binary;
        this.areaThreshold = areaThreshold;
        this.fillFlag = fillFlag;
        this.onFillString = "on";  // 채우기 여부 설정 예시
        this.boundingBoxString = boundingBoxString;
        this.circleString = circleString;
        this.convexHullString = convexHullString;
        this.enclosingType = enclosingType;

        initGUI();
    }

    // GUI 초기화 (이미지 표시)
    private void initGUI() {
        frame = new JFrame("Contour Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        imageLabel = new JLabel();
        frame.add(imageLabel);
        frame.setVisible(true);
    }

    // 윤곽선 그리기 메소드
    protected void drawContours() {
        Mat contourMat = binary.clone();
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        int thickness = (fillFlag.equals(onFillString)) ? -1 : 2;

        Imgproc.findContours(contourMat, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        for (int i = 0; i < contours.size(); i++) {
            MatOfPoint currentContour = contours.get(i);
            double currentArea = Imgproc.contourArea(currentContour);
            if (currentArea > areaThreshold) {
                Imgproc.drawContours(image, contours, i, new Scalar(0, 255, 0), thickness);

                if (boundingBoxString.equals(enclosingType)) {
                    drawBoundingBox(currentContour);
                } else if (circleString.equals(enclosingType)) {
                    drawEnclosingCircle(currentContour);
                } else if (convexHullString.equals(enclosingType)) {
                    drawConvexHull(currentContour);
                }
            } else {
                Imgproc.drawContours(image, contours, i, new Scalar(0, 0, 255), thickness);
            }
        }

        updateView();
    }

    private void drawBoundingBox(MatOfPoint currentContour) {
        Rect rectangle = Imgproc.boundingRect(currentContour);

        Imgproc.rectangle(image, rectangle.tl(), rectangle.br(), new Scalar(255, 0, 0), 1);
    }

    private void drawEnclosingCircle(MatOfPoint currentContour) {
        float[] radius = new float[1];
        Point center = new Point();

        MatOfPoint2f currentContour2f = new MatOfPoint2f();
        currentContour.convertTo(currentContour2f, CvType.CV_32FC2);
        Imgproc.minEnclosingCircle(currentContour2f, center, radius);

        Imgproc.circle(image, center, (int) radius[0], new Scalar(255, 0, 0));
    }

    private void drawConvexHull(MatOfPoint currentContour) {
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(currentContour, hull);

        List<MatOfPoint> hullContours = new ArrayList<MatOfPoint>();
        MatOfPoint hullMat = new MatOfPoint();
        hullMat.create((int) hull.size().height, 1, CvType.CV_32SC2);

        for (int j = 0; j < hull.size().height; j++) {
            int index = (int) hull.get(j, 0)[0];
            double[] point = new double[] { currentContour.get(index, 0)[0], currentContour.get(index, 0)[1] };
            hullMat.put(j, 0, point);
        }

        hullContours.add(hullMat);

        Imgproc.drawContours(image, hullContours, 0, new Scalar(128, 0, 0), 2);
    }

    // 화면 갱신 기능: 이미지 라벨을 업데이트하는 메소드
    private void updateView() {
        // OpenCV Mat을 BufferedImage로 변환
        BufferedImage bufferedImage = matToBufferedImage(image);
        ImageIcon imageIcon = new ImageIcon(bufferedImage);
        imageLabel.setIcon(imageIcon);
        frame.pack();
    }


public BufferedImage matToBufferedImage(Mat mat) {
    try {
        // Mat 객체를 RGB로 변환 (그레이스케일 또는 RGBA일 경우 변환)
        Mat matRGB = new Mat();
        if (mat.channels() == 1) {
            Imgproc.cvtColor(mat, matRGB, Imgproc.COLOR_GRAY2RGB); // 그레이스케일을 RGB로 변환
        } else if (mat.channels() == 4) {
            Imgproc.cvtColor(mat, matRGB, Imgproc.COLOR_RGBA2RGB); // RGBA를 RGB로 변환
        } else {
            matRGB = mat; // 이미 RGB인 경우 그대로 사용
        }

        // Mat 객체를 바이트 배열로 변환
        byte[] byteArray = new byte[(int) (matRGB.total() * matRGB.channels())];
        matRGB.get(0, 0, byteArray);

        // 바이트 배열을 BufferedImage로 변환
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        BufferedImage bufferedImage = ImageIO.read(bais);

        return bufferedImage;
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
}
