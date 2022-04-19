package com.jxz;

import Jama.Matrix;

public class soft {
    //input data
    double[][]x;
    double T;

    public soft(double[][] x, double t) {
        this.x = x;
        this.T = t;
    }

    public double[][] run(){
        double[][]y;
        double[][]maxAbs;
        double[][]result;
        double[][]maxAbsPlus;
        Matrix maxAbsMatrix;
        Matrix maxAbsPlusMatrix;
        Matrix xMatrix;
        Matrix resultMatrix;

        maxAbs= new double[x.length][x[0].length];
        for(int i=0;i<x.length;i++){
            for(int j=0;j<x[0].length;j++){
                maxAbs[i][j]=Math.max(Math.abs(x[i][j])-T,0);
            }
        }

        maxAbsPlus= new double[maxAbs.length][maxAbs[0].length];
        for(int i=0;i<maxAbsPlus.length;i++){
            for(int j=0;j<maxAbsPlus[0].length;j++){
                maxAbsPlus[i][j]=maxAbs[i][j]+T;
            }
        }

        maxAbsMatrix=new Matrix(maxAbs);
        maxAbsPlusMatrix= new Matrix(maxAbsPlus);
        xMatrix=new Matrix(x);
        resultMatrix=maxAbsMatrix.arrayRightDivide(maxAbsPlusMatrix).arrayTimes(xMatrix);
        result=resultMatrix.getArrayCopy();

        return result;
    }
}
