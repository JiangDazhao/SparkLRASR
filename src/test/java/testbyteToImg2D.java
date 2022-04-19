import com.csvreader.CsvWriter;
import com.jxz.Tools;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

public class testbyteToImg2D {
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create("src/main/resources/img2D.bin"), conf);
        DataInputStream is = fs.open(new Path("src/com.jxz.main/resources/Urban_img.bin"));
        int size= is.available();
        byte[] data = new byte[size];
        for(int i=0;i<size;i++){
            data[i]= (byte) is.read();
        }
        float[][]result = Tools.byteToImg2D(data,4,162);

        CsvWriter csvWriter = new CsvWriter("./out/byteToImg2D.csv", ',', Charset.forName("UTF-8"));
        for(int i=0;i<result.length;i++){
            String[] onerow=new String[result[0].length];
            for(int j=0;j<result[0].length;j++){
                onerow[j]=String.valueOf(result[i][j]);
            }
            csvWriter.writeRecord(onerow);
        }
        csvWriter.close();
    }
}
