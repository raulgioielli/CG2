import java.awt.image.ReplicateScaleFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.StandardCopyOption;

import org.opencv.core.Core;

public class IA {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		for(int i=0;i<26;i++){
			  File[] files = new File("C:\\Users\\raulg\\OneDrive\\Documentos\\GitHub\\epia\\IA\\" + (char)('A' + i)).listFiles();
			  for(File file : files){
				  if(!file.isDirectory()){
					  try {
						java.nio.file.Files.move(FileSystems.getDefault().getPath(file.getAbsolutePath()), FileSystems.getDefault().getPath("C:\\Users\\raulg\\OneDrive\\Documentos\\GitHub\\epia\\IA\\" + file.getName()), (CopyOption)StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				  }
			  }
		}
		    
		  //Imgproc.dilate(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,4)));
		  //Imgproc.erode(imagemFinal, imagemFinal, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3,4)));
		  //Imgcodecs.imwrite("C:\\Users\\raulg\\Desktop\\img3.png", imagemCopia);
	}

}
