import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by user on 28.06.2017.
 */
public class ColorHistogram {
    private BufferedImage buffered;
    private int[] bins; // bins[0] = [#r-bins,#g-bins, #b-bins]
    private int cells;
    private int[][][] lookup;

    public ColorHistogram(int[] bins, int cells) {
        this.bins = bins;
        this.cells = cells;
        lookup = new int[bins[0]][bins[1]][bins[2]];
        for (int r= 0; r<lookup.length; r++){
            for (int g = 0; g<lookup[r].length; g++){
                for (int b = 0; b<lookup[r][g][b]; b++){
                    lookup[r][g][b] = 0;
                }
            }
        }
    }

    public ColorHistogram(int[] bins, int cells, File img) {
        this(bins,cells);
        setImage(img);
    }

    public boolean setImage(File img){
        try {
            buffered = ImageIO.read(img);
            int rgb;
            for (int x = 0; x<buffered.getWidth(); x++){
                for (int y= 0; y<buffered.getHeight(); y++){
                    rgb = buffered.getRGB(x,y);
                    lookup[(rgb & 0x00ff0000) >> 16][(rgb & 0x0000ff00) >> 8][rgb & 0x000000ff]+=1;
                    //System.out.println(lookup[(rgb & 0x00ff0000) >> 16][(rgb & 0x0000ff00) >> 8][rgb & 0x000000ff]);
                }
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
