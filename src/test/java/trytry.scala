//import org.apache.spark.mllib.linalg.Vector
//import org.apache.spark.rdd.RDD
//import scala.collection.mutable.ArrayBuffer
//import breeze.linalg.norm
//import org.apache.spark.mllib.linalg.Vectors
//
////该case类保存了每个向量pixel本身与该向量的L2范数
//case class VectorInform(var Point:Vector,var norm2:Double)
////该case类保存一个向量pixel所对应的中心向量的id与该向量与中心向量之间的花费
//case class CenterInform(val center_id:Int,val cost:Double)
//class Kmeans(val data:RDD[Vector],val numClusters:Int,val MaxIterations:Int,val runs:Int = 1,
//             val threshold:Double=1e-4,val savepath:String="/home/hadoop/haha")extends Serializable{
//  def output(data:Array[Array[VectorInform]])
//  {
//    data.foreach {_.foreach {x=>x.Point.foreachActive((index,value)=>print(index+" "+value+"  "));println}}
//  }
//  //返回两向量的和向量
//  def add(p1:Vector,p2:Vector):Vector=
//  {
//    var p3=new Array[Double](p1.size)
//    for(i<- 0 until p1.size)
//    {
//      p3(i)=p1(i)+p2(i)
//    }
//    Vectors.dense(p3)
//  }
//  //获取初始中心点
//  def InitCenterPoint(data:RDD[VectorInform]):Array[Array[VectorInform]]={
//    var sample=data.takeSample(false, numClusters*runs)
//    Array.tabulate(runs)(r=>sample.slice(numClusters*r, numClusters*(r+1)))
//  }
//  //查找该点属于第k个并行模块的哪个类
//  def FindClostCenter(center:Array[VectorInform],point:VectorInform):CenterInform=
//  {
//    var bestdistance=Double.PositiveInfinity
//    var id=0
//    for(i <- 0 until center.length)
//    {
//      var dist=Vectors.sqdist(center(i).Point, point.Point)
//      if(dist<bestdistance)
//      {
//        bestdistance=dist
//        id=i
//      }
//    }
//    CenterInform(id,bestdistance)
//  }
//  def plus(a:(Vector,Int),b:(Vector,Int)):(Vector,Int)={
//    (add(a._1, b._1),a._2+b._2)
//  }
//  def divide(sum:Vector,n:Int):Vector={
//    val m=new Array[Double](sum.size)
//    for(i<- 0 until sum.size)
//      m(i)=sum(i)/n.toDouble
//    Vectors.dense(m)
//  }
//  //算法核心，返回最优的中心向量及开销
//  def runAlgorithm(data:RDD[VectorInform]):(Array[VectorInform],Double)=
//  {
//    var sc=data.sparkContext
//    var center=InitCenterPoint(data)
//    var runactive=Array.fill(runs)(true)
//    var cost=Array.fill(runs)(0.0)
//    var activeRuns=new ArrayBuffer[Int]++(0 until runs)//记录还在活跃的计算，因为有的计算可能已经收敛停止了
//  var k=0
//    while(k<MaxIterations&&(!activeRuns.isEmpty))
//    {
//      k+=1
//      var cost2=Array.fill(runs)(0).map {_=>sc.accumulator(0.0)}//累加器
//    var activecenter=activeRuns.map { x => center(x)}//这步很重要，每次迭代的时候去除那些已经收敛的计算
//    var bestcenter=sc.broadcast(center)//广播，把中心的数据传输到每个分区，接下来就马上体现了并行计算的思想
//
//      //每个mapPartition分区进行预先计算，然后将所有mapPartition分区累加起来
//      var result=data.mapPartitions
//      {points=>
//        /*
//         * 获取必要的参数
//         */
//
//        val thiscenter=bestcenter.value //每个分区中获取中心点
//        val runs= thiscenter.length//并行计算数量
//      //n个中心点，n个簇
//      val n=thiscenter(0).length
//        val dims=thiscenter(0)(0).Point.size//中心点的维度
//      /*
//       * 获取该分区类，每个并行计算中的每个类的向量的和与向量的个数
//       */
//      var sum=Array.fill(runs,n)(Vectors.zeros(dims))//保存每个并行度下每个类的向量的和
//      var count=Array.fill(runs, n)(0)//保存每个并行度下每个类中向量的个数
//        points.foreach
//        { point =>
//          //并行runs计算
//          for(i<- 0 until runs)
//          {
//            val vp=FindClostCenter(thiscenter(i), point)
//            count(i)(vp.center_id)+=1
//            sum(i)(vp.center_id)=add(sum(i)(vp.center_id), point.Point)
//            cost2(i)+=vp.cost
//          }
//        }
//        //最后再弄答案
//        var result = List[Any]()
//
//        result.::((sum,count)).iterator
//        // a:(Vector,Int),b:(Vector,Int)
//        //def reduceByKey(func: (V, V) => V): RDD[(K, V)]
//        // (i,j)->(sum(i)(j),count(i)(j))
//      }.reduce(
//        (a: Any, b)=>{
//
//        }
//      )
//
//      /*
//       *更新中心点并判断是否满足停止的条件
//       */
//      for((run,i)<-activeRuns.zipWithIndex)//注意理解这里，有的并行已经停止，run的值与i不一定相等
//      {
//        var change=false
//        for(j<- 0 until numClusters)
//        {
//          val (sum,n)=result(i,j)//第i个并行计算中第j个类的向量和与向量总数
//        var newc=divide(sum, n)s
//          if(Vectors.sqdist(newc, center(run)(j).Point)>threshold)
//            change=true
//          //更新center
//          center(run)(j).Point=newc
//        }
//        if(!change)
//        {
//          runactive(run)=false
//          cost(run)=cost2(run).value
//          println("Run "+run+" has stopped")
//        }
//      }
//      activeRuns=activeRuns.filter {runactive(_)}
//    }
//    /*
//     * 选择runs个并行中cost最小的中心点
//     */
//    var (mincost,bestrun)=cost.zipWithIndex.min
//    (center(bestrun),mincost)
//  }
//
//  def run()
//  {
//    var norm2=data.map {Vectors.norm(_, 2)}
//    var zipdata=data.zip(norm2).map(f=>new VectorInform(f._1,f._2))
//    var center=InitCenterPoint(zipdata)
//    var (endcenter,cost)=runAlgorithm(zipdata)
//    println("-------------------------------")
//    endcenter.foreach {x=>x.Point.foreachActive((a,b)=>print(b+" "));println}
//    println("最小花费为："+cost)
//  }
//}
//
//package zzl
//
//import org.apache.spark.SparkContext
//import org.apache.spark.SparkConf
//import org.apache.spark.mllib.linalg.{Vectors,Vector}
//object Main {
//
//  def main(args: Array[String]): Unit = {
//    var sc=new SparkContext(new SparkConf().setAppName("zzl").setMaster("local"))
//    var data=sc.textFile("/home/hadoop/xixi", 2).map { s =>Vectors.dense(s.split(" ").map {_.toDouble})}
//
//    var k=new Kmeans(data,2,40,20)
//    k.run()
//
//
//  }
//}