import java.io.File;

/**
 * Created by user on 28.06.2017.
 */
public class Initiator {


    public static void main(String[] args){
        ColorHistogram c = new ColorHistogram(new int[]{256,256,256}, 1);
        File dir = new File("data/hedgehog");
        for (File img: dir.listFiles()){
           c.setImage(img);
        }
    }
}
