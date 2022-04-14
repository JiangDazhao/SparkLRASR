import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object testScala {
  def main(args: Array[String]): Unit = {
    var myMatrix = Array.ofDim[Int](3, 3)
    var n = 0
    // 创建矩阵
    for (i <- 0 to 2) {
      for ( j <- 0 to 2) {
        n=n+1;
        myMatrix(i)(j) =n ;
      }
    }
//    val a= myMatrix(0)
//    for(i<-0 to 2) print(" " + a(i));

//    val changeRow= Array(7,7,7)
//    myMatrix(2)=changeRow;
//    myMatrix=myMatrix.transpose;
//    for (i <- 0 to 2) {
//      for ( j <- 0 to 2) {
//        print(" "+myMatrix(i)(j))
//      }
//      println()
//    }



  }
}
