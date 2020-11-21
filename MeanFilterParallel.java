/* Tshilidzi Mphelo
 * 20/11/2020
 * Image Filtering
 */
 
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import javax.imageio.ImageIO;

public class MeanFilterParallel {

    static class MeanFilter extends RecursiveTask<Integer[][]> {
        private int xLow, xHigh, yLow, yHigh, filter;
        private int[][] rgbArray;
        private boolean splitLength;

        private int THRESHOLD = 10000;

        //Constructor
        public MeanFilter(int xLow,int yLow,int xHigh,int yHigh,int filter,int[][] rgbArray,boolean splitLength) {
            this.xLow = xLow;
            this.yLow = yLow;
            this.xHigh = xHigh;
            this.yHigh = yHigh;
            this.filter = filter;
            this.rgbArray = rgbArray;
            this.splitLength = splitLength;
        }
        
        //Method for processing the image
        private Integer[][] meanFilter() {

            int w = xHigh - xLow;
            int h = yHigh - yLow;
            
            //Getting the sizes of the picture
            int width = rgbArray[0].length;
            int height = rgbArray.length;
            
            //Blocks
            int radius = (filter - 1) / 2;
            int blocks = filter * filter;
            Integer[][] pixels = new Integer[h][w];

            int yIndex = 0;
            for (int y = yLow; y < yHigh; y++) {
                int xIndex = 0;
                for (int x = xLow; x < xHigh; x++) {

                    int startX = x - radius;
                    int startY = y - radius;

                    int red = 0;
                    int green = 0;
                    int blue = 0;
                    int alpha = 0;
                    
                    //For processing the blocks
                    for (int i = startX; i < startX + filter; i++) {
                        for (int j = startY; j < startY + filter; j++) {

                            if (!(i < 0 || i >= width || j < 0 || j >= height)) {

                                int px = rgbArray[j][i];

                                red += (px >> 16) & 0xff;
                                green += (px >> 8) & 0xff;
                                blue += px & 0xff;
                                alpha += (px >> 24) & 0xff;

                            }
                        }
                    }
                    
                    //Seting the rgbs
                    int r = (int) (red / blocks);
                    int g = (int) (green / blocks);
                    int b = (int) (blue / blocks);
                    int a = (int) (alpha / blocks);              
                    int rgb = (a << 24) | (r << 16) | (g << 8) | b;
                    pixels[yIndex][xIndex] = rgb;
                    xIndex++;

                }
                yIndex++;
            }

            return pixels;
        }

        }

    }

}