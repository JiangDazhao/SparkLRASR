import Jama.Matrix;
import com.jxz.soft;

public class testsoft {
    public static void main(String[] args) {
        double[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,9.}};
        Matrix A = new Matrix(vals);
        Matrix B = new Matrix(vals);
        Matrix C= A.arrayRightDivide(B);
        soft ft= new soft(vals,3);
        double[][] res= ft.run();
        for(int i=0;i<res.length;i++){
            for(int j=0;j<res[0].length;j++){
                System.out.print(res[i][j]+" ");
            }
            System.out.println();
        }
    }
}
