import com.jxz.Data;
import com.jxz.DicCon;

import java.io.IOException;

public class testDicCon {
    public static void main(String[] args) throws IOException {
        int K=15;
        int P=20;
        Data data = new Data("Urban_img.mat", "Urban_gt.mat");
        double[][] res;
        DicCon dicCon= new DicCon(data.img2D,K,P);
        res=dicCon.run();
        System.out.println(res.length);
        System.out.println(res[0].length);
        for(int i=0;i<res.length;i++){
            for(int j=0;j<res[0].length;j++){
                System.out.print(res[i][j]+" ");
            }
            System.out.println();
        }
    }
}
