import java.io.File;

/**
 * Created by user on 28.06.2017.
 */
public class Initiator {


    public static void main(String[] args){

        /*
                {256,256,256} -> bins per color channel
                cellcount per dimension: i.e. 4 counts in total
         */
        File dir = new File("data/hedgehog");
        for (File img: dir.listFiles()){
            ColorHistogram c = new ColorHistogram(img, 2,2);
           //c.getResults() returns a 2D dim. first dim -> different cells, 2nd dim -> color bins
        }
    }
}
