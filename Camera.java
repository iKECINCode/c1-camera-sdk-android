package com.Petwant.www;

/**
 * Created by zhaotao on 17-7-31.
 */


public class Camera {
    /**
     * 设备连接状态
     */
    public static final int OFFLINE = 0; //设备离线
    public static final int ONLINE = 1; //设备在线
    public static final int ONCONNECTION = 0; //正在连接设备

    /**
     * 音视频格式
     *  视频现在只支持h.264
     *  音频值支持g711a
     */
    public static final int MEDIA_CODEC_UNKNOWN			= 0x00;
    public static final int MEDIA_CODEC_VIDEO_MPEG4		= 0x4C;
    public static final int MEDIA_CODEC_VIDEO_H263		= 0x4D;
    public static final int MEDIA_CODEC_VIDEO_H264		= 0x4E;
    public static final int MEDIA_CODEC_VIDEO_MJPEG		= 0x4F;
    public static final int MEDIA_CODEC_AUDIO_AAC       = 0x88;
    public static final int MEDIA_CODEC_AUDIO_G711U     = 0x89;
    public static final int MEDIA_CODEC_AUDIO_G711A     = 0x8A;
    public static final int MEDIA_CODEC_AUDIO_ADPCM     = 0X8B;
    public static final int MEDIA_CODEC_AUDIO_PCM		= 0x8C;
    public static final int MEDIA_CODEC_AUDIO_SPEEX		= 0x8D;
    public static final int MEDIA_CODEC_AUDIO_MP3		= 0x8E;
    public static final int MEDIA_CODEC_AUDIO_G726      = 0x8F;

    //视频清晰度
    public static final int VIDEO_QUALITY_UNKNOWN			= 0x00;
    public static final int VIDEO_QUALITY_MAX				= 0x01;
    public static final int VIDEO_QUALITY_HIGH				= 0x02;
    public static final int VIDEO_QUALITY_MIDDLE			= 0x03;
    public static final int VIDEO_QUALITY_LOW				= 0x04;
    public static final int VIDEO_QUALITY_MIN				= 0x05;

    //事件类型
    public static final int EVENTLOG_ALL			= 0x00;

    /**
     * wifi加密方式
     */
    public static final int WIFIAPENC_INVALID			= 0x00;
    public static final int WIFIAPENC_NONE				= 0x01; //
    public static final int WIFIAPENC_WEP				= 0x02; //WEP, for no password
    public static final int WIFIAPENC_WPA_TKIP			= 0x03;
    public static final int WIFIAPENC_WPA_AES			= 0x04;
    public static final int WIFIAPENC_WPA2_TKIP			= 0x05;
    public static final int WIFIAPENC_WPA2_AES			= 0x06;
    public static final int WIFIAPENC_WPA_PSK_TKIP      = 0x07;
    public static final int WIFIAPENC_WPA_PSK_AES       = 0x08;
    public static final int WIFIAPENC_WPA2_PSK_TKIP     = 0x09;
    public static final int WIFIAPENC_WPA2_PSK_AES      = 0x0A;

    /**
     * sound_name: 音频的名字最大３1个字节
     * sound_alias: 音频别名最大６3个字节
     */
    public static final class audioFileInfo
    {
        private audioFileInfo(String n, String a) {
            sound_name = n;
            sound_alias = a;

        }
        //超出字符最大的后面的字符会截断
        public String sound_name; //音频的名字最大３1个字节
        public String sound_alias; // 音频别名最大６3个字节
    }

    /**
     * ssid : wifi ssid
     * enctype: wifi加密方式请看WIFIAPENC_
     * signal: wifi信号强度 0 - 100
     * status: 当前连接的wifi　１当前站在使用的wifi　０没有连接或者断开的ｗｉｆｉ
     */
    public static final class wifiAp {
        private wifiAp(String s, byte e, byte ss, byte st) {
            ssid = s;
            enctype = e;
            signal = ss;
            status = st;
        }
        public String ssid;
        public byte enctype;
        public byte signal;
        public byte status;
    }

    /**
     * 串口透出接口
     */
    public interface feedingDataCallback {
        void data(byte [] array);
    }
    /**
     * 列出设备周边的wifi
     */
    public interface listWifiApCallback {
        void listWifiAp(wifiAp [] ap);
    }

