package com.example.zhaotao.camera_test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.SurfaceTexture;
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
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.zhaotao.camera_test.utils.TimePlanManager;
import com.example.zhaotao.camera_test.utils.TimeSlot;
import com.khj.Camera;
import com.khj.Muxing;
import com.khj.glVideoDecodec;
import com.socks.library.KLog;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.khj.Camera.AVIOCTRL_PTZ_RIGHT;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "camera";
    @BindView(R.id.textureView)
    TextureView mTextureView;
    @BindView(R.id.gridView)
    GridView gridView;
    @BindView(R.id.et_uid)
    EditText etUid;
    private Camera mCamera = null;
    private glVideoDecodec videoDecodec = null;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private AudioTrack mAudioTrack = null; //音频播放
    private AudioRecord mAudioRecord; //录音

    private SurfaceHolder mSurfaceHolder = null;


    private Handler mHandler;
    private Muxing muxing;//视频合成器，传入视频流，合成mp4或者mov


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        requestPerm();
        initGridView();
        mTextureView = (TextureView) findViewById(R.id.textureView);

        int size = AudioTrack.getMinBufferSize(8000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, // 指定在流的类型
                8000, AudioFormat.CHANNEL_OUT_MONO,// 设置输出声道为双声道立体声
                AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
                size, AudioTrack.MODE_STREAM);

        size = AudioRecord
                .getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, // 指定在流的类型
                8000, AudioFormat.CHANNEL_IN_MONO,// 设置输出声道为单声道
                AudioFormat.ENCODING_PCM_16BIT,// 设置音频数据块是8位还是16位
                size);

/*
        if (AcousticEchoCanceler.isAvailable()) {
            canceler = AcousticEchoCanceler.create(mAudioRecord.getAudioSessionId());
            int error = canceler.setEnabled(true);
            if (error != AudioEffect.SUCCESS)
                Log.d(MainActivity.TAG, "AcousticEchoCanceler failed AudioEffect");
        }*/

        videoDecodec = new glVideoDecodec();

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Surface surface1 = new Surface(surface);
                videoDecodec.videoDecodecStart(surface1);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Surface surface1 = new Surface(surface);
                videoDecodec.videoDecodecStart(surface1);
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                if (videoDecodec != null) {
                    videoDecodec.videoDecodecStop();
                }

                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Toast.makeText(MainActivity.this, "连接成功!", Toast.LENGTH_LONG).show();


                        /**
                         * 设置报警推送的服务器地址
                         */
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


                        //获取设备当前的时区
                        mCamera.getTimezone(new Camera.successCallbackI() {
                            @Override
                            public void success(int issuccess) {
                                if (issuccess == -200) {
                                    Log.d(MainActivity.TAG, "get timezone failed");
                                }
                                {
                                    //与utc之间的小时差　如: +8代表中国utc+8　-8代表utc-8 -200:获取失败
                                    Log.d(MainActivity.TAG, "get timezone: " + issuccess);
                                    if (issuccess != 8) {
                                        ////和utc之间的时间差(单位小时) 如: 中国与utc相差８小时
                                        mCamera.setTimezone(8, new Camera.successCallback() {
                                            @Override
                                            public void success(boolean issuccess) {
                                                if (issuccess) {
                                                    Log.d(MainActivity.TAG, "set timezone success");
                                                } else
                                                    Log.d(MainActivity.TAG, "set timezone failed");
                                            }
                                        });

                                    }
                                }
                            }
                        });
