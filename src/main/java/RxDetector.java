import Jama.Matrix;

public class RxDetector {
    double[][]M;
    int p; //æÿ’Û––£¨bands
    int N; //æÿ’Û¡–£¨pixels
    public RxDetector(double[][] m) {
        this.M = m;
    }

    public double[] run(){
        double[][] u;
        double[][] meanMinusM;
        double[][] sigma;
        double[][] result;
        double[][] absResult;

        this.p= M.length;
        this.N= M[0].length;
        u=new double[p][1];
        meanMinusM=new double[p][N];
        for(int i=0;i<p;i++){
            double sumDim=0.0;
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
        HyperCov hyperCov = new HyperCov(M);
        sigma=hyperCov.run();
//        for(int i=0;i<sigma.length;i++){
//            for(int j=0;j<sigma[0].length;j++){
//                System.out.print(sigma[i][j]+" ");
//            }
//            System.out.println();
//        }
        double delta=1e-5;
        Matrix eyeMatrix= Matrix.identity(p,p);
        Matrix sigmaMatrix= new Matrix(sigma);
        Matrix invTemp= sigmaMatrix.plus(eyeMatrix.times(delta));
        Matrix sigmaInvMatrix= invTemp.inverse();
//        for(int i=0;i<sigmaInvMatrix.getRowDimension();i++){
//            for(int j=0;j<sigmaInvMatrix.getColumnDimension();j++){
//                System.out.print(sigmaInvMatrix.get(i,j)+" ");
//            }
//            System.out.println();
//        }


        result= new double[N][1];
        Matrix meanMinusMMatrix= new Matrix(meanMinusM);
        for(int i=0;i<N;i++){
            result[i][0]=(meanMinusMMatrix.getMatrix(0,p-1,i,i)).transpose()
                    .times(sigmaInvMatrix).times(meanMinusMMatrix.getMatrix(0,p-1,i,i))
                    .get(0,0);
        }

        absResult= new double[N][1];
        for(int i=0;i<result.length;i++){
            absResult[i][0]=Math.abs(result[i][0]);
        }

        double[] transResult= new double[N];
        for(int i=0;i<N;i++){
            transResult[i]=absResult[i][0];
        }
        return transResult;
    }
}
