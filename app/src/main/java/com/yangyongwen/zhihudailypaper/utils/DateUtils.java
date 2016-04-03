package com.yangyongwen.zhihudailypaper.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by samsung on 2016/2/26.
 */
public class DateUtils {

    public static String getToday(){
        return getDay(0);
    }


    /*
    * offset:以今天为中点，未来 offset > 0 ；过去 offset < 0
    * */
    public static String getDay(int offset){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,offset);
        return sdf.format(cal.getTime());
    }


    public static String convertDay(String dateString){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        try {
            Date date=simpleDateFormat.parse(dateString);
            simpleDateFormat=new SimpleDateFormat("cccc",Locale.CHINA);
            String result=simpleDateFormat.format(date);
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String nextDay(String dateString){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        try {
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(sdf.parse(dateString));
            calendar.add(Calendar.DATE,1);
            String reslut=sdf.format(calendar.getTime());
            return reslut;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
    public static String lastDay(String dateString){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        try {
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(sdf.parse(dateString));
            calendar.add(Calendar.DATE,-1);
            String reslut=sdf.format(calendar.getTime());
            return reslut;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }


}