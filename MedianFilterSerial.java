/* Tshilidzi Mphelo
 * 20/11/2020
 * Image Filtering
 */
 
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;

public class MedianFilterSerial{
   public static void main(String[] args) throws IOException {
      //Handling terminal input
      String path = args[0];
      String out = args[1];
      int filter = Integer.parseInt(args[2]);
      
      //Reading in the imgage
      File f = new File(path);
      BufferedImage img = ImageIO.read(f);  
         
      //Getting the image's sizes
      int height = img.getHeight();
      int width = img.getWidth();     
      int[][] processed = new int[width][height];
      BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);     
      
      //Creating the blocks
      int radius = (int) ((filter - 1) / 2);
      int blocks = (filter * filter);
      
      //Getting processing time
      long currentTime = System.currentTimeMillis();
      for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            //for processing the surrounding pixels
            int startX = x - radius;
            int startY = y - radius;

            //The surrounding pixels of the current pixel will be put in and arraylist, inorder to find the median
            ArrayList<Integer> red = new ArrayList<Integer>();
            ArrayList<Integer> green = new ArrayList<Integer>();
            ArrayList<Integer> blue = new ArrayList<Integer>();
            ArrayList<Integer> alpha = new ArrayList<Integer>();
        
            for (int i = startX; i < startX + filter; i++) {
                for (int j = startY; j < startY + filter; j++) {
                    if (!(i < 0 || i >= width || j < 0 || j >= height)) {
                        int px = img.getRGB(i, j);
                        red.add((px >> 16) & 0xff);
                        green.add((px >> 8) & 0xff);
                        blue.add(px & 0xff);
                        alpha.add((px >> 24) & 0xff);
                    }
                }
            }

          }
      }
   }
}