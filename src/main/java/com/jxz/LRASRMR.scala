package com.jxz

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

class LRASRMR(val blockImgTotal:RDD[(Int,Array[Array[Float]])],val headHdr: Broadcast[HeadHdr],
              val Dic:Broadcast[Array[Array[Double]]])extends Serializable {
  var partECollect:Array[(Int,Array[Array[Double]])]=_
  var fullE:Array[Array[Double]]=_

  private[this] def processE={
    partECollect=blockImgTotal.map(blockImg => {
      val offset= blockImg._1
      val blockImgData=blockImg._2
      val blockDic=Dic.value
      val blockImgDataDouble=Array.ofDim[Double](headHdr.value.getBands,blockImgData(0).length)
      for(i<-0 until(blockImgDataDouble(0).length)){
        for(j<-0 until(headHdr.value.getBands)){
          blockImgDataDouble(j)(i)=blockImgData(j)(i).toDouble
        }
      }

      val ladmap_lrasr = new LADMAP_LRASR(blockImgDataDouble, blockDic, headHdr.value.getLambda, headHdr.value.getBeta)
      val SE = ladmap_lrasr.run
      (offset,SE._2)
    }
    ).collect()

    fullE=Array.ofDim[Double](headHdr.value.getBands,headHdr.value.getSamples)
    for(i<-0 until(partECollect.length)){
      val partEoffset=partECollect(i)._1
      val partEValue=partECollect(i)._2
      for(pixelId<-0 until(partEValue(0).length)){
        for(band<-0 until(headHdr.value.getBands)){
          fullE(band)(partEoffset+pixelId)=partEValue(band)(pixelId)
        }
      }
    }

  }

  def process(): Unit ={
    processE
  }

  def getpartECollect=partECollect
  def getFullE=fullE
}
