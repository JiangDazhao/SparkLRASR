import org.ujmp.core.Matrix;
import org.ujmp.jmatio.ImportMatrixMAT;

import java.io.File;
import java.io.IOException;


public class DataFloat {
    int imgRows;
    int imgCols;
    int GTRows;
    int GTCols;

    float [][]img2D;
    float [][]GT;

    public DataFloat(String img2DPath, String GTPath) throws IOException {
        String dataPathName="./src/main/resources/"+img2DPath;
        String GTPathName="./src/main/resources/"+GTPath;

        ImportMatrixMAT testimg = new ImportMatrixMAT();

        File imgfile  = new File(dataPathName);
        Matrix Matriximg2D = ImportMatrixMAT.fromFile(imgfile);
        long[] img_dimentions=Matriximg2D.getSize();
        this.imgRows= (int) img_dimentions[0];
        this.imgCols= (int) img_dimentions[1];
        this.img2D= new float[imgRows][imgCols];
        for(int i=0;i<imgRows;i++){
            for(int j=0;j<imgCols;j++){
                this.img2D[i][j]=Matriximg2D.getAsFloat(i,j);
            }
        }

        File GTfile  = new File(GTPathName);
        Matrix MatrixGT = ImportMatrixMAT.fromFile(GTfile);
        long[] GT_dimentions=MatrixGT.getSize();
        this.GTRows= (int) GT_dimentions[0];
        this.GTCols= (int) GT_dimentions[1];
        this.GT= new float[GTRows][GTCols];
        for(int i=0;i<GTRows;i++){
            for(int j=0;j<GTCols;j++){
                this.GT[i][j]=MatrixGT.getAsFloat(i,j);
            }
        }
    }

    public float[][] getImg2D() {
        return img2D;
    }

    public float[][] getGT() {
        return GT;
    }
}
