package search.callables;

import feature.ColorHistogram;
import search.Measures;
import search.QFWrapper;
import search.ScoreItem;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */

/*
    ANALOGOUS TO CanmanCallable!
    In addition, it takes the QFWrapper into account.
 */
public class QFCallable implements Callable
{
    private ColorHistogram query;
    private ColorHistogram[] candidates;
    private int start;
    private int end;
    private QFWrapper qf;

    public QFCallable(ColorHistogram query, ColorHistogram[] candidates, int start, int end, QFWrapper qf){
        this.query = query;
        this.candidates = candidates;
        this.start = start;
        this.end = end;
        this.qf = qf;
    }

    @Override
    public Object call() throws Exception {
        ArrayList<ScoreItem> localresults = new ArrayList<>();
        for (int i = start; i<Math.min(end, candidates.length); i++)
        {
            Double dist = Measures.quadraticform(query, candidates[i], qf);
            if (dist != null){
                localresults.add(new ScoreItem(candidates[i], dist));
            }
        }
        return localresults;
    }
}