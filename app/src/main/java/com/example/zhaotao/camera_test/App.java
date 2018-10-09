package com.example.zhaotao.camera_test;

import android.app.Application;

import com.lzy.okgo.OkGo;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.getInstance().init(this);
    }
}
