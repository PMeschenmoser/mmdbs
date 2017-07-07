package search;

import feature.ColorHistogram;

import java.io.File;

/**
 * Created by HP on 01.07.2017.
 */
public class ScoreItem {
    private File f;
    private Double value;
    private ColorHistogram histogram;

    public ScoreItem(ColorHistogram histogram, Double value){

        this.value = value;
        this.histogram = histogram;
    }

    public Double getScore(){
        return value;
    }

    public File getFile(){
        return histogram.getFile();
    }

    public ColorHistogram getHistogram(){
        return histogram;
    }
}
