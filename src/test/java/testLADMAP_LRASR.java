import com.jxz.LADMAP_LRASR;
import scala.Tuple2;

import java.io.IOException;

public class testLADMAP_LRASR {
    public static void main(String[] args) throws IOException {
//        double lambda = 0.1;
//        double beta = 0.1;
//        int K=15;
//        int P=20;
//        com.jxz.Data data = new com.jxz.Data("Urban_img.mat");
//        double[][] X;
//        double[][] Dic;
//        X=data.img2D;
//
//        com.jxz.DicCon dicCon= new com.jxz.DicCon(X,K,P);
//        Dic=dicCon.run();
//        com.jxz.LADMAP_LRASR ladmap_lrasr= new com.jxz.LADMAP_LRASR(X,Dic,lambda,beta);
//        Tuple2<double[][],double[][]> SE=ladmap_lrasr.run();
//        double[][]S= SE._1;
//        double[][]E= SE._2;
//        System.out.println(S.length+" "+S[0].length);
//        System.out.println(E.length+" "+E[0].length);
//
        double[][]X= new double[][]{
                {1,2,3,4,5,6,7,8,9,10},
                {11,12,13,14,15,16,17,18,19,20},
                {21,22,23,24,25,26,27,28,29,30}};
        double[][]A= new double[][]{
                {1,2,3,4,5,6},
                {11,12,13,14,15,16},
                {21,22,23,24,25,26}};
        double lambda=0.1;
        double beta=0.1;
        Tuple2<double[][],double[][]> res= new LADMAP_LRASR(X,A,lambda,beta).run();
        double[][]S=res._1;
        double[][]E=res._2;
        System.out.println("S=");
        for (int i = 0; i <S.length; i++) {
            for (int j = 0; j < S[0].length; j++) {
                System.out.print(S[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println("E=");
        for (int i = 0; i <E.length; i++) {
            for (int j = 0; j < E[0].length; j++) {
                System.out.print(E[i][j]+" ");
            }
            System.out.println();
        }
    }
}
