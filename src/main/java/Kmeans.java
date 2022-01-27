import java.util.*;

public class Kmeans {
    private double[][] X;//ԭʼ��������
    private int K; //�صĸ���
    private int iterMaxTimes=10000; //���ε���������д���
    private int iterRunTimes=0; //���ε���ʵ�����д���
    private double disDiff=0.01; //���ε�����ֹ���������������������ĵľ����
    private static List<Point> pointList= null; //���ڴ�ԭʼ���ݹ��ɵĵ㼯
    private DistanceCompute disC= new DistanceCompute();
    private int bands;
    private int pixelNum;

    public Kmeans(double[][] x, int k) {
        this.X = x;
        this.K = k;
    }

    public class Point{
        public int pointId; //�������
        public int clusterId; //��ʶ������
        public double dist; //��ʶ�����������ĵľ���
        public double[] localArray; //����

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
        //����֮���������ظ��ж�
        public boolean equals(Object obj){
            if (obj == null || getClass() != obj.getClass())
                return false;

            Point point = (Point) obj;
            if (point.localArray.length != localArray.length)
                return false;

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
    ��ʼ�����ݼ���������ת��ΪPoint����
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
    ��ʼ��������
     */
    public Set<Cluster> initClusterCenter(){
        Set<Cluster>clusterSet= new HashSet<Cluster>();
        Random random= new Random();
        for(int id=0;id<K;){
            Point point = pointList.get(random.nextInt(pointList.size()));
            //���ڱ���Ƿ�ѡ���������
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
    Ϊÿ�������һ����
     */
    public void allocCluster(Set<Cluster>clusterSet){
        //����ÿ���㵽K�����ĵľ��룬����Ϊÿ�����Ǵغ�
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

        //���ԭ�����еĵ㣬���´��еĵ�
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
     * ���¼���ÿ���ص�����λ�ã��������Ƿ�Ҫ�������е���
     */
    public boolean updateCluster(Set<Cluster>clusterSet) {
        boolean ifNeedIter= false;
        for(Cluster cluster:clusterSet){
            List<Point>pointList=cluster.members;
            double[] sumDimension=new double[bands];
            double[] aveDimension=new double[bands];

            if(pointList!=null){
                //��bands���
                for(int i=0;i<bands;i++){
                    for(int j=0;j<pointList.size();j++){
                        sumDimension[i]+=pointList.get(j).localArray[i];
                    }
                }

                //��bands����ƽ��ֵ
                for(int i=0;i<bands;i++){
                    aveDimension[i]=sumDimension[i]/pointList.size();
                }
            }

            //�����¾����ĵľ���
            if(disC.getEuclideanDis(cluster.center,new Point(aveDimension))>disDiff){
                ifNeedIter= true;
            }

            cluster.center=new Point(aveDimension);
        }
        return ifNeedIter;
    }

    //����Kmeans
    public Set<Cluster> run(){
        //��װPoint
        bands= X.length;
        pixelNum=X[0].length;
        initData();
        Set<Cluster> clusterSet = initClusterCenter();
        boolean ifNeedIter= true;
        while (ifNeedIter&&iterRunTimes<=iterMaxTimes){
            allocCluster(clusterSet);
            ifNeedIter= updateCluster(clusterSet);
            iterRunTimes++;
        }
        return clusterSet;
    }

    public int getIterTimes(){
        return iterRunTimes;
    }
}


