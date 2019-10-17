# C1 Camera Android SDK

[![](https://jitpack.io/v/iKECINCode/c1-camera-sdk-android.svg)](https://jitpack.io/#iKECINCode/c1-camera-sdk-android)

## Description

This is description.

## Usage

### Gradle Dependency 

- Project level `build.gradle`

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

- App level `build.gradle`

```gradle
dependencies {
    implementation 'com.github.iKECINCode:c1-camera-sdk-android:x.x.x'
}
```

## 配网

最新固件仅支持二维码扫描配网的方式

### 二维码生成规则

#### WiFi无密码


`SSID=ssid,PWD=,TYPE=0`

#### WiFi有密码

`SSID=ssid,PWD=password,TYPE=1`

## API文档

https://github.com/TopCode280/MonitorGitBook
