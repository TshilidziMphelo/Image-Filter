/*Tshilidzi Mphelo
 *20/11/2020
 *Image Filtering
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MeanFilterSerial {
   public static void main(String[] args) throws IOException {
      
      //Handling terminal inputs
      String path = args[0];
      String out = args[1];
      int filter = Integer.parseInt(args[2]);
      
      //Reading in the imgage
      File f = new File(path);
      BufferedImage img = ImageIO.read(f);
   
      //Getting the image's sizes
      int height = img.getHeight();
      int width = img.getWidth();

    
   }
}
