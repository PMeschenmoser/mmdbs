package search;

import misc.Util;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 * Authors: P. Meschenmoser, C. Gutknecht
 */
public class QFWrapper {
    /*
        This class returns the eigenvalues, yielded by the SVD of a bincount*bincount
        human perception weight matrix. It ensures that all properties are compactly
        represented and no repetitive computation are required. Instantiated by the Calculator.
     */
    private RealMatrix A;
    private double[] eigenvalues;
    private RealMatrix u;
    private int maxeigen;

    public QFWrapper(int bincount, int maxeigen){
        A = MatrixUtils.createRealMatrix(Util.createHumanPerceptionSimilarityMatrix(bincount)); //input matrix
        SingularValueDecomposition svd = new SingularValueDecomposition(A);
        eigenvalues = svd.getSingularValues(); //main diagonal of Sigma, in descending order
        u = svd.getU(); //eigenvectors, needed for qV
        this.maxeigen = maxeigen; //maximum amount of eigenvalues to use for distance computation
    }

    public RealMatrix getA(){
        return A;
    }

    public double[] getEigenvalues(){
        return eigenvalues;
    }

    public RealMatrix getU(){
        return u;
    }

    public int getMaxEigen(){
        return maxeigen;
    }


}
