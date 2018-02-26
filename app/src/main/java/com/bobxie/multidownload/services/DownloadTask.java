package com.bobxie.multidownload.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bobxie.multidownload.db.dao.ThreadInfoDao;
import com.bobxie.multidownload.entities.FileInfo;
import com.bobxie.multidownload.entities.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * 下载任务类
 * Created by bob on 2018/2/25.
 */
public class DownloadTask {
    private Context mContext = null;
    private FileInfo mFileInfo = null;
    private ThreadInfoDao threadInfoDao = null;
    private int mFinished = 0;
    public boolean isPause = false;
    private int mThreadCount = 1;
    private List<DownloadThread> mThreadList = null;
    //线程池
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();


    public DownloadTask(Context context, FileInfo mFileInfo, int mThreadCount) {
        this.mContext = context;
        this.mFileInfo = mFileInfo;
        this.mThreadCount = mThreadCount;
        threadInfoDao = new ThreadInfoDao(context);
    }


    /**
     * 执行下载
     */
    public void download() {

        try {
            //读取数据库的线程信息
            List<ThreadInfo> threadInfos = threadInfoDao.getThreads(mFileInfo.getUrl());
            if (null == threadInfos || threadInfos.isEmpty()) {
                //获得每个线程下载长度
                long length = mFileInfo.getLength() / mThreadCount;
                for (int i = 0; i < mThreadCount; i++) {
                    ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), length * i, (i + 1) * length - 1, 0);
                    if (i == mThreadCount - 1) {
                        threadInfo.setEnd(mFileInfo.getLength());
                    }
                    //添加到线程信息到集合
                    threadInfos.add(threadInfo);
                    //向数据库插入线程信息
                    threadInfoDao.insetThread(threadInfo);
                }
            }

            mThreadList = new ArrayList<>();
            //执行多个线程开始下载
            for (ThreadInfo info : threadInfos) {
                DownloadThread thread = new DownloadThread(info);
                mExecutorService.execute(thread);
                mThreadList.add(thread);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 判断是否所有线程都执行完毕
     */
    private synchronized void checkAllThreadsFinshed() {
        boolean allFinished = true;
        //遍历线程集合，判断线程是否都执行完毕
        for (DownloadThread thread : mThreadList) {
            if (!thread.isFinished) {
                allFinished = false;
                break;
            }
        }
        if (allFinished) {
            //发送广播通知UI下载任务结束
            Intent intent = new Intent(DownloadService.ACTION_FINISH);
            intent.putExtra("fileInfo", mFileInfo);
            mContext.sendBroadcast(intent);

            //删除线程信息
            threadInfoDao.deleteThread(mFileInfo.getUrl());
        }
    }

    /**
     * 下载线程
     */
    class DownloadThread extends Thread {
        private ThreadInfo threadInfo = null;
        public boolean isFinished = false;//标识线程是否结束

        public DownloadThread(ThreadInfo threadInfo) {
            this.threadInfo = threadInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(threadInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(4000);
                conn.setRequestMethod("GET");
                //设置下载位置
                long start = threadInfo.getStart() + threadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
                //设置文件写入位置
                File file = new File(DownloadService.DOWNLOAD_PATH, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");
                raf.seek(start);

                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                mFinished += threadInfo.getFinished();

                //开始下载
                if (conn.getResponseCode() == HttpsURLConnection.HTTP_PARTIAL) {
                    //读取数据
                    inputStream = conn.getInputStream();
                    byte[] buffer = new byte[1024 * 1];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(buffer)) != -1) {
                        //写入文件
                        raf.write(buffer, 0, len);
                        //累加整个文件完成进度
                        mFinished += len;
                        //累加每个线程完成的进度
                        threadInfo.setFinished(threadInfo.getFinished() + len);

                        //把下载进度发送广播给Activity
                        if (System.currentTimeMillis() - time > 1000) {
                            time = System.currentTimeMillis();
                            int percent = (int) (mFinished * 100L / mFileInfo.getLength());
                            Log.d("bob", "当前完成：" + mFinished + " 总长度:" + mFileInfo.getLength() + " 已完成=" + percent + "%");
                            intent.putExtra("finished", percent);
                            intent.putExtra("id", mFileInfo.getId());
                            mContext.sendBroadcast(intent);
                        }
                        //在下载暂停时，保存下载进度
                        if (isPause) {
                            threadInfoDao.updateThread(threadInfo);
                            return;
                        }
                    }
                }
                //标识线程执行完成
                isFinished = true;
                checkAllThreadsFinshed();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != conn) {
                        conn.disconnect();
                    }
                    if (null != raf) {
                        raf.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