    /**
     * 获取音频文件回调接口
     */
    public interface getAudioFileCallback {
        void audioFileInfo(audioFileInfo[] info);
    }

    /**
     * utc时间类
     *   year	 The number of year.
     *   month	 The number of months since January, in the range 1 to 12.
     *   day	 The day of the month, in the range 1 to 31.
     *   wday    The number of days since Sunday, in the range 0 to 6. (Sunday = 0, Monday = 1, ...)
     *   hour    The number of hours past midnight, in the range 0 to 23.
     *   minute  The number of minutes after the hour, in the range 0 to 59.
     *   second  The number of seconds after the minute, in the range 0 to 59.
     */
    public static final  class timeDay
    {
        public timeDay(short y, byte M, byte d, byte w, byte h, byte m, byte s) {
            this.year = y;
            this.month = M;
            this.day = d;
            this.wday = w;
            this.hour = h;
            this.minute = m;
            this.second = s;
        }

        public short year;	// The number of year.
        public byte month;	// The number of months since January, in the range 1 to 12.
        public byte day;		// The day of the month, in the range 1 to 31.
        public byte wday;		// The number of days since Sunday, in the range 0 to 6. (Sunday = 0, Monday = 1, ...)
        public byte hour;     // The number of hours past midnight, in the range 0 to 23.
        public byte minute;   // The number of minutes after the hour, in the range 0 to 59.
        public byte second;   // The number of seconds after the minute, in the range 0 to 59.

    }

    /**
     * 事件记录
     */
    public static final class eventLog
    {
        public timeDay stTime; //事件产生的时间
        public byte event; //事件类型
        public int status; //事件状态
    }

    /**
     * 搜素局域网设备
     */
    public static final  class searchDeviceInfo {
        private searchDeviceInfo(String uid, String ip, int port) {
            this.UID = uid;
            this.IP = ip;
            this.port = port;
        }
        public String 	UID; //设备UID
        public String 	IP;  //设备ip
        public int 	port; //设备端口号
    }


    /**
     * 搜素局域网设备回调接口
     */
    public interface searchDeviceInfoCallback {
        void searchDeviceInfo(searchDeviceInfo[] info);
    }

    /**
     * 得到事件记录回调接口
     */
    public interface getEventLogCallback {
        void eventLog(eventLog[] l);
    }

    /**
     * 上线和离线通知接口
     */
    public interface onOffLineCallback {
        /**
         * 设备连接成功或者失败　isSuccess：
         * @param m
         * @param isSuccess :　0:suceess other:失败
         */
        void Online(Camera m, int isSuccess);

        /**
         * 设备离线时回调接口
         * @param m
         */
        void Offline(Camera m);
    }

    /**
     * 接受视频数据回调接口
     */
    public interface  recvVideoCallback {
        /**
         *
         * @param data h.264数据
         * @param pts 时间戳
         * @param keyframe 是否为关键帧 1:关键　其他:非关键
         */
        void recvVideo(byte[] data, int pts, int keyframe);
    }

    /**
     * 接受音频数据回调接口
     */
    public interface recvAudioCallback {
        /**
         *
         * @param data g711a压缩的数据
         * @param pts　时间戳
         */
        void recvAudio(byte[] data, int pts);
    }

    /**
     * 发送音频数据回调接口
     */
    public interface sendAudioCallback {
        /**
         *
         * @param data 填充g711a数据发送给设备　数据大小为data.length
         */
        int sendAudio(byte[] data);
    }

    /**
     * 是否执行成功回调接口
     */
    public interface successCallback {
        /**
         *
         * @param issuccess ture:成功　false:失败
         */
        void success(boolean issuccess);
    }

    /**
     * 查询设备信息以及版本号
     */
    public interface deviceInfoCallback {
        /**
         * 版本号组成: version1．version２．version３．version４ 如: 1.0.0.2
         * @param sdcardTotal sd卡总大小
         * @param sdcardFree　sd卡剩余空间大小
         * @param version1 版本号１
         * @param version2　版本号２
         * @param version3　版本号３
         * @param version4　版本号４
         * @param model
         * @param vendor
         */
        void deviceInfo(int sdcardTotal, int sdcardFree, byte version1,
                        byte version2, byte version3,
                        byte version4, String model, String vendor);
    }

