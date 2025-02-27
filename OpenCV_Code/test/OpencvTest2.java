package test;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;


public class OpencvTest2 {
	static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

	public static void main(String[] args) {
		Mat image2 = new Mat(480,640,CvType.CV_8UC3);
		Mat image3 = new Mat(new Size(640,480),CvType.CV_8UC3);
		
		System.out.println(image2 + "rows " + image2.rows() + " cols "+ image2.cols() + "elementsize" + image2.elemSize());
	}
}

