package com.jxz;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URI;

public class GtData {
    float[][] GT;
    public GtData(String filepath, HeadHdr headHdr) throws IOException {
        this.readData(filepath,headHdr);
    }

    public void readData(String filepath, HeadHdr headHdr) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(URI.create(filepath), conf);
        DataInputStream is = fs.open(new Path(filepath));

        int size= is.available();
        byte[] data = new byte[size];
        for(int i=0;i<size;i++){
            data[i]= (byte) is.read();
        }

        GT=new float[headHdr.getGTRow()][headHdr.getGTCol()];
        int n=0;
        for(int j=0;j<headHdr.getGTCol();j++){
            for(int i=0;i<headHdr.getGTRow();i++){
                int accum = 0;
                accum = accum|(data[n++] & 0xff) << 0;
                accum = accum|(data[n++] & 0xff) << 8;
                accum = accum|(data[n++] & 0xff) << 16;
                accum = accum|(data[n++] & 0xff) << 24;
                GT[i][j]=Float.intBitsToFloat(accum);
            }
        }
    }

    public float[][] getGT() {
        return GT;
    }
}
