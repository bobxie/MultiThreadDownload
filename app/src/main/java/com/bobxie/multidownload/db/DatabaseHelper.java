package com.bobxie.multidownload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bobxie.multidownload.entities.ThreadInfo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "download.db";
    public static final int DATABASE_VERSION = 1;// 数据库版本

    private Map<String, Dao> daos = new HashMap<>();

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            Log.d("bob","开始创建表");
            TableUtils.createTable(connectionSource, ThreadInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        /*if (oldVersion < 2) {
            try {
                getUserDao().executeRaw("ALTER TABLE `tb_user_info` ADD COLUMN age INTEGER DEFAULT 0;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (oldVersion < 3) {
            try {
                getUserDao().executeRaw("ALTER TABLE `tb_user_info` ADD COLUMN addr TEXT DEFAULT 'china';");
                getUserDao().executeRaw("ALTER TABLE `tb_user_info` ADD COLUMN sex TEXT DEFAULT '男';");
                getUserDao().updateRaw("UPDATE `tb_user_info` SET sex = '女' WHERE gender = 1;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }*/
    }

    private static DatabaseHelper instance;

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        context = context.getApplicationContext();
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();

        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }

}