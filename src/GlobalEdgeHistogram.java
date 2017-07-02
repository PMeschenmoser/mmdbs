import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

public class GlobalEdgeHistogram {

    private BufferedImage source;
    private BufferedImage cells[];
    private int[] histogram;


    public GlobalEdgeHistogram(File img) {
        try {
            source = ImageIO.read(img);
            calculate();
        }  catch (IOException e) { }
    }

    public int[] calculate(){

        // gradient calculation
        float xGradientArray[] = {1,0,-1,2,0,-2,1,0,-1};
        float yGradientArray[] = {1,2,1,0,0,0,-1,-2,-1};

        ConvolveOp xOp = new ConvolveOp(new Kernel(3,3, xGradientArray), ConvolveOp.EDGE_ZERO_FILL,null);
        ConvolveOp yOp = new ConvolveOp(new Kernel(3,3, yGradientArray), ConvolveOp.EDGE_ZERO_FILL,null);

        BufferedImage xGradient = xOp.filter(source, null);
        BufferedImage yGradient = yOp.filter(source, null);

        // histogram generation
        histogram = new int[4];   // edge orientation: none, horizontal, diagonal\, vertical

// inspired by https://github.com/kevinduraj/Edge-Detection/blob/master/Sobel/src/sobel/Sobel.java
        int Gx = 0;
        int Gy = 0;

        for (int i = 0; i < xGradient.getWidth(); i++) {
            for (int j = 0; j < xGradient.getHeight(); j++) {

                // use only one (green) dimension
                Gx = xGradient.getRGB(i, j) >> 8 & 0xFF;
                Gy = yGradient.getRGB(i, j) >> 8 & 0xFF;

                if(Gx < 0 || Gy < 0) System.out.println("Gx="+Gx+" Gy="+Gy);
                // caculate gradient magnitude and direction
                double magnitude = Math.sqrt(Gx * Gx + Gy * Gy);
                double direction = Math.atan2(Gy, Gx);

                // increase histogram bin: 0 for no magnitude, 1 for horizontal, 2 for diagonal, 3 for vertical
                if(magnitude < 10) histogram[0]++;
                else if(direction < 3.145/8) histogram[1]++;
                else if(direction < 3*(3.145/8)) histogram[2]++;
                else histogram[3]++;
            }
        }
        return histogram;
    }

    public int[] getResults(){
        return histogram;
    }
}
