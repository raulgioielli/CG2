
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;

public class Teste
{
	
	static ArrayList<Jogador> jogadores_ = new ArrayList<Jogador>();
	
   public static void main( String[] args )
   {
	  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	  String output = "C:\\Users\\raulg\\Desktop\\final.mp4";
	  String input = "C:\\Users\\raulg\\Desktop\\4742799_TESTEATLETICO.mp4";
	  
	  VideoCapture videoCapture=new VideoCapture(input);
	  Size frameSize=new Size((int)videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH),(int)videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT));
	  
	  VideoWriter videoWriter=new VideoWriter(output,VideoWriter.fourcc('X', 'V', 'I', 'D'),videoCapture.get(Videoio.CAP_PROP_FPS),frameSize,true);
	  
	  Mat mascara = new Mat();
	  
	  for(int i=0;i<(27*29);i++){
		  videoCapture.read(mascara);
		  mascara.release();
	  }
	  
	  
	  videoCapture.read(mascara);
	  Mat imagemProcessar = new Mat();
	  
	  int frame;
	  for(frame=27*29;frame<(3600);frame++){
		  Mat asd = new Mat();
		  videoCapture.read(asd);
		  asd.release();
	  }
	  
	  while(frame++ < 4001){
		  videoCapture.read(imagemProcessar);
		  Mat imagemCopia = mascara.clone();
		  
		  desenharFrame(imagemProcessar, mascara, imagemCopia);  
		  
		  videoWriter.write(imagemCopia);
		  
		  imagemCopia.release();
		  System.out.println(frame + "/" +  4000);
	  }
	  
	  videoWriter.release();
	  videoCapture.release();
	  
