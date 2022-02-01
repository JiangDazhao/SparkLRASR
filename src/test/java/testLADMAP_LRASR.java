import scala.Tuple2;

import java.io.IOException;

public class testLADMAP_LRASR {
    public static void main(String[] args) throws IOException {
        double lambda = 0.1;
        double beta = 0.1;
        int K=15;
        int P=20;
        Data data = new Data("img2D.mat");
        double[][] X;
        double[][] Dic;
        X=data.img2D;

        DicCon dicCon= new DicCon(X,K,P);
        Dic=dicCon.run();
        LADMAP_LRASR ladmap_lrasr= new LADMAP_LRASR(X,Dic,lambda,beta);
        Tuple2<double[][],double[][]> SE=ladmap_lrasr.run();
        double[][]S= SE._1;
        double[][]E= SE._2;
        System.out.println(S.length+" "+S[0].length);
        System.out.println(E.length+" "+E[0].length);
    }
}
