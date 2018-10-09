package com.ikecin.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.Petwant.www.Camera;

import java.util.ArrayList;
import java.util.HashMap;

public class LanSearch extends AppCompatActivity {
    private static String TAG = LanSearch.class.getSimpleName();
    private SimpleAdapter mSimpleAdapter;
    private ArrayList<HashMap<String, String>> mListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lan_search);

        mSimpleAdapter = new SimpleAdapter(
                this,
                mListData,
                android.R.layout.simple_list_item_2,
                new String[]{"uid", "ip"}, new int[]{android.R.id.text1, android.R.id.text2}
        );

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(mSimpleAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("uid", mListData.get(position).get("uid"));
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Toast.makeText(this, "正在搜索设备", Toast.LENGTH_SHORT).show();

        mListData.clear();
        mSimpleAdapter.notifyDataSetChanged();

        Log.i(TAG, "search cameras");
        //fixme:如果从摄像头页面返回，执行该操作会crash，可能原因是摄像头页面的destroy中调用了release，release是在onResume后！！！
        Camera.searchDevice(searchDeviceInfos -> {
            if (searchDeviceInfos == null) {
                runOnUiThread(() -> Toast.makeText(this, "没有找到设备", Toast.LENGTH_SHORT).show());
                return;
            }
            for (final Camera.searchDeviceInfo deviceInfo : searchDeviceInfos) {
                Log.i(TAG, deviceInfo.UID);
                mListData.clear();
                mListData.add(new HashMap<String, String>() {{
                    put("uid", deviceInfo.UID.trim());
                    put("ip", deviceInfo.IP.trim() + ": " + String.valueOf(deviceInfo.port));
                }});
                runOnUiThread(() -> mSimpleAdapter.notifyDataSetChanged());
            }
        });
    }
}
