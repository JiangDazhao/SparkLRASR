import java.text.SimpleDateFormat
import java.util.Date

import com.jxz.{AUC, DataFloat, DicConMR, HeadHdr, KmeansMR, LRASRMR, Repartition}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object testMR {
  def main(args: Array[String]): Unit = {
      val conf= new SparkConf()
        .setAppName("JavaParallelTest")
        .setMaster("local[*]")
        .set("spark.testing.memory", "2147480000")
      val spark=new SparkContext(conf)

      val header= new HeadHdr("Urban","./src/com.jxz.main/resources/")
      val broadcastHeader= spark.broadcast(header)
      val samples=header.getSamples
      val bands = header.getBands
      val parallelnum=header.getParallelnum
      val GTCol = header.getGTCol
      val GTRow = header.getGTRow
      val datatype = header.getDatatype
      val lambda = header.getLambda
      val beta = header.getBeta
      val K = header.getK
      val P = header.getP

      val data = new DataFloat("Urban_img.mat", "Urban_gt.mat")
      val  X = data.getImg2D
      val GT = data.getGT

      val array1= Array.ofDim[Float](bands,2000);
      for(i<- 0 until 2000){
          for(j<- 0 until bands){
              array1(j)(i)=X(j)(i);
          }
      }
      val array2= Array.ofDim[Float](bands,2000);
      for(i<- 2000 until 4000){
          for(j<- 0 until bands){
              array2(j)(i-2000)=X(j)(i);
          }
      }
      val array3= Array.ofDim[Float](bands,2000);
      for(i<- 4000 until 6000){
          for(j<- 0 until bands){
              array3(j)(i-4000)=X(j)(i);
          }
      }
      val array4= Array.ofDim[Float](bands,2000);
      for(i<- 6000 until 8000){
          for(j<- 0 until bands){
              array4(j)(i-6000)=X(j)(i);
          }
      }

      val img2DRDD: RDD[(Int, Array[Array[Float]])] =spark.parallelize(
          Array((0,array1),
              (2000,array2),
              (4000,array3),
              (6000,array4)),
          4).cache()

      //设置日期格式
      val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      System.out.println("start time:" + df.format(new Date))

      val kmeansMR = new KmeansMR(img2DRDD, broadcastHeader)
      kmeansMR.process()
      val kmeansCenter= kmeansMR.getCenter

      val broadcastCenter= spark.broadcast(kmeansCenter)
      val repartition= new Repartition(img2DRDD,broadcastHeader,broadcastCenter)
      repartition.process()
      val ClassPixelRDD=repartition.getClassPixelRDD
//      val PartitionInner=repartition.getPartitionInner

      val dicConMR = new DicConMR(ClassPixelRDD, broadcastHeader)
      dicConMR.process()
     // val partDicCollect=dicConMR.getpartDicCollect
      val fullDic=dicConMR.getFullDic

      val broadFullDic= spark.broadcast(fullDic)
      val lrasrmr = new LRASRMR(img2DRDD, broadcastHeader, broadFullDic)
      lrasrmr.process()
      val fullE: Array[Array[Double]] = lrasrmr.getFullE

      val t7 = System.currentTimeMillis
      val re = Array.ofDim[Double](GT.length, GT(0).length)
      for (i <- 0 until fullE(0).length) {
          var sum = 0.0
          for (j <- 0 until fullE.length) {
              sum += Math.pow(fullE(j)(i), 2)
          }
          re(i % GT.length)(i / GT.length) = Math.sqrt(sum)
      }

      val auc = new AUC(GT, re)
      val aucresult = auc.run
      System.out.println("AUC=" + aucresult)
      val t8 = System.currentTimeMillis
      System.out.println("AUC time:" + (t8 - t7) * 1.0 / 1000 + "s")
      System.out.println("AUC Finish:" + df.format(new Date))
  }
}
