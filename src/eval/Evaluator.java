package eval;

import feature.ColorHistogram;
import search.ScoreItem;

import java.io.File;
import java.util.List;

/**
 * Created by Phil on 08.07.2017.
 */
public class Evaluator {
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
        File relev_folder =  in.getFile().getParentFile();
        double relev_all = relev_folder.listFiles().length; //double to avoid later rounding problems...
        double relev_count = 0;
        pr = new double[score.size()][2];
        f = new double[score.size()];
        int count = 0;
        double num;
        double denom;
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
            f[count-1] = 2*(pr[count-1][0]* pr[count-1][1])/(pr[count-1][0] + pr[count-1][1]);
        }
    }
    public double[][] getPR(){
        return pr;
    }
    public double[] getFMeasure(){
        return f;
    }
}
