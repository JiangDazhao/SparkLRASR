import java.util.*;

public class Kmeans {
    private double[][] X;//原始输入数据
    private int K; //簇的个数
    private int iterMaxTimes=10000; //单次迭代最大运行次数
    private int iterRunTimes=0; //单次迭代实际运行次数
    private double disDiff=0.01; //单次迭代终止条件，两次运行中类中心的距离差
    private static List<Point> pointList= null; //用于存原始数据构成的点集
    private DistanceCompute disC= new DistanceCompute();
    private int bands;
    private int pixelNum;

    public Kmeans(double[][] x, int k) {
        this.X = x;
        this.K = k;
    }

    public class Point{
        public int pointId; //坐标序号
        public int clusterId; //标识所属类
        public double dist; //标识和所属类中心的距离
        public double[] localArray; //坐标

        public Point(int pointId, int clusterId, double dist, double[] localArray) {
            this.pointId = pointId;
            this.clusterId = clusterId;
            this.dist = dist;
            this.localArray = localArray;
        }
        public Point(double [] localArray){
            this.pointId=-1;
            this.localArray=localArray;
        }
        //用于之后的随机化重复判断
        @Override
        public boolean equals(Object obj){
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            Point point = (Point) obj;
            if (point.localArray.length != localArray.length) {
                return false;
            }

            for (int i = 0; i < localArray.length; i++) {
                if (Double.compare(point.localArray[i], localArray[i]) != 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            String result= "pointId="+pointId+" clusterID="+clusterId+" dist="+dist;
            return result;
        }
    }

    public class Cluster{
        public int clusterId;
        public Point center;
        public List<Point> members=new ArrayList<Point>();

        public Cluster(int clusterId, Point center, List<Point> members) {
            this.clusterId = clusterId;
            this.center = center;
            this.members = members;
        }

        public void addPoint(Point newPoint){
            if(members!=null){
                if(!members.contains(newPoint)){
                    members.add(newPoint);
                }
            }
        }

        @Override
        public String toString() {
            String toString ="Cluster_id=" + this.clusterId;
            if(members!=null){
                for (Point point : members) {
                    toString+="\n"+point.toString();
                }
            }
            return toString+"\n";
        }
    }
    public class DistanceCompute{
        public double getEuclideanDis(Point p1, Point p2){
            double disRes = 0;
            double[] p1_localArray = p1.localArray;
            double[] p2_localArray = p2.localArray;
            for (int i = 0; i < p1_localArray.length; i++) {
                disRes += Math.pow(p1_localArray[i] - p2_localArray[i], 2);
            }
            return Math.sqrt(disRes);
        }
    }

    /*
    初始化数据集，把数组转化为Point类型
     */
    public void initData(){
        pointList= new ArrayList<Point>();
        int i,k;
        for(i=0,k=pixelNum;i<k;i++){
            double[] pixel=new double[bands];
            for(int j=0;j<bands;j++){
                pixel[j]=X[j][i];
            }
            pointList.add(new Point(i,-1,-1,pixel));
        }
    //    System.out.println("i="+i);
    }

    /*
    初始化簇中心
     */
    public Set<Cluster> initClusterCenter(){
        Set<Cluster>clusterSet= new HashSet<Cluster>();
        Random random= new Random();
        for(int id=0;id<K;){
            Point point = pointList.get(random.nextInt(pointList.size()));
            //用于标记是否选择过该数据
            boolean flag= true;
            for(Cluster cluster:clusterSet){
                if(cluster.center.equals(point.localArray)){
                    flag=false;
                }
            }
            if(flag){
                List<Point> initMembers= new ArrayList<Point>();
                initMembers.add(point);
                Cluster newCluster= new Cluster(id, point,initMembers);
                clusterSet.add(newCluster);
                id++;
            }
        }
        return clusterSet;
    }

    /*
    为每个点分配一个簇
     */
    public void allocCluster(Set<Cluster>clusterSet){
        //计算每个点到K个中心的距离，并且为每个点标记簇号
        for(Point point:pointList){
            double minDis=Integer.MAX_VALUE;
            for(Cluster cluster:clusterSet){
                double tempDis= Math.min(disC.getEuclideanDis(point,cluster.center),minDis);
                if(tempDis!=minDis){
                    minDis=tempDis;
                    point.clusterId=cluster.clusterId;
                    point.dist=minDis;
                }
            }
        }

        //清除原来簇中的点，更新簇中的点
        for(Cluster cluster:clusterSet){
            if(cluster.members!=null){
                cluster.members.clear();
            }
            for(Point point:pointList){
                if(point.clusterId==cluster.clusterId){
                    cluster.addPoint(point);
                }
            }
        }
    }

    /**
     * 重新计算每个簇的中心位置，并返回是否要继续进行迭代
     */
    public boolean updateCluster(Set<Cluster>clusterSet) {
        boolean ifNeedIter= false;
        for(Cluster cluster:clusterSet){
            List<Point>pointList=cluster.members;
            double[] sumDimension=new double[bands];
            double[] aveDimension=new double[bands];

            if(pointList!=null){
                //各bands求和
                for(int i=0;i<bands;i++){
                    for(int j=0;j<pointList.size();j++){
                        sumDimension[i]+=pointList.get(j).localArray[i];
                    }
                }

                //各bands计算平均值
                for(int i=0;i<bands;i++){
                    aveDimension[i]=sumDimension[i]/pointList.size();
                }
            }

            //计算新旧中心的距离
            if(disC.getEuclideanDis(cluster.center,new Point(aveDimension))>disDiff){
                ifNeedIter= true;
            }

            cluster.center=new Point(aveDimension);
        }
        return ifNeedIter;
    }

    //进行Kmeans
    public int[] run(){
        bands= X.length;
        pixelNum=X[0].length;
        //每个像素ID对应的类别
        int[]K1=new int[pixelNum];

        initData();
        Set<Cluster> clusterSet = initClusterCenter();
        boolean ifNeedIter= true;
        while (ifNeedIter&&iterRunTimes<=iterMaxTimes){
            allocCluster(clusterSet);
            ifNeedIter= updateCluster(clusterSet);
            iterRunTimes++;
        }
        for(Cluster cluster:clusterSet){
           List<Point> clustermembers= cluster.members;
           for(Point point:clustermembers){
               K1[point.pointId]=point.clusterId;
           }
        }
        return K1;
    }

    public int getIterTimes(){
        return iterRunTimes;
    }
}


