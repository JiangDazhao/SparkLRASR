import Jama.Matrix;

public class HyperCov {
    double [][]M;
    int p; //æÿ’Û––£¨bands
    int N; //æÿ’Û¡–£¨pixels
    public HyperCov(double[][] m) {
        this.M = m;
    }

    public double[][] run(){
        double[][] C;
        double[][] u;
        double[][] meanMinusM;
        this.p= M.length;
        this.N= M[0].length;
        C=new double[p][p];
        u=new double[p][1];
        meanMinusM=new double[p][p];
        for(int i=0;i<p;i++){
            double sumDim=0;
            double ave;
            for(int j=0;j<N;j++){
                sumDim+=M[i][j];
            }
            ave=sumDim/N;
            u[i][0]=ave;
        }
        for(int k=0;k<N;k++){
            for(int j=0;j<p;j++){
                meanMinusM[j][k]=M[j][k]-u[j][0];
            }
        }
        Matrix matrixMeanMinusM= new Matrix(meanMinusM);
        Matrix matrixC= (matrixMeanMinusM.times(matrixMeanMinusM.transpose())
                .times(1.0/(N-1)));
        C=matrixC.getArrayCopy();
        return C;
    }
}
