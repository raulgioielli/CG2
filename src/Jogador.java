import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Jogador {
	public Scalar cor;
	public int label = 10;
	public int centerX;
	public int centerY;
	public Point[] percurso = new Point[(15 * 60) + 1];
	public int pos = 0;
	public int tamanhoPercurso = 0;
	
	public void adicionarPonto(int x, int y){
		Point p = new Point(x, y);
		percurso[pos] = p;
		tamanhoPercurso++;
		pos = (pos + 1) % (15 * 60); 
	}
	
	public void desenharPercurso(Mat img){
		int tamanho = (tamanhoPercurso > (15*60) ? 15*60 : tamanhoPercurso);
		for(int i=0;i<tamanho;i++){
			Scalar cor = getCor();
			
			Imgproc.circle(img, percurso[i], 2, cor);
			
//			img.get((int)percurso[i].y, (int)percurso[i].x)[0] = cor.val[0];
//			img.get((int)percurso[i].y, (int)percurso[i].x)[1] = cor.val[1];
//			img.get((int)percurso[i].y, (int)percurso[i].x)[2] = cor.val[2];
		}
	}
	
	public Scalar getCor(){
		if(label == 0)
			return new Scalar(255,0,0);
		
		return new Scalar(0,0,255);
	}
	
	public static void novoFrame(ArrayList<Jogador> frameAnterior, ArrayList<Jogador> frameAtual){
		for(int i=0;i<frameAtual.size();i++){
			int bestMatch = 0;
			double bestMatchDistance = Double.MAX_VALUE;
			for(int j=0;j<frameAnterior.size();j++){
				double dist = Math.sqrt(Math.pow(frameAtual.get(i).centerX - frameAnterior.get(j).centerX, 2)) + Math.sqrt(Math.pow(frameAtual.get(i).centerY - frameAnterior.get(j).centerY, 2));
				if(dist < bestMatchDistance){
					bestMatch = j;
					bestMatchDistance = dist;
				}
			}
			if(bestMatchDistance < 4){
				frameAtual.get(i).label = frameAnterior.get(bestMatch).label;
				frameAtual.get(i).percurso = frameAnterior.get(bestMatch).percurso.clone();
				frameAtual.get(i).pos = frameAnterior.get(bestMatch).pos;
				frameAtual.get(i).tamanhoPercurso = frameAnterior.get(bestMatch).tamanhoPercurso;
				frameAtual.get(i).adicionarPonto(frameAnterior.get(bestMatch).centerX, frameAnterior.get(bestMatch).centerY);
			}
		}
	}
}
