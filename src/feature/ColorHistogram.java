package feature;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class ColorHistogram implements Serializable{
    private File input;
    private int cellcount; //per axis
    private int bincount; //per channel
    private double[][][] allhistograms; //double instead of int due to Commons Math RealMatrices


    public ColorHistogram(File img,  int cellcount, int bincount) {
        input = img;
        this.cellcount = cellcount;
        this.bincount = bincount;
        try {
            BufferedImage source = ImageIO.read(input);
            BufferedImage[] splits = split(source); //forward splitted buffered images to histogram calculation
            calculate(splits, cellcount, bincount);
        }  catch (IOException e) {

        }
    }

    public double[][][] calculate(BufferedImage[] cells, int cellcount, int bincount){
        this.bincount = bincount;
        this.cellcount = cellcount;
        double[] empty = new double[bincount];
        Arrays.fill(empty, 0); // empty has #bins zeros now
        allhistograms =  new double[cellcount*cellcount][3][bincount];
            int rgb;
            int c = 0;
            for (BufferedImage cell: cells) {
                double[] r = empty.clone(); //assign empty histogram array
                double[] g = empty.clone();
                double[] b = empty.clone();

                //one-pass binning:
                for (int x = 0; x < cell.getWidth(); x++) {
                    for (int y = 0; y < cell.getHeight(); y++) {
                        rgb = cell.getRGB(x, y);
                        int binwidth = (int) Math.floor(256 /bincount);
                        //get r, g and b values, get their bins and increase:
                        r[Math.min(((rgb & 0x00ff0000) >> 16)/binwidth,bincount-1)]++;
                        g[Math.min(((rgb & 0x0000ff00) >> 8)/binwidth, bincount-1)]++;
                        b[Math.min((rgb & 0x000000ff)/binwidth, bincount-1)]++;
                        allhistograms[c][0] = r;
                        allhistograms[c][1] = g;
                        allhistograms[c][2] = b;
                    }
                }
                c++;
            }
            return allhistograms;
    }

    private BufferedImage[] split(BufferedImage source){
        //returns an array of subimages (BufferedImage[])
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

    public double[][] getMergedChannelHistograms(){
        //this method transforms the 3D array allhistograms into a 2D array
        // merged with double[3][#bins]. Thus, we need this to fill the three rgb plots with data.
        if (allhistograms.length == 1) return allhistograms[0]; //only one cell
        double[][] merged = new double[3][bincount];
        //for each channel, assign the "root number" in the histogram, using the first cell.
        for (int channel = 0; channel<allhistograms[0].length; channel++){
            merged[channel] = allhistograms[0][channel];
        }
        for (int c = 1; c<allhistograms.length; c++){ //for every further cell
            for (int channel = 0; channel<allhistograms[c].length; channel++){ //add values
                for (int bin = 0; bin <allhistograms[c][channel].length; bin++ )// for each channel and bin
                merged[channel][bin] += allhistograms[c][channel][bin];
            }
        }
        return merged;
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

}
