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

   }
}