//	  Mat imagemInicial = Imgcodecs.imread("C:\\Users\\raulg\\Desktop\\img2.png");
//	  Mat mascara = Imgcodecs.imread("C:\\Users\\raulg\\Desktop\\img1.png");
//	  
//	  desenharFrame(imagemInicial, mascara, imagemInicial);
//	    
//	  
//	  Imgcodecs.imwrite("C:\\Users\\raulg\\Desktop\\CG\\ResultadoFinal.png", imagemInicial);
   }
   
   public static void desenharFrame(Mat imagemProcessar, Mat mascara, Mat imagemCopia){
	   	  Mat imagemFinal = imagemProcessar.clone();
		  Mat imagemFinalBW = new Mat(imagemProcessar.rows(),imagemProcessar.cols(),Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		  Mat imagemTime1 = mascara.clone();
		  Mat imagemTime2 = mascara.clone();
		  
		  List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		  
		  Core.absdiff(imagemProcessar, mascara, imagemFinal);
		  Imgcodecs.imwrite("C:\\Users\\raulg\\Desktop\\CG\\DiferencaMascaraImagem.png", imagemFinal);
		  Core.absdiff(mascara, mascara, imagemTime1);
		  Core.absdiff(mascara, mascara, imagemTime2);

		  corOriginal(imagemFinal, imagemProcessar, 45);
		  Imgcodecs.imwrite("C:\\Users\\raulg\\Desktop\\CG\\ImagemComMascaraEThreshold.png", imagemFinal);
		  Imgproc.erode(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
		  Imgproc.dilate(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,3)));
		  Imgcodecs.imwrite("C:\\Users\\raulg\\Desktop\\CG\\ImagemComMascaraEThresholdAposAbertura.png", imagemFinal);
		  
		  Imgproc.dilate(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,4)));
		  Imgproc.erode(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,4)));
		  Imgcodecs.imwrite("C:\\Users\\raulg\\Desktop\\CG\\ImagemComMascaraEThresholdAposAberturaEFechamento.png", imagemFinal);
		  
		  Imgproc.cvtColor(imagemFinal, imagemFinalBW, 7);
		  
		  Imgproc.findContours(imagemFinalBW, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);  
		  
		  
		  List<MatOfPoint> jogadores = new ArrayList<MatOfPoint>();
		  
		  for (int i=0; i<contours.size(); i++){
			  if(Imgproc.contourArea(contours.get(i)) > 20){
				jogadores.add(contours.get(i));  
			  } 

		  }
		  
		  ArrayList<Jogador> jogadores_2 = new ArrayList<Jogador>();
		  
		  List<Moments> mu = new ArrayList<Moments>(jogadores.size());
		    for (int i = 0; i < jogadores.size(); i++) {
		        mu.add(i, Imgproc.moments(jogadores.get(i), false));
		        Moments p = mu.get(i);
		        int x = (int) (p.get_m10() / p.get_m00());
		        int y = (int) (p.get_m01() / p.get_m00());
		        
		        Mat mask = Mat.zeros(imagemFinalBW.rows(), imagemFinalBW.cols(), CvType.CV_8U);
		        Imgproc.drawContours(mask, contours, i, new Scalar(255,0,0), -1);
		        //Scalar mean = Core.mean(imagemCopia, mask);
		        Scalar mean = getCorDominante(imagemFinal, mask, x, y);
		        //Scalar mean = new Scalar(imagemFinal.get(y - 3,x)[0],imagemFinal.get(y - 3,x)[1],imagemFinal.get(y - 3,x)[2]);
		        //Imgproc.circle(imagemCopia, new Point(x, y), 4, mean, -1);
		        Imgproc.circle(imagemTime1, new Point(x, y), 4, mean, -1);
		        Imgproc.circle(imagemTime2, new Point(x, y), 4, mean, -1);
		        Jogador j = new Jogador();
		        j.centerX = x;
		        j.centerY = y;
		        j.cor = mean;
		        j.label = 0;
		        jogadores_2.add(j);
		        
		    }
		    
		    Jogador.novoFrame(jogadores_, jogadores_2);
		    
		    if(jogadores_2.size() > 0 && jogadores_2.size() > jogadores_.size())
		    	MeuKmeans(jogadores_2, 2);
		    
		    for(int i=0;i<jogadores_2.size();i++){
		    	Scalar cor = (jogadores_2.get(i).label == 0 ? new Scalar(255,0,0) : (jogadores_2.get(i).label == 1 ? new Scalar(0,0,255) : new Scalar(0,0,0)));
		    	//Imgproc.circle(imagemCopia, new Point(jogadores_.get(i).centerX,jogadores_.get(i).centerY), 4, cor, -1);
		    	Imgproc.circle(imagemCopia, new Point(jogadores_2.get(i).centerX,jogadores_2.get(i).centerY), 4, cor, -1);
		    	jogadores_2.get(i).desenharPercurso(imagemCopia);
		    }
		    
		    
		    jogadores_ = jogadores_2;
		    
		    imagemFinal.release();
		    imagemFinalBW.release();
		    imagemTime1.release();
		    imagemTime2.release();
   }
   
   public static void MeuKmeans(ArrayList<Jogador> jogadores, int k){
	   double[][] centers = new double[k][3];
	   boolean labelsOk = false;
	   for(int i=0;i<jogadores.size();i++){
		   if(jogadores.get(i).label == 1 || jogadores.get(i).label == 0){
			   labelsOk = true;
			   break;
		   }
	   }
	   
	   if(labelsOk){
		   for(int i=0;i<k;i++){
			   double media1 = 0;
			   double media2 = 0;
			   double media3 = 0;
			   int div = 0;
			   for(int j=0;j<jogadores_.size();j++){
				   if(jogadores_.get(j).label == i){
					   media1 += jogadores_.get(j).cor.val[0];
					   media2 += jogadores_.get(j).cor.val[1];
					   media3 += jogadores_.get(j).cor.val[2];
					   div++;
				   }
			   }
			   if(div > 0){
				   centers[i][0] = media1 / div;
				   centers[i][1] = media2 / div;
				   centers[i][2] = media3 / div;
			   }else{
				   Random r = new Random(System.currentTimeMillis());
				   centers[i][0] = r.nextDouble() * 255;
				   centers[i][1] = r.nextDouble() * 255;
				   centers[i][2] = r.nextDouble() * 255;
			   }
		   }
	   }else{
		   for(int i=0;i<k;i++){
			   centers[i][0] = jogadores.get((jogadores.size() - 1) * i).cor.val[0];
			   centers[i][1] = jogadores.get((jogadores.size() - 1) * i).cor.val[1];
			   centers[i][2] = jogadores.get((jogadores.size() - 1) * i).cor.val[2];
		   }   
	   }
	   
	   for(int z=0;z<100;z++){
		   for(int i=0;i<jogadores.size();i++){
			   double best = Double.MAX_VALUE;
			   int bestIndex = 0;
			   for(int j=0;j<k;j++){
				   double total = Math.pow(centers[j][0] - jogadores.get(i).cor.val[0], 2) + Math.pow(centers[j][1] - jogadores.get(i).cor.val[1], 2) + Math.pow(centers[j][2] - jogadores.get(i).cor.val[2], 2);
				   if(total < best){
					   best = total;
					   bestIndex = j;
				   }
			   }
			   jogadores.get(i).label = bestIndex;
		   }
		   
		   for(int i=0;i<k;i++){
			   double media1 = 0;
			   double media2 = 0;
			   double media3 = 0;
			   int div = 0;
			   for(int j=0;j<jogadores.size();j++){
				   if(jogadores.get(j).label == i){
					   media1 += jogadores.get(j).cor.val[0];
					   media2 += jogadores.get(j).cor.val[1];
					   media3 += jogadores.get(j).cor.val[2];
					   div++;
				   }
			   }
			   centers[i][0] = media1 / div;
			   centers[i][1] = media2 / div;
			   centers[i][2] = media3 / div;
		   }
	   }  
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
			   if(mudarCor != 0 && j > 350 && j < (m1.rows() - 75)){
				   m1.put(j, i, m2.get(j, i));
			   }else{
				   double[] d = {0,0,0};
				   m1.put(j, i, d);
			   }
		   }
	   }
   }
   
   public static Scalar getCorDominante(Mat imagem, Mat mask, int centerX, int centerY){
	   ArrayList<Scalar> cores = new ArrayList<Scalar>();
	   
	   for(int i=-3;i<1;i++){
		   for(int j=-1;j<2;j++){
			   if(centerY + i > 0 && centerX + j > 0){
				   cores.add(new Scalar(imagem.get(centerY + i, centerX + j)[0],imagem.get(centerY + i, centerX + j)[1],imagem.get(centerY + i, centerX + j)[2]));
			   }
		   }
	   }
	  
	   double best = Double.MAX_VALUE;
	   int bestIndex = 0;
	   for(int i=0;i<cores.size();i++){
		   int total = 0;
		   for(int j=0;j<cores.size();j++){
			   if(i != j){
				   total += Math.pow(cores.get(i).val[0] - cores.get(j).val[0], 2) + Math.pow(cores.get(i).val[1] - cores.get(j).val[1], 2) + Math.pow(cores.get(i).val[2] - cores.get(j).val[2], 2);
			   }
		   }
		   if(total < best){
			   best = total;
			   bestIndex = i;
		   }
	   }
	   
	   return cores.get(bestIndex);
   }
}


