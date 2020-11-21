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

        
        public MeanFilter(int xLow,int yLow,int xHigh,int yHigh,int filter,int[][] rgbArray,boolean splitLength) {
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