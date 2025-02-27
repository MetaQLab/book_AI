package utils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public interface VideoProcessor_weight {
    Mat process(Mat inputImage);

    public class AbsDifferenceBackground implements VideoProcessor_weight {
        private Mat backgroundImage;
        private Mat accumulatedBackground;
        private double learningRate;
        private double threshold;

        public AbsDifferenceBackground(Mat backgroundImage, double learningRate, double threshold) {
            this.backgroundImage = backgroundImage.clone();
            this.accumulatedBackground = new Mat();
            this.learningRate = learningRate;
            this.threshold = threshold;

            Imgproc.cvtColor(backgroundImage, this.accumulatedBackground, Imgproc.COLOR_BGR2GRAY);
            this.accumulatedBackground.convertTo(this.accumulatedBackground, CvType.CV_32F);
        }

        @Override
        public Mat process(Mat inputImage) {
            Mat inputGray = new Mat();
            Mat foregroundThresh = new Mat();

            Imgproc.cvtColor(inputImage, inputGray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.threshold(inputGray, foregroundThresh, threshold, 255, Imgproc.THRESH_BINARY_INV);

            Mat inputFloating = new Mat();
            inputGray.convertTo(inputFloating, CvType.CV_32F);
            Imgproc.accumulateWeighted(inputFloating, accumulatedBackground, learningRate, foregroundThresh);

            return negative(foregroundThresh);
        }

        private Mat negative(Mat foregroundThresh) {
            Mat result = new Mat();
            Mat white = foregroundThresh.clone();
            white.setTo(new Scalar(255.0));  // 흰색 배경 생성
            Core.subtract(white, foregroundThresh, result);  // 색상 반전
            return result;
        }
    }
}
