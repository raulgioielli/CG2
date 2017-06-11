import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.videoio.*;
import org.opencv.imgproc.*;

import java.util.List;
import java.util.ArrayList;



import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Image;
import java.awt.image.*;
import java.awt.FlowLayout;;


public class TrabalhoRenan{
	//initial min and max HSV filter values.
	//these will be changed using trackbars
	
	public static int H_MIN = 0;
	public static int H_MAX = 256;
	public static int S_MIN = 0;
	public static int S_MAX = 256;
	public static int V_MIN = 0;
	public static int V_MAX = 256;
	
	//default capture width and height
	public static final int FRAME_WIDTH = 640;
	public static final int FRAME_HEIGHT = 480;
	
	//max number of objects to be detected in frame
	public static final int MAX_NUM_OBJECTS = 50;
	
	//minimum and maximum object area
	public static final int MIN_OBJECT_AREA = 20 * 20;
	public static final int MAX_OBJECT_AREA = (int)(FRAME_HEIGHT * FRAME_WIDTH / 1.5);
	
	//names that will appear at the top of each window
	public static final String windowName = "Original Image";
	public static final String windowName1 = "HSV Image";
	public static final String windowName2 = "Thresholded Image";
	public static final String windowName3 = "After Morphological Operations";
	
	public static JFrame frame = new JFrame();
	public static JLabel lbl1 = new JLabel();
	public static JLabel lbl2 = new JLabel();
	public static int buffer = 1;

	public static void drawObject(int x, int y, Mat frame){

		Point p1 = new Point(x,y);
		Point p2 = new Point(x,y - 25);
		Point p3 = new Point(x,0);
		Point p4 = new Point(x,y + 25);	
		Point p5 = new Point(x,FRAME_HEIGHT);	
		Point p6 = new Point(x - 25,y);	
		Point p7 = new Point(0,y);	
		Point p8 = new Point(x + 25,y);	
		Point p9 = new Point(FRAME_WIDTH,y);
		Point p10 = new Point(x,y + 30);
		Scalar scal = new Scalar(0,255,0);
		
	
		Imgproc.circle(frame, p1,20, scal,2);
		
		
		if (y - 25 > 0)	Imgproc.line(frame, p1, p2 , scal,2);
		else Imgproc.line(frame, p1, p3, scal,2);
		
		if (y + 25 < FRAME_HEIGHT)Imgproc.line(frame, p1, p4, scal,2);
		else Imgproc.line(frame, p1, p5, scal,2);
		
		if (x - 25 > 0)	Imgproc.line(frame, p1, p6, scal,2);
		else Imgproc.line(frame, p1, p7,scal,2);
		
		if (x + 25 < FRAME_WIDTH) Imgproc.line(frame, p1, p8, scal,2);
		else Imgproc.line(frame, p1, p9, scal,2);
		

		Imgproc.putText(frame,Integer.toString(x) + "," + Integer.toString(y), p10,1,1, scal,2);

	}
	
