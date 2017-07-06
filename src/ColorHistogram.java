import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by user on 28.06.2017.
 */
public class ColorHistogram {
    private BufferedImage source;
    private BufferedImage cells[];
    private int cellcount;
    private int bincount;
    private double[][][] allhistograms; //double instead of int due to Commons Math RealMatrices


    public ColorHistogram(File img,  int cellcount, int bincount) {
        this.cellcount = cellcount;
        this.bincount = bincount;
        try {
            source = ImageIO.read(img);
            split(); //update cell array
            calculate(cellcount, bincount);
        }  catch (IOException e) {

        }
    }


    public double[][][] calculate(int cellcount, int bincount){
        this.bincount = bincount;
        this.cellcount = cellcount;

        allhistograms =  new double[cellcount*cellcount][3][bincount];
            int rgb;
            int c = 0;
            for (BufferedImage cell: cells) {
                double[] r = new double[bincount];
                double[] g;
                double[] b;
                for (int i = 0; i < r.length; i++) r[i] = 0;
                g = r;
                b = r;
                for (int x = 0; x < cell.getWidth(); x++) {
                    for (int y = 0; y < cell.getHeight(); y++) {
                        rgb = cell.getRGB(x, y);
                        int binwidth = (int) Math.floor(256 /bincount);

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

    private void split(){
        cells = new BufferedImage[cellcount*cellcount];
        int cellwidth = source.getWidth()/cellcount;
        int cellheight = source.getHeight()/cellcount;
        for (int x = 0; x<cellcount; x++ ){
            for (int y= 0; y<cellcount; y++){
                cells[x*cellcount+y] = source.getSubimage(x*cellwidth, y*cellheight, cellwidth, cellheight);
            }
        }
    }

    public double[][][] getResults(){
        return allhistograms;
    }

    public double[][] getMergedChannelHistograms(){
        if (allhistograms.length == 1) return allhistograms[0];
        double[][] merged = new double[3][bincount];
        for (int channel = 0; channel<allhistograms[0].length; channel++){
            merged[channel] = allhistograms[0][channel];
        }
        for (int c = 1; c<allhistograms.length; c++){
            for (int channel = 0; channel<allhistograms[c].length; channel++){
                for (int bin = 0; bin <allhistograms[c][channel].length; bin++ )
                merged[channel][bin] += allhistograms[c][channel][bin];
            }
        }
        return merged;
    }

}
