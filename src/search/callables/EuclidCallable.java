package search.callables;

import feature.*;
import search.Measures;
import search.ScoreItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */

/*
    ANALOGOUS TO CanmanCallable!
 */
public class EuclidCallable implements Callable
{
    private FeatureHistogram query;
    private FeatureHistogram[] candidates;
    private int start;
    private int end;

    public EuclidCallable(FeatureHistogram query, FeatureHistogram[] candidates, int start, int end){
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
            Double dist = Measures.euclid(query,candidates[i],0);
            if (dist != null){
                localresults.add(new ScoreItem(candidates[i], dist));
            }
        }
        return localresults;
    }
}
