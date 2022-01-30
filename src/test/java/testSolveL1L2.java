import Jama.Matrix;

public class testSolveL1L2 {
    public static void main(String[] args) {
//        double[][] vals = {{1},{2},{3}};
//        double res= new Matrix(vals).norm2();
//        System.out.println(res);
        double[][] vals = {{1.,2.,3},{4.,5.,6.},{7.,8.,9.}};
        Matrix A = new Matrix(vals);
        SolveL1L2 solveL1L2= new SolveL1L2(vals,0.3);
        double[][]res=solveL1L2.run();
        for(int i=0;i<res.length;i++){
            for(int j=0;j<res[0].length;j++){
                System.out.print(res[i][j]+" ");
            }
            System.out.println();
        }
    }
}
