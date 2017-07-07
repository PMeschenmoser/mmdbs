package search;

import feature.ColorHistogram;
import org.apache.commons.math3.linear.RealMatrix;
import java.util.stream.DoubleStream;

public class Measures {


    public static double euclid(ColorHistogram a, ColorHistogram b, int mode){
        double[][][] acells = a.getResults();
        double[][][] bcells = b.getResults();
        if (acells.length != bcells.length) return -1; //different cell length

        double[] cellresults = new double[acells.length*3];
        for (int cell = 0; cell<acells.length; cell++){
            if (acells[cell].length != bcells[cell].length) return -1;
            for (int channel = 0; channel < 3; channel++){
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
        if (dims == -1){
            dims = a.length;
        } else {
            dims = Math.min(Math.max(0, dims), a.length);
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
        if (acells.length != bcells.length) return -1; //different cell length
        if (acells[0].length != bcells[0].length) return -1; //different #channels
        if (acells[0][0].length != bcells[0][0].length  ) return -1; //different #bins

        double[] cellresults = new double[acells.length*3];
        RealMatrix u = wrapper.getU();
        double[] weights = wrapper.getEigenvalues();
        for (int cell = 0; cell<acells.length; cell++){
            if (acells[cell].length != bcells[cell].length) return -1;
            for (int channel = 0; channel < 3; channel++){
                double[] pV = u.operate(acells[cell][channel]); //pV
                double[] qV = u.operate(bcells[cell][channel]); //qV
                cellresults[cell] = weighted_euclid_inner(pV, qV, weights,1);
            }
        }
        return DoubleStream.of(cellresults).average().getAsDouble();
    }
}
