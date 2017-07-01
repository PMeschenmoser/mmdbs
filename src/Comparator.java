import java.util.stream.DoubleStream;


/**
 * Created by user on 29.06.2017.
 */
public class Comparator {


    public static double euclid(ColorHistogram a, ColorHistogram b, int mode){
        int[][][] acells = a.getResults();
        int[][][] bcells = b.getResults();
        if (acells.length != bcells.length) return -1; //different cell length

        double[] cellresults = new double[acells.length*3];
        for (int cell = 0; cell<acells.length; cell++){
            if (acells[cell].length != bcells[cell].length) return -1;
            for (int channel = 0; channel < 3; channel++){
                cellresults[cell] = euclid_inner(acells[cell][channel], bcells[cell][channel]);
            }
        }
        DoubleStream s = DoubleStream.of(cellresults);
        if (mode == 0){
            return s.average().getAsDouble();
        } else {
            return s.min().getAsDouble();
        }
    }

    private static double euclid_inner(int[] a, int[] b){
        double result = 0;
        for (int bin = 0; bin<a.length; bin++){
            result += Math.pow(a[bin] - b[bin],2);
        }
        return Math.sqrt(result);
    }

    private static double quadraticform(ColorHistogram a, ColorHistogram b){
        int[][][] acells = a.getResults();
        int[][][] bcells = b.getResults();
        if (acells.length != bcells.length) return -1; //different cell length
        if (acells[0].length != bcells[0].length) return -1; //different #channels
        if (acells[0][0].length != bcells[0][0].length  ) return -1; //different #bins
        int bincount = acells[0][0].length;
        double[][] perceptmat = Util.createHumanPerceptionSimilarityMatrix(bincount);
        double[] cellresults = new double[acells.length*3];

        for (int cell = 0; cell<acells.length; cell++){
            if (acells[cell].length != bcells[cell].length) return -1;
            for (int channel = 0; channel < 3; channel++){

            }
        }
        return 0;
    }
}
