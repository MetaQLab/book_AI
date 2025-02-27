package test;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.Scalar;


public class OpencvTest3 {
	static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

	public static void main(String[] args) {
		Mat image = new Mat(new Size(3,3), CvType.CV_8UC3, new Scalar(new double[]{128,3,4}));
		
		System.out.println(image.dump());
	}
}

