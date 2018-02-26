package com.bobxie.multidownload.db.dao;

import android.content.Context;

import com.bobxie.multidownload.db.DatabaseHelper;
import com.bobxie.multidownload.entities.ThreadInfo;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by bob on 2018/2/25.
 */
public class ThreadInfoDao {
    private Dao<ThreadInfo, Integer> threadInfoDao;
    private DatabaseHelper helper;

    public ThreadInfoDao(Context context) {
        try {
            helper = DatabaseHelper.getHelper(context);
            threadInfoDao = helper.getDao(ThreadInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加
     *
     * @param fileInfo
     */
    public synchronized boolean insetThread(ThreadInfo fileInfo) {
        try {
            threadInfoDao.create(fileInfo);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除
     *
     * @param url
     * @return
     */
    public synchronized boolean deleteThread(String url) {
        try {
            threadInfoDao.deleteBuilder().where().eq("url",url);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新
     *
     * @param fileInfo
     * @return
     */
    public synchronized boolean updateThread(ThreadInfo fileInfo) {
        try {
            threadInfoDao.update(fileInfo);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 查询所有
     *
     * @return
     * @throws SQLException
     */
    public List<ThreadInfo> queryAll() throws SQLException {
        return threadInfoDao.queryBuilder().orderBy("id", true).query();
    }

    /**
     * 查询文件的线程信息
     *
     * @param url
     * @return
     * @throws SQLException
     */
    public List<ThreadInfo> getThreads(String url) throws SQLException {
        return threadInfoDao.queryForEq("url", url);
    }

    /**
     * 线程信息是否存在
     *
     * @param fileInfo
     * @return
     */
    public synchronized boolean isExists(ThreadInfo fileInfo) {
        try {
            List<ThreadInfo> threadInfos = threadInfoDao.queryForMatching(fileInfo);
            if (null != threadInfos && !threadInfos.isEmpty()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
