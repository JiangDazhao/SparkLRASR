public class testAUC {
    public static void main(String[] args) {
         double[][] intput1=new double[][]{{0,0,0},{1,1,1},{0,0,0}};
         double[][] intput2=new double[][]{{9.2,8,7.5},{6,5,4.3},{3,2.1,1}};
         double AUC= new AUCDouble(intput1,intput2).run();
        System.out.println("AUC= "+AUC);
    }
}
