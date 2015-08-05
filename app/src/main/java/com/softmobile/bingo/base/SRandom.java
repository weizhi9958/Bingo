package com.softmobile.bingo.base;


import java.util.ArrayList;

public class SRandom {
    static ArrayList s_alAllRange;
    public SRandom(int iMin, int iMax){
        s_alAllRange = new ArrayList();

        //將範圍依序填入ArrayList
        for(int i = iMin; i <= iMax; i++){
            s_alAllRange.add(i);
        }

    }

    public int[] getRandom(int iCount){
        int[] iArray = new int[iCount];

        for(int i = 0; i < iArray.length; i++){
            iArray[i] = SRandom.createRandom(); //取得回傳之抽離值
        }
        return iArray; //回傳此陣列
    }

    public static int createRandom(){
        int iSize = s_alAllRange.size(); //變數iSize為目前ArrayList長度
        if(iSize > 0){
            //產生一iSize長度內亂數 並將ArrayList從此亂數位置抽離一數 並回傳
            return ((Integer) s_alAllRange.remove((int)(iSize * Math.random()))).intValue();
        } else {
            return -1;
        }
    }
}
