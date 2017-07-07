package search;

import misc.Util;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 * Created by HP on 01.07.2017.
 */
public class QFWrapper {

    private RealMatrix A;
    private double[] eigenvalues;
    private int maxeigen;
    private RealMatrix u;

    public QFWrapper(int bincount, int maxeigen){
        A = MatrixUtils.createRealMatrix(Util.createHumanPerceptionSimilarityMatrix(bincount));
        SingularValueDecomposition svd = new SingularValueDecomposition(A);
        eigenvalues = svd.getSingularValues();
        u = svd.getU();
    }

    public RealMatrix getA(){
        return A;
    }

    public double[] getEigenvalues(){
        return eigenvalues;
    }

    public RealMatrix getU(){
        return A;
    }

    public int getMaxEigen(){
        return maxeigen;
    }
}
