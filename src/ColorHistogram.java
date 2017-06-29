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
    private int binwidth;
    private int[][] allhistograms;


    public ColorHistogram(File img,  int cellcount, int binwidth) {
        this.cellcount = cellcount;
        this.binwidth = binwidth;
        try {
            source = ImageIO.read(img);
            split(); //update cell array
            calculate(cellcount, binwidth);
        }  catch (IOException e) {

        }
    }


    public int[][] calculate(int cellcount, int binwidth){
        this.binwidth = binwidth;
        this.cellcount = cellcount;

        allhistograms =  new int[cellcount*cellcount][(256*256*256)/binwidth+1 ];
            int rgb;
            int bin;
            int c = 0;
            for (BufferedImage cell: cells) {
                int[] result = new int[(256 * 256 * 256) / binwidth + 1];
                for (int i = 0; i < result.length; i++) result[i] = 0;
                for (int x = 0; x < cell.getWidth(); x++) {
                    for (int y = 0; y < cell.getHeight(); y++) {
                        rgb = cell.getRGB(x, y);
                        bin = 65536 * ((rgb & 0x0000ff00) >> 8) + 256 * ((rgb & 0x0000ff00) >> 8) + (rgb & 0x000000ff);
                        result[bin / binwidth]++;
                    }
                }
                allhistograms[c] = result;
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

    public int[][] getResults(){
        return allhistograms;
    }




}
