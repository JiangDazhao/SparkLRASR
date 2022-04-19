import Jama.Matrix;
import com.jxz.HyperCov;

public class testhyperCov {
    public static void main(String[] args) {
        double[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,9.}};
        Matrix A = new Matrix(vals);
        Matrix B= A.times(1.0/3);
        double[][] res= B.getArrayCopy();
        HyperCov hypercov= new HyperCov(vals);
        res=hypercov.run();
        for(int i=0;i<res.length;i++){
            for(int j=0;j<res[0].length;j++){
                System.out.print(res[i][j]+" ");
            }
            System.out.println();
        }

    }
}
