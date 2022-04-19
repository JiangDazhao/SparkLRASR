package com.jxz;

import scala.Tuple2;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class main {
    public static void main(String[] args) throws IOException {
        double lambda = 0.1;
        double beta = 0.1;
        int K=15;
        int P=20;

        //设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("start time:"+df.format(new Date()));

        double t1=System.currentTimeMillis();
        Data data = new Data("Urban_img.mat", "Urban_gt.mat");
        double[][] X;
        double[][] GT;
        double[][] Dic;
        X=data.getImg2D();
        GT=data.getGT();
//        System.out.println("X data="+X.length+" "+X[0].length);
//        System.out.println("GT data="+GT.length+" "+GT[0].length);
        double t2=System.currentTimeMillis();
        System.out.println("ReadData time:"+(t2-t1)*1.0/1000+"s");
        System.out.println("ReadData Finish:"+df.format(new Date()));

        double t3=System.currentTimeMillis();
        DicCon dicCon= new DicCon(X,K,P);
        Dic=dicCon.run();
        double t4=System.currentTimeMillis();
        System.out.println("com.jxz.DicCon time:"+(t4-t3)*1.0/1000+"s");
        System.out.println("com.jxz.DicCon Finish:"+df.format(new Date()));

        double t5=System.currentTimeMillis();
        LADMAP_LRASR ladmap_lrasr= new LADMAP_LRASR(X,Dic,lambda,beta);
        Tuple2<double[][],double[][]> SE=ladmap_lrasr.run();
        double t6=System.currentTimeMillis();
        System.out.println("LADMAP time:"+(t6-t5)*1.0/1000+"s");
        System.out.println("LADMAP Finish:"+df.format(new Date()));


        double[][]S= SE._1;
        double[][]E= SE._2;
//        System.out.println(S.length+" "+S[0].length);
//        System.out.println(E.length+" "+E[0].length);

        double t7=System.currentTimeMillis();
        double[][]re= new double[GT.length][GT[0].length];
        for(int i=0;i<E[0].length;i++){
            double sum=0;
            for(int j=0;j<E.length;j++){
                sum+=Math.pow(E[j][i],2);
            }
            re[i%GT.length][i/GT.length]=Math.sqrt(sum);
        }
        AUCDouble auc = new AUCDouble(GT,re);
        double aucresult= auc.run();
        System.out.println("com.jxz.AUC="+aucresult);
        double t8=System.currentTimeMillis();
        System.out.println("com.jxz.AUC time:"+(t8-t7)*1.0/1000+"s");
        System.out.println("com.jxz.AUC Finish:"+df.format(new Date()));
    }
}
