import java.util.List;
import java.util.Random;

public class Tools {

    public static float[][] byteToImg2D(byte[]data,int datatype,int bands){
        int bytelen=data.length;
        int imgIdxLen=bytelen/(datatype*bands);
        float[][]result=new float[bands][imgIdxLen];
        int n=0;
        for(int j=0;j<imgIdxLen;j++){
            for(int i=0;i<bands;i++){
                int accum = 0;
                accum = accum|(data[n++] & 0xff) << 0;
                accum = accum|(data[n++] & 0xff) << 8;
                accum = accum|(data[n++] & 0xff) << 16;
                accum = accum|(data[n++] & 0xff) << 24;
                result[i][j]=Float.intBitsToFloat(accum);
            }
        }
        return result;
    }



    /**
     * 随机指定范围内N个不重复的数
     * 在初始化的无重复待选数组中随机产生一个数放入结果中，
     * 将待选数组被随机到的数，用待选数组(len-1)下标对应的数替换
     * 然后从len-2里随机产生下一个随机数，如此类推
     * @param max 指定范围最大值
     * @param min 指定范围最小值
     * @param n 随机数个数
     * @return int[] 随机数结果集
     */
    public static int[] randomArray(int min,int max,int n){
        int len = max-min+1;

        if(max < min || n > len){
            return null;
        }

        //初始化给定范围的待选数组
        int[] source = new int[len];
        for (int i = min; i < min+len; i++){
            source[i-min] = i;
        }

        int[] result = new int[n];
        Random rd = new Random();
        int index = 0;
        for (int i = 0; i < result.length; i++) {
            //待选数组0到(len-2)随机一个下标
            index = Math.abs(rd.nextInt() % len--);
            //将随机到的数放入结果集
            result[i] = source[index];
            //将待选数组中被随机到的数，用待选数组(len-1)下标对应的数替换
            source[index] = source[len];
        }
        return result;
    }

    public static double getEuclideanDis(float[] p1,float[] p2){
        double disRes = 0;
        for (int i = 0; i < p1.length; i++) {
            disRes += Math.pow(p1[i] - p2[i], 2);
        }
        return Math.sqrt(disRes);
    }

    public static float[] addPixel(float[] p1,float[] p2){
        float[] result= new float[p1.length];
        for(int i=0;i<p1.length;i++){
            result[i]=p1[i]+p2[i];
        }
        return result;
    }

    public static float[] dividePixelNum(float[] pixel, int num){
        float[] result=new float[pixel.length];
        for(int i=0;i<pixel.length;i++){
            result[i]=pixel[i]/num;
        }
        return result;
    }

}
