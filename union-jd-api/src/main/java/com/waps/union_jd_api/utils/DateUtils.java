package com.waps.union_jd_api.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String timeTmp2DateStr(String tmp) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(tmp);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String formatStringToDateString(String timeS) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(timeS);
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }
}
