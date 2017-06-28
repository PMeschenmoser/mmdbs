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
    private int[] bincounts; // bincounts [#r-bins,#g-bins, #b-bins]
    private int cellcount; //count per x resp. y dimension
    private int[][][] lookup;

    public ColorHistogram(int[] bincounts, int cellcount) {
        this.bincounts = bincounts;
        this.cellcount = cellcount;
        lookup = new int[bincounts[0]][bincounts[1]][bincounts[2]];
        empty();
    }

    public void empty(){
        /*
            Reset all color counts to 0.
         */
        for (int r= 0; r<lookup.length; r++){
            for (int g = 0; g<lookup[r].length; g++){
                for (int b = 0; b<lookup[r][g][b]; b++){
                    lookup[r][g][b] = 0;
                }
            }
        }
    }

    public ColorHistogram(int[] bincounts, int cellcount, File img) {
        this(bincounts,cellcount);
        setImage(img);
    }

    public boolean setImage(File img){
        empty();
        try {
            source = ImageIO.read(img);
            System.out.println("Compute Histogram for " +  img.getAbsolutePath());
            split(); //update cell array
            int rgb;
            int c = 1;
            for (BufferedImage cell: cells){
                System.out.println("Cell: " + c);
                for (int x = 0; x<cell.getWidth(); x++){
                    for (int y= 0; y<cell.getHeight(); y++){
                        rgb = cell.getRGB(x,y);
                        lookup[(rgb & 0x00ff0000) >> 16][(rgb & 0x0000ff00) >> 8][rgb & 0x000000ff]+=1;
                    }
                }
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
