import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Created by user on 29.06.2017.
 */
public class Comparator {


    public static double euclid(ColorHistogram a, ColorHistogram b, int mode){
        int[][] acells = a.getResults();
        int[][] bcells = b.getResults();
        if (acells.length != bcells.length) return -1; //different cell length

        double[] cellresults = new double[acells.length];
        for (int cell = 0; cell<acells.length; cell++){
            if (acells[cell].length != bcells[cell].length) return -1;
            cellresults[cell] = euclid_inner(acells[cell], bcells[cell]);
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
}
