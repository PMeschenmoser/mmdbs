import java.io.File;

/**
 * Created by user on 28.06.2017.
 */
public class Initiator {


    public static void main(String[] args){
        ColorHistogram c = new ColorHistogram( 2);
        /*
                {256,256,256} -> bins per color channel
                cellcount per dimension: i.e. 4 counts in total
         */
        File dir = new File("data/hedgehog");
        for (File img: dir.listFiles()){
           c.setImage(img,100);
        }
    }
}
