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
        
        @Override
        protected Integer[][] compute() { //Overriding compute

            //Recursively dividing the image
            if ((xHigh - xLow) * (yHigh - yLow) <= THRESHOLD) {
                return meanFilter();
            } else {

                if (splitLength) {

                    int mid = xLow + (xHigh - xLow) / 2; //Split the image from the midpoint
                    MeanFilter left = new MeanFilter(xLow, yLow, mid, yHigh, filter, rgbArray, !splitLength); //The left part of the image
                    MeanFilter right = new MeanFilter(mid, yLow, xHigh, yHigh, filter, rgbArray, !splitLength); //The right part of the image
                    left.fork(); //Running the left thread
                    Integer[][] rightSection = right.compute();//Running the right thread on the main thread
                    Integer[][] leftSection = left.join(); //Main thread should wait for the left thread to complete

                    return merge(leftSection, rightSection, splitLength); //Meging the left and right image into one

                } else {
                    //Diving the image by its height
                    int mid = yLow + (yHigh - yLow) / 2;
                    MeanFilter top = new MeanFilter(xLow, yLow, xHigh, mid, filter, rgbArray, !splitLength);
                    MeanFilter bottom = new MeanFilter(xLow, mid, xHigh, yHigh, filter, rgbArray, !splitLength);
                    top.fork();
                    Integer[][] rightSection = bottom.compute();
                    Integer[][] leftSection = top.join();

                    return merge(leftSection, rightSection, splitLength);
                }
            }

        }
    }

    //Combines two matrices into one
    private static Integer[][] merge(Integer[][] sectionA, Integer[][] sectionB, boolean splitLength) {

        if (splitLength) { //Finding out whether its horizontal or vertical

            Integer[][] res = new Integer[sectionA.length][sectionA[0].length + sectionB[0].length];

            for (int i = 0; i < res.length; i++) {
                int index = 0;

                for (int a : sectionA[i]) {
                    res[i][index] = a;
                    index++;
                }

                for (int b : sectionB[i]) {
                    res[i][index] = b;
                    index++;
                }
            }

            return res;
        } else {
            int h = sectionA.length + sectionB.length;
            Integer[][] res = new Integer[h][sectionA.length];

            int index = 0;

            for (Integer[] a : sectionA) {
                res[index] = a;
                index++;
            }

            for (Integer[] b : sectionB) {
                res[index] = b;
                index++;
            }

            return res;

        }

   }

}

