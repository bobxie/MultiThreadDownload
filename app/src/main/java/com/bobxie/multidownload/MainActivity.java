package com.bobxie.multidownload;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.bobxie.multidownload.adapter.FileListAdapter;
import com.bobxie.multidownload.entities.FileInfo;
import com.bobxie.multidownload.services.DownloadService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private List<FileInfo> fileInfos = new ArrayList<>();
    private ListView mListView;
    private FileListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mListView = findViewById(android.R.id.list);


        initData();

        adapter = new FileListAdapter(this, fileInfos);
        mListView.setAdapter(adapter);

        //注册广播接收
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_UPDATE);
        intentFilter.addAction(DownloadService.ACTION_FINISH);
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        for (int i = 0; i < 5; i++) {
            FileInfo fileInfo = new FileInfo(i, "http://shouji.360tpcdn.com/161105/dbb921f21f2bfec3aa42ce732f625ed3/com.bobxie.greenwallpaper_6.apk"
                    , "greenwallpaper" + i + ".apk", 0, 0);
            fileInfos.add(fileInfo);
        }
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
                //更新进度条
                int id = intent.getIntExtra("id", 0);
                int finished = intent.getIntExtra("finished", 0);
                adapter.updateProgressBar(id, finished);
            } else if (DownloadService.ACTION_FINISH.equals(intent.getAction())) {
                //下载结束
                FileInfo fileInfo = intent.getParcelableExtra("fileInfo");
                adapter.updateProgressBar(fileInfo.getId(), 100);
                Toast.makeText(MainActivity.this, fileInfos.get(fileInfo.getId()).getFileName() + "下载完成", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
