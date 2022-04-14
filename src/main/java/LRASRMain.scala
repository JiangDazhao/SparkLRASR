import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object LRASRMain {
  def main(args: Array[String]): Unit = {
    val exetype= args(0)
    val jobname=args(1)   //jobname and the filename
    val filepath=args(2)  //the hadoop directory of all the data

    val  conf= new SparkConf().setMaster(exetype).setAppName(jobname).set("spark.testing.memory", "2147480000")
    val spark= new SparkContext(conf)

    //set data format
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    println("start time:" + df.format(new Date))

    // initialize header info
    val header= new HeadHdr(jobname, filepath)
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

    val t1 = System.currentTimeMillis
    println("readdata start time:"+df.format(new Date))

    //partition the byteImg2DPath
    val byteImg2DPath=filepath+"img2D.bin"
    val byteImg2DRDD: RDD[(Integer, Array[Byte])] =spark.newAPIHadoopFile(byteImg2DPath,classOf[DataInputFormat],classOf[Integer],classOf[Array[Byte]])

    // byteImg2DRDD to img2DRDD
    val img2DRDD: RDD[(Int, Array[Array[Float]])]
    =byteImg2DRDD.map(pair=>{
      val blockImg2DId=pair._1/(datatype*bands)
      val blockImg2D=Tools.byteToImg2D(pair._2,datatype,bands)
      (blockImg2DId,blockImg2D)
    }
    ).cache()

    val t2 = System.currentTimeMillis
    println("readdata end time:"+df.format(new Date))
    println("readdata time:" + (t2 - t1) * 1.0 / 1000 + "s")

    val kmeansMR = new KmeansMR(img2DRDD, broadcastHeader)
    kmeansMR.process()
    val kmeansCenter= kmeansMR.getCenter

    val broadcastCenter= spark.broadcast(kmeansCenter)
    val repartition= new Repartition(img2DRDD,broadcastHeader,broadcastCenter)
    repartition.process()
    val ClassPixelRDD=repartition.getClassPixelRDD


  }
}
