package com.jxz

import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

class DicConMR(val ClassPixelRDD: RDD[(Int, Array[Float])],val headHdr: Broadcast[HeadHdr])extends Serializable {
  var partDicCollect: Array[(Int,Array[Array[Double]])]=_
  var fullDic:Array[Array[Double]]=_

  private[this] def processDicCon:Unit={
    partDicCollect =ClassPixelRDD.mapPartitions(pixels=>{
        var dicIterator=List[(Int,Array[Array[Double]])]()
        val (sizeiter,tolistiter)=pixels.duplicate
        val partitionSize=sizeiter.size
        val iterToList=tolistiter.toList

        if(partitionSize<headHdr.value.getP){
          val zero=Array.ofDim[Double](headHdr.value.getBands,0)
          dicIterator.::((0,zero)).iterator
        }else{
          val tempImg=Array.ofDim[Double](headHdr.value.getBands,partitionSize)
          var num=0
          for (iterId<-0 until(iterToList.length)){
            val classpixel=iterToList(iterId)
            val pixel=classpixel._2
            for(i<-0 until headHdr.value.getBands){
              tempImg(i)(num)=pixel(i)
            }
            num=num+1
          }

          val rxDetector = new RxDetector(tempImg)
          val kr = rxDetector.run

          val sort = new Sort(kr)
          val d2 = sort.run

          val subDic= Array.ofDim[Double](headHdr.value.getBands,headHdr.value.getP)
          for (iii <- 0 until headHdr.value.getBands) {
            for (jjj <- 0 until headHdr.value.getP) { //从temp中取出对应的id
              subDic(iii)(jjj) = tempImg(iii)(d2(jjj))
            }
          }
          dicIterator.::((headHdr.value.getP,subDic)).iterator
        }
      }
      ).collect()
  }

  private[this] def processFullDic:Unit={
    var dicNum=0
    for(i<-0 until  partDicCollect.length){
      val partSizeDic=partDicCollect(i)
      dicNum+=partSizeDic._1
    }
    fullDic= Array.ofDim[Double](headHdr.value.getBands,dicNum)
    var accumulateNum=0
    for(i<-0 until partDicCollect.length){
      val partSizeDic=partDicCollect(i)
      val partSize=partSizeDic._1
      val partDic=partSizeDic._2
      for(partId<-0 until(partSize)){
        for(band<-0 until(headHdr.value.getBands)){
          fullDic(band)(accumulateNum+partId)=partDic(band)(partId)
        }
      }
      accumulateNum+=partSize
    }
  }

  def process():Unit={
    processDicCon
    processFullDic
  }

  def getpartDicCollect=partDicCollect
  def getFullDic=fullDic

}
