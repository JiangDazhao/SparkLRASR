package com.jxz

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.{Partitioner, SparkContext}

class Repartition(val blockImgTotal:RDD[(Int,Array[Array[Float]])],val headHdr: Broadcast[HeadHdr],val center:Broadcast[Array[Array[Float]]]) extends Serializable {
  var repartitionRDD: RDD[(Int, Array[Float])] = _
  @transient
  var sparkContext: SparkContext = blockImgTotal.sparkContext
  var ClassPixelRDD:RDD[(Int, Array[Float])] = _

//  var CollectedClassPixelRDD:Array[(Int, Array[Float])]=_
  var partitionInner: Any =_

  private[this] def processRepartitionRDD = {
    ClassPixelRDD = blockImgTotal.flatMap(f = blockImg => {
      val offset: Int = blockImg._1
      var blockImgData: Array[Array[Float]] = blockImg._2
      var pixelClass = Array.ofDim[Int]((blockImgData(0).length))

      for (imgId <- 0 until (blockImgData(0).length)) {
        val pixel = Array.ofDim[Float](headHdr.value.getBands)
        for (i <- 0 until (headHdr.value.getBands)) {
          pixel(i) = blockImgData(i)(imgId)
        }
        var minDis = Double.PositiveInfinity
        var pixelClusterId = 0
        for (clusterId <- 0 until (headHdr.value.getK)) {
          val clusterCenter = center.value(clusterId)
          val tempDis = Math.min(minDis, Tools.getEuclideanDis(clusterCenter, pixel))
          if (tempDis != minDis) {
            minDis = tempDis
            pixelClusterId = clusterId
          }
        }
        pixelClass(imgId) = pixelClusterId
      }

      val transposeImg=blockImgData.transpose
      for(resultid<-0 until(pixelClass.length))
        yield {
          (pixelClass(resultid),transposeImg(resultid))
        }
    }
    ).partitionBy(
      new ClassPartitioner(headHdr.value.getK)
    ).cache()


//    partitionInner=ClassPixelRDD.mapPartitionsWithIndex{
//      (partid,iter)=>{
//        var part_map = scala.collection.mutable.Map[String,List[Any]]()
//        var part_name = "part_" + partid
//        part_map(part_name) = List[Any]()
//        while(iter.hasNext){
//          part_map(part_name) :+= iter.next()//:+= 列表尾部追加元素
//        }
//        part_map.iterator
//      }
//    }.collect

  }



  def process():Unit={
    processRepartitionRDD
    //CollectedClassPixelRDD=ClassPixelRDD.collect
  }

  def getClassPixelRDD=ClassPixelRDD
  //def getCollectedClassPixelRDD =CollectedClassPixelRDD
  def getPartitionInner=partitionInner


  class ClassPartitioner(numParts: Int) extends Partitioner {
    // 总分区数
    override def numPartitions: Int = numParts

    // 这里的key就是rdd map中的key，返回的是该key-row对应的partition序号（0 ～ numParts - 1）
    override def getPartition(key: Any): Int = {
      key.asInstanceOf[Int]
    }

    override def hashCode(): Int = super.hashCode()

    override def equals(obj: scala.Any): Boolean = super.equals(obj)
  }
}



