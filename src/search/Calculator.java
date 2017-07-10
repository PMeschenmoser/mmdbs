package search;

import feature.ColorHistogram;
import gui.Settings;
import search.callables.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */

public class Calculator {
    /*
        This is our search engine. It takes 'settings' as input and processes the search space in parallel,
        using appropriate Callables.

     */

    private Settings settings;
    public Calculator(Settings settings){
        this.settings = settings;
    }

    public List<ScoreItem> run(ColorHistogram query, ColorHistogram[] candidates){
        List<ScoreItem> merged = new ArrayList<>(); //final results, merged from multiple threads
        int width = candidates.length/settings.getThreadCount(); //how many items should each thread check?
        ExecutorService pool = Executors.newFixedThreadPool(settings.getThreadCount()); //parallel execution
        try {
            switch (settings.getMetric()){
                case "Euclidean":
                    for (int i=0; i<candidates.length; i+= width){
                        Future<List<ScoreItem>> s = pool.submit(new EuclidCallable(query,candidates, i, i+width));
                        merged.addAll(s.get()); //adds all results of current callable when finished
                    }
                    break;
                //EVERYTHING ELSE: analogous
                case "Quadratic Form":
                    QFWrapper qf = new QFWrapper(settings.getBinCount(), settings.getMaxEigen()); //bundled qf data
                    for (int i=0; i<candidates.length; i+= width){
                        Future<List<ScoreItem>> s = pool.submit(new QFCallable(query,candidates, i, i+width, qf));
                        merged.addAll(s.get());
                    }
                    break;
                case "Manhattan":
                case "Canberra":
                    boolean canberra = settings.getMetric().equals("Canberra");
                    for (int i=0; i<candidates.length; i+= width){
                        Future<List<ScoreItem>> s = pool.submit(new CanmanCallable(query,candidates, i, i+width,canberra));
                        merged.addAll(s.get());
                    }
                    break;
                case "Chebyshev":
                    for (int i=0; i<candidates.length; i+= width){
                        Future<List<ScoreItem>> s = pool.submit(new ChebyshevCallable(query,candidates, i, i+width));
                        merged.addAll(s.get());
                    }
                    break;
                case "EMD":
                    for (int i=0; i<candidates.length; i+= width){
                        Future<List<ScoreItem>> s = pool.submit(new EMDCallable(query,candidates, i, i+width));
                        merged.addAll(s.get());
                    }
                    break;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    return merged; // returns List<ScoreItem>
    }
}
