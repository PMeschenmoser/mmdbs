import java.io.File;

/**
 * Created by HP on 01.07.2017.
 */
public class ScoreItem {
    private File f;
    private Double value;
    public ScoreItem(File f, Double value){
        this.f = f;
        this.value = value;
    }

    public Double getScore(){
        return value;
    }

    public File getFile(){
        return f;
    }
}
