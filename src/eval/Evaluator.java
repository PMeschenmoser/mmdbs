package eval;

import feature.ColorHistogram;
import search.ScoreItem;

import java.io.File;
import java.util.List;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class Evaluator {
    /*
        This class computes Precision/Recall Values and the F-Score for a given Score.

     */
    private ColorHistogram in;
    private List<ScoreItem> score;
    private double[][] pr;
    private double[] f;

    public Evaluator(ColorHistogram in, List<ScoreItem> score){
        this.in = in;
        this.score = score;
        run();
    }
    private void run(){
        //FULLY AUTOMATED, folder-based evaluation system
        File relev_folder =  in.getFile().getParentFile();
        double relev_all = relev_folder.listFiles().length; //double to avoid later rounding problems...
        double relev_count = 0;
        pr = new double[score.size()][2];
        f = new double[score.size()];
        int count = 0;
        for (ScoreItem item : score){
            if (item.getFile().getParentFile().equals(relev_folder)){
                /*
                    A score item is relevant, iff its file's directory equals to the
                    search file's directory:
                 */
                relev_count++;
            }
            count++;
            pr[count-1][0] = relev_count/count;  //precision: (relevant + found) vs found.
            pr[count-1][1] = relev_count/relev_all;  //recall: how many of all relevants were found so far?
            f[count-1] = 2*(pr[count-1][0]* pr[count-1][1])/(pr[count-1][0] + pr[count-1][1]); //fscore
        }
    }
    public double[][] getPR(){
        return pr;
    }
    public double[] getFMeasure(){
        return f;
    }
}
