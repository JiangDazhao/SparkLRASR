package com.jxz;

import java.util.ArrayList;

public class DicCon {
    double[][]X;
    int K; //簇的个数
    int P; //每簇里面挑出来的像素个数
    int bands;
    int pixelNum;

    public DicCon(double[][] x, int k, int p) {
        this.X = x;
        this.K = k;
        this.P = p;
    }

    //直接返回全局下的字典
    public double[][] run(){
        //构造的字典矩阵
        double[][]Dictionary=null;
        //所有pixel的clusterID
        int[] K1;

        bands= X.length;
        pixelNum=X[0].length;
        Kmeans kmeans= new Kmeans(X,K);
        K1=kmeans.run();// K1为所有像素的类别
        for(int i=0;i<K;i++){
            //每簇pointID
            ArrayList<Integer> st1=new ArrayList<Integer>();
            //每簇构成的部分图像
            double[][]temp;
            //RxDetector的值
            double[]kr;
            //排序后的kr
            int[] d2;

            //从原图像X中挑选出每个类别的所有图像生成temp，若不满P=20则直接忽略该类别
            for(int j=0;j<K1.length;j++){
                if(K1[j]==i){
                    st1.add(j);
                }
            }
            if(st1.size()<P){
                continue;
            }else{
                temp=new double[bands][st1.size()];
                for(int ii=0;ii<bands;ii++){
                    for(int jj=0;jj<st1.size();jj++){
                        temp[ii][jj]=X[ii][st1.get(jj)];
                    }
                }
            }

            //返回temp图像每个pixel的RX值
            RxDetector rxDetector= new RxDetector(temp);
            kr=rxDetector.run();

            //kr放入并从0开始重新标号，RX升序排列，同时返回该pixel在temp中的index
            Sort sort= new Sort(kr);
            d2=sort.run();

            //如果是第一份字典则创建
            if(Dictionary==null){
                Dictionary= new double[bands][P];
                for(int iii=0;iii<bands;iii++){
                    for(int jjj=0;jjj<P;jjj++){
                        //从temp中取出对应的id
                        Dictionary[iii][jjj]=temp[iii][d2[jjj]];
                    }
                }
            }else {
                //否则进行拼接生成新的
                double[][] preDic=new double[bands][Dictionary[0].length];
                for(int iii=0;iii<bands;iii++){
                    for(int jjj=0;jjj<Dictionary[0].length;jjj++){
                        preDic[iii][jjj]=Dictionary[iii][jjj];
                    }
                }

                Dictionary= new double[bands][preDic[0].length+P];
                for(int iii=0;iii<bands;iii++){
                    for(int jjj=0;jjj<preDic[0].length;jjj++){
                        Dictionary[iii][jjj]=preDic[iii][jjj];
                    }
                }
                for(int iii=0;iii<bands;iii++){
                    for(int jjj=0;jjj<P;jjj++){
                        Dictionary[iii][preDic[0].length+jjj]=temp[iii][d2[jjj]];
                    }
                }
            }
        }
        return Dictionary;
    }
}
