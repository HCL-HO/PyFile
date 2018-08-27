package com.hec.app.util;

/**
 * Created by wangxingjian on 16/2/24.
 */
public class APIParaTransmit {
    public static int PageIndexToOffset(int pageindex,int pagesize){
        return pageindex+pagesize;
    }
}
