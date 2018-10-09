package com.example.zhaotao.camera_test;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.socks.library.KLog;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by tp on 2018/3/8.
 */

public class WiFiUtil {
    private static final String TAG = "shurun";
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    //扫描出的设备热点
    private List<ScanResult> mApList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration;
    // 定义一个WifiLock
    private WifiManager.WifiLock mWifiLock;
    private static WiFiUtil util;
    /**
     * 单例方法
     *
     * @param context
     * @return
     */
    public static WiFiUtil getInstance(Context context) {
        if (util == null) {
            synchronized (WiFiUtil.class) {
                util = new WiFiUtil(context);
            }
        }
        return util;
    }
    // 构造器
    private WiFiUtil(Context context) {
        // 取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        mWifiList=new ArrayList<>(4);
        mApList=new ArrayList<>(4);
        KLog.e(mWifiInfo.getSSID());
    }
    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }
    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }
    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }
    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }
    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
    }
    // 创建一个WifiLock
    public void creatWifiLock(String lockName) {
        mWifiLock = mWifiManager.createWifiLock(lockName);
    }
    // 创建一个WifiLock
    public int getSigLevel(int rssi) {
       return WifiManager.calculateSignalLevel(rssi,4);
    }
    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfiguration;
    }
    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }
    public void startScan() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();
    }
    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }
    // 得到网络列表
    public List<ScanResult> getApList() {
        mApList.clear();
        for (int i = 0; i < mWifiList.size(); i++) {
            KLog.i(mWifiList.get(i).SSID);
            if (mWifiList.get(i).SSID.contains("camera_")){
                mApList.add(mWifiList.get(i));
            }
        }
        return mApList;
    }
    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder
                    .append("Index_" + new Integer(i + 1).toString() + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }
    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }
    // 得到接入点的BSSID
    public String getBSSID() {
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
    // 得到接入点的BSSID
    public String getSSID() {
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
    }

    /**
     *
     * @return 0代表无密码，1代表wpa/wpa2,2代表wep，这是设备端要求的，与安卓不同
     */
    public int getType() {
        startScan();
        // 重新取得WifiInfo对象
        String ssid=  mWifiManager.getConnectionInfo().getSSID();
        List<ScanResult> list = mWifiManager.getScanResults();

        for (ScanResult scResult : list) {

            if (!TextUtils.isEmpty(scResult.SSID) && scResult.SSID.equals(ssid)) {
                String capabilities = scResult.capabilities;
                Log.i("river","capabilities=" + capabilities);

                if (!TextUtils.isEmpty(capabilities)) {

                    if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                        Log.i("river", "wpa");
                        //设备端1代表WPA，而安卓1代表wep
                        return 1;

                    } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                        Log.i("river", "wep");
                        return 2;
                    } else {
                        Log.i("river", "no");
                        return 0;
                    }
                }
            }
        }
        //默认返回wpa类型
        return 1;
    }
    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }
    // 得到连接的ID
    public int getNetworkId() {
        mWifiInfo=mWifiManager.getConnectionInfo();
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }
    // 得到WifiInfo的所有信息包
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }
    // 添加一个网络并连接
    public void addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        boolean b = mWifiManager.enableNetwork(wcgID, true);
        System.out.println("a--" + wcgID);
        System.out.println("b--" + b);
    }
    /**
     * 添加WiFi网络
     *
     * @param SSID
     * @param password
     * @param type
     */
    public int addWiFiNetwork(String SSID, String password, Data type) {
        // 创建WiFi配置
        WifiConfiguration configuration = createWifiConfig(SSID, password, type);
        // 添加WIFI网络
        int networkId = mWifiManager.addNetwork(configuration);
        if (networkId == -1) {
            return -1;
        }
        // 使WIFI网络有效
        mWifiManager.enableNetwork(networkId, true);
        return networkId;
    }
    /**
     * 断开WiFi连接
     *
     * @param networkId
     */
    public void disconnectWiFiNetWork(int networkId) {
        // 设置对应的wifi网络停用
        mWifiManager.disableNetwork(networkId);
        // 断开所有网络连接
        mWifiManager.disconnect();
    }
    /**
     * 断开WiFi连接
     *
     *
     */
    public void disconnect() {
        // 设置对应的wifi网络停用
        mWifiManager.disableNetwork(getNetworkId());
        // 断开所有网络连接
        mWifiManager.disconnect();
    }
    /**
     * 创建WifiConfiguration
     * 三个安全性的排序为：WEP<WPA<WPA2。
     * WEP是Wired Equivalent Privacy的简称，有线等效保密（WEP）协议是对在两台设备间无线传输的数据进行加密的方式，
     * 用以防止非法用户窃听或侵入无线网络
     * WPA全名为Wi-Fi Protected Access，有WPA和WPA2两个标准，是一种保护无线电脑网络（Wi-Fi）安全的系统，
     * 它是应研究者在前一代的系统有线等效加密（WEP）中找到的几个严重的弱点而产生的
     * WPA是用来替代WEP的。WPA继承了WEP的基本原理而又弥补了WEP的缺点：WPA加强了生成加密密钥的算法，
     * 因此即便收集到分组信息并对其进行解析，也几乎无法计算出通用密钥；WPA中还增加了防止数据中途被篡改的功能和认证功能
     * WPA2是WPA的增强型版本，与WPA相比，WPA2新增了支持AES的加密方式
     *
     * @param SSID
     * @param password
     * @param type
     * @return
     **/
    public WifiConfiguration createWifiConfig(String SSID, String password, Data type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        WifiConfiguration tempConfig = isExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        if (type == Data.WIFI_CIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == Data.WIFI_CIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == Data.WIFI_CIPHER_WPA) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        } else if (type == Data.WIFI_CIPHER_WPA2) {
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }
    /**
     * 密码加密类型
     */
    public enum Data {
        WIFI_CIPHER_NOPASS(0), WIFI_CIPHER_WEP(1), WIFI_CIPHER_WPA(2), WIFI_CIPHER_WPA2(3);
        private final int value;
        //构造器默认也只能是private, 从而保证构造函数只能在内部使用
        Data(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }


    //通过反射的方式去判断wifi是否已经连接上，并且可以开始传输数据
    public boolean checkWiFiConnectSuccess() {
        Class classType = WifiInfo.class;
        try {
            Object invo = classType.newInstance();
            Object result = invo.getClass().getMethod("getMeteredHint").invoke(invo);
            return (boolean) result;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return false;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 切换到指定wifi
     * @param wifiName  指定的wifi名字
     * @param wifiPwd   wifi密码，如果已经保存过密码，可以传入null
     * @return
     */
    public boolean changeToWifi(String wifiName, String wifiPwd,Data type){
        if(mWifiManager == null){
            Log.i(TAG, " ***** init first ***** ");
            return false;
        }

        String __wifiName__ = "\"" + wifiName + "\"";
        printCurWifiInfo();

        List wifiList = mWifiManager.getConfiguredNetworks();
        boolean bFindInList = false;
        for (int i = 0; i < wifiList.size(); ++i) {
            WifiConfiguration wifiInfo0 = (WifiConfiguration) wifiList.get(i);

            // 先找到对应的wifi
            if (__wifiName__.equals(wifiInfo0.SSID) || wifiName.equals(wifiInfo0.SSID)) {
                // 1、 先启动，可能已经输入过密码，可以直接启动
                Log.i(TAG, " set wifi 1 = " + wifiInfo0.SSID);
                return doChange2Wifi(wifiInfo0.networkId);
            }

        }

        // 2、如果wifi还没有输入过密码，尝试输入密码，启动wifi
        if(!bFindInList){
            KLog.e("密码连接");
            WifiConfiguration wifiNewConfiguration = createWifiConfig(wifiName, wifiPwd,type);//使用wpa2的wifi加密方式
            int newNetworkId = mWifiManager.addNetwork(wifiNewConfiguration);
            if (newNetworkId == -1) {
                Log.e(TAG, "操作失败,需要您到手机wifi列表中取消对设备连接的保存");
            } else {
                return doChange2Wifi(newNetworkId);
            }
        }

        return false;
    }

    private boolean doChange2Wifi(int newNetworkId) {
        // 如果wifi权限没打开（1、先打开wifi，2，使用指定的wifi
        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }

        boolean enableNetwork = mWifiManager.enableNetwork(newNetworkId, true);
        if (!enableNetwork) {
            Log.e(TAG, "切换到指定wifi失败");
            return false;
        } else {
            Log.e(TAG, "切换到指定wifi成功");
            return true;
        }
    }
    public void printCurWifiInfo(){
        if(mWifiManager == null){
            return;
        }

        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        Log.i(TAG, "cur wifi = " + wifiInfo.getSSID());
        Log.i(TAG, "cur getNetworkId = " + wifiInfo.getNetworkId());
    }
    // 查看以前是否也配置过这个网络
    public WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

}
