import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.net.URI;

public class HeadHdr {
    //从hdr头文件中读出来的参数
    private String name;
    private String path;
    private int samples;
    private int GTRow;
    private int GTCol;
    private int bands;
    private short datatype;
    private double lambda;
    private double beta;
    private int K;
    private int P;

    //默认构造函数,标识文件名和路径,以及对头文件参数进行读取
    public HeadHdr(String filename, String path) throws IOException {
        //文件分割,分出文件名
        this.name = filename + "_hdr.hdr";//头文件.hdr组装

        this.path = path;// 文件所在目录路径

        this.ReadInformation(); //重写读取函数
    }

    public void ReadInformation() throws IOException {
        /*客户端读取HDFS数据*/
        //返回实例文件系统HDFS的fs
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(this.path + this.name), conf);
        //有了fs实例后，调用open来获取文件输入流din
        DataInputStream din = fs.open(new Path(this.path + this.name));
        //客户端对输入流调用read方法
        BufferedReader read = new BufferedReader(new InputStreamReader(din));


        String strline;
        try {
            while((strline = read.readLine()) != null) {
                int pos = strline.indexOf(61);
                if (pos >= 0) {
                    //属性
                    String strleft = strline.substring(0, pos).trim();
                    //值
                    String strright = strline.substring(pos + 1, strline.length()).trim();
                    if (strleft.equals("samples")) {
                        this.samples = Integer.parseInt(strright);
                    } else if (strleft.equals("GTRow")) {
                        this.GTRow = Integer.parseInt(strright);
                    } else if (strleft.equals("GTCol")) {
                        this.GTCol = Integer.parseInt(strright);
                    }else if (strleft.equals("bands")) {
                        this.bands = Integer.parseInt(strright);
                    } else if (strleft.equals("data type")) {
                        this.datatype = Short.parseShort(strright);
                    }else if (strleft.equals("lambda")) {
                        this.lambda = Double.parseDouble(strright);
                    }else if (strleft.equals("beta")) {
                        this.beta = Double.parseDouble(strright);
                    }else if (strleft.equals("K")) {
                        this.K = Integer.parseInt(strright);
                    }else if (strleft.equals("P")) {
                        this.P = Integer.parseInt(strright);
                    }
                }
            }
        } catch (FileNotFoundException var9) {
            System.out.println("failed to read");
            var9.printStackTrace();
        }

    }

    public int getSamples() {
        return samples;
    }

    public int getGTRow() {
        return GTRow;
    }

    public int getGTCol() {
        return GTCol;
    }

    public int getBands() {
        return bands;
    }

    public short getDatatype() {
        return datatype;
    }

    public double getLambda() {
        return lambda;
    }

    public double getBeta() {
        return beta;
    }

    public double getK() {
        return K;
    }

    public double getP() {
        return P;
    }
}