    /**
     * 设备视频录制或者设备截图回调接口
     */
    public interface videoRecordingCallback {
        /**
         *
         * @param err 0成功 1:没sd卡 2:失败
         */
        void videoRecording(int err);
    }

    /**
     * 查询第几次喂食记录
     */
    public interface queryFeedingSettingCallback {
        /**
         *
         * @param isSuccess 当前查询是否成功
         * @param h 喂食时间　小时 utc时间
         * @param m 喂食时间  分钟 utc时间
         * @param w 喂食重量
         * @param manual 喂食方式　手动或者定时喂食 true 定时喂食　false手动喂食
         * @param sound 喂食时播放的第几段录音
         */
        void queryFeedingSetting(boolean isSuccess, byte h, byte m, int w, boolean manual, byte sound);
    }


    /**
     * 监听设备喂食相关事件
     */
    public interface listenerDeviceEvent {
        /**
         * 喂食告警回调函数
         *    event:参数含义如下
         * 			0x01：食物口被堵住；
         *			0x02：粮食已经吃完或者快没了；
         *			0x03：喂食失败，电机可能坏了，通过检测重量传感器发现喂食后，剩余量没有减少；(manual字段）
         *			0x04：宠物靠近
         *	  manual: 喂食方式　手动或者定时喂食 true 定时喂食　false手动喂食
         */
        void feedingAlarmCallback(int event, boolean manual);
        /**
         * 喂食完成提醒
         *   h: 喂食时间　小时 utc时间
         *	 m: 喂食时间  分钟 utc时间
         *	 weight: 喂食重量
         *	 manual: 喂食方式　手动或者定时喂食 true 定时喂食　false手动喂食
         *
         */
        void feedingCompleteCallback(int h, int m, int weight, boolean manual);

        void rawFeedingData(byte[] data);
    };

    /**
     * 列出设备周围可用的wifi列表
     * @param callback
     * @return
     */
    public boolean listWifiAp(listWifiApCallback callback)
    {
        return native_CameraListWifiAp(callback);
    }

    /**
     * 串口透出：　发送喂食方面的数据
     * @param data
     * @param callback
     * @return
     */
    public int sendFeedingData(byte[] data, feedingDataCallback callback)
    {
        return native_CameraSendFeedinData(data, callback);
    }

    /**
     *
     * @param ssid 最大３１个字符
     * @param pass　最大３１个字符
     * @param encty　ｗｉｆｉ加密方式请查看WIFIAPENC_定义
     * @param callback 回调接口
     * @return
     */
    public boolean setWifiAp(String ssid, String pass, byte encty, successCallback callback)
    {
        return native_CameraSetWifiAp(ssid, pass, encty, callback);
    }

    /**
     *
     * @param mode: true INDOOR_50HZ
     *              false INDOOR_60HZ
     * @param callback
     * @return
     */
    public boolean setEnvironmentMode(boolean mode, successCallback callback) {
        return native_CameraSetEnvironmentMode(mode, callback);
    }

    /**
     *
     * @param callback
     * @return
     */
    public boolean getEnvironmentMode(successCallback callback) {
        return native_CameraGetEnvironmentMode(callback);
    }

    /**
     * 查询一份喂食的重量
     */
    public  interface queryEachFeedingWeightCallback {
        void queryEachFeedingWeight(boolean isSuccess, int w);
    }

    /**
     * 设置推送服务器地址
     */
    public interface getPhpServerCallback {
        void getPhpServer(boolean isSuccess, String ip);
    }

