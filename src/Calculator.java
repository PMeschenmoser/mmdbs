import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.*;

/**
 * Created by Phil on 06.07.2017.
 */
 class SearchThread implements Callable
{
    private ColorHistogram query;
    private ColorHistogram[] candidates;
    private int start;
    private int end;

    public SearchThread(ColorHistogram query, ColorHistogram[] candidates, int start, int end){
        this.query = query;
        this.candidates = candidates;
        this.start = start;
        this.end = end;
    }

    @Override
    public Object call() throws Exception {
        ArrayList<ScoreItem> localresults = new ArrayList<>();
        for (int i = start; i<Math.min(end, candidates.length); i++)
        {
            Double dist = Measures.euclid(query,candidates[i],0);
            if (dist != null){
                localresults.add(new ScoreItem(candidates[i], Measures.euclid(query,candidates[i],0)));
            }
        }
        return localresults;
    }

}
public class Calculator {
    private UISettings settings;
    public Calculator(UISettings settings){
        this.settings = settings;
    }

    public ArrayList<ScoreItem> run(ColorHistogram query, ColorHistogram[] candidates){
        ArrayList<ScoreItem> merged = new ArrayList<>();
        int width = candidates.length/settings.getThreadCount();
        ExecutorService pool = Executors.newFixedThreadPool(settings.getThreadCount()); //parallel execution
        try {
            for (int i=0; i<candidates.length; i+= width){
                Future<ArrayList<ScoreItem>> s = pool.submit(new SearchThread(query,candidates, i, i+width));
                merged.addAll(s.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    return merged;
    }
}
