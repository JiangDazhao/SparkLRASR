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

    public double[][] run(){
        //构造的字典矩阵
        double[][]Dictionary=null;
        //所有pixel的clusterID
        int[] K1;

        bands= X.length;
        pixelNum=X[0].length;
        Kmeans kmeans= new Kmeans(X,K);
        K1=kmeans.run();
        for(int i=0;i<K;i++){
            //每簇pointID
            ArrayList<Integer> st1=new ArrayList<Integer>();
            //每簇构成的部分图像
            double[][]temp;
            //RxDetector的值
            double[]kr;
            //排序后的kr
            int[] d2;

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
            RxDetector rxDetector= new RxDetector(temp);
            kr=rxDetector.run();
            Sort sort= new Sort(kr);
            d2=sort.run();
//            System.out.println("d2 length"+d2.length);

            if(Dictionary==null){
                Dictionary= new double[bands][P];
                for(int iii=0;iii<bands;iii++){
                    for(int jjj=0;jjj<P;jjj++){
                        Dictionary[iii][jjj]=temp[iii][d2[jjj]];
                    }
                }
            }else {
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
