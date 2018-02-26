package com.bobxie.multidownload.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.bobxie.multidownload.entities.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 下载服务
 */
public class DownloadService extends Service {

    public static final String DOWNLOAD_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/download/";

    public static final String INTENT_FILEINFO = "fileInfo";
    //开始下载命令
    public static final String ACTION_START = "action_start";
    //停止下载命令
    public static final String ACTION_STOP = "action_stop";
    //结束下载命令
    public static final String ACTION_FINISH = "action_finish";
    //更新UI
    public static final String ACTION_UPDATE = "action_update";

    public static final int MSG_INIT = 1;
    //下载任务集合
    private Map<Integer, DownloadTask> downloadTaskMap = new LinkedHashMap<>();

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ACTION_START.equals(intent.getAction())) {
            FileInfo fileInfo = intent.getParcelableExtra(INTENT_FILEINFO);
            Log.d("bob", "开始下载:" + fileInfo.toString());
            if (downloadTaskMap.containsKey(fileInfo.getId())) {
                DownloadTask downloadTask = downloadTaskMap.get(fileInfo.getId());
                if (downloadTask != null) {
                    downloadTask.isPause = false;
                }
            }
            InitThread initThread = new InitThread(fileInfo);
            initThread.start();
        } else if (ACTION_STOP.equals(intent.getAction())) {
            FileInfo fileInfo = intent.getParcelableExtra(INTENT_FILEINFO);
            Log.d("bob", "停止下载:" + fileInfo.toString());
            if (downloadTaskMap.containsKey(fileInfo.getId())) {
                DownloadTask downloadTask = downloadTaskMap.get(fileInfo.getId());
                if (downloadTask != null) {
                    downloadTask.isPause = true;
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.d("bob", "init:" + fileInfo.toString());
                    DownloadTask downloadTask = new DownloadTask(DownloadService.this, fileInfo, 5);
                    downloadTask.download();
                    //将下载任务添加到集合中
                    downloadTaskMap.put(fileInfo.getId(), downloadTask);
                    break;
            }
        }
    };

    /**
     * 初始化子线程
     */
    class InitThread extends Thread {
        private FileInfo fileInfo;

        public InitThread(FileInfo fileInfo) {
            this.fileInfo = fileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                //连接网络文件
                URL url = new URL(fileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(4000);
                conn.setRequestMethod("GET");
                int length = -1;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    length = conn.getContentLength();
                }
                //获得文件长度
                if (length <= 0) {
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                if (null != dir && !dir.exists()) {
                    dir.mkdir();
                }
                //在本地创建文件
                File file = new File(dir, fileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                //设置文件长度
                raf.setLength(length);
                fileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT, fileInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    conn.disconnect();
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
