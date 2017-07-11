package feature;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import misc.Util;
import org.apache.commons.math3.geometry.spherical.twod.Edge;

import java.awt.Graphics;

public class TamuraLikeDescriptor implements Serializable, FeatureHistogram {
    private File input;
    private int cellcount;
    private int bincount = 4;
    private double[][][] allhistograms; //double instead of int due to Commons Math RealMatrices


    public TamuraLikeDescriptor(File img, int cellcount) {
        input = img;
        this.cellcount = cellcount;
        try {
            BufferedImage source = ImageIO.read(input);
            BufferedImage[] splits = split(source); //update cell array
            // calculate granularity & contrast
            calculate(splits, cellcount);
            // add direction histogram
//            EdgeHistogram edgy = new EdgeHistogram(img, cellcount);
//            for(int i = 0;  i<cellcount*cellcount; i++){
//                allhistograms[i][2] = edgy.getResults()[i][0];
//            }
        }  catch (IOException e) {

        }
    }

    public double[][][] calculate(BufferedImage[] cells, int cellcount){

        allhistograms =  new double[cellcount*cellcount][2][bincount];

        // for each subimage, do
        int c = 0;
        for (BufferedImage cell: cells) {

            // convert to grayscale
            BufferedImage grayCell = new BufferedImage(cell.getWidth(), cell.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = grayCell.getGraphics();
            g.drawImage(cell, 0, 0, null);
            g.dispose();
            Raster grayRaster = grayCell.getData();

            // granularity normalized on max window size mapped to 0-100
            double granularity = calcGranularity(grayRaster) / Math.min(cell.getHeight(), cell.getWidth()) * 100;
            double[] temp = new double[bincount];
            Arrays.fill(temp, granularity); //to give more weight to the single value compared to direction histogram with 4 bins
            allhistograms[c][0] = temp;

            // contrast values are in range 0-100 (?)
            double contrast = calcContrast(grayRaster);
            Arrays.fill(temp, contrast); //to give more weight to the single value compared to direction histogram with 4 bins
            allhistograms[c][1] = temp;
            c++;
        }

        return allhistograms;
    }

    public double calcContrast(Raster grayRaster){
        int[] grayHistogram = new int[256];
        for (int x = 0; x < grayRaster.getWidth(); x++) {
            for (int y = 0; y < grayRaster.getHeight(); y++) {
                grayHistogram[grayRaster.getSample(x, y,0)]++;
            }
        }
        double n = grayRaster.getHeight()*grayRaster.getWidth(),
                mean = 0,
                m2 = 0, //variance = 2nd moment
                m4 = 0,
                kurtosis,
                contrast;

        for(int i = 0; i < grayHistogram.length; i++){
            mean += (i+1)*grayHistogram[i]; // 256 values starting from 1
        }
        mean = mean / n;

        for(int i = 0; i < grayHistogram.length; i++){
            m2 += Math.pow(((i+1)-mean), 2) * grayHistogram[i];
            m4 += Math.pow(((i+1)-mean), 4) * grayHistogram[i];
        }
        m2 = m2/n; m4 = m4/n;

        kurtosis = m4/Math.pow(m2, 2);

        contrast = Math.sqrt(m2)/Math.pow(kurtosis, (1.0/4.0));

        return contrast;
    }

    public double calcGranularity(Raster grayRaster){
        int maxSize = Math.min(grayRaster.getWidth(), grayRaster.getHeight())/2;
        // slide with windows of different size through the raster and calculate difference of mean gray values
        int[] patchSizes = {1, 3, 5, 9, 15, 21, 27, 35, 47, 61, 79, 101, 131, 171, 221}; //approx +30% each later step

        int[][] onLevelMax = new int[grayRaster.getWidth()][grayRaster.getHeight()];
        double[][] maxDifference = new double[grayRaster.getWidth()][grayRaster.getHeight()];

        int i = 0;
        while (maxSize > patchSizes[i]) {
            for (int x = 0; x < grayRaster.getWidth(); x++) {
                for (int y = 0; y < grayRaster.getHeight(); y++) {
                    patchDifference(grayRaster, x, y, patchSizes[i], onLevelMax, maxDifference);
                }
            }
            i++;
        }

        double mean = Util.mean(onLevelMax);
        return mean;
    }

    public void patchDifference(Raster grayRaster, int posX, int posY, int patchSize, int[][] onLevelMax, double[][] maxDifference){
        if((posX+2*patchSize) > grayRaster.getWidth() && (posY+2*patchSize) > grayRaster.getHeight()) {}
        else if ((posX+2*patchSize) <= grayRaster.getWidth() && (posY+patchSize) <= grayRaster.getHeight() && (posY+2*patchSize) > grayRaster.getHeight()){
            // calculate only horizontal difference
            double meanGrayLeft, meanGrayRight, differenceHori;
            int graySum = 0;
            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    graySum += grayRaster.getSample(posX+x, posY+y,0);
                }
            }
            meanGrayLeft = graySum / (patchSize*patchSize);
            graySum = 0;
            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    graySum += grayRaster.getSample(posX+patchSize+x, posY+y,0);
                }
            }
            meanGrayRight = graySum / (patchSize*patchSize);

            differenceHori = Math.abs(meanGrayLeft - meanGrayRight);

            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    if(maxDifference[posX+x][posY+y] < differenceHori){
                        maxDifference[posX+x][posY+y] = differenceHori;
                        onLevelMax[posX+x][posY+y] = patchSize;
                    }
                }
            }

            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    if(maxDifference[posX+patchSize+x][posY+y] < differenceHori){
                        maxDifference[posX+patchSize+x][posY+y] = differenceHori;
                        onLevelMax[posX+patchSize+x][posY+y] = patchSize;
                    }
                }
            }
        }
        else if ((posX+patchSize) <= grayRaster.getWidth() && (posX+2*patchSize) > grayRaster.getWidth() && (posY+2*patchSize) <= grayRaster.getHeight()){
            // calculate only vertical difference
            double meanGrayLeft, meanGrayBottom, differenceVert;
            int graySum = 0;
            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    graySum += grayRaster.getSample(posX+x, posY+y,0);
                }
            }
            meanGrayLeft = graySum / (patchSize*patchSize);
            graySum = 0;
            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    graySum += grayRaster.getSample(posX+x, posY+patchSize+y,0);
                }
            }
            meanGrayBottom= graySum / (patchSize*patchSize);

            differenceVert = Math.abs(meanGrayLeft - meanGrayBottom);

            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    if(maxDifference[posX+x][posY+y] < differenceVert){
                        maxDifference[posX+x][posY+y] = differenceVert;
                        onLevelMax[posX+x][posY+y] = patchSize;
                    }
                }
            }

            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    if(maxDifference[posX+x][posY+patchSize+y] < differenceVert){
                        maxDifference[posX+x][posY+patchSize+y] = differenceVert;
                        onLevelMax[posX+x][posY+patchSize+y] = patchSize;
                    }
                }
            }
        }
        else if((posX+2*patchSize) < grayRaster.getWidth() && (posY+2*patchSize) < grayRaster.getHeight()){
            double meanGrayLeft, meanGrayRight, meanGrayBottom, differenceHori, differenceVert;
            int graySum = 0;
            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    graySum += grayRaster.getSample(posX+x, posY+y,0);
                }
            }
            meanGrayLeft = graySum / (patchSize*patchSize);
            graySum = 0;
            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    graySum += grayRaster.getSample(posX+patchSize+x, posY+y,0);
                }
            }
            meanGrayRight = graySum / (patchSize*patchSize);
            graySum = 0;
            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    graySum += grayRaster.getSample(posX+x, posY+patchSize+y,0);
                }
            }
            meanGrayBottom= graySum / (patchSize*patchSize);

            differenceHori = Math.abs(meanGrayLeft - meanGrayRight);
            differenceVert = Math.abs(meanGrayLeft - meanGrayBottom);

            double biggerDiff = Math.max(differenceHori, differenceVert);

            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    if(maxDifference[posX+x][posY+y] < biggerDiff){
                        maxDifference[posX+x][posY+y] = biggerDiff;
                        onLevelMax[posX+x][posY+y] = patchSize;
                    }
                }
            }

            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    if(maxDifference[posX+patchSize+x][posY+y] < differenceHori){
                        maxDifference[posX+patchSize+x][posY+y] = differenceHori;
                        onLevelMax[posX+patchSize+x][posY+y] = patchSize;
                    }
                }
            }

            for (int x = 0; x < patchSize; x++) {
                for (int y = 0; y < patchSize; y++) {
                    if(maxDifference[posX+x][posY+patchSize+y] < differenceVert){
                        maxDifference[posX+x][posY+patchSize+y] = differenceVert;
                        onLevelMax[posX+x][posY+patchSize+y] = patchSize;
                    }
                }
            }
        }


    }

