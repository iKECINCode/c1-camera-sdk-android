package com.example.zhaotao.camera_test.utils;

import android.text.TextUtils;

import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ShuRun on 2018/5/22.
 */
public class TimePlanManager {
    private static TimePlanManager timePlanManager;
    private ArrayList<TimeSlot> slotArrayList;
    private ArrayList<String> toString;

    public ArrayList<TimeSlot> getSlotArrayList() {
        return slotArrayList;
    }

    public void setSlotArrayList(ArrayList<TimeSlot> slotArrayList) {
        Collections.sort(slotArrayList, (o1, o2) -> {
            if (o1.startTime > o2.startTime) {
                return 1;
            } else {
                return -1;
            }

        });
        this.slotArrayList = slotArrayList;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> transformToString(ArrayList<TimeSlot> timeSlots){
        ArrayList<String> arrayList=new ArrayList<>();
        for (int i = 0; i < timeSlots.size(); i++) {
            String startTime = TimeUtil.long2String(timeSlots.get(i).startTime, "HH:mm");
            String stopTime = TimeUtil.long2String(timeSlots.get(i).endTime, "HH:mm");
            long temStart=TimeUtil.date2Long(startTime,"HH:mm");
            long temStop=TimeUtil.date2Long(stopTime,"HH:mm");
            if (temStart> temStop){
                stopTime=stopTime+"(第二天)";
            }
            String task=startTime+"-"+stopTime;
            arrayList.add(task);
        }
        return arrayList;
    }



    private TimePlanManager() {
        slotArrayList = new ArrayList<>();
    }

    public static TimePlanManager getInstance() {
        if (timePlanManager == null) {
            synchronized (TimePlanManager.class) {
                if (timePlanManager == null) {
                    timePlanManager = new TimePlanManager();
                    return timePlanManager;
                }

            }

        }
        return timePlanManager;
    }

    /**
     * 检测一个timeslot的时间段是否与已存在的其他时间段出现重叠，重叠返回true
     *
     * @param timeSlot
     * @return
     */
    public boolean checkIfContained(TimeSlot timeSlot) {
        for (TimeSlot slot : slotArrayList) {
            if (timeSlot.startTime >= slot.startTime && timeSlot.startTime <= slot.endTime) {
                return true;
            }
            if (timeSlot.endTime >= slot.startTime && timeSlot.endTime <= slot.endTime) {
                return true;
            }
            if (timeSlot.startTime < slot.startTime && timeSlot.endTime > slot.endTime) {
                return true;
            }
        }
        //如果设置的开启时间是第二天，且大于前一天的最早关闭时间，则时间重叠
        if (slotArrayList.size() > 0 && timeSlot.endTime - 24 * 60 * 60 * 1000 >= slotArrayList.get(0).startTime) {
            return true;
        }
        if (slotArrayList.size() > 0 && slotArrayList.get(slotArrayList.size() - 1).endTime - 24 * 60 * 60 * 1000 >= timeSlot.startTime) {

            return true;

        }


        return false;
    }

    /**
     * @param timeSlot
     * @param index    跳过哪一个数据
     * @return
     */
    public boolean checkIfContained(TimeSlot timeSlot, int index) {

        for (int i = 0; i < slotArrayList.size(); i++) {
            if (i == index) {
                continue;
            }
            if (timeSlot.startTime >= slotArrayList.get(i).startTime && timeSlot.startTime <= slotArrayList.get(i).endTime) {
                return true;
            }
            if (timeSlot.endTime >= slotArrayList.get(i).startTime && timeSlot.endTime <= slotArrayList.get(i).endTime) {
                return true;
            }
            if (timeSlot.startTime < slotArrayList.get(i).startTime && timeSlot.endTime > slotArrayList.get(i).endTime) {
                return true;
            }
        }
        //如果最后一个计划的结束时间减去一天仍旧大于添加计划的起始时间，则重叠
        if (slotArrayList.size() > 0 && slotArrayList.get(slotArrayList.size() - 1).endTime - 24 * 60 * 60 * 1000 >= timeSlot.startTime) {

            return true;

        }
        //如果设置的开启时间是第二天，且大于前一天的最早关闭时间，则时间重叠
        if (slotArrayList.size() > 0 && timeSlot.endTime - 24 * 60 * 60 * 1000 >= slotArrayList.get(0).startTime) {
            return true;
        }
        //如果设置的开启时间是第二天，且大于前一天的最早关闭时间，则时间重叠
        return false;
    }

    public int addTimeSlot(TimeSlot timeSlot) {
        slotArrayList.add(timeSlot);
        Collections.sort(slotArrayList, (o1, o2) -> {
            if (o1.startTime > o2.startTime) {
                return 1;
            } else {
                return -1;
            }

        });
        return slotArrayList.indexOf(timeSlot);
    }

    /**
     *新添加一个计划任务
     * @param s 格式必须为08:03-09:59或者23:00-01:00 03:11–04:33
     *          后面的时间小于前面的时间代表后面的时间是第二天------
     * @return
     */
    public int addTask(String s) throws IllegalArgumentException {
        Pattern p = Pattern.compile("^([0-9]{1,2}):[0-5][0-9]-([0-9]{1,2}):[0-5][0-9]$");
        Matcher m = p.matcher(s);
//        KLog.e("输入合法吗"+s+m.matches());
        if (!m.matches()){
            throw new IllegalArgumentException("输入的时间格式不对");
        }
        String[] split = s.split("-");
//        KLog.i(split[0]);
//        KLog.i(split[1]);
        long startTime=TimeUtil.date2Long(split[0],"HH:mm")+24*60*60*1000;
        long endTime=TimeUtil.date2Long(split[1],"HH:mm")+24*60*60*1000;
//        KLog.i(startTime);
//        KLog.i(endTime);
        if (startTime==endTime){
            throw new IllegalArgumentException("开始与结束时间不能相同");
        }
        if (endTime<startTime){
            endTime+=24*60*60*1000;
        }
        TimeSlot timeSlot=new TimeSlot(startTime,endTime);
        boolean ifContained = checkIfContained(timeSlot);
        if (ifContained){
            throw new IllegalArgumentException("该计划时间段与已有的计划重叠");
        }
        int i = addTimeSlot(timeSlot);
        return i;
    }

    /**
     * 更改一个已经添加的计划
     * @param task 计划时间段格式必须为08:03-09:59或者23:00-01:00
     * @param index 原计划在原计划列表中的位置
     * @return
     */
    public int updateTask(String task, int index){
        Pattern p = Pattern.compile("^([0-9]{1,2}):[0-5][0-9]-([0-9]{1,2}):[0-5][0-9]$");
        Matcher m = p.matcher(task);
//        KLog.e("输入合法吗"+task+m.matches());
        if (!m.matches()){
            throw new IllegalArgumentException("输入的时间格式不对");
        }
        String[] split = task.split("-");
        long startTime=TimeUtil.date2Long(split[0],"HH:mm")+24*60*60*1000;
        long endTime=TimeUtil.date2Long(split[1],"HH:mm")+24*60*60*1000;
        if (startTime==endTime){
            throw new IllegalArgumentException("开始与结束时间不能相同");
        }
        if (endTime<startTime){
            endTime+=24*60*60*1000;
        }
        TimeSlot timeSlot=new TimeSlot(startTime,endTime);
        boolean ifContained= checkIfContained(timeSlot, index);
        if (ifContained){
            throw new IllegalArgumentException("该计划时间段与已有的计划重叠");
        }
        slotArrayList.remove(index);
        int i = addTimeSlot(timeSlot);
        return i;
    }


    public void deleteTimeSlot(TimeSlot timeSlot) {
        slotArrayList.remove(timeSlot);
    }


    public String toPlan() {
        // 取得本地时间：
        int size = slotArrayList.size();
        if (size == 0) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < size; i++) {
                TimeSlot timeSlot = slotArrayList.get(i);
                long closeMills = timeSlot.startTime;
                long openMills = timeSlot.endTime;
                String close = closeMills / 1000 + "";
                String open = openMills / 1000 + "";
                if (i == size - 1) {
                    builder.append(close).append("-").append(open);
                } else {
                    builder.append(close).append("-").append(open).append("\n");
                }

            }

            String s = builder.toString();
//            KLog.e(s);
            return s;
        }
    }

    /**
     * @param plan
     */
    public  ArrayList<TimeSlot> fromPlan(String plan) {
        KLog.i(plan);
        if (TextUtils.isEmpty(plan)) {
            KLog.i("无计划");
            return null;
        }
        String[] strings = plan.split("\n");
        for (String string : strings) {
            String[] split = string.split("-", 2);
            String closeTime = split[0];
            String openTime = split[1];
            KLog.e(closeTime + "*" + openTime);
            long close = Long.parseLong(closeTime) * 1000;
            KLog.w("close" + TimeUtil.long2String(close, "yyyy-MM-dd HH:mm:ss"));
            long open = Long.parseLong(openTime) * 1000;
            KLog.w("open" + TimeUtil.long2String(open, "yyyy-MM-dd HH:mm:ss"));
            TimeSlot timeSlot = new TimeSlot( close, open);
            slotArrayList.add(timeSlot);

        }
        Collections.sort(slotArrayList, (o1, o2) -> {
            if (o1.startTime > o2.startTime) {
                return 1;
            } else {
                return -1;
            }
        });
//      ArrayList<String> list = transformToString(slotArrayList);
//      StringBuilder builder=new StringBuilder();
//        for (String s : list) {
//            builder.append(s);
//            builder.append("\n");
//        }
//        KLog.e(builder.toString());

    return  slotArrayList;

    }

    public void release() {
        KLog.i("清空任务");
        slotArrayList.clear();
        slotArrayList = null;
        timePlanManager = null;
    }


}
