import org.ujmp.core.Matrix;
import org.ujmp.jmatio.ImportMatrixMAT;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Data {
    int rows;
    int cols;
    double [][]img2D;
    public Data(String img2DPath) throws IOException {
        String dataPathName="./src/main/resources/"+img2DPath;

        ImportMatrixMAT testimg = new ImportMatrixMAT();
        File imgfile  = new File(dataPathName);
        Matrix Matriximg2D = testimg.fromFile(imgfile);
        long[] img_dimentions=Matriximg2D.getSize();
        this.rows= (int) img_dimentions[0];
        this.cols= (int) img_dimentions[1];
//        System.out.println(img_dimentions[0]);
//        System.out.println(img_dimentions[1]);
        this.img2D= new double[rows][cols];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                this.img2D[i][j]=Matriximg2D.getAsDouble(i,j);
            }
        }

    }

    public static void main(String[] args) throws IOException {
        Data data = new Data("img2D.mat");
    }
}
