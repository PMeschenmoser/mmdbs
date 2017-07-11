package feature;
import java.io.File;

public interface FeatureHistogram {
    double[][][] getResults();
    File getFile();
    int getBinCount();
    int getCellCount();
    int getType();
}
