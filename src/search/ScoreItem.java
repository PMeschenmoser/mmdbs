package search;

import feature.*;

import java.io.File;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class ScoreItem {
    /*
        This class is a wrapper fro FeatureHistogram and their score value.
        E.g. it simplifies sorting file names according to  their score value.
     */

    private File f;
    private Double value;
    private FeatureHistogram histogram;

    public ScoreItem(FeatureHistogram histogram, Double value){
        this.value = value;
        this.histogram = histogram;
    }

    public Double getScore(){
        return value;
    }

    public File getFile(){
        return histogram.getFile();
    }

    public FeatureHistogram getHistogram(){
        return histogram;
    }
}
