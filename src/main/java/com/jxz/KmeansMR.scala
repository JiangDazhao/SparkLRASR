package com.jxz

import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer

class KmeansMR(val blockImgTotal:RDD[(Int,Array[Array[Float]])],val headHdr: Broadcast[HeadHdr]) extends Serializable{
  var initIndex:Array[Int]=_
  var broadcastInitIndex: Broadcast[Array[Int]] = _
  @transient
  var sparkContext: SparkContext=blockImgTotal.sparkContext
  var initResultCollect: Array[ListBuffer[Array[Float]]]=_
  var center: Array[Array[Float]] =_
  var classSumCount: (Array[Array[Float]], Array[Int]) = _
  val iterMaxTimes:Int= 10000
  val disDiff:Float=0.01f
  var iterRunTimes:Int=_
  var ifNeedIter:Boolean=true
  var broadcastCenter:Broadcast[Array[Array[Float]]]=_

  initIndex=Tools.randomArray(0,headHdr.value.getSamples-1,headHdr.value.getK)
  broadcastInitIndex= sparkContext.broadcast(initIndex)

  private[this] def getInitCenter:Unit={
    initResultCollect =blockImgTotal.map(blockImg => {
      val offset= blockImg._1
      val blockImgData=blockImg._2
      val innerGap=blockImgData(0).length
      //      println("innerGap="+innerGap)
      var blockResult= new ListBuffer[Array[Float]]
      val broadcastInitIndexLen=broadcastInitIndex.value.length
      for(i<-0 until broadcastInitIndexLen){
        val selectIndex=broadcastInitIndex.value(i)
        if(selectIndex>=offset&&selectIndex<offset+innerGap){
          val bands=headHdr.value.getBands
          val selectedPixel= Array.ofDim[Float](bands)
          val inBlockIndex=selectIndex-offset
          for(j<-0 until(bands)){
            //            println("selectIndex="+selectIndex+" offset"+offset)
            selectedPixel(j)=blockImgData(j)(inBlockIndex)
          }
          blockResult+=selectedPixel
        }
      }
      blockResult
    }).collect()

    center=Array.ofDim[Float](headHdr.value.getK,headHdr.value.getBands)
    var num = 0
    for(i<-0 until initResultCollect.length){
      val buffer = initResultCollect(i)
      for(j<-0 until(buffer.length)){
        val pixel=buffer(j)
        center(num)=pixel
        num=num+1
      }
    }
  }

  private[this] def getFinalCenter:Unit={
    iterRunTimes=0
    while (iterRunTimes<iterMaxTimes&&ifNeedIter){
      iterRunTimes+=1
      broadcastCenter=sparkContext.broadcast(center)

      classSumCount=blockImgTotal.map(blockImg => {
        val offset = blockImg._1
        val blockImgData = blockImg._2

        var sum = Array.ofDim[Float](headHdr.value.getK, headHdr.value.getBands)
        var count = Array.ofDim[Int](headHdr.value.getK)

        for (imgId <- 0 until (blockImgData(0).length)) {
          val pixel = Array.ofDim[Float](headHdr.value.getBands)
          for (i <- 0 until (headHdr.value.getBands)) {
            pixel(i) = blockImgData(i)(imgId)
          }

          var minDis=Double.PositiveInfinity
          var pixelClusterId=0
          for(clusterId<-0 until(headHdr.value.getK)){
            val clusterCenter=center(clusterId)
            val tempDis=Math.min(minDis,Tools.getEuclideanDis(clusterCenter,pixel))
            if(tempDis!=minDis){
              minDis=tempDis
              pixelClusterId=clusterId
            }
          }
          count(pixelClusterId)+=1
          sum(pixelClusterId)=Tools.addPixel(sum(pixelClusterId),pixel)
        }
        (sum,count)
      }
      ).reduce((left,right)=>{
        val leftSum=left._1
        val leftCount=left._2
        val rightSum=right._1
        val rightCount=right._2
        var resultSum = Array.ofDim[Float](headHdr.value.getK, headHdr.value.getBands)
        var resultCount = Array.ofDim[Int](headHdr.value.getK)
        for(k<-0 until leftCount.length){
          resultSum(k)=Tools.addPixel(leftSum(k),rightSum(k))
          resultCount(k)=leftCount(k)+rightCount(k)
        }
        (resultSum,resultCount)
      })

      ifNeedIter=false
      var classSumCount_Sum = classSumCount._1
      var classSumCount_Count = classSumCount._2
      for(clusterId<-0 until(headHdr.value.getK)){
        val newPoint=Tools.dividePixelNum(classSumCount_Sum(clusterId),classSumCount_Count(clusterId))
        val diff= Tools.getEuclideanDis(newPoint,center(clusterId))
        if(diff>disDiff){
          ifNeedIter=true
        }
        center(clusterId)=newPoint
        //println("iterRunTimes= "+iterRunTimes+" diff= "+diff)
      }
    }
  }

  def process():Unit={
    getInitCenter
    getFinalCenter
  }

  def getInitResultCollect=initResultCollect
  def getCenter=center
}
