package com.example.zhaotao.camera_test.utils;


import java.io.Serializable;

/**
 * 时间段
 * Created by HDL on 2017/9/4.
 */

public class TimeSlot implements Serializable {
    /**
     * 开始时间
     */
    public long startTime;
    /**
     * 结束时间
     */
    public long endTime;

    public TimeSlot(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

}
