package utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

public interface VideoProcessor {
    Mat process(Mat inputImage);

	public class AbsDifferenceBackground implements VideoProcessor {
		private Mat backgroundImage;
		public AbsDifferenceBackground(Mat backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public Mat process(Mat inputImage) {
		Mat foregroundImage = new Mat();
		Core.absdiff(backgroundImage,inputImage , foregroundImage); return foregroundImage;
		}
	}
	
    public class MixtureOfGaussianBackground implements VideoProcessor {
        private BackgroundSubtractorMOG2 mog;
        private Mat foreground;
        private double learningRate;

        public MixtureOfGaussianBackground() {
            // 배경 제거 객체 생성 (기본값: history=500, varThreshold=16, detectShadows=true)
            mog = Video.createBackgroundSubtractorMOG2();
            foreground = new Mat();
            learningRate = 0.01;
        }

        @Override
        public Mat process(Mat inputImage) {
            mog.apply(inputImage, foreground, learningRate);
            return foreground;
        }
    }
		
}