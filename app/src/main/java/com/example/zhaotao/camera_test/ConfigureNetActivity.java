package com.example.zhaotao.camera_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.socks.library.KLog;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.net.wifi.WifiManager.WIFI_STATE_ENABLED;

public class ConfigureNetActivity extends AppCompatActivity {

    @BindView(R.id.tv_selectWifi)
    Button tvSelectWifi;
    @BindView(R.id.et_pwd)
    EditText etPwd;
    @BindView(R.id.btn_createQRcode)
    Button btnCreateQRcode;
    @BindView(R.id.btn_ap)
    Button btnAp;
    @BindView(R.id.ivQRcode)
    ImageView ivQRcode;
    @BindView(R.id.btn_queryUID)
    Button btnQueryUID;
    private WiFiUtil wiFiUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_net);
        ButterKnife.bind(this);
        wiFiUtil = WiFiUtil.getInstance(getApplicationContext());
    }

    @OnClick({R.id.tv_selectWifi, R.id.btn_createQRcode, R.id.btn_ap,R.id.btn_queryUID})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_selectWifi:
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
                break;
            case R.id.btn_createQRcode:
                createEwm2();
                break;
            case R.id.btn_ap:
                String ssid = tvSelectWifi.getText().toString().trim();
                String pwd = etPwd.getText().toString().trim();
                if (ssid.equals("") || pwd.equals("")) {
                    Toast.makeText(this, "选择要连接的路由器SSID和密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, ApActivity.class);
                intent.putExtra("SSID", ssid);
                intent.putExtra("PWD", pwd);
                startActivity(intent);
                break;
            case R.id.btn_queryUID:
                OkGo.<String>post("http://www.khjtecapp.com/smart-camera-ucenter/temp/queryTempDevice")
                        .params("account","账号")
                        .params("appName","公司名称或者app名称")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                KLog.e(response.body());
                            }
                        });
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (wiFiUtil.checkState() == WIFI_STATE_ENABLED) {
            //获取的SSID带有双引号
            String temp = wiFiUtil.getSSID();
            KLog.e(temp);
            tvSelectWifi.setText(temp.replace("\"", ""));
        } else {

        }
    }

    /**
     * 第一种配网方式，二维码内容只包含wifi信息，设备扫描到wifi信息后，连接上wifi，然后app和设备处于局域网时使用 Camera.searchDevice()搜索局域网Uid查找设备
     */
    private void createEwm() {
        String ssid = tvSelectWifi.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
//        int wifiType=wiFiUtil.getType();
        int wifiType = 1;//1是wpa/wpa2加密方式

        StringBuilder builder = new StringBuilder();
        String wifiString = builder.append("SSID=").append(ssid).append(",")
                .append("PWD=").append(pwd).append(",")
                .append("TYPE=").append(wifiType).toString();

        int with = 500;
        Bitmap mBitmap = CodeUtils.createImage(wifiString, with, with, null);
        ivQRcode.setImageBitmap(mBitmap);
    }

    /**
     * 第二种配网方式，二维码内容只包含wifi信息与用户的账号信息，设备扫描到wifi信息后，连接上wifi，
     * 设备会将自己的Uid与二维码中的"A"字段和"U"字段绑定存储到服务器数据库，
     * 访问http://www.khjtecapp.com/smart-camera-ucenter/temp/queryTempDevice?account="账号"&appName="公司名称或者app名称",Post和GET都可以
     * 服务器返回就是设备UID，调用该接口需要在设备配网成功后五分钟内使用，否则数据会清空返回Null
     */
    private void createEwm2() {
        String ssid = tvSelectWifi.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
//        int wifiType=wiFiUtil.getType();
        int wifiType = 1;//1是wpa/wpa2加密方式

        StringBuilder builder = new StringBuilder();
        TimeZone aDefault = TimeZone.getDefault();
        int rawOffset = aDefault.getRawOffset();
        rawOffset = rawOffset / (1000 * 60);
        String wifiString = builder.
                append("S=").append(ssid).append(",")//wifi名称
                .append("P=").append(pwd).append(",")//wifi密码
                .append("T=").append(1).append(",")//加密方式
                .append("Z=").append(rawOffset).append(",")//设置设备时区，这个字段可以不设置
                .append("A=").append("15111520684").append(",")//用户账号account
                .append("U=").append("khj")//公司代号appName,使用字母简写，生成的二维码易于识别
                .toString();

        int with = 700;
        Bitmap mBitmap = CodeUtils.createImage(wifiString, with, with, null);
        ivQRcode.setImageBitmap(mBitmap);
    }

}
