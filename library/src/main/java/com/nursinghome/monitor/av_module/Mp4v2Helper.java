package com.nursinghome.monitor.av_module;

/**
 * Created by ShuRun on 2018/4/14.
 */
public class Mp4v2Helper {
    static {
        System.loadLibrary("native-c1");
    }
    public static native void init(String mp4FilePath);

    public static native int writeH264Data(byte[] data, int size);

    public static native void close();


}
