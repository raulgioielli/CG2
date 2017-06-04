import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.photo.Photo;

public class Teste
{
   public static void main( String[] args )
   {
	  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	  Mat mascara = Imgcodecs.imread("C:\\Users\\raulg\\Desktop\\img1.png", Imgcodecs.CV_LOAD_IMAGE_ANYCOLOR);
	  Mat imagemProcessar = Imgcodecs.imread("C:\\Users\\raulg\\Desktop\\img2.png", Imgcodecs.CV_LOAD_IMAGE_ANYCOLOR);
	  Mat imagemFinal = imagemProcessar.clone();
	  Mat imagemFinalBW = new Mat(imagemProcessar.rows(),imagemProcessar.cols(),Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
	  Mat imagemCopia = mascara.clone();
	  
	  System.out.println(mascara.size());
	  System.out.println(imagemProcessar.size());
	  List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	  
	  Core.absdiff(imagemProcessar, mascara, imagemFinal);
	  corOriginal(imagemFinal, imagemProcessar, 45);
	  Imgproc.erode(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
	  Imgproc.dilate(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
	  
	  Imgproc.dilate(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,4)));
	  Imgproc.erode(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,4)));
	  
	  Imgproc.cvtColor(imagemFinal, imagemFinalBW, 7);
	  
	  Imgproc.findContours(imagemFinalBW, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
	  
	  List<MatOfPoint> jogadores = new ArrayList<MatOfPoint>();
	  
	  for (int i=0; i<contours.size(); i++){
		  if(Imgproc.contourArea(contours.get(i)) > 20){
			jogadores.add(contours.get(i));  
		  }
		  
//	        //Convert contours(i) from MatOfPoint to MatOfPoint2f
//	        MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
//	        //Processing on mMOP2f1 which is in type MatOfPoint2f
//	        double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
//	        Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
//
//	        //Convert back to MatOfPoint
//	        MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
//
//	        // Get bounding rect of contour
//	        Rect rect = Imgproc.boundingRect(points);
//
//	         // draw enclosing rectangle (all same color, but you could use variable i to make them unique)
//	        Imgproc.rectangle(imagemFinal, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(0, 255, 0, 255), 3); 

	  }
	  
	  List<Moments> mu = new ArrayList<Moments>(jogadores.size());
	    for (int i = 0; i < jogadores.size(); i++) {
	        mu.add(i, Imgproc.moments(jogadores.get(i), false));
	        Moments p = mu.get(i);
	        int x = (int) (p.get_m10() / p.get_m00());
	        int y = (int) (p.get_m01() / p.get_m00());
	        
	        Mat mask = Mat.zeros(imagemFinal.rows(), imagemFinal.cols(), CvType.CV_8U);
	        Imgproc.drawContours(mask, contours, i, new Scalar(255,255,255), -1);
	        Scalar mean = Core.mean(imagemFinal, mask);
	        Imgproc.circle(imagemCopia, new Point(x, y), 4, mean, -1);
	        
	    }
	  
	  Imgcodecs.imwrite("C:\\Users\\raulg\\Desktop\\img3.png", imagemCopia);
   }
   
   public static void corOriginal(Mat m1, Mat m2, int threshold){
	   for(int i=0;i<m1.cols();i++){
		   for(int j=0;j<m1.rows();j++){
			   int mudarCor = 0;
			   for(int k=0;k<m1.get(j, i).length;k++){
				   if(m1.get(j, i)[k] > threshold){
					   mudarCor++;
				   }
			   }
			   if(mudarCor != 0 && j > 25 && j < (m1.rows() - 25)){
				   m1.put(j, i, m2.get(j, i));
			   }else{
				   double[] d = {0,0,0};
				   m1.put(j, i, d);
			   }
		   }
	   }
   }


}