	public static void morphOps(Mat thresh)	{

		Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3,3));
		//dilate with larger element so make sure object is nicely visible
		Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(8,8));

		Imgproc.erode(thresh,thresh,erodeElement);
		Imgproc.erode(thresh,thresh,erodeElement);


		Imgproc.dilate(thresh,thresh,dilateElement);
		Imgproc.dilate(thresh,thresh,dilateElement);



	}
	
	
	public static void trackFilteredObject(int x, int y, Mat threshold, Mat cameraFeed)	{
		
		Mat temp = new Mat();
		threshold.copyTo(temp);
		//these two vectors needed for output of findContours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		MatOfInt4 hierarchy = new MatOfInt4();
		//find contours of filtered image using openCV findContours function
		Imgproc.findContours(temp,contours,hierarchy,Imgproc.RETR_CCOMP,Imgproc.CHAIN_APPROX_SIMPLE);
		//use moments method to find our filtered object
		double refArea = 0;
		boolean objectFound = false;
		if (hierarchy.toArray().length>0){
			
			int numObjects = hierarchy.toArray().length;
			
			//if number of objects greater than MAX_NUM_OBJECTS we have a noisy filter
			if (numObjects < MAX_NUM_OBJECTS){
				
				for (int index = 0; index >= 0; index = hierarchy.toArray()[0])	{

					Moments moment = Imgproc.moments((Mat)contours.get(index));
					double area = moment.m00;

					//if the area is less than 20 px by 20px then it is probably just noise
					//if the area is the same as the 3/2 of the image size, probably just a bad filter
					//we only want the object with the largest area so we safe a reference area each
					//iteration and compare it to the area in the next iteration.
					if (area > MIN_OBJECT_AREA && area < MAX_OBJECT_AREA && area> refArea){
						x = (int)(moment.m10 / area);
						y = (int)(moment.m01 / area);
						objectFound = true;
						refArea = area;
					}
					else{
						objectFound = false;
					}


				}
				//let user know you found an object
				if (objectFound == true){
					Imgproc.putText(cameraFeed,"Tracking Object",new Point(0,50),2,1,new Scalar(0,255,0),2);
					//draw object location on screen
					drawObject(x,y,cameraFeed);}
					//tracking
				}
			else{
				Imgproc.putText(cameraFeed,"TOO MUCH NOISE! ADJUST FILTER",new Point(0,50),1,2,new Scalar(0,0,255),2);
			}
		}
	}
	
	 public static BufferedImage Mat2BufferedImage(Mat m) {
		    // Fastest code
		    // output can be assigned either to a BufferedImage or to an Image

		    int type = BufferedImage.TYPE_BYTE_GRAY;
		    if ( m.channels() > 1 ) {
		        type = BufferedImage.TYPE_3BYTE_BGR;
		    }
		    int bufferSize = m.channels()*m.cols()*m.rows();
		    byte [] b = new byte[bufferSize];
		    m.get(0,0,b); // get all the pixels
		    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		    System.arraycopy(b, 0, targetPixels, 0, b.length);  
		    return image;
		}
	
	public static void displayImage(Image img2) {

	    //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
	    ImageIcon icon = new ImageIcon(img2);
	    frame.setLayout(new FlowLayout());        
	    frame.setSize(img2.getWidth(null)+50, img2.getHeight(null)+50);     
	    if(buffer % 2 == 1){
	    	if(buffer > 1)
	    		frame.remove(lbl2);
	    	lbl1.setIcon(icon);
	    	frame.add(lbl1);
	    }else{
	    	frame.remove(lbl1);
	    	lbl2.setIcon(icon);
	    	frame.add(lbl2);
	    }
	    buffer++;
	    frame.repaint();
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		//some boolean variables for different functionality within this
		//program
		boolean trackObjects = false;
		boolean useMorphOps = false;
		
		
		//Matrix to store each frame of the webcam feed
		Mat cameraFeed = new Mat();

		//matrix storage for HSV image
		Mat HSV = new Mat();
		
		//matrix storage for binary threshold image
		Mat threshold = new Mat();
		
		//x and y values for the location of the object
		int x = 0;
		int y = 0;
		
		//create slider bars for HSV filtering
		//createTrackbars();
	
		
		//video capture object to acquire webcam feed
		VideoCapture capture = new VideoCapture();
	
		//open capture object at location zero (default location for webcam)
		capture.open(0);
		
		//set height and width of capture frame
		capture.set(Videoio.CAP_PROP_FRAME_WIDTH,FRAME_WIDTH);
		capture.set(Videoio.CAP_PROP_FRAME_HEIGHT,FRAME_HEIGHT);
		
		//start an infinite loop where webcam feed is copied to cameraFeed matrix
		//all of our operations will be performed within this loop
		while (true){
		
			//store image to matrix
			capture.read(cameraFeed);
		
			//convert frame from BGR to HSV colorspace
			Imgproc.cvtColor(cameraFeed,HSV,Imgproc.COLOR_BGR2HSV);
		
			//filter HSV image between values and store filtered image to
			//threshold matrix
			Core.inRange(HSV,new Scalar(0,S_MIN,V_MIN),new Scalar(0,S_MAX,V_MAX),threshold);
	
			//perform morphological operations on thresholded image to eliminate noise
			//and emphasize the filtered object(s)
			if (useMorphOps){
				morphOps(threshold);
			}
			
			//pass in thresholded frame to our object tracking function
			//this function will return the x and y coordinates of the
			//filtered object
			if (trackObjects){
				trackFilteredObject(x, y, threshold, cameraFeed);
		
			}
			//show frames 
			//imshow(windowName2,threshold);
			
			//imshow(windowName,cameraFeed);
			//displayImage(Mat2BufferedImage(cameraFeed));
			//imshow(windowName1,HSV);
			displayImage(Mat2BufferedImage(threshold));

			//delay 30ms so that screen can refresh.
			//image will not appear without this waitKey() command
			//wait(30);
		}
	}

}