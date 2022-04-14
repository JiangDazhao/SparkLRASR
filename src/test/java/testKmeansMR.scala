import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object testKmeansMR {
  def main(args: Array[String]): Unit = {
      val conf= new SparkConf()
        .setAppName("JavaParallelTest")
        .setMaster("local[*]")
        .set("spark.testing.memory", "2147480000")
      val spark=new SparkContext(conf)

      val header= new HeadHdr("LRASR","./src/main/resources/")
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

      val data = new DataFloat("img2D.mat", "GtUrban.mat")
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

      val total: Array[Array[Float]] = Array.ofDim[Float](bands,8000);
      for(i<- 0 until 8000){
          for(j<- 0 until bands){
              total(j)(i)=X(j)(i);
          }
      }

      val img2DRDD_img: RDD[Array[Float]] =spark.parallelize(
          total,4
      )
      img2DRDD_img.map(points=>{

      })

      val kmeansMR = new KmeansMR(img2DRDD, broadcastHeader)
      kmeansMR.process()
      val kmeansCenter= kmeansMR.getCenter

      val broadcastCenter= spark.broadcast(kmeansCenter)
      val repartition= new Repartition(img2DRDD,broadcastHeader,broadcastCenter)
      repartition.process()
      val ClassPixelRDD=repartition.getClassPixelRDD

  }
}
