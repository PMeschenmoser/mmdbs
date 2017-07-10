package search;

import feature.ColorHistogram;

import java.io.File;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class ScoreItem {
    /*
        This class is a wrapper fro ColorHistograms and their score value.
        E.g. it simplifies sorting file names according to  their score value.
     */

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
