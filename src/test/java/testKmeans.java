import java.io.IOException;

public class testKmeans {
    public static void main(String[] args) throws IOException {
        int []K1;
        Data data = new Data("img2D.mat", "GtUrban.mat");
        Kmeans kmeans=new Kmeans(data.img2D,15);
        K1= kmeans.run();
        System.out.println(kmeans.getIterTimes());
        for(int i:K1){
            System.out.print(i+" ");
        }

    }

}
