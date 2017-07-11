package feature;

import misc.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class EdgeHistogram implements Serializable, FeatureHistogram {

    private File input;
    private int cellcount;
    private int bincount = 4;
    private double[][][] allhistograms; //double instead of int due to Commons Math RealMatrices

    public EdgeHistogram(File img, int cellcount) {
        input = img;
        this.cellcount = cellcount;
        try {
            BufferedImage source = ImageIO.read(input);

// EVEN IF CELL-WISE HISTOGRAMS ARE REQUIRED, FIRST WE FILTER THE WHOLE IMAGE TO MINIMIZE BORDER ARTEFACTS
            // gradient calculation
            float xGradientArray[] = {1,0,-1,2,0,-2,1,0,-1};
            float yGradientArray[] = {1,2,1,0,0,0,-1,-2,-1};

            ConvolveOp xOp = new ConvolveOp(new Kernel(3,3, xGradientArray), ConvolveOp.EDGE_ZERO_FILL,null);
            ConvolveOp yOp = new ConvolveOp(new Kernel(3,3, yGradientArray), ConvolveOp.EDGE_ZERO_FILL,null);

            // use grayscale image
            BufferedImage grayImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = grayImage.getGraphics();
            g.drawImage(source, 0, 0, null);
            g.dispose();

            // convert to RGB
//            BufferedImage convertedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
//            convertedImg.getGraphics().drawImage(image, 0, 0, null);

            BufferedImage xGradient = xOp.filter(grayImage, null); //changed from grayImage
            BufferedImage yGradient = yOp.filter(grayImage, null);

            Raster xGradientRaster = xGradient.getData();
            Raster yGradientRaster = yGradient.getData();

//            try {
//                File outputfile = new File("saved2.jpg");
//                ImageIO.write(xGradient, "jpg", outputfile);
//            } catch (IOException e) {
//            }

            // the subimages for the X-Sobel-Filtered-Image are in xSplits
//            BufferedImage[] xSplits = split(xGradient); //forward splitted buffered images to histogram calculation
//            BufferedImage[] ySplits = split(yGradient);

// AFTER CALCULATING THE GRADIENTS, THE HISTOGRAMS ARE CREATED FOR EACH CELL
            calculate(xGradientRaster, yGradientRaster, cellcount);

        }  catch (IOException e) {

        }
    }

    public double[][][] calculate(Raster xGradientRaster, Raster yGradientRaster, int cellcount) {

        double[] histogram = new double[4]; // todo: 9 bins: no gradient; xdir = 0°, ydir = 90°,... 8 bins
        allhistograms = new double[cellcount * cellcount][1][4];

        int cellwidth = xGradientRaster.getWidth()/cellcount;
        int cellheight = xGradientRaster.getHeight()/cellcount;
        for (int i = 0; i<cellcount; i++ ){
            for (int j= 0; j<cellcount; j++){

                for (int x = i*cellwidth; x < i*cellwidth+cellwidth; x++) {
                    for (int y = j*cellheight; y < j*cellheight+cellheight; y++) {

                        int Gx = xGradientRaster.getSample(x, y, 0);
                        int Gy = yGradientRaster.getSample(x, y, 0);

                        // caculate gradient magnitude and direction
                        double magnitude = Math.sqrt(Gx * Gx + Gy * Gy);
                        double direction = Math.atan2(Gy, Gx);

                        // increase histogram bins: clockwise from xdir = bin 1; ... ydir = bin 3; ...
                        if (magnitude < 100) histogram[0]++;
                        else if (Math.abs(direction) <= 3.145 / 8) histogram[1]++;
                        else if (direction > 3.145 / 8 && direction <= 3 * 3.145 / 8) histogram[2]++;
                        else if (direction > 3 * 3.145 / 8 && direction <= 5 * 3.145 / 8) histogram[3]++;
                        // since ConvolveOP does not work correctly (no negative result values) following bins missing:
//                    else if(direction > 5*3.145/8 && direction <= 7*3.145/8) histogram[4]++;
//                    else if(Math.abs(direction) > 7*3.145/8) histogram[5]++;
//                    else if(direction < -3.145/8 && direction >= -3*3.145/8) histogram[6]++;
//                    else if(direction < -3*3.145/8 && direction >= -5*3.145/8) histogram[7]++;
//                    else if(direction < -5*3.145/8 && direction >= -7*3.145/8) histogram[8]++;
                    }
                }

                //normieren
//                double max = 0;
//                for(int k = 0; k < histogram.length; k++) {if(max < histogram[k]) max = histogram[k];}
//                for(int k = 0; k < histogram.length; k++) {histogram[k] = histogram[k]/max*100;}
//                Util.printHistogram(histogram);
                allhistograms[i*cellcount+j][0] = histogram;
            }
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
    public File getFile(){
        return input;
    }
    public int getBinCount(){
        return bincount;
    }
    public int getCellCount(){
        return cellcount;
    }
    public int getType() {return 1; } // 0 for ColorHistogram, 1 for EdgeHistogram, 2 for TamuraLikeDescriptor
}
