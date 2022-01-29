import java.io.IOException;
import java.util.Set;

public class main {
    public static void main(String[] args) throws IOException {
        Data data = new Data("img2D.mat");
        Kmeans kmeans=new Kmeans(data.img2D,15);
        Set<Kmeans.Cluster> clusterSet= kmeans.run();
        System.out.println("迭代次数为"+kmeans.getIterTimes());
        for(Kmeans.Cluster cluster:clusterSet){
            System.out.println(cluster);
        }
    }
}
