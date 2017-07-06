import java.io.File;

/**
 * Created by HP on 01.07.2017.
 */
public class ScoreItem {
    private File f;
    private Double value;
    private ColorHistogram histogram;

    public ScoreItem(File f, Double value, ColorHistogram histogram){
        this.f = f;
        this.value = value;
        this.histogram = histogram;
    }

    public Double getScore(){
        return value;
    }

    public File getFile(){
        return f;
    }

    public ColorHistogram getHistogram(){
        return histogram;
    }
}
