import Jama.Matrix;
import com.jxz.RxDetector;

public class testRxDetector {
    public static void main(String[] args) {
        double[] res;
        double[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,9.}};
        Matrix A = new Matrix(vals);

        RxDetector rxDetector= new RxDetector(vals);
        res= rxDetector.run();
        for(int i=0;i<res.length;i++){
            System.out.print(res[i]+" ");
        }

    }
}
