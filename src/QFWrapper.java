import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 * Created by HP on 01.07.2017.
 */
public class QFWrapper {

    private RealMatrix A;
    private double[] eigenvalues;
    private RealMatrix u;

    public QFWrapper(int bincount){
        A = MatrixUtils.createRealMatrix(Util.createHumanPerceptionSimilarityMatrix(bincount));
        SingularValueDecomposition svd = new SingularValueDecomposition(A);
        eigenvalues = (new EigenDecomposition(A)).getRealEigenvalues();
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
}