//    public double[][] calcRGBhistogram(BufferedImage cell, int bincount){
//        this.bincount = bincount;
//        double[] empty = new double[bincount];
//        rgbHistogramArray =  new double[3][bincount];
//        int rgb;
//        double[] r = empty.clone();
//        double[] g = empty.clone();
//        double[] b = empty.clone();
//
//        for (int x = 0; x < cell.getWidth(); x++) {
//            for (int y = 0; y < cell.getHeight(); y++) {
//                rgb = cell.getRGB(x, y);
//                int binwidth = (int) Math.floor(256 /bincount);
//                r[Math.min(((rgb & 0x00ff0000) >> 16)/binwidth,bincount-1)]++;
//                g[Math.min(((rgb & 0x0000ff00) >> 8)/binwidth, bincount-1)]++;
//                b[Math.min((rgb & 0x000000ff)/binwidth, bincount-1)]++;
//                rgbHistogramArray[0] = r;
//                rgbHistogramArray[1] = g;
//                rgbHistogramArray[2] = b;
//            }
//        }
//
//        return rgbHistogramArray;
//    }

    private BufferedImage[] split(BufferedImage source){
        BufferedImage[] cells = new BufferedImage[cellcount*cellcount];
        int cellwidth = source.getWidth()/cellcount;
        int cellheight = source.getHeight()/cellcount;
        for (int x = 0; x<cellcount; x++ ){
            for (int y= 0; y<cellcount; y++){
                cells[x*cellcount+y] = source.getSubimage(x*cellwidth, y*cellheight, cellwidth, cellheight);
            }
        }
        return cells;
    }

    public double[][][] getResults(){
        return allhistograms;
    }
    public File getFile(){
        return input;
    }
    public int getBinCount(){
        return bincount;
    }
    public int getCellCount(){
        return cellcount;
    }
    public int getType() {return 2; }// 0 for ColorHistogram, 1 for EdgeHistogram, 2 for TamuraLikeDescriptor}
}
