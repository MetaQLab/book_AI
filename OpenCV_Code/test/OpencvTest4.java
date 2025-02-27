package test;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;


public class OpencvTest4 {
	static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

	public static void main(String[] args) {
		Mat image = new Mat(new Size(3,3), CvType.CV_8UC3, new Scalar(new double[]{128,3,4}));
		
		for(int i=0;i<image.rows();i++) {
			  for(int j=0;j<image.cols();j++) {
			    image.put(i, j, new byte[]{1,2,3});
			  }
			}
		
		
		System.out.println(image.dump());
	}
	
	public void filter(Mat image) {
		int totalBytes = (int)(image.total()*image.elemSize());
		byte buffer[] = new byte[totalBytes];
		image.get(0, 0,buffer);
		for(int i=0;i<totalBytes;i++) {
	 		if(i%3==0) buffer[i]=0;
		}
		image.put(0, 0, buffer);
	}
	
	public Mat openFile(String fileName) throws Exception{
		  Mat newImage = Imgcodecs.imread(fileName);
		  if(newImage.dataAddr()==0){
		  throw new Exception ("Couldn't open file"+fileName);
		  }
		  return newImage;
		}
}

