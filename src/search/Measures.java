package search;

import feature.ColorHistogram;
import org.apache.commons.math3.linear.RealMatrix;
import java.util.stream.DoubleStream;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class Measures {
    /*
        Given two ColorHistogram's, this class computes their distance by the following metrics:
        Chebyshev, Canberra, Manhattan (merged into "Canman"), Euclidean, Weighted Euclidean,
        Quadratic Form and Earth-Mover's.

     */

    public static double chebyshev(ColorHistogram a, ColorHistogram b){
        double[][][] acells = a.getResults(); //1. dim -> cells, 2. dim -> channel, 3. dim -> bins
        double[][][] bcells = b.getResults();

        double[] cellresults = new double[acells.length*3];
        for (int cell = 0; cell < acells.length; cell++) { //for each cell
            for (int channel = 0; channel < acells[cell].length; channel++) { //for each channel (-> 3)
                //populate result array
                cellresults[cell*acells.length+channel] = chebyshev_inner(acells[cell][channel], bcells[cell][channel]);
            }
        }
        DoubleStream s = DoubleStream.of(cellresults); //build a stream and average over cells/channels
        return s.average().getAsDouble();
    }

    private static double chebyshev_inner(double[] a, double[] b){
        //chebyshev returns the maximum distance in one dimension
        double max = 0.0;
        double tmp;
        for (int i=0; i<a.length; i++){
            tmp =Math.abs(a[i]-b[i]); //dist
            if (tmp > max) max = tmp; //update maximum
        }
        return max;
    }

    public static double canman(ColorHistogram a, ColorHistogram b, boolean iscanberra){
        //Analogous to Chebyshev. We differ between Canberra and Manhattan Distance.
        //Canberra gets an additional weighting. c.f. inner functions
        double[][][] acells = a.getResults();
        double[][][] bcells = b.getResults();

        double[] cellresults = new double[acells.length*3];
        if (iscanberra){
            for (int cell = 0; cell<acells.length; cell++){
                for (int channel = 0; channel < acells[cell].length; channel++){
                    cellresults[cell*acells.length+channel] = canberra_inner(acells[cell][channel], bcells[cell][channel]);
                }
            }
        } else { //manhattan
            for (int cell = 0; cell<acells.length; cell++){
                for (int channel = 0; channel < acells[cell].length; channel++){
                    cellresults[cell*acells.length+channel] = manhattan_inner(acells[cell][channel], bcells[cell][channel]);
                }
            }
        }
        DoubleStream s = DoubleStream.of(cellresults); //averaging over cells and channels
        return s.average().getAsDouble();
    }
    private static double canberra_inner(double[] a, double[] b){
        //returns sum(|p_i-q_i|/(p_i+q_i))
        // the original formula has further abs-operators, but our vectors contain only positive numbers..
        double result = 0.0;
        double div;
        for (int i=0; i<a.length; i++){
            div = a[i] + b[i];
            if (div > 0){ //catch division by zero!
                result += Math.abs(a[i]-b[i])/div;
            }
        }
        return result;
    }

    private static double manhattan_inner(double[] a, double[] b){
        //trivial.
        double result = 0.0;
        for (int i=0; i<a.length; i++){
            result += Math.abs(a[i]-b[i]);
        }
        return result;
    }

    public static double euclid(ColorHistogram a, ColorHistogram b, int mode){
        //analogous to Chebyshev.
        //We have included the experimental mode-feature, where we could
        //vary the aggregation method over channels and cells.
        double[][][] acells = a.getResults();
        double[][][] bcells = b.getResults();

        double[] cellresults = new double[acells.length*3];
        for (int cell = 0; cell<acells.length; cell++){
            for (int channel = 0; channel < acells[cell].length; channel++){
                //euclidean distance, using all dimension (-> -1)
                cellresults[cell*acells.length+channel] = euclid_inner(acells[cell][channel], bcells[cell][channel], -1);
            }
        }
        DoubleStream s = DoubleStream.of(cellresults);
        if (mode == 0){
            return s.average().getAsDouble();
        } else {
            return s.min().getAsDouble();//experimental...
        }
    }

    private static double euclid_inner(double[] a, double[] b, int dims){
        //custom number of dims, still needs to be in the range of [0, a.length]
        if (dims < 1 || dims > a.length) dims = a.length;

        //compute distance, trivial.
        double result = 0;
        for (int bin = 0; bin<dims; bin++){
            result += Math.pow(a[bin] - b[bin],2);
        }
        return Math.sqrt(result);
    }
    public static double weighted_euclid_inner(double[] a, double[] b, double[] w, int dims){
        if (dims < 1 || dims > a.length) dims = a.length;

        //the same as euclid_inner, but with another weighting vector. used at quadratic form distance.
        double result = 0;
        for (int bin = 0; bin<dims; bin++){
            result += w[bin] * Math.pow(a[bin] - b[bin],2);
        }
        return Math.sqrt(result);
    }

    public static double quadraticform(ColorHistogram a, ColorHistogram b, QFWrapper wrapper){
        double[][][] acells = a.getResults();
        double[][][] bcells = b.getResults();

        double[] cellresults = new double[acells.length*3];
        RealMatrix u = wrapper.getU();
        double[] weights = wrapper.getEigenvalues(); //w(i,j)
        for (int cell = 0; cell<acells.length; cell++){ //for each cell
            for (int channel = 0; channel < acells[cell].length; channel++){ //for each channel
                double[] pV = u.operate(acells[cell][channel]); //pV
                double[] qV = u.operate(bcells[cell][channel]); //qV
                //compute weighted euclidean distance:
                cellresults[cell*acells.length+channel] = weighted_euclid_inner(pV, qV, weights, wrapper.getMaxEigen());
            }
        }
        return DoubleStream.of(cellresults).average().getAsDouble();  //average over cells and channels
    }

    public static double emd(ColorHistogram a, ColorHistogram b){
        //analogous to Chebyshev.
        double[][][] acells = a.getResults();
        double[][][] bcells = b.getResults();

        double[] cellresults = new double[acells.length*3];
        for (int cell = 0; cell < acells.length; cell++) {
            for (int channel = 0; channel < acells[cell].length; channel++) {
                cellresults[cell*acells.length+channel] = emd_inner(acells[cell][channel], bcells[cell][channel]);
            }
        }
        DoubleStream s = DoubleStream.of(cellresults);
        return s.average().getAsDouble();
    }

    private static double emd_inner(double[] a, double[] b){
        //Compute the earth mover's distance by the hungarian algorithm.
        double result = 0.0;
        for (int i=0; i<a.length; i++){
            result+= Math.abs(a[i]+ result - b[i]);
        }
        return result*Math.pow(10, -14); //because the plotter cant handle 1.051893223099762E16 :)
    }
}
