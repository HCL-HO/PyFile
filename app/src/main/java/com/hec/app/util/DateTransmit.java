package com.hec.app.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangxingjian on 16/2/16.
 */
public class DateTransmit {
    public static String dateTransmits(String severDate){
        if (severDate==null)
            return "";
        String midStr = severDate.substring(6,19);
        Date date = new Date();
        date.setTime(Long.parseLong(midStr));
        DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf2.format(date);
        return dateStr;
    }
}
