import com.jxz.Data;
import com.jxz.Kmeans;

import java.io.IOException;

public class testKmeans {
    public static void main(String[] args) throws IOException {
        int []K1;
        Data data = new Data("Urban_img.mat", "Urban_gt.mat");
        Kmeans kmeans=new Kmeans(data.img2D,15);
        K1= kmeans.run();
        System.out.println(kmeans.getIterTimes());
        for(int i:K1){
            System.out.print(i+" ");
        }

    }

}
