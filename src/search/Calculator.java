package search;

import feature.ColorHistogram;
import gui.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

class QFThread implements Callable
{
    private ColorHistogram query;
    private ColorHistogram[] candidates;
    private int start;
    private int end;
    private QFWrapper qf;

    public QFThread(ColorHistogram query, ColorHistogram[] candidates, int start, int end, QFWrapper qf){
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
class EuclidThread implements Callable
{
    private ColorHistogram query;
    private ColorHistogram[] candidates;
    private int start;
    private int end;

    public EuclidThread(ColorHistogram query, ColorHistogram[] candidates, int start, int end){
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

public class Calculator {
    private Settings settings;
    public Calculator(Settings settings){
        this.settings = settings;
    }

    public List<ScoreItem> run(ColorHistogram query, ColorHistogram[] candidates){
        List<ScoreItem> merged = new ArrayList<>();
        int width = candidates.length/settings.getThreadCount();
        ExecutorService pool = Executors.newFixedThreadPool(settings.getThreadCount()); //parallel execution
        try {
            if (settings.getMetric().equals("Euclidean")){
                for (int i=0; i<candidates.length; i+= width){
                    Future<List<ScoreItem>> s = pool.submit(new EuclidThread(query,candidates, i, i+width));
                    merged.addAll(s.get());

                }
            } else { //for now: quadratic form
                QFWrapper qf = new QFWrapper(settings.getBinCount(), settings.getMaxEigen());
                System.out.println("RUN QF");
                for (int i=0; i<candidates.length; i+= width){
                    Future<List<ScoreItem>> s = pool.submit(new QFThread(query,candidates, i, i+width, qf));
                    merged.addAll(s.get());
                }
            } //insert earth mover here!

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    return merged;
    }
}
