package search.callables;

import feature.ColorHistogram;
import search.Measures;
import search.ScoreItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Phil on 08.07.2017.
 */
public class EMDCallable implements Callable {

    private ColorHistogram query;
    private ColorHistogram[] candidates;
    private int start;
    private int end;

    public EMDCallable(ColorHistogram query, ColorHistogram[] candidates, int start, int end){
        this.query = query;
        this.candidates = candidates;
        this.start = start;
        this.end = end;
    }
    @Override
    public Object call() throws Exception {
        List<ScoreItem> localresults = new ArrayList<>();
        for (int i = start; i<Math.min(end, candidates.length); i++)
        {
            Double dist = Measures.emd(query,candidates[i]);
            if (dist != null){
                localresults.add(new ScoreItem(candidates[i], dist));
            }
        }
        return localresults;
    }
}