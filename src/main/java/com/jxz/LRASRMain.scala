package com.jxz

import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object LRASRMain {
  def main(args: Array[String]): Unit = {
    val jobname=args(0)   //jobname and the filename
    val hdfspath=args(1)  //the hadoop directory of all the data

    val  conf= new SparkConf().setAppName(jobname)
//      .setMaster("local[2]")
//      .set("spark.testing.memory", "2147480000")
//      .set("spark.driver.memory", "2147480000")
//      .set("spark.driver.maxResultSize","2147480000")
    val spark= new SparkContext(conf)

    //set data format
    val df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    println("start time:" + df.format(new Date))

    // initialize header info
    val header= new HeadHdr(jobname, hdfspath)
    val broadcastHeader= spark.broadcast(header)
    val bands = header.getBands
    val datatype = header.getDatatype

    // initialize gt
    val gtDataPath=hdfspath+jobname+ "_gt.bin"
    val gtData=new GtData(gtDataPath,header)
    val GT=gtData.getGT

    //partition the byteImg2DPath
    val byteImg2DPath=hdfspath+jobname+ "_img.bin"
    val byteImg2DRDD: RDD[(Integer, Array[Byte])] =spark.newAPIHadoopFile(byteImg2DPath,classOf[DataInputFormat],classOf[Integer],classOf[Array[Byte]])
    //    val byteImg2DRDDCollect=byteImg2DRDD.collect()

    //record time t1
    val t1 = System.currentTimeMillis

    // byteImg2DRDD to img2DRDD
    val img2DRDD: RDD[(Int, Array[Array[Float]])]
    =byteImg2DRDD.map(pair=>{
      val blockImg2DId=pair._1/(datatype*bands)
      val blockImg2D=Tools.byteToImg2D(pair._2,datatype,bands)
      (blockImg2DId,blockImg2D)
    }
    ).cache()
    println("Read data finish.....")

    val kmeansMR = new KmeansMR(img2DRDD, broadcastHeader)
    kmeansMR.process()
    val kmeansCenter= kmeansMR.getCenter
    println("KmeansMR finish.....")

    val broadcastCenter= spark.broadcast(kmeansCenter)
    val repartition= new Repartition(img2DRDD,broadcastHeader,broadcastCenter)
    repartition.process()
    val ClassPixelRDD: RDD[(Int, Array[Float])] =repartition.getClassPixelRDD

    val dicConMR = new DicConMR(ClassPixelRDD, broadcastHeader)
    dicConMR.process()
    val fullDic=dicConMR.getFullDic
    println("dicConMR finish.....")

    val broadFullDic= spark.broadcast(fullDic)
    val lrasrmr = new LRASRMR(img2DRDD, broadcastHeader, broadFullDic)
    lrasrmr.process()
    val fullE: Array[Array[Double]] = lrasrmr.getFullE

    //record time t2
    val t2 = System.currentTimeMillis
    System.out.println("MapReduce time:" + (t2 - t1) * 1.0 / 1000 + "s")

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
    System.out.println("AUC Finish:" + df.format(new Date))

  }
}