//                            ////和utc之间的时间差(单位分钟) 如: 中国与utc相差480分钟
//                            mCamera.setTimezone(0, new Camera.successCallback() {
//                                @Override
//                                public void success(boolean issuccess) {
//                                    if (issuccess) {
//                                        Log.d(MainActivity.TAG, "set timezone success");
//                                    } else
//                                        Log.d(MainActivity.TAG, "set timezone failed");
//                                }
//                            });


                        break;
                    case 2:
                        Toast.makeText(MainActivity.this, "连接失败!", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this, "设置清晰度成功!", Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(MainActivity.this, "设置清晰度失败!", Toast.LENGTH_LONG).show();
                        break;
                    case 5:
                        Toast.makeText(MainActivity.this, "收到设备信息!", Toast.LENGTH_LONG).show();
                        break;
                    case 6:
                        Toast.makeText(MainActivity.this, "截图成功!", Toast.LENGTH_LONG).show();
                        break;
                    case 7:
                        Toast.makeText(MainActivity.this, "截图失败!", Toast.LENGTH_LONG).show();
                        break;
                    default:
                }
            }
        };

        //mCamera = new Camera("L67HLF3NWXH866DU111A");
//        mCamera = new Camera("E5PUB57CYN7WLGPGYHZJ");
        //mCamera = new Camera("F5YA9H5MK1ZCBG6GUHRJ");

    }

    /**
     * 初始化网格
     */
    private void initGridView() {
        final List<String> stringList = new ArrayList<>();
        stringList.add("设备配网");
        stringList.add("连接设备");
        stringList.add("接受视频");
        stringList.add("发送音频");
        stringList.add("接受音频");
        stringList.add("手机录像");
        stringList.add("手机截图");
        stringList.add("视频质量设置");
        stringList.add("观看人数查看");
        stringList.add("云台转动");
        stringList.add("获取设备信息");
        stringList.add("SD卡视频图片");
        stringList.add("网络设置");
        stringList.add("画面翻转");
        stringList.add("录像设置");
        stringList.add("设备开关");
        stringList.add("声音设置");
        stringList.add("添加定时计划");
        stringList.add("断开连接");
        stringList.add("取消接受视频");
        stringList.add("修改设备密码");
        stringList.add("设置设备时区");
        stringList.add("移动侦测和声音侦测报警");
        stringList.add("格式化SD卡");
        stringList.add("设备广播");
        stringList.add("搜索局域网设备");
        stringList.add("设置心跳包");
        stringList.add("报警推送地址");
        stringList.add("回放SD卡视频");

        ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, R.layout.item_grid, R.id.tv_name, stringList);
        gridView.setAdapter(arrayAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position!=0&&position!=1){
//                    if (mCamera==null){
//                        Toast.makeText(MainActivity.this, "请输入uid连接设备", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
                Toast.makeText(MainActivity.this, stringList.get(position), Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, ConfigureNetActivity.class);
                        startActivity(intent);
                        break;
                    case 1://连接设备
                        connectCamera();
                        break;
                    case 2://接受视频
                        startReceiceVideo();
                        break;
                    case 3://发送音频
                        if (mCamera != null) {
                            if (mAudioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                                mAudioRecord.startRecording();
                            }
                            mCamera.startSendAudio(new Camera.sendAudioCallback() {
                                @Override
                                public int sendAudio(byte[] data) {
                                    Log.d(MainActivity.TAG, "send audio data length: " + data.length);
                                    if (mAudioRecord != null) {
                                        mAudioRecord.read(data, 0, data.length);
                                    }
                                    return data.length;

                                }
                            });
                        }

                        break;
                    case 4://接受音频
                        if (mCamera != null) {
                            if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
                                mAudioTrack.play();
                            mCamera.startRecvAudio(new Camera.recvAudioCallback() {
                                @Override
                                public void recvAudio(byte[] data, long pts) {
//                            Log.d(MainActivity.TAG,
//                                    "audio data lenght: " + data.length + " pts: " + pts);

                                    if (mAudioTrack != null)
                                        mAudioTrack.write(data, 0, data.length);

                                }
                            });
                        }


                        break;
                    case 5://手机录像
                        //录制视频
                        muxing = new Muxing();
                        File file = new File(Environment.getExternalStorageDirectory(), "haha.mp4");
//        File file=new File(Environment.getExternalStorageDirectory(),"haha.mov");
                        muxing.open(file.getAbsolutePath(),true);//合成mp4的需要音频则设置TURE，如果要生成的MP4没有音频则写FALSE
                        muxing.write("视频帧".getBytes(),false);//合成MP4需要同时写入视频和音频文件
                        muxing.write("音频帧".getBytes(),true);
                        //muxing.close();结束录制，生成文件
                        break;
                    case 6://手机截图，不推介使用以下方法，推介使用 Bitmap bitmap = textureView.getBitmap();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");//设置日期格式
                        String name = df.format(new Date()) + ".jpg";
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + name;
                        videoDecodec.takeJpeg(path, new glVideoDecodec.takeJpegCallback() {
                            @Override
                            public void takeJpeg(boolean b) {

                            }
                        });
                        break;
                    case 7://视频质量设置 1高清,3标清,5流畅
                        int videoQuality = mCamera.getVideoQuality();
                        mCamera.setQuality(1, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        break;
                    case 8://观看人数查看
                        mCamera.getNumberOfWatchVideo(new Camera.successCallbackI() {
                            @Override
                            public void success(int i) {

                            }
                        });


                        break;
                    case 9://云台转动 参数1方向，参数2一次转动多少步

                        mCamera.setPtz(AVIOCTRL_PTZ_RIGHT, 30);
//                        mCamera.setPtz(AVIOCTRL_PTZ_LEFT, 30);
//                        mCamera.setPtz(AVIOCTRL_PTZ_UP, 30);
//                        mCamera.setPtz(AVIOCTRL_PTZ_DOWN, 30);
//                        mCamera.setPtz(AVIOCTRL_PTZ_AUTO, 30);//自动巡航
                        break;
                    case 10://获取设备信息
                        mCamera.queryDeviceInfo(new Camera.deviceInfoCallback() {
                            @Override
                            //sdcardTotal 设备SD卡总容量MB，sdcardFree设备SD卡剩余容量
                            public void deviceInfo(int sdcardTotal, int sdcardFree, byte v1, byte v2, byte v3, byte v4, String model, String vendor, String s2) {
                                StringBuilder builder = new StringBuilder();
                                String version = builder.append(v2).append(".").append(v3).append(".").append(v4).toString();//固件版本
                            }
                        });
                        break;
                    case 11://SD卡视频图片

                        /**
                         * 查询设备SD卡上的图片
                         */

                        long date = date2Long("2018-05-10", "yyyy-MM-dd") / 1000;//传入某一天日期的时间戳，单位需要转换为秒
                        mCamera.listJpegFileStart(date);//开始查询
                        //该方法每调用一次返回下一页数据，调用listJpegFileStart后会重新从第一页开始查询
                        mCamera.listJpegfile(date, new Camera.listFileInfoCallback() {
                            @Override
                            public void listFileInfo(int i, Camera.fileInfo[] fileInfos) {
                                //i= 0: 后面还有文件 i=1:最后一个文件后面没有了 2：文件目录发生变化，请重新开始
                                if (i == 0) {
                                    //可以继续调用mCamera.listJpegfile，返回下一页数据
                                } else {
                                    //已经是最后一页了

                                }
                                if (fileInfos != null) {
                                    for (Camera.fileInfo fileInfo : fileInfos) {
                                        Log.i("shurun", fileInfo.filename);
                                        //可以调用 mCamera.downLoadFile(fileInfo.filename,,)下载

                                    }
                                }
                            }
                        });
//*************************************************************************************************************************************
                        /**
                         * 下载SD卡上的视频录像,使用方法与下载图片一致
                         */
                        long date2 = date2Long("2018-05-10", "yyyy-MM-dd") / 1000;//传入某一天日期的时间戳，单位需要转换为秒
                        mCamera.listvideoFile(date2, new Camera.listFileInfoCallback() {
                            @Override
                            public void listFileInfo(int i, Camera.fileInfo[] fileInfos) {

                            }
                        });
                        mCamera.listvideoFile(date2, new Camera.listFileInfoCallback() {
                            @Override
                            public void listFileInfo(int i, Camera.fileInfo[] fileInfos) {

                            }
                        });
                        //*************************************************************************************************************************************
                        /**
                         * 下载SD卡上的文件，包括视频和图片
                         *
                         */
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream("文件夹全路径");
                            mCamera.downLoadFile("查询到的SD卡上文件名", fileOutputStream, new Camera.downLoadFileCallback() {
                                @Override
                                //result=0成功，1文件不存在，2正在发送文件，3失败
                                public void complete(int result, byte[] bytes, long total, long current, Object o) {
                                    FileOutputStream o1 = (FileOutputStream) o;
                                    if (result == 0) {
                                        if (current >= total) {
                                            //下载完成
                                            int progress = (int) (current * 100 / total);
                                            KLog.i(progress + "total" + total);
                                            try {
                                                o1.write(bytes);
                                                o1.flush();
                                                o1.close();
                                                mCamera.cancelDownLoadFile();//下载完成需要调用该方法才能继续下载下一个文件
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            int progress = (int) (current * 100 / total);
                                            KLog.i(progress + "total" + total);
                                            try {
                                                o1.write(bytes);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        //下载失败
                                    }
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        break;
                    case 12://网络设置
                        //设备周围wifi信号
                        mCamera.listWifiAp(new Camera.listWifiApCallback() {
                            @Override
                            public void listWifiAp(Camera.wifiAp[] wifiAps) {

                            }
                        });
                        //切换wifi和热点模式
                        mCamera.switchingAp(true, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        break;
                    case 13://画面翻转
                        mCamera.getFlipping(new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        //0不翻转，1翻转
                        mCamera.setFlipping(1, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        break;
                    case 14://录像设置
                        //查询当前sd录像的模式
                        // 0关闭录像
                        // 1连续录像
                        // 2定时计划
                        // 3报警录像
                     mCamera.getVideoRecordType(new Camera.successCallbackI() {
                         @Override
                         public void success(int i) {

                         }
                     });
                     //设置SD卡录像模式
                     mCamera.setVideoRecordType(1, new Camera.successCallback() {
                         @Override
                         public void success(boolean b) {

                         }
                     });
                        break;
                    case 15://设备开关
                        //查询设备开关状态
                        mCamera.getForceOpenCamera(new Camera.successCallbackI() {
                            @Override
                            public void success(int i) {
                                // i==1代表开
                            }
                        });
                        mCamera.forceOpenCamera(true, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        break;
                    case 16://声音设置 设备音量大小0-100
                        mCamera.getDeviceVolume(new Camera.successCallbackI() {
                            @Override
                            public void success(int i) {

                            }
                        });
                        mCamera.setDeviceVolume(50, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        break;
                    case 17://添加定时计划
                        //获取定时开关机的计划
                        mCamera.getTimedCameraTask(new Camera.getTimedCameraTaskCallback() {
                            @Override
                            public void timedCameraTask(String s) {
                                //假如两个定时开关计划，8:00-9:00和13:00-19:00，代表8点关闭，9点打开，13点关闭，19点打开，每天执行。计划之间换行符分割
                              /*  String[] strings = s.split("\n");
                                for (String string : strings) {
                                    String[] split = string.split("-", 2);
                                    String closeTime = split[0];
                                    String openTime = split[1];
                                    //获取到的时间是秒为单位的时间戳，例如8:00-9:00，
                                    //格式化后为1970-01-02 08:00:00-1970-01-02 09:00:00
                                    KLog.e(closeTime+"*"+openTime);
                                    long close = Long.parseLong(closeTime)*1000;
                                    KLog.w("close"+long2String(close,"yyyy-MM-dd HH:mm:ss"));
                                    long open = Long.parseLong(openTime)*1000;
                                    KLog.w("open"+long2String(open,"yyyy-MM-dd HH:mm:ss"));


                                }*/
                                //从设备端获取已设置的任务列表，转化为ArrayList<TimeSlot>
                                ArrayList<TimeSlot> timeSlots = TimePlanManager.getInstance().fromPlan(s);
                                //把TimeSlot格式化为08:00-9:00
                                ArrayList<String> arrayList = TimePlanManager.getInstance().transformToString(timeSlots);
                                String task = "09:00-10:15";
                                String task1 = "22:00-01:55";
                                String task2 = "22:12-01:55";
                                TimePlanManager.getInstance().addTask(task);//增加一个新任务，新增或者修改的计划任务不能与已存在的计划时间重叠
                                TimePlanManager.getInstance().addTask(task1);//增加一个新任务
                                TimePlanManager.getInstance().updateTask(task2, 1);//更新列表中一个已存在的任务
                                String plan = TimePlanManager.getInstance().toPlan();//把计划集合转为设备端的字符串格式
                                //把更新后所有的任务设置给设备
                                mCamera.addTimedCameraTask(plan, new Camera.successCallback() {
                                    @Override
                                    public void success(boolean b) {

                                    }
                                });
                            }
                        });
                        //录像计划和设备开关计划所有方法一致，只是8:00-9:00代表八点开始录像，九点结束录像
                        mCamera.getTimedRecordVideoTask(new Camera.getTimedCameraTaskCallback() {
                            @Override
                            public void timedCameraTask(String s) {
                                //从设备端获取已设置的任务列表，转化为ArrayList<TimeSlot>
                                ArrayList<TimeSlot> timeSlots = TimePlanManager.getInstance().fromPlan(s);
                                //把TimeSlot格式化为08:00-9:00
                                ArrayList<String> arrayList = TimePlanManager.getInstance().transformToString(timeSlots);
                                String task = "09:00-10:15";
                                String task1 = "22:00-01:55";
                                String task2 = "22:12-01:55";
                                TimePlanManager.getInstance().addTask(task);//增加一个新任务
                                TimePlanManager.getInstance().addTask(task1);//增加一个新任务
                                TimePlanManager.getInstance().updateTask(task2, 1);//更新列表中一个已存在的数据
                                String plan = TimePlanManager.getInstance().toPlan();//把计划集合转为设备端的字符串格式
                                mCamera.addTimedRecordVideoTask(plan, new Camera.successCallback() {
                                    @Override
                                    public void success(boolean b) {

                                    }
                                });
                            }
                        });

                        break;
                    case 18://断开连接
                        mCamera.disconnect();//该方法只是断开了连接，mCamera还可以使用connect方法重新连接
                        mCamera.release();//这个方法将销毁对象，不能再使用
                        break;
                    case 19://停止接受视频
                        mCamera.stopRecvVideo();
                        break;
                    case 20://修改设备密码
                        mCamera.changePassword("888888", "666666", new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        break;
                    case 21://设置时区

                        TimeZone aDefault = TimeZone.getDefault();
                        int rawOffset = aDefault.getRawOffset();
                        rawOffset = rawOffset / (1000 * 60);//转为分钟，例如北京为+8时区，设置的时区为8*60=480
                        mCamera.setTimezone(rawOffset, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        break;
                    case 22://移动侦测和声音侦测报警
                        //查询移动侦测的开关状态
                        mCamera.getAlarmSwitch(new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        mCamera.setAlarmSwitch(true, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        //查询移动侦测灵敏度，等级1-5 5灵敏度最高
                        mCamera.getMotionDetect(new Camera.successCallbackI() {
                            @Override
                            public void success(int i) {

                            }
                        });
                        mCamera.setMotionDetect(3, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        //查询声音侦测报警开关
                        mCamera.geteSoundAlarm(new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        //开关声音侦测报警
                        mCamera.setSoundAlarm(true, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        //设备侦测报警后，设备端是否发出报警声音
                        mCamera.getAlarmVolume(new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });
                        //设置报警时设备是否发出报警声音
                        mCamera.setAlarmVolume(true, new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });

                        break;
                    case 23://格式化SD卡
                        /**
                         * 格式化sd卡
                         */
                        mCamera.formatSdcard(new Camera.successCallbackI() {
                            @Override
                            public void success(int issuccess) {
                                Log.d(MainActivity.TAG, "format sdcard: " + issuccess);
                            }
                        });
                        break;
                    case 24://设备广播
                        /**
                         * 获取当前观看视频的人数
                         */
                        mCamera.getNumberOfWatchVideo(new Camera.successCallbackI() {
                            @Override
                            public void success(int i) {

                            }
                        });
                        /**
                         * 设备注册广播回调，比如调用camere.forceOpenCamera成功后，设备会向所有注册该回调的
                         * 设备，发送广播，i代表时间类型，s包含时间的内容参数
                         */
                        mCamera.registerActivePush2(new Camera.activePushCallback() {
                            @Override
                            public void activePush(int i, String s) {
                                switch (i) {
                                    case 0://关闭摄像头
                                        KLog.d("关闭摄像头");

                                        break;
                                    case 1://打开摄像头


                                        break;
                                    case 2://设备端开始录制视频
                                        KLog.d("设备端开始录制视频");

                                        break;
                                    case 3://设备端停止录制视频
                                        KLog.d("设备端停止录制视频");

                                        break;
                                    case 4://当前观看视频的人数
                                        int audience = Integer.parseInt(s);
                                        break;
                                }
                            }
                        });


                        break;
                    case 25://搜索局域网设备
                        /**
                         * 搜索局域网设备
                         */
                        Camera.searchDevice(new Camera.searchDeviceInfoCallback() {
                            @Override
                            public void searchDeviceInfo(Camera.searchDeviceInfo[] info) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (info != null) {
                                            Log.d(MainActivity.TAG, "info length: " + info.length);
                                            Toast.makeText(MainActivity.this, "搜索到设备数量" + info.length, Toast.LENGTH_SHORT).show();
                                            etUid.setText(info[0].UID);
                                        } else {
                                            Toast.makeText(MainActivity.this, "局域网没有搜索到设备", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }
                        });
                        break;
                    case 26:
                        mCamera.setHeartbeatService("http://www.khjtecapp.com/smart-camera-ucenter/pushMsg", new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {
                                KLog.e("设置心跳包服务器地址成功");
                            }
                        });
                        mCamera.setHeartbeatTime(5, new Camera.successCallbackI() {
                            @Override
                            //i=0成功，-1未设置服务器地址
                            public void success(int i) {
                                KLog.e("设置成功" + i);
                            }
                        });

                        break;
                    case 27:

                   /*     http://39.107.250.53:8000/pushMsg
                        参数：deviceUid
                              msgType
                              timeStamp
                              imageName*/
                        mCamera.setPhpServer("www.hao123.com", new Camera.getPhpServerCallback() {
                            @Override
                            public void getPhpServer(boolean b, String s) {

                            }
                        });
                        //设备报警上传到上述setPhpServer接口的json数据包含图片的名称，可以连接设备后使用mCamera.downLoadFile()下载图片,
                        //但是，连接设备再从设备端下载图片可能由于网络等各种原因造成下载图片失败，为了确保下载图片成功，用户可以调用mCamera.setPictureUrl，
                        //设置一个服务器图片上传的接口，设置改接口后，设备会向该接口上传图片，设备在推送到setPhpServer接口的json数据中包含一个imgUrl字段，就是上传图片的地址，用户可以使用
                        //该url地址从服务器下载报警图片，服务器下载的url地址必须是"服务器上传报警图片接口/图片的名称"
                        mCamera.setPictureUrl("服务器上传报警图片接口", new Camera.successCallback() {
                            @Override
                            public void success(boolean b) {

                            }
                        });

                        break;
                    case 28:
                        //参数二是播放的初始偏移量，毫秒单位
                        mCamera.playBackVideoStart("文件名", 0, new Camera.playBackVideoCallback() {
                            @Override//i=0回放正常，bytes 数据，total 视频的I帧总数，current当前第几帧，current/total算出播放进度
                            public void playBackVideo(int i, byte[] bytes, long total, long current, boolean b) {
                                videoDecodec.videoDecodec(bytes,0);
                            }
                        }, new Camera.recvAudioCallback() {
                            @Override
                            public void recvAudio(byte[] bytes, long l) {
                                if (mAudioTrack != null)
                                    mAudioTrack.write(bytes, 0, bytes.length);

                            }

                        });
                        break;
                    case 29:
                        break;
                    case 30:
                        break;
                    case 31:
                        break;
                    case 32:
                        break;
                    case 33:
                        break;


                    default:
                }
            }
        });

    }

    private void startReceiceVideo() {
        if (mCamera != null) {
            int ret = mCamera.startRecvVideo(new Camera.recvVideoCallback() {
                @Override
                public void recvVideo(byte[] data, long pts, int keyframe) {
                    //如果需要把视频录制成mp4格式或者mov格式存到手机
//                    muxing.write(data);

                    if (!isRunning.get() && keyframe == 1 && videoDecodec != null) {
                        videoDecodec.videoDecodec(data, pts);
                        isRunning.set(true);
                    } else if (isRunning.get() && videoDecodec != null)
                        videoDecodec.videoDecodec(data, pts);
                }
            });
            Log.e(MainActivity.TAG, "start recv video ret: " + ret);
        }
    }


    /**
     * 通过uid，账号，密码连接设备
     */
    private void connectCamera() {
        String uid = etUid.getText().toString().trim();
        if (uid == null || uid.length() < 20) {
            Toast.makeText(this, "uid输入错误", Toast.LENGTH_SHORT).show();
            return;
        }
        mCamera = new Camera(uid);

        if (mCamera != null) {
            mCamera.connect("admin", "888888",0, new Camera.onOffLineCallback() {
                @Override
                public void Online(Camera m, final int isSuccess) {
                    Log.d(MainActivity.TAG, "camer ison: " + isSuccess);
                    if (isSuccess == 0) {
                        mHandler.sendEmptyMessage(1);
                    } else {
                        //mHandler.sendEmptyMessage(2);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "连接失败: " + isSuccess,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @Override
                public void Offline(Camera m) {
                    Log.d(MainActivity.TAG, "camera offline");
                }
            });
        }

    }


  /*  @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_connect: {
                String uid = editText.getText().toString().trim();
                if (uid == null || uid.length() < 20) {
                    Toast.makeText(this, "uid输入错误", Toast.LENGTH_SHORT).show();
                    return;
                }
                mCamera = new Camera(uid);

                if (mCamera != null) {
                    mCamera.connect("admin", "888888", new Camera.onOffLineCallback() {
                        @Override
                        public void Online(Camera m, final int isSuccess) {
                            Log.d(MainActivity.TAG, "camer ison: " + isSuccess);
                            if (isSuccess == 0) {
                                mHandler.sendEmptyMessage(1);
                            } else {
                                //mHandler.sendEmptyMessage(2);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "连接失败: " + isSuccess,
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
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
            case R.id.button_disconnect: {
                if (mCamera != null)
                    mCamera.disconnect();
            }
            break;
            case R.id.button_reconnect: {
                if (mCamera != null) {
                    mCamera.reconnect("admin", "888888", new Camera.onOffLineCallback() {
                        @Override
                        public void Online(Camera m, final int isSuccess) {
                            Log.d(MainActivity.TAG, "camer ison: " + isSuccess);
                            if (isSuccess == 0) {
                                mHandler.sendEmptyMessage(1);
                            } else {
                                //mHandler.sendEmptyMessage(2);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "连接失败: " + isSuccess,
                                                Toast.LENGTH_LONG).show();
                                    }
                                });
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
                    int ret = mCamera.startRecvVideo(new Camera.recvVideoCallback() {
                        @Override
                        public void recvVideo(byte[] data, int pts, int keyframe) {
                            //如果需要把视频录制成mp4格式或者mov格式存到手机
                            muxing.write(data);

                            if (!isRunning.get() && keyframe == 1 && videoDecodec != null) {
                                videoDecodec.videoDecodec(data, pts);
                                isRunning.set(true);
                            } else if (isRunning.get() && videoDecodec != null)
                                videoDecodec.videoDecodec(data, pts);
                        }
                    });
                    Log.e(MainActivity.TAG, "start recv video ret: " + ret);
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

                    if (mAudioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING)
                        mAudioRecord.startRecording();

                    mCamera.startSendAudio(new Camera.sendAudioCallback() {
                        @Override
                        *//**
     * 返回实际可是数据大小
     *//*
                        public int sendAudio(byte[] data) {
                            Log.d(MainActivity.TAG, "send audio data length: " + data.length);

                            if (mCamera.getAudioFormat() == Camera.MEDIA_CODEC_AUDIO_G711A) {
                                *//**
     * g711格式库内部已经解码或压缩成了pcm或者g711格式了
     *//*
                                if (mAudioRecord != null)
                                    mAudioRecord.read(data, 0, data.length);
                                return data.length;
                            } else {
                                */

    /**
     * 不是ｇ７１１ａ编码的
     * 如:aac编码　读2048个pcm数据直接再压缩aac格式,返回压缩后的aac数据大小
     *//*
                                return 1024;
                            }
//                            Random random = new Random();
//                            for (int i = 0; i < data.length; ++i)
//                                data[i] = (byte) random.nextInt(255);
                        }
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

                    int ret = videoDecodec.takeJpeg(path, new glVideoDecodec.takeJpegCallback() {
                        @Override
                        public void takeJpeg(boolean success) {
                            Log.d(MainActivity.TAG, "################# take jpeg success: " + success);
                            if (success)
                                mHandler.sendEmptyMessage(6);
                            else
                                mHandler.sendEmptyMessage(7);
                        }
                    });
                    Log.d(MainActivity.TAG, "take jpeg ret: " + ret);
                } else {
                    Toast.makeText(MainActivity.this, "视频没有开启!", Toast.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.addNet:
                Intent intent = new Intent(MainActivity.this, ConfigureNetActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }*/
    public void requestPerm() {
        AndPermission.with(this)
                .permission(
                        Permission.Group.MICROPHONE,
                        Permission.Group.STORAGE,
                        Permission.Group.CAMERA,
                        Permission.Group.LOCATION

                ).onGranted(new Action() {
            @Override
            public void onAction(List<String> permissions) {

            }
        }).onDenied(new Action() {
            @Override
            public void onAction(List<String> permissions) {
                Toast.makeText(MainActivity.this, "拒绝麦克风和存储权限无法录像和录音，截屏", Toast.LENGTH_SHORT).show();

            }
        })
                .start();
    }

    /**
     * 标准时间转换成时间戳
     */
    public static long date2Long(String _data, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(_data);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 将时间戳格式化
     *
     * @param time
     * @param format
     * @return
     */
    public static String long2String(long time, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = new Date(time);
            String s = simpleDateFormat.format(date);
            return s;
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected void onDestroy() {
      /*  if (AcousticEchoCanceler.isAvailable()) {
            canceler.setEnabled(false);
            canceler.release();
        }*/
        if (mCamera != null) {
            mCamera.stopRecvVideo();
            mCamera.stopSendAudio();
            mCamera.unRegisterListener();
            mCamera.release();
            mCamera = null;
        }
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


        super.onDestroy();
    }
}
