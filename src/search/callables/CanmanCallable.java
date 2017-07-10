package search.callables;

import feature.ColorHistogram;
import search.Measures;
import search.ScoreItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class CanmanCallable implements Callable {

    private ColorHistogram query;
    private ColorHistogram[] candidates;
    private int start;
    private int end;
    private boolean canberra;

    public CanmanCallable(ColorHistogram query, ColorHistogram[] candidates, int start, int end, boolean canberra){
        //assign class variables, so that these values are accessible in call().
        this.query = query;
        this.candidates = candidates;
        this.start = start;
        this.end = end;
        this.canberra = canberra;
    }
    @Override
    public Object call() throws Exception {
        List<ScoreItem> localresults = new ArrayList<>();
        //performs search in one single window, using the canman metric.
        for (int i = start; i<Math.min(end, candidates.length); i++)
        {
            Double dist = Measures.canman(query,candidates[i],canberra);
            if (dist != null){
                localresults.add(new ScoreItem(candidates[i], dist));
            }
        }
        return localresults;
    }
}
