import java.util.*;

public class Sort {
    double[] list;//要排序的数组
    int [] idResult; //排序后的数组
    int listLen;

    public Sort(double[] list) {
        this.list = list;
    }

    public class RXElement{
        int id;
        double rxValue;

        public RXElement(int id, double rxValue) {
            this.id = id;
            this.rxValue = rxValue;
        }
    }

    //封装数组
    public LinkedList<RXElement> initData(){
        listLen= list.length;
        LinkedList<RXElement> RXList= new LinkedList<RXElement>();
        for(int i=0;i<listLen;i++){
            RXList.add(new RXElement(i,list[i]));
        }
        return RXList;
    }

    //实现排序器
    public static class RXElementComparator implements Comparator<RXElement>{
        public int compare(RXElement o1, RXElement o2) {
            double val=o1.rxValue-o2.rxValue;
            if(val>0){
                return 1;
            }else if(val<0){
                return -1;
            }else{
                return 0;
            }
        }
    }

    public int[] run(){
        LinkedList<RXElement> RXList=initData();
        Collections.sort(RXList,new RXElementComparator());
        idResult= new int[RXList.size()];
        for(int i=0;i<idResult.length;i++){
            idResult[i]=RXList.get(i).id;
        }
        return idResult;
    }

}
