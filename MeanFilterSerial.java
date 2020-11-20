/* Tshilidzi Mphelo
 * 20/11/2020
 * Image Filtering
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
      
      int[][] processed = new int[width][height];
      BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      
      //Creating the blocks
      int radius = (int) ((filter - 1) / 2);
      int blocks = (filter * filter);
      
      //Getting processing time
      long currentTime = System.currentTimeMillis();
      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            //for processing the surrounding pixels as a block
            int startX = x - radius;
            int startY = y - radius;
            int red = 0;
            int green = 0;
            int blue = 0;
            int alpha = 0;
            
            //Processing the block
            for (int i = startX; i < startX + filter; i++) {
               for (int j = startY; j < startY + filter; j++) {
                  if (!(i < 0 || i >= width || j < 0 || j >= height)) {
                     int px = img.getRGB(i, j);
                     red += (px >> 16) & 0xff;
                     green += (px >> 8) & 0xff;
                     blue += px & 0xff;
                     alpha += (px >> 24) & 0xff;
                     }
               }
            }
            int r = (int) (red / blocks);
            int g = (int) (green / blocks);
            int b = (int) (blue / blocks);
            int a = (int) (alpha / blocks);

            //Setting the processed pixels
            int rgb = (a << 24) | (r << 16) | (g << 8) | b;
            processed[x][y] = rgb;
            outputImage.setRGB(x, y, rgb);
            }
         }
         long lapsed = System.currentTimeMillis() - currentTime;
         System.out.println(lapsed);
         File outputFile = new File(out);
         ImageIO.write(outputImage, "jpg", outputFile);
   }
}