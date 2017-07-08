package search;

import feature.ColorHistogram;
import org.apache.commons.math3.linear.RealMatrix;
import java.util.stream.DoubleStream;

public class Measures {


    public static double chebyshev(ColorHistogram a, ColorHistogram b){
        double[][][] acells = a.getResults();
        double[][][] bcells = b.getResults();

        double[] cellresults = new double[acells.length*3];
        for (int cell = 0; cell < acells.length; cell++) {
            for (int channel = 0; channel < acells[cell].length; channel++) {
                cellresults[cell] = chebyshev_inner(acells[cell][channel], bcells[cell][channel]);
            }
        }
        DoubleStream s = DoubleStream.of(cellresults);
        return s.average().getAsDouble();
    }

    private static double chebyshev_inner(double[] a, double[] b){
        double max = 0.0;
        double tmp;
        for (int i=0; i<a.length; i++){
            tmp =Math.abs(a[i]-b[i]);
            if (tmp > max) max = tmp;
        }
        return max;
    }
    public static double canman(ColorHistogram a, ColorHistogram b, boolean iscanberra){
        double[][][] acells = a.getResults();
        double[][][] bcells = b.getResults();

        double[] cellresults = new double[acells.length*3];
        if (iscanberra){
            for (int cell = 0; cell<acells.length; cell++){
                for (int channel = 0; channel < acells[cell].length; channel++){
                    cellresults[cell] = canberra_inner(acells[cell][channel], bcells[cell][channel]);
                }
            }
        } else { //manhattan
            for (int cell = 0; cell<acells.length; cell++){
                for (int channel = 0; channel < acells[cell].length; channel++){
                    cellresults[cell] = manhattan_inner(acells[cell][channel], bcells[cell][channel]);
                }
            }
        }
        DoubleStream s = DoubleStream.of(cellresults);
        return s.average().getAsDouble();
    }
    private static double canberra_inner(double[] a, double[] b){
        double result = 0.0;
        double div;
        for (int i=0; i<a.length; i++){
            div = a[i] + b[i];
            if (div > 0){
                result += Math.abs(a[i]-b[i])/div;
            }
        }
        return result;
    }

    private static double manhattan_inner(double[] a, double[] b){
        double result = 0.0;
        for (int i=0; i<a.length; i++){
            result += Math.abs(a[i]-b[i]);
        }
        return result;
    }

    public static double euclid(ColorHistogram a, ColorHistogram b, int mode){
        double[][][] acells = a.getResults();
        double[][][] bcells = b.getResults();

        double[] cellresults = new double[acells.length*3];
        for (int cell = 0; cell<acells.length; cell++){
            for (int channel = 0; channel < acells[cell].length; channel++){
                cellresults[cell] = euclid_inner(acells[cell][channel], bcells[cell][channel], -1);
            }
        }
        DoubleStream s = DoubleStream.of(cellresults);
        if (mode == 0){
            return s.average().getAsDouble();
        } else {
            return s.min().getAsDouble();
        }
    }

    private static double euclid_inner(double[] a, double[] b, int dims){
        if (dims == -1){
            dims = a.length;
        } else {
            dims = Math.min(Math.max(0, dims), a.length);
        }
        double result = 0;
        for (int bin = 0; bin<dims; bin++){
            result += Math.pow(a[bin] - b[bin],2);
        }
        return Math.sqrt(result);
    }
    public static double weighted_euclid_inner(double[] a, double[] b, double[] w, int dims){
        if (dims < 1 || dims > a.length){
            dims = a.length;
        }
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
        double[] weights = wrapper.getEigenvalues();
        for (int cell = 0; cell<acells.length; cell++){
            if (acells[cell].length != bcells[cell].length) return -1;
            for (int channel = 0; channel < 3; channel++){
                double[] pV = u.operate(acells[cell][channel]); //pV
                double[] qV = u.operate(bcells[cell][channel]); //qV
                cellresults[cell] = weighted_euclid_inner(pV, qV, weights, wrapper.getMaxEigen());
            }
        }
        return DoubleStream.of(cellresults).average().getAsDouble();
    }
    public static double emd(ColorHistogram a, ColorHistogram b){
        double[][][] acells = a.getResults();
        double[][][] bcells = b.getResults();

        double[] cellresults = new double[acells.length*3];
        for (int cell = 0; cell < acells.length; cell++) {
            for (int channel = 0; channel < acells[cell].length; channel++) {
                cellresults[cell] = emd_inner(acells[cell][channel], bcells[cell][channel]);
            }
        }
        DoubleStream s = DoubleStream.of(cellresults);
        return s.average().getAsDouble();
    }

    private static double emd_inner(double[] a, double[] b){
        double result = 0.0;
        for (int i=0; i<a.length; i++){
            result+= Math.abs(a[i]+ result - b[i]);
        }
        return result*Math.pow(10, -14); //because the plotter cant handle 1.051893223099762E16 :)
    }
}
