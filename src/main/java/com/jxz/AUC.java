package com.jxz;

import java.util.*;

public class AUC {
    private float[][] intput1;
    private double[][] intput2;
    private double[] GT;
    private double[] re;

    public AUC(float[][] intput1, double[][] intput2) {
        this.intput1 = intput1;
        this.intput2 = intput2;
    }

    public void init(){
        int gtRow=intput1.length;
        int gtCol=intput1[0].length;
        int reRow=intput2.length;
        int reCol=intput2[0].length;
        GT=new double[gtRow*gtCol];
        re=new double[reRow*reCol];
        for (int i = 0; i <gtCol; i++) {
            for (int j = 0; j <gtRow; j++) {
                GT[i*gtRow+j]=intput1[j][i];
            }
        }
        for (int i = 0; i <reCol; i++) {
            for (int j = 0; j <reRow; j++) {
                re[i*reRow+j]=intput2[j][i];
            }
        }
    }

    public double run(){
        init();
        HashMap<Double,Double> valueTruthMap= new HashMap<>();
        for(int i=0;i<GT.length;i++){
            valueTruthMap.put(re[i],GT[i]);
        }

        List<Map.Entry<Double,Double>> valueTruthList= new ArrayList<Map.Entry<Double,Double>>(valueTruthMap.entrySet());
//        //		排序前打印
//        System.out.println("排序前");
//        for (Map.Entry<Double, Double> entry : valueTruthList) {
//            System.out.println(entry.toString());
//        }

        Collections.sort(valueTruthList, new Comparator<Map.Entry<Double,Double>>() {
            @Override
            public int compare(Map.Entry<Double,Double> o1, Map.Entry<Double,Double> o2) {
                return o2.getKey().compareTo(o1.getKey());
            }
        });
//        //		排序后打印
//        System.out.println("排序后");
//        for (Map.Entry<Double, Double> entry : valueTruthList) {
//            System.out.println(entry.toString());
//        }

        ArrayList<Double> fprList= new ArrayList<>();
        ArrayList<Double> tprList= new ArrayList<>();

        for(int i=0;i<valueTruthList.size();i++){
            double threshold=valueTruthList.get(i).getKey();
            double tp=0;
            double fn=0;
            double fp=0;
            double tn=0;
            double x=0;
            double y=0;
            for(int j=0;j<valueTruthList.size();j++){
                double EValue= valueTruthList.get(j).getKey();
                double label= valueTruthList.get(j).getValue();
                if(EValue>=threshold&&label==1.0){
                    tp=tp+1;
                }else if(EValue<threshold&&label==1.0){
                    fn=fn+1;
                }else if(EValue>=threshold&&label==0.0){
                    fp=fp+1;
                }else if(EValue<threshold&&label==0.0){
                    tn=tn+1;
                }
            }
            x=fp/(tn+fp);
            fprList.add(x);
            y=tp/(tp+fn);
            tprList.add(y);
        }
//        for(double fpr:fprList){
//            System.out.print(fpr+" ");
//        }
//        System.out.println();
//        for(double tpr:tprList){
//            System.out.print(tpr+" ");
//        }

        double AUC=0;
        int i=0;
        while (i<valueTruthList.size()-1){
            AUC+=(fprList.get(i+1)-fprList.get(i))*(tprList.get(i)+tprList.get(i+1));
            i++;
        }
        AUC/=2;
        return AUC;
    }
}
