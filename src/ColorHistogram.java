import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by user on 28.06.2017.
 */
public class ColorHistogram {
    private BufferedImage source;
    private BufferedImage cells[];
    private int cellcount; //count per x resp. y dimension
    private int[][][] emptylookup;


    public ColorHistogram( int cellcount) {
        this.cellcount = cellcount;
        emptylookup = getEmptyLookup();
    }

    public int[][][] getEmptyLookup(){
        int[][][] tmp = new int[256][256][256];
        for (int r= 0; r<tmp.length; r++){
            for (int g = 0; g<tmp[r].length; g++){
                for (int b = 0; b<tmp[r][g].length; b++){
                    tmp[r][g][b] = 0;
                }
            }
        }
        return tmp;
    }

    public ColorHistogram( int cellcount, File img) {
        this(cellcount);
        setImage(img,1);
    }

    public boolean setImageOld(File img, int binwidth){
        try {
            source = ImageIO.read(img);
            int[][] allresults = new int[cellcount*cellcount][(256*256*256)/binwidth];
            split(); //update cell array
            int rgb;
            int c = 0;
            for (BufferedImage cell: cells){
                int[][][] currlookup = emptylookup;
                for (int x = 0; x<cell.getWidth(); x++){
                    for (int y= 0; y<cell.getHeight(); y++){
                        rgb = cell.getRGB(x,y);
                        currlookup[(rgb & 0x00ff0000) >> 16][(rgb & 0x0000ff00) >> 8][rgb & 0x000000ff]+=1;
                    }
                }
                int count = 0;
                int sum = 0;
                int[] result = new int[(256*256*256)/binwidth];
                for (int r=0; r<currlookup.length;r++){
                    for (int g=0;g<currlookup[r].length;g++){
                        for (int b=0; b<currlookup[r][g].length;b++){
                            sum += currlookup[r][g][b];
                            count++;
                            if (count == binwidth){
                                result[count/binwidth] = sum;
                                count = 0;
                            }
                        }
                    }
                }
                allresults[c] = result;
                c++;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public boolean setImage(File img, int binwidth){
        try {
            source = ImageIO.read(img);
            int[][] allresults = new int[cellcount*cellcount][(256*256*256)/binwidth+1];
            split(); //update cell array
            int rgb;
            int bin;
            int c = 0;
            for (BufferedImage cell: cells){
                int[] result = new int[(256*256*256)/binwidth+1];
                for (int i= 0; i<result.length; i++) result[i]=0;
                for (int x = 0; x<cell.getWidth(); x++){
                    for (int y= 0; y<cell.getHeight(); y++){
                        rgb = cell.getRGB(x,y);
                        bin = 65536 * ((rgb & 0x0000ff00) >> 8) + 256 * ((rgb & 0x0000ff00) >> 8) + (rgb & 0x000000ff);
                        result[ bin/binwidth]++;
                    }
                }
                allresults[c]= result;
                c++;
            }
        } catch (IOException e) {
            return false;
        }
        return true;
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




}
