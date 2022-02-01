import Jama.Matrix;
import Jama.SingularValueDecomposition;
import scala.Tuple2;

import javax.swing.text.StyleContext;

public class LADMAP_LRASR {
    double[][]X;
    double[][]A;
    double lambda;
    double beta;

    double tol=1e-6;
    double tol2=1e-2;
    double maxIter=1e6;
    int d;
    int n;
    int m;
    double rho=1.1;
    double max_mu=1e10;
    double mu = 1e-3;
    double ita1;
    double [][]J;
    double [][]S;
    double [][]E;
    double [][]Y1;
    double [][]Y2;


    public LADMAP_LRASR(double[][] x, double[][] a, double lambda, double beta) {
        X = x;
        A = a;
        this.lambda = lambda;
        this.beta = beta;
    }

    public void init(){
        d=X.length;
        n=X[0].length;
        m=A[0].length;
        ita1= Math.pow(new Matrix(A).norm2(),2);
        J= new double[m][n];
        S= new double[m][n];
        E= new double[d][n];
        Y1= new double[d][n];
        Y2= new double[m][n];
    }


    public Tuple2<double[][],double[][]> run(){
        init();
        int iter=0;
        while (iter<maxIter){
            double[][]sk= new double[S.length][S[0].length];
            double[][]jk= new double[J.length][J[0].length];
            double[][]ek= new double[E.length][E[0].length];
            for(int i=0;i<S.length;i++){
                for(int j=0;j<S[0].length;j++){
                    sk[i][j]=S[i][j];
                }
            }
            for(int i=0;i<J.length;i++){
                for(int j=0;j<J[0].length;j++){
                    jk[i][j]=J[i][j];
                }
            }
            for(int i=0;i<E.length;i++){
                for(int j=0;j<E[0].length;j++){
                    ek[i][j]=E[i][j];
                }
            }
            iter++;

            Matrix tempMatrix;
            Matrix temp2Matrix;
            Matrix SMatrix;
            Matrix JMatrix;
            Matrix EMatirx;

            Matrix XMatrix= new Matrix(X);
            Matrix AMatrix= new Matrix(A);
            Matrix skMatrix= new Matrix(sk);
            Matrix ekMatrix= new Matrix(ek);
            Matrix Y1Matrix= new Matrix(Y1);
            Matrix Y2Matrix= new Matrix(Y2);
            Matrix jkMatrix= new Matrix(jk);
            Matrix tempMinus1=AMatrix.transpose().times(XMatrix.minus(AMatrix.times(skMatrix)).
                    minus(ekMatrix).plus(Y1Matrix.times(1.0/mu)));
            Matrix tempMinus2=skMatrix.minus(jkMatrix).plus(Y2Matrix.times(1.0/mu));
            tempMatrix=skMatrix.plus((tempMinus1.minus(tempMinus2)).times(1.0/ita1));

            Matrix UMatrix;
            Matrix VMatrix;
            Matrix SingularMatrix;

            // X=U*S*V^T --> Ucopy Scopy Vcopy
            // X^T=V*S^T*U^T -->  Output U=V  Output S=S Output V=U
            UMatrix= new SingularValueDecomposition(tempMatrix.transpose()).getV();
            SingularMatrix= new SingularValueDecomposition(tempMatrix.transpose()).getS();
            VMatrix= new SingularValueDecomposition(tempMatrix.transpose()).getU();


            double[] sigma = new double[SingularMatrix.getRowDimension()];
            for(int i=0;i<SingularMatrix.getRowDimension();i++){
                sigma[i]=SingularMatrix.get(i,i);
            }
            int svp=0;
            for(int i=0;i<sigma.length;i++){
                double jud= sigma[i]-1.0/(mu*ita1);
                if(jud>0) svp++;
            }

            double [][]diagSigma;
            if(svp>=1){
                for(int i=0;i<svp;i++){
                    sigma[i]=sigma[i]-1.0/(mu*ita1);
                }
                diagSigma= new double[svp][svp];
                for(int i=0;i<svp;i++){
                    diagSigma[i][i]=sigma[i];
                }
            }
            else {
                svp=1;
                diagSigma= new double[svp][svp];
                for(int i=0;i<svp;i++){
                    diagSigma[i][i]=0;
                }
            }

            int[] svpIndex= new int[svp];
            for(int i=0;i<svp;i++){
                svpIndex[i]=i;
            }

            Matrix UMatrixSvp= UMatrix.getMatrix(0, UMatrix.getRowDimension()-1,svpIndex);
            Matrix VMatrixSvp= VMatrix.getMatrix(0,VMatrix.getRowDimension()-1,svpIndex);

            Matrix diagSigmaMatrix= new Matrix(diagSigma);
            SMatrix=UMatrixSvp.times(diagSigmaMatrix).times(VMatrixSvp.transpose());

            //Update J
            temp2Matrix=SMatrix.plus(Y2Matrix.times(1.0/mu));
            double[][]temp2= temp2Matrix.getArrayCopy();
            soft s=new soft(temp2,beta/mu);
            J=s.run();

            //Update E
            Matrix xmazMatrix= XMatrix.minus(AMatrix.times(SMatrix));
            Matrix temp3Matrix= xmazMatrix.plus(Y1Matrix.times(1.0/mu));
            double[][] temp3= temp3Matrix.getArrayCopy();
            SolveL1L2 solveL1L2= new SolveL1L2(temp3,lambda/mu);
            E=solveL1L2.run();

            EMatirx=new Matrix(E);
            JMatrix=new Matrix(J);
            Matrix leq1Matrix= xmazMatrix.minus(EMatirx);
            Matrix leq2Matrix= SMatrix.minus(JMatrix);

            double stopC= leq1Matrix.normF()/XMatrix.normF();
            double stopC2;
            double stopC2Up;
            double stopC2Down;
            double max1;
            double max2;
            double max3;
            max1=Math.sqrt(ita1)*((SMatrix.minus(skMatrix)).normF());
            max2=(JMatrix.minus(jkMatrix)).normF();
            max3=(EMatirx.minus(ekMatrix)).normF();
            stopC2Up=mu*Math.max(Math.max(max1,max2),max3);
            stopC2Down=XMatrix.normF();
            stopC2=stopC2Up/stopC2Down;

            System.out.println("iter="+iter+" stopC="+stopC+" stopC2="+stopC2);


            if(stopC<tol&&stopC2<tol2){
                break;
            }else {
                Y1Matrix=Y1Matrix.plus(leq1Matrix.times(mu));
                Y2Matrix=Y2Matrix.plus(leq2Matrix.times(mu));
                mu= Math.min(max_mu,mu*rho);
            }
        }
        return new Tuple2<>(S,E);
    }
}
