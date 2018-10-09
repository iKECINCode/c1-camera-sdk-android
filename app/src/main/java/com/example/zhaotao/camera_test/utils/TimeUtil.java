package com.example.zhaotao.camera_test.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Author: nanchen
 * Email: liushilin520@foxmail.com
 * Date: 2017-04-14  10:48
 */

public class TimeUtil {

    public static String dateFormat(String timestamp) {
        if (timestamp == null) {
            return "unknown";
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return "unknown";
        }
    }
    /**
     * ��õ�ǰʱ��
     * @return  2011-06-07
     */
    public static String getCurrDate(){
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(aDate);
        return formattedDate;


    }

    /**
     * 安卓视频播放器不支持"yyyy-MM-dd HH:mm:ss"文件名
     * @return
     */
    public static String getCurrDateTime(){
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }
    public static String getCurrDateTime(String format){
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }
    public static String getCurrDateTime(long time){
        Date aDate = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }
    public static String getCurrDateTime2(long time){
        Date aDate = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }
    public static String getCurrDateTime2(){
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }


    /**
     * 将"yyyy_MM_dd_HH_mm_ss"格式的时间转化为Long
     * @param date
     * @return
     */
    public static long DateTime2Long(String date){
        SimpleDateFormat format =   new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        try {
            Date time = format.parse(date);
            return time.getTime();
        }catch(Exception e){
            return 0;
        }
    }
    public static String getCurrDateTime3(){
        Date aDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }

    public static String ConvertDate(String arg0){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(arg0));
        return formatter.format(calendar.getTime());

    }
    public static String getDateTime(long time){
        Date aDate = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String formattedDate = formatter.format(aDate);
        return formattedDate;
    }
    /**毫秒转换成小时*/
    public static String ms2HMS(int _ms){
        String HMStime;
        _ms/=1000;
        int hour=_ms/3600;
        int mint=(_ms%3600)/60;
        int sed=_ms%60;
        String hourStr= String.valueOf(hour);
        if(hour<10){
            hourStr="0"+hourStr;
        }
        String mintStr= String.valueOf(mint);
        if(mint<10){
            mintStr="0"+mintStr;
        }
        String sedStr= String.valueOf(sed);
        if(sed<10){
            sedStr="0"+sedStr;
        }
        HMStime=hourStr+":"+mintStr+":"+sedStr;
        return HMStime;
    }
    /**毫秒转换成标准时间*/
    public static String ms2Date(long _ms){
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    public static String ms2DateOnlyDay(long _ms){
        Date date = new Date(_ms);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return format.format(date);
    }
    /**标准时间转换成时间戳*/
    public static long date2Long(String _data){
        SimpleDateFormat format =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(_data);
            return date.getTime();
        }catch(Exception e){
            return 0;
        }
    }
    /**标准时间转换成时间戳*/
    public static long date2Long(String _data, String format){
        SimpleDateFormat simpleDateFormat =   new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(_data);
            return date.getTime();
        }catch(Exception e){
            return 0;
        }
    }
    /**标准时间转换*/
    public static String Date2String(Date data){
        SimpleDateFormat format =   new SimpleDateFormat("yyyy-MM-dd");
        try {

            return format.format(data);
        }catch(Exception e){
            return "";
        }
    }

    public static String Date2String(Date data, String format){
        SimpleDateFormat simpleDateFormat =   new SimpleDateFormat(format);
        try {

            return simpleDateFormat.format(data);
        }catch(Exception e){
            return "";
        }
    }
    /**把date转换为10:20格式*/
    public static String date2Hour(Date data){
        SimpleDateFormat format =   new SimpleDateFormat("HH:mm");
        try {

            return format.format(data);
        }catch(Exception e){
            return "转换异常";
        }
    }
    /**标准时间转换*/
    public static long String2long(String data){
        SimpleDateFormat format =   new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date time = format.parse(data);
            return time.getTime();
        }catch(Exception e){
            return 0;
        }
    }
    public static long String2long(String data, String format){
        SimpleDateFormat simpleDateFormat =   new SimpleDateFormat(format);
        try {
            Date time = simpleDateFormat.parse(data);
            return time.getTime();
        }catch(Exception e){
            return 0;
        }
    }
    public static String long2String(long time, String format){
        SimpleDateFormat simpleDateFormat =   new SimpleDateFormat(format);
        try {
            Date date=new Date(time);
            String s = simpleDateFormat.format(date);
            return s;
        }catch(Exception e){
            return "";
        }
    }


}