    @Override
    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }

    /**
     * 注册监听设备喂食事件
     * @param listener
     */
    public void registerListener(listenerDeviceEvent listener) {
        native_CameraRegisterListener(listener);
    }

    /**
     * 取消监听设备喂食事件
     */
    public void unRegisterListener() {
        native_CameraUnregisterListener();
    }

    /**
     * 搜索局域网设备
     * @param callback
     */
    public static void searchDevice(searchDeviceInfoCallback callback) {
        native_CameraSearchDevcie(callback);
    }

    /**
     * 得到设备uid
     * @return　null false  is not null success
     */
    public String getUID() {
        return native_CameraGetUID();
    }

    /**
     * 改变设备密码
     * @param oldPass 最大３１个字符
     * @param newPass 最大３１个字符
     * @param callback
     * @return
     */
    public boolean changePassword(String oldPass, String newPass, successCallback callback) {
        return native_CameraChangePasswd(oldPass, newPass, callback);
    }

    /**
     * 发送录音文件给设备
     * @param file 将要发送的文件名字
     * @param name   音频文件名字
     * @param alias  音频文件别名
     * @param callback
     * @return false设备离线　truc成功
     */
    public boolean sendAudioFile(String file, String name, String alias, successCallback callback) {
        return native_CameraSendAudioFile(file, name, alias, callback);
    }

    /**
     * 手动喂食
     * @param weight 喂食　重量g
     * @param sound 喂食时播放的第几段录音
     * @param callback
     * @return false设备不再线或者传入参数有问题 true成功
     */
    public boolean setManualFeeding(int weight, byte sound, successCallback callback) {
        return native_CameraSetManualFeeding(weight, sound, callback);
    }

    /**
     * 定时喂食设置
     * @param hour 定时喂食小时(utc) 0到２３
     * @param minute 定时喂食分钟(utc)　０到59
     * @param weight 定时喂食　重量g
     * @param cnt    第几次定时喂食 1到４
     * @param sound  定时喂食时播放的第几段录音
     * @param callback 定时喂食成功还是失败　回调接口通知此次设置定时喂食是成功还是失败
     * @return false设备不再线或者传入参数有问题 true成功
     */
    public boolean setTimedFeeding(byte hour, byte minute,
                                   int weight, byte cnt ,
                                   byte sound, successCallback callback) {

        return native_CameraSetTimedFeeding(hour, minute, weight, cnt, sound, callback);
    }
    /**
     * 定时喂食设置
     * @param hour 定时喂食小时(utc) 0到２３
     * @param minute 定时喂食分钟(utc)　０到59
     * @param weight 定时喂食　重量g
     * @param cnt    第几次定时喂食 1到４
     * @param on  0:禁用喂食 1:启用喂食
     * @param sound  定时喂食时播放的第几段录音
     * @param callback 定时喂食成功还是失败　回调接口通知此次设置定时喂食是成功还是失败
     * @return false设备不再线或者传入参数有问题 true成功
     */
    public boolean setTimedFeeding2(byte hour, byte minute,
                                   int weight, byte cnt , byte on,
                                   byte sound, successCallback callback) {
        return native_CameraSetTimedFeeding2(hour, minute, weight, cnt, on, sound, callback);
    }

    /**
     * 同步喂食时间 (使用utc时间)
     * @param callback
     * @return
     */
    public boolean syncFeedingtime(successCallback callback) {
        return native_CameraSyncFeedingTime(callback);
    }
    /**
     * 查询一份喂食重量单位克
     * @param callback
     * @return false设备不在线 true成功
     */
    public boolean queryEachFeedingWeight(queryEachFeedingWeightCallback callback) {
        return native_CameraQueryEachFeedingWeight(callback);
    }

    /**
     *
     * @param cnt 查询第几次定时喂食记录 1到4
     * @param callback
     * @return false设备不在线或者cnt参数错误 true成功
     */
    public boolean queryFeedingSetting(int cnt, queryFeedingSettingCallback callback) {
        return  native_CameraQueryFeedingSetting(cnt, callback);
    }

    /**
     * 删除定时喂食
     * @param cnt　删除第几次定时喂食 1到4
     * @param callback
     * @return
     */
    public boolean delTimedFeeding(int cnt, successCallback callback) {
        return native_CameraDelTimedFeeding(cnt, callback);
    }

    /**
     * 设置推送服务器地址
     * @param ip 最大４７个字符 >47会被截断
     * @param callback
     * @return
     */
    public boolean setPhpServer(String ip, getPhpServerCallback callback) {
        return native_CameraSetPhpServer(ip, callback);
    }

    /**
     * 获取服务器推送地址
     * @param callback
     * @return
     */
    public boolean getPhpServer(getPhpServerCallback callback) {
        return native_CameraGetPhpServer(callback);
    }

    /**
     * 设备截图并存在设备sdcard上
     * @param callback
     * @return
     */
    public  boolean screenShot(videoRecordingCallback callback) {
        return native_CameraScreenShot(callback);
    }

    /**
     * 设备录制视频开始
     * @param callback
     * @return
     */
    public boolean videoRecordingStart(videoRecordingCallback callback) {
        return native_CameraVideoRecordingStart(callback);
    }

    /**
     * 停止视频录制
     * @param callback
     * @return
     */
    public boolean videoRecordingStop(videoRecordingCallback callback) {
        return native_CameraVideoRecordingStop(callback);
    }
    /**
     * 播放声音
     * @param name
     * @param alias
     * @param callback
     * @return
     */
    public boolean playAudioFile(String name, String alias, successCallback callback) {
        return native_CameraPlayAudioFile(name, alias, callback);
    }

    /**
     * 删除音频文件
     * @param name
     * @param alias
     * @param callback
     * @return
     */
    public boolean delAudioFile(String name, String alias, successCallback callback) {
        return native_CameraDelAudioFile(name, alias, callback);
    }

    /**
     * 获得设备音频文件信息
     * @param callback
     * @return native_CameraGetAudioFileInfo
     */
    public boolean getAudioFileInfo(getAudioFileCallback callback) {
        return native_CameraGetAudioFileInfo(callback);
    }

    /**
     *
     * @param start utc时间开始
     * @param end　utc时间
     * @param eventType　事件类型
     * @param callback　事件回调接口
     * @return false设备不在线　ture成功
     */
    public boolean getEventLog(timeDay start, timeDay end, int eventType,
                               getEventLogCallback callback) {
        return naive_CameraGetEventLog(start, end, eventType, callback);
    }

    /**
     * 查询设备信息或者版本号
     * @param callback
     * @return
     */
    public boolean queryDeviceInfo(deviceInfoCallback callback) {
        return native_CameraQuerDeviceInfo(callback);
    }

    /**
     * 设置视频清晰度
     * @param quality see VIDEO_QUALITY_
     * @param callback
     */
    public void setQuality(int quality, successCallback callback) {
        native_CameraSetVideoQuality(quality, callback);
    }

    /**
     * 查看视频是否开启
     * @return
     */
    public boolean isRecvVideoOn() {
        return native_CameraIsRecvVideo();
    }

    /**
     * 获取视频清晰度,设备上线了查询才有效 seeVIDEO_QUALITY
     * @return
     */
    public int getVideoQuality() {
        return  native_CameraGetVideoQuality();
    }

    /**
     * 查看音频接受是否开启
     * @return
     */
    public boolean isRecvAudioOn() {
        return native_CameraIsRecvAudio();
    }

    /**
     * 查看发送音频是否开启
     * @return
     */
    public boolean isSendAudioOn() {
        return native_CameraIsSendAudio();
    }

    /**
     * 查看设备状态
     * @return
     */
    public int checkStatus() {
        return native_CameraCheckStatus();
    }

    /**
     * 查询设备音频格式
     * @return
     */
    public int getAudioFormat() {
        return native_CameraGetAudioFormat();
    }

    /**
     * 开启发送音频
     * @param callback
     * @return
     */
    public int startSendAudio(sendAudioCallback callback) {
        return native_CameraStartSendAudio(callback);
    }

    /**
     * 停止音频发送
     */
    public void stopSendAudio() {
        native_CameraStopSendAudio();
    }

    /**
     * 开启接受视频数据
     * @param callback
     * @return
     */
    public int startRecvVideo(recvVideoCallback callback) {
        return native_CameraStartRecvVideo(callback);
    }

    /**
     * 停止接受视频数据
     */
    public void stopRecvVideo() {
        native_CameraStopRecvVideo();
    }

    /**
     * 开启音频接受
     * @param callback
     * @return
     */
    public int startRecvAudio(recvAudioCallback callback) {
        return native_CameraStartRecvAudio(callback);
    }

    /**
     * 停止音频接受
     */
    public void stopRecvAudio() {
        native_CameraStopRecvAudio();
    }

    /**
     * 连接设备
     * @param account
     * @param passwd
     * @param callback
     */
    public void connect(String account, String passwd, onOffLineCallback callback)
    {
        native_CameraConnect(account, passwd, callback);
    }

    /**
     * 重新连接设备
     * @param account
     * @param passwd
     * @param callback
     */
    public void reconnect(String account, String passwd, onOffLineCallback callback)
    {
        native_CameraReConnect(account, passwd, callback);
    }

    /**
     * 与设备断开连接
     */
    public void disconnect()
    {
        native_CameraDisconnect();
    }

    public Camera(String uid) {
        native_CameraCreate(uid);
    }

    /**
     * 释放资源
     */
    public void release()
    {
        native_CameraDestory();
    }


    private native int native_CameraCreate(String uid);
    private synchronized  native void native_CameraDestory();
    private synchronized native void native_CameraConnect(String account, String passwd,
                                                                onOffLineCallback callback);
    private synchronized  native  void native_CameraDisconnect();

    private synchronized native void native_CameraReConnect(String account, String passwd,
                                                          onOffLineCallback callback);

    private synchronized native int native_CameraStartRecvVideo(recvVideoCallback callback);
    private synchronized native  void native_CameraStopRecvVideo();

    private synchronized  native int native_CameraStartRecvAudio(recvAudioCallback callback);
    private synchronized  native  void native_CameraStopRecvAudio();

    private synchronized  native  int native_CameraStartSendAudio(sendAudioCallback callback);
    private synchronized  native void native_CameraStopSendAudio();

    private native boolean native_CameraIsRecvVideo();

    private native boolean native_CameraIsRecvAudio();

    private native boolean native_CameraIsSendAudio();

    private native int native_CameraCheckStatus();

    private native int native_CameraGetAudioFormat();

    private native int native_CameraGetVideoQuality();

    private native int native_CameraSetVideoQuality(int quality, successCallback callback);

    private native boolean native_CameraQuerDeviceInfo(deviceInfoCallback callback);

    private native boolean naive_CameraGetEventLog(timeDay start, timeDay end,
                                                   int event, getEventLogCallback callback);

    private native boolean native_CameraGetAudioFileInfo(getAudioFileCallback callback);

    private native boolean native_CameraPlayAudioFile(String name, String alias, successCallback callback);

    private native boolean native_CameraDelAudioFile(String name, String alias, successCallback callback);

    private native boolean native_CameraScreenShot(videoRecordingCallback callback);

    private native boolean native_CameraVideoRecordingStart(videoRecordingCallback callback);

    private native boolean native_CameraVideoRecordingStop(videoRecordingCallback callback);

    private native boolean native_CameraSetPhpServer(String ip, getPhpServerCallback callback);

    private native boolean native_CameraGetPhpServer(getPhpServerCallback callback);

    private native boolean native_CameraDelTimedFeeding(int cnt, successCallback callback);

    private native boolean native_CameraQueryFeedingSetting(int cnt, queryFeedingSettingCallback callback);

    private native boolean native_CameraQueryEachFeedingWeight(queryEachFeedingWeightCallback callback);

    private native boolean native_CameraSyncFeedingTime(successCallback callback);

    private native boolean native_CameraSetTimedFeeding(byte hour, byte minute,
                                                        int weight, byte cnt ,
                                                        byte sound, successCallback callback);
    private native boolean native_CameraSetTimedFeeding2(byte hour, byte minute,
                                                        int weight, byte cnt , byte on,
                                                        byte sound, successCallback callback);

    private native boolean native_CameraSetManualFeeding(int weight, byte sound,
                                                         successCallback callback);

    private native boolean native_CameraSendAudioFile(String file, String name, String alias,
                                                      successCallback callback);

    private native boolean native_CameraChangePasswd(String oldPass, String newPass,
                                                      successCallback callback);

    private native static void native_CameraSearchDevcie(searchDeviceInfoCallback callback);

    private native String native_CameraGetUID();

    private synchronized native void native_CameraRegisterListener(listenerDeviceEvent listener);

    private synchronized native void native_CameraUnregisterListener();

    private native boolean native_CameraSetEnvironmentMode(boolean mode, successCallback callback);

    private native boolean native_CameraGetEnvironmentMode(successCallback callback);

    private native boolean native_CameraListWifiAp(listWifiApCallback callback);

    private native boolean native_CameraSetWifiAp(String ssid, String passwd, byte encty,
                                                                    successCallback callback);

    private native int native_CameraSendFeedinData(byte [] data, feedingDataCallback callback);

    private long mNativePtr;

    static {
        try {
            System.loadLibrary("p2plib");
        } catch (UnsatisfiedLinkError ule) {
            System.out
                    .println("loadLibrary camer library error : " + ule.getMessage());
        }
    }
}
