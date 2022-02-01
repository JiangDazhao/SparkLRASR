import Jama.Matrix;

public class testAll {
    public static void main(String[] args) {
        double[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,9.}};
        Matrix A = new Matrix(vals);
        double res= A.normF();
        System.out.println(res);
    }
}
