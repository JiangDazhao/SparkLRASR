package com.jxz;

import Jama.Matrix;

public class SolveL1L2 {
    double[][] W;
    double lamda;
    int bands;
    int pixelnum;

    public SolveL1L2(double[][] w, double lamda) {
        this.W = w;
        this.lamda = lamda;
    }

    public double[][]run(){
        double[][] E;
        bands=W.length;
        pixelnum=W[0].length;
        E=new double[W.length][W[0].length];
        for(int i=0;i<pixelnum;i++){
            double[][]l2Input= new double[bands][1];
            double[][]l2Output= new double[bands][1];
            for(int j=0;j<bands;j++){
                l2Input[j][0]=W[j][i];
            }

            l2Output=solveL2(l2Input,lamda);
            for(int j=0;j<bands;j++){
                E[j][i]=l2Output[j][0];
            }
        }
        return E;
    }

    public double[][] solveL2(double[][]w,double lamda){
        double nm= new Matrix(w).norm2();
        double[][]x= new double[bands][1];
        Matrix xMatrix;
        Matrix wMatirx= new Matrix(w);
        if(nm>lamda){
            xMatrix=wMatirx.times(nm-lamda).times(1.0/nm);
            for(int i=0;i<bands;i++){
                x[i][0]=xMatrix.get(i,0);
            }
        }else{
            for(int i=0;i<bands;i++){
                x[i][0]=0;
            }
        }
        return x;
    }

}
