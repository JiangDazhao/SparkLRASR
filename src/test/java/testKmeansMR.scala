import org.apache.spark.{SparkConf, SparkContext}

object testKmeansMR {
  def main(args: Array[String]): Unit = {
      val conf= new SparkConf()
        .setAppName("JavaParallelTest")
        .setMaster("local[*]")
        .set("spark.testing.memory", "2147480000")
      val spark=new SparkContext(conf)

      val header= new HeadHdr("LRASR","./src/main/resources/")
      val samples=header.getSamples
      val bands = header.getBands
      val GTCol = header.getGTCol
      val GTRow = header.getGTRow
      val datatype = header.getDatatype
      val lambda = header.getLambda
      val beta = header.getBeta
      val K = header.getK
      val P = header.getP

      val data = new Data("img2D.mat", "UGt.mat")
      val  X = data.getImg2D
      val GT = data.getGT

      val array1= Array.ofDim[Double](bands,2000);
      for(i<- 0 until 2000){
          for(j<- 0 until bands){
              array1(j)(i)=X(j)(i);
          }
      }
      val array2= Array.ofDim[Double](bands,2000);
      for(i<- 2000 until 4000){
          for(j<- 0 until bands){
              array2(j)(i)=X(j)(i);
          }
      }
      val array3= Array.ofDim[Double](bands,2000);
      for(i<- 4000 until 6000){
          for(j<- 0 until bands){
              array3(j)(i)=X(j)(i);
          }
      }
      val array4= Array.ofDim[Double](bands,2000);
      for(i<- 6000 until 8000){
          for(j<- 0 until bands){
              array4(j)(i)=X(j)(i);
          }
      }

      val blockedImg=spark.parallelize(
          Array((0,array1),
              (2000,array2),
              (4000,array3),
              (6000,array4)),
          4).cache()

      val kmeansMR= new KmeansMR(blockedImg,header)
  }
}
