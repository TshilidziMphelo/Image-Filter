/* Tshilidzi Mphelo
 * 21/11/2020
 * Image Filtering
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

   }
}
