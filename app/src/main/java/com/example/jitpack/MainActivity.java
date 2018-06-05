package com.example.jitpack;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.Petwant.www.Camera;
import com.Petwant.www.glVideoDecodec;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String UID = "SBZL55UAVH6X68Z3111A";
    private static final String ACCOUNT = "admin";
    private static final String PASSWORD = "888888";
    public static final String TAG = "camera";
    private Camera mCamera = null;
    private glVideoDecodec videoDecodec = null;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
//    private AcousticEchoCanceler canceler = null;

    private AudioTrack mAudioTrack = null; //音频播放
    private AudioRecord mAudioRecord = null; //录音

    private Handler mHandler = null;

    private Camera.listenerDeviceEvent listener = new Camera.listenerDeviceEvent() {
        @Override
        public void feedingAlarmCallback(int event, boolean manual) {

        }

        @Override
        public void feedingCompleteCallback(int h, int m, int weight, boolean manual) {
            Log.d(MainActivity.TAG, "###################feedingCompleteCallback##############");
        }

        @Override
        public void rawFeedingData(byte[] data) {
            Log.d(MainActivity.TAG, "###################rawFeedingData##############: " + data.length);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStartRecvVideo = findViewById(R.id.button_startRecvVideo);
        Button buttonConnect = findViewById(R.id.button_connect);
        Button buttonDisconnect = findViewById(R.id.button_disconnect);
        Button buttonReConnect = findViewById(R.id.button_reconnect);
        Button buttonStopRecvVideo = findViewById(R.id.button_sotpRecvVideo);
        Button buttonStartRecvAudio = findViewById(R.id.button_startRecvAudio);
        Button buttonStopRecvAudio = findViewById(R.id.button_stopRecvAudio);
        Button buttonStartSendAudio = findViewById(R.id.button_startSendAudio);
        Button buttonStopSendAudio = findViewById(R.id.button_stopSendAudio);
        Button buttonTakeJpeg = findViewById(R.id.button_takeJpeg);

        buttonTakeJpeg.setOnClickListener(this);
        buttonStartSendAudio.setOnClickListener(this);
        buttonStopSendAudio.setOnClickListener(this);
        buttonStopRecvAudio.setOnClickListener(this);
        buttonStartRecvAudio.setOnClickListener(this);
        buttonStopRecvVideo.setOnClickListener(this);
        buttonReConnect.setOnClickListener(this);
        buttonStartRecvVideo.setOnClickListener(this);
        buttonConnect.setOnClickListener(this);
        buttonDisconnect.setOnClickListener(this);

        int size = AudioTrack.getMinBufferSize(44100,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, // 指定在流的类型
                44100, AudioFormat.CHANNEL_OUT_MONO,// 设置输出声道为双声道立体声
                AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
                size, AudioTrack.MODE_STREAM);

        size = AudioRecord
                .getMinBufferSize(44100, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION, // 指定在流的类型
                8000, AudioFormat.CHANNEL_IN_MONO,// 设置输出声道为双声道立体声
                AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
                size);


//        if (AcousticEchoCanceler.isAvailable()) {
//            canceler = AcousticEchoCanceler.create(mAudioRecord.getAudioSessionId());
//            int error = canceler.setEnabled(true);
//            if (error != AudioEffect.SUCCESS)
//                Log.d(MainActivity.TAG, "AcousticEchoCanceler failed AudioEffect");
//        }

        videoDecodec = new glVideoDecodec();

        final SurfaceView surfaceView = findViewById(R.id.surfaceView);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();

        // mSurfaceHolder.getSurface();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.d(MainActivity.TAG, "surfaceDestroyed ...");

                videoDecodec.videoDecodecStop();
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(MainActivity.TAG, "surfaceCreated ...");


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
                Log.d(MainActivity.TAG, "surfaceChanged ...");
                videoDecodec.videoDecodecStart(holder.getSurface());

            }
        });


        mCamera = new Camera(UID);
        //mCamera = new Camera("F5YA9H5MK1ZCBG6GUHRJ");
        mCamera.registerListener(listener);

        mHandler = new MyHandler(this, mCamera);
    }

    @Override
    protected void onDestroy() {
//        if (AcousticEchoCanceler.isAvailable()) {
//            canceler.setEnabled(false);
//            canceler.release();
//        }

        if (mAudioTrack != null) {
            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
                mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }

        if (mAudioRecord != null) {
            if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }

        videoDecodec.videoDecodecStop();
        videoDecodec = null;
        if (mCamera.isRecvVideoOn())
            Log.d(MainActivity.TAG, "---------------######---------------");
        Log.d(MainActivity.TAG, "############### quality: " + mCamera.getVideoQuality());
        mCamera.unRegisterListener();
        Log.i(MainActivity.TAG, "release camera");
        mCamera.release();
        mCamera = null;

        System.gc();

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_connect: {
                if (mCamera != null) {
                    mCamera.connect(ACCOUNT, PASSWORD, new Camera.onOffLineCallback() {
                        @Override
                        public void Online(Camera m, final int isSuccess) {
                            Log.d(MainActivity.TAG, "camer ison: " + isSuccess);
                            if (isSuccess == 0) {
                                mHandler.post(() -> Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_LONG).show());
                                mHandler.sendEmptyMessage(1);
                            } else {
                                //mHandler.sendEmptyMessage(2);
                                mHandler.post(() -> Toast.makeText(MainActivity.this, "连接失败: " + isSuccess, Toast.LENGTH_LONG).show());
                            }
                        }

                        @Override
                        public void Offline(Camera m) {
                            mHandler.post(() -> Toast.makeText(MainActivity.this, "设备离线", Toast.LENGTH_LONG).show());
                            Log.d(MainActivity.TAG, "camera offline");
                        }
                    });
                }
            }
            break;
            case R.id.button_disconnect: {
                if (mCamera != null)
                    mCamera.disconnect();
            }
            break;
            case R.id.button_reconnect: {
                if (mCamera != null) {
                    mCamera.reconnect(ACCOUNT, PASSWORD, new Camera.onOffLineCallback() {
                        @Override
                        public void Online(Camera m, final int isSuccess) {
                            Log.d(MainActivity.TAG, "camer ison: " + isSuccess);
                            if (isSuccess == 0) {
                                mHandler.sendEmptyMessage(1);
                            } else {
                                //mHandler.sendEmptyMessage(2);
                                mHandler.post(() -> Toast.makeText(MainActivity.this, "连接失败: " + isSuccess, Toast.LENGTH_LONG).show());
                            }

                        }

                        @Override
                        public void Offline(Camera m) {
                            Log.d(MainActivity.TAG, "camera offline");
                        }
                    });
                }
            }
            break;
            case R.id.button_startRecvVideo: {
                if (mCamera != null) {
                    int ret = mCamera.startRecvVideo((data, pts, keyframe) -> {
//                            Log.d(MainActivity.TAG,
//                                    "video data lenght: " + data.length + " pts: " + pts + " key: " + keyframe);
//                          for (int i = 0; i < 6; ++i)
//                              Log.d(MainActivity.TAG, "data[" + i + "] = 0x" + Integer.toHexString(data[i]));
                        if (!isRunning.get() && keyframe == 1) {
                            videoDecodec.videoDecodec(data, pts);
                            isRunning.set(true);
                        } else if (isRunning.get())
                            videoDecodec.videoDecodec(data, pts);
                    });
                    Log.d(MainActivity.TAG, "start recv video ret: " + ret);
                }
            }
            break;
            case R.id.button_sotpRecvVideo: {
                if (mCamera != null) {
                    isRunning.set(false);
                    mCamera.stopRecvVideo();
                }
            }
            break;
            case R.id.button_startRecvAudio: {
                if (mCamera != null) {
                    if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
                        mAudioTrack.play();
                    mCamera.startRecvAudio((data, pts) -> {
//                            Log.d(MainActivity.TAG,
//                                    "audio data lenght: " + data.length + " pts: " + pts);
                        if (mCamera.getAudioFormat() == Camera.MEDIA_CODEC_AUDIO_G711A) {
                            if (mAudioTrack != null) {
                                mAudioTrack.write(data, 0, data.length);
                            }
                        } else {

                        }
                    });
                }
            }
            break;
            case R.id.button_stopRecvAudio: {
                if (mCamera != null) {
                    mCamera.stopRecvAudio();
                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
                        mAudioTrack.stop();
                }
            }
            break;
            case R.id.button_startSendAudio: {
                if (mCamera != null) {

                    if (mAudioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                        mAudioRecord.startRecording();
                    }

                    /*
                      返回实际可是数据大小
                     */
                    mCamera.startSendAudio(data -> {
                        Log.d(MainActivity.TAG, "send audio data length: " + data.length);

                        if (mCamera.getAudioFormat() == Camera.MEDIA_CODEC_AUDIO_G711A) {
                            /*
                              g711格式库内部已经解码或压缩成了pcm或者g711格式了
                             */
                            if (mAudioRecord != null)
                                mAudioRecord.read(data, 0, data.length);
                            return data.length;
                        } else {
                            /*
                              不是ｇ７１１ａ编码的
                              如:aac编码　读2048个pcm数据直接再压缩aac格式,返回压缩后的aac数据大小

                             */
                            return 1024;
                        }
//                            Random random = new Random();
//                            for (int i = 0; i < data.length; ++i)
//                                data[i] = (byte) random.nextInt(255);
                    });
                }
            }
            break;
            case R.id.button_stopSendAudio: {
                if (mCamera != null) {

                    mCamera.stopSendAudio();

                    if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING)
                        mAudioRecord.stop();
                }
            }
            break;
            case R.id.button_takeJpeg: {
                if (mCamera.isRecvVideoOn()) {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");//设置日期格式
                    String path = df.format(new Date()) + ".jpg";
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        try {
                            path = Environment.getExternalStorageDirectory().getCanonicalPath() + "/" + path;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(MainActivity.TAG, "############## take jpeg path: " + path);

//                    Toast.makeText(MainActivity.this, "###########take jpeg path: " + path,
//                            Toast.LENGTH_LONG).show();

                    int ret = videoDecodec.takeJpeg(path, success -> {
                        Log.d(MainActivity.TAG, "################# take jpeg success: " + success);
                        if (success) {
                            mHandler.sendEmptyMessage(6);
                        } else {
                            mHandler.sendEmptyMessage(7);
                        }
                    });
                    Log.d(MainActivity.TAG, "take jpeg ret: " + ret);
                } else {
                    Toast.makeText(MainActivity.this, "视频没有开启!", Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                break;
        }
    }

    public static class MyHandler extends Handler {
        private Context mContext;
        private Camera mCamera;

        MyHandler(Context context, Camera camera) {
            super();
            this.mContext = context;
            this.mCamera = camera;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    Toast.makeText(mContext, "连接成功!", Toast.LENGTH_LONG).show();
                    if (mCamera != null) {
                        Log.d(MainActivity.TAG, "audio format: " + mCamera.getAudioFormat());

                        mCamera.syncFeedingtime(issuccess -> Log.d(MainActivity.TAG, "sync feeding time: " + issuccess));


//                            mCamera.setQuality(Camera.VIDEO_QUALITY_MIDDLE, new Camera.successCallback() {
//                                @Override
//                                public void success(boolean issuccess) {
//                                    Log.d(MainActivity.TAG, "----------set quality: " + issuccess);
//                                    if (issuccess)
//                                        mHandler.sendEmptyMessage(3);
//                                    else
//                                        mHandler.sendEmptyMessage(4);
//                                }
//                            });
                        Log.d(MainActivity.TAG, "create ############### quality: " + mCamera.getVideoQuality());
                        mCamera.listWifiAp(ap -> {
                            Log.d(MainActivity.TAG, "-----------list wifi ap---------------------------");
                            if (ap != null) {
                                for (Camera.wifiAp anAp : ap) {
                                    Log.d(MainActivity.TAG, "ssid: " + anAp.ssid + " encty: " + anAp.enctype +
                                            " signal: " + anAp.signal + " status: " + anAp.status);
                                }
                            }
                        });


                        mCamera.queryDeviceInfo((sdcardTotal, sdcardFree, version1, version2, version3, version4, model, vendor) -> {
                            Log.d(MainActivity.TAG, "sdcard total: " + sdcardTotal +
                                    " sdcard free: " + sdcardFree);
                            Log.d(MainActivity.TAG, "version: " + version1 + "."
                                    + version2 + "." + version3 + "." + version4);
                            Log.d(MainActivity.TAG, "model: " + model);
                            Log.d(MainActivity.TAG, "vendor: " + vendor);
                            //mHandler.sendEmptyMessage(5);
                            MyHandler.this.post(() -> Toast.makeText(mContext, "收到设备信息!", Toast.LENGTH_LONG).show());
                        });
                        Camera.timeDay start = new Camera.timeDay((short) 2016,
                                (byte) 8, (byte) 6, (byte) 6, (byte) 14, (byte) 8, (byte) 0);
                        Camera.timeDay end = new Camera.timeDay((short) 2019,
                                (byte) 10, (byte) 6, (byte) 6, (byte) 14, (byte) 8, (byte) 0);

                        mCamera.getEventLog(start, end, Camera.EVENTLOG_ALL, l -> {
                            for (int i = 0; i < l.length; ++i) {
                                Log.d(MainActivity.TAG, (i + 1) + " : " + l[i].stTime.year +
                                        "-" + l[i].stTime.month + "-" + l[i].stTime.day + " "
                                        + l[i].stTime.hour + ":" + l[i].stTime.minute + ":"
                                        + l[i].stTime.second);
                                Log.d(MainActivity.TAG, (i + 1) + ": event type = " + l[i].event +
                                        " status: " + l[i].status);
                            }
                        });

//                            mCamera.getAudioFileInfo(new Camera.getAudioFileCallback() {
//                                @Override
//                                public void audioFileInfo(Camera.audioFileInfo[] info) {
//                                    for (int i = 0; i < info.length; ++i) {
//                                        Log.d(MainActivity.TAG, "name: " + info[i].sound_name + " " +
//                                                " alias: " + info[i].sound_alias);
//                                    }
//                                }
//                            });
//
//                            mCamera.playAudioFile("audio0", "zhaotao", new Camera.successCallback() {
//                                @Override
//                                public void success(boolean issuccess) {
//                                    if (issuccess) {
//                                        Log.d(MainActivity.TAG, "播放声音成功!");
//                                    } else {
//                                        Log.d(MainActivity.TAG, "播放声音失败!");
//                                    }
//                                }
//                            });
//
//                            mCamera.screenShot(new Camera.videoRecordingCallback() {
//                                @Override
//                                public void videoRecording(int err) {
//                                    Log.d(MainActivity.TAG, "screen shot: " + err);
//                                }
//                            });

//                            mCamera.videoRecordingStart(new Camera.videoRecordingCallback() {
//                                @Override
//                                public void videoRecording(int err) {
//                                    Log.d(MainActivity.TAG, "video recording start: " + err);
//                                }
//                            });
//
//                            mCamera.videoRecordingStop(new Camera.videoRecordingCallback() {
//                                @Override
//                                public void videoRecording(int err) {
//                                    Log.d(MainActivity.TAG, "video recording stop: " + err);
//                                }
//                            });
//                            mCamera.setPhpServer("http://35.166.173.148/JPJPSSer/server.php", new Camera.getPhpServerCallback() {
//                                @Override
//                                public void getPhpServer(boolean isSuccess, String ip) {
//                                    if (isSuccess)
//                                        Log.d(MainActivity.TAG, "set php server ok");
//                                    else
//                                        Log.d(MainActivity.TAG, "set php server failed");
//                                }
//                            });
//
//                            mCamera.getPhpServer(new Camera.getPhpServerCallback() {
//                                @Override
//                                public void getPhpServer(boolean isSuccess, String ip) {
//                                    if (isSuccess) {
//                                        Log.d(MainActivity.TAG, "get php server ok ip: " + ip);
//                                    } else {
//                                        Log.d(MainActivity.TAG, "get phpserver faled");
//                                    }
//                                }
//                            });

//                            mCamera.setManualFeeding(50, (byte)2, new Camera.successCallback() {
//                                @Override
//                                public void success(boolean issuccess) {
//                                    if (issuccess) {
//                                        Log.d(MainActivity.TAG, "manual feeding success!");
//                                    } else
//                                        Log.d(MainActivity.TAG, "manual feeding failed!");
//                                }
//                            });

                        for (int i = 1; i < 5; ++i) {
                            mCamera.queryFeedingSetting(i, (isSuccess, h, m, w, manual, sound) -> {
                                if (isSuccess) {
                                    Log.d(MainActivity.TAG, "h: " + h + " m: " + m + " w: " +
                                            w + " manual: " + manual + " sound: " + sound);
                                }
                            });
                        }

//                            for (int i = 1; i < 5; ++i) {
//                                mCamera.delTimedFeeding(i, new Camera.successCallback() {
//                                    @Override
//                                    public void success(boolean issuccess) {
//
//                                        Log.d(MainActivity.TAG, "delTimedFeeding : " + issuccess);
//
//                                    }
//                                });
//                            }

//                            mCamera.setTimedFeeding((byte) 20, (byte) 20, 200, (byte)2, (byte) 2, new Camera.successCallback() {
//                                @Override
//                                public void success(boolean issuccess) {
//                                    Log.d(MainActivity.TAG, "setTimedFeeding: " + issuccess);
//
//                                    if (mCamera != null) {
//                                        mCamera.setManualFeeding(50, (byte)2, new Camera.successCallback() {
//                                            @Override
//                                            public void success(boolean issuccess) {
//                                                if (issuccess) {
//                                                    Log.d(MainActivity.TAG, "manual feeding success!");
//                                                } else
//                                                    Log.d(MainActivity.TAG, "manual feeding failed!");
//                                            }
//                                        });
//                                    }
//
//                                }
//                            });

                        mCamera.setTimedFeeding2((byte) 7, (byte) 35, 200, (byte) 2, (byte) 1, (byte) 2, issuccess -> {
                            Log.d(MainActivity.TAG, "setTimedFeeding2: " + issuccess);

                            if (mCamera != null) {
                                mCamera.setManualFeeding(50, (byte) 2, issuccess12 -> {
                                    if (issuccess12) {
                                        Log.d(MainActivity.TAG, "manual feeding success!");
                                    } else
                                        Log.d(MainActivity.TAG, "manual feeding failed!");
                                });
                            }

                        });

                        mCamera.formatSdcard(issuccess -> Log.d(MainActivity.TAG, "format sdcard: " + issuccess));
                        //设备需要升级
                        mCamera.getTimezone(issuccess -> {
                            if (issuccess == -200) {
                                Log.d(MainActivity.TAG, "get timezone failed");
                            }
                            {
                                //与utc之间的小时差　如: +8代表中国utc+8　-8代表utc-8 -200:获取失败
                                Log.d(MainActivity.TAG, "get timezone: " + issuccess);
                                if (issuccess != 8) {
                                    ////和utc之间的时间差(单位小时) 如: 中国与utc相差８小时
                                    mCamera.setTimezone(8, issuccess1 -> {
                                        if (issuccess1) {
                                            Log.d(MainActivity.TAG, "set timezone success");
                                        } else
                                            Log.d(MainActivity.TAG, "set timezone failed");
                                    });

                                }
                            }
                        });
//                            ////和utc之间的时间差(单位小时) 如: 中国与utc相差８小时
//                            mCamera.setTimezone(0, new Camera.successCallback() {
//                                @Override
//                                public void success(boolean issuccess) {
//                                    if (issuccess) {
//                                        Log.d(MainActivity.TAG, "set timezone success");
//                                    } else
//                                        Log.d(MainActivity.TAG, "set timezone failed");
//                                }
//                            });

                        Camera.searchDevice(info -> {
                            if (info != null)
                                Log.d(MainActivity.TAG, "info length: " + info.length);
                            else
                                Log.d(MainActivity.TAG, "info length: " + 0);
                        });


                    }
                    break;
                case 2:
                    Toast.makeText(mContext, "连接失败!", Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    Toast.makeText(mContext, "设置清晰度成功!", Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Toast.makeText(mContext, "设置清晰度失败!", Toast.LENGTH_LONG).show();
                    break;
                case 5:
                    Toast.makeText(mContext, "收到设备信息!", Toast.LENGTH_LONG).show();
                    break;
                case 6:
                    Toast.makeText(mContext, "截图成功!", Toast.LENGTH_LONG).show();
                    break;
                case 7:
                    Toast.makeText(mContext, "截图失败!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }
}
