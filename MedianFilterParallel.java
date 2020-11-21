/* Tshilidzi Mphelo
 * 21/11/2020
 * Image Filtering using parallelism on a median filter
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class MedianFilterParallel {
   static class MedianFilter extends RecursiveTask<Integer[][]> {         
      
      private int xLow, xHigh, yLow, yHigh, filter;
      private int[][] rgbArray;
      private boolean splitLength;  
          
      private int THRESHOLD = 10000;   
              
      //Constructor
      public MedianFilter(int xLow,int yLow,int xHigh,int yHigh,int filter,int[][] rgbArray,boolean splitLength) {
          this.xLow = xLow;
          this.yLow = yLow;
          this.xHigh = xHigh;
          this.yHigh = yHigh;
          this.filter = filter;
          this.rgbArray = rgbArray;
          this.splitLength = splitLength;
      }
      
      //Method for processing the image
      private Integer[][] medianFilter() {
         int w = xHigh - xLow;
         int h = yHigh - yLow;
         
         //Getting the sizes of the picture
         int width = rgbArray[0].length;
         int height = rgbArray.length;
         
         int radius = (filter - 1) / 2;

         Integer[][] pixels = new Integer[h][w];

         int yIndex = 0;

         //iterating through each pixel of the image
         for (int y = yLow; y < yHigh; y++) {
             int xIndex = 0;
             for (int x = xLow; x < xHigh; x++) {

                 int startX = x - radius;
                 int startY = y - radius;

                 //arraylist for listing each of the rgb colours of the current pixel and that of its surrounding pixels
                 ArrayList<Integer> red = new ArrayList<Integer>();
                 ArrayList<Integer> green = new ArrayList<Integer>();
                 ArrayList<Integer> blue = new ArrayList<Integer>();
                 ArrayList<Integer> alpha = new ArrayList<Integer>();

                 //iterating through the surrounding pixels
                 for (int i = startX; i < startX + filter; i++) {
                     for (int j = startY; j < startY + filter; j++) {

                         if (!(i < 0 || i >= width || j < 0 || j >= height)) {

                             int px = rgbArray[j][i];
                                                               //adding the colours of each pixel into the arraylists
                             red.add((px >> 16) & 0xff); 
                             green.add((px >> 8) & 0xff);
                             blue.add(px & 0xff);
                             alpha.add((px >> 24) & 0xff);

                         }

                         else {
                             red.add(0);
                             green.add(0);
                             blue.add(0);
                             alpha.add(0);
                         }
                     }
                 }

                 //Sot the arraylists
                 Collections.sort(red);
                 Collections.sort(green);
                 Collections.sort(blue);
                 Collections.sort(alpha);

                 //Find the midpoints
                 int r = red.get(red.size() / 2);
                 int g = green.get(green.size() / 2);
                 int b = blue.get(blue.size() / 2);
                 int a = alpha.get(alpha.size() / 2);

                 //Setting the the rgb colours to the medians
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
             return medianFilter();
         } else {

             if (splitLength) {

                 int mid = xLow + (xHigh - xLow) / 2; //Split the image from the midpoint
                 MedianFilter left = new MedianFilter(xLow, yLow, mid, yHigh, filter, rgbArray, !splitLength); //The left part of the image
                 MedianFilter right = new MedianFilter(mid, yLow, xHigh, yHigh, filter, rgbArray, !splitLength); //The right part of the image
                 left.fork(); //Running the left thread
                 Integer[][] rightSection = right.compute();//Running the right thread on the main thread
                 Integer[][] leftSection = left.join(); //Main thread should wait for the left thread to complete

                 return merge(leftSection, rightSection, splitLength); //Meging the left and right image into one

             } else {
                 //Diving the image by its height
                 int mid = yLow + (yHigh - yLow) / 2;
                 MedianFilter top = new MedianFilter(xLow, yLow, xHigh, mid, filter, rgbArray, !splitLength);
                 MedianFilter bottom = new MedianFilter(xLow, mid, xHigh, yHigh, filter, rgbArray, !splitLength);
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

            Integer[][] res = new Integer[sectionA.length][sectionA[0].length + sectionB[0].length];  //the combined array with its size made by adding up those of the 
                                                                                                      //arrays they're made off
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
   
   public static void main(String args[]) throws IOException {

        //Handling terminal input
        String path = args[0];
        String out = args[1];
        int filter = Integer.parseInt(args[2]);
        
        //Reading in the image and getting its sizes
        BufferedImage img = ImageIO.read(new File(path));
        int xLength = img.getWidth();
        int yLength = img.getHeight();
        int[][] rgbArray = new int[yLength][xLength];

        //Setting the RGBs from the image onto a matrix
        for (int y = 0; y < yLength; y++) {
            for (int x = 0; x < xLength; x++) {
                rgbArray[y][x] = img.getRGB(x, y);
            }
        }

        //Creating a thread object
        MedianFilter medianFilter = new MedianFilter(0, 0, xLength, yLength, Integer.parseInt(args[2]), rgbArray, true);

        // Create the Thread Pool
        int noOfThreads = Runtime.getRuntime().availableProcessors();
        ForkJoinPool forkJoinPool = new ForkJoinPool(noOfThreads);

        System.out.println("Performing the mean filter ...");
        
        long t1 = System.currentTimeMillis();
        Integer[][] pixels = forkJoinPool.invoke(meanFilter); //invoking the thread pool
        long lapsed = System.currentTimeMillis() - t1; //Calculating the processing time

        //Writing the filtered image
        File output = new File(out);
        BufferedImage outImage = new BufferedImage(xLength, yLength, BufferedImage.TYPE_INT_RGB);

        // Setting the new filtered pixels matrix to the output image
        for (int x = 0; x < xLength; x++) {
            for (int y = 0; y < yLength; y++) {
                int rgb = pixels[y][x];
                outImage.setRGB(x, y, rgb);
            }
        }

        ImageIO.write(outImage, "jpg", output);
        System.out.println("The mean filter took " + lapsed + " milliseconds to complete.");

    }

 

}
