import java.io.IOException;

public class testData {
    public static void main(String[] args) throws IOException {
        Data data = new Data("img2D.mat","UGt.mat");
        double [][]img2D= data.getImg2D();
        System.out.println("img2D row="+img2D.length+" img2D col="+img2D[0].length);
        double [][]GT= data.getGT();
        System.out.println("GT row="+GT.length+" GT col="+GT[0].length);
    }
}
