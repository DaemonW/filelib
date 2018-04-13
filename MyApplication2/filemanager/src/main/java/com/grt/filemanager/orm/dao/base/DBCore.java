package com.grt.filemanager.orm.dao.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by liucheng on 2015/10/16
 */
public class DBCore {

    private static final String DEFAULT_DB_NAME = "Manager.db";
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static SQLiteDatabase db;
    private static final AtomicInteger lock = new AtomicInteger(0);

    private static Context mContext;
    private static String DB_NAME;

    //Befor Table Columns
    public final static String DATA = MediaStore.Files.FileColumns.DATA;
    public final static String TITLE = MediaStore.Files.FileColumns.TITLE;
    public final static String SIZE = MediaStore.Files.FileColumns.SIZE;

    //Befor Tables
    public static final String SEARCH_HISTORY = "search_history";
    public static final String ROOT_FILE = "root_file";
    public static final String TABLE_MUSIC_PLAY = "music_play";
    public static final String NAVIGATION = "navigation";
    public static final String SETTING = "setting";
    public static final String APPS = "apps";
    public static final String APKS = "apks";
    public static final String GCLOUD_ACCOUNT = "gcloud_account";
    public static final String GCLOUD_FILE = "gcloud_file";
    public static final String WIFI_DEVICE = "wifi_device";
    public static final String TASK_DOWNLOAD = "task_download";
    public static final String THREAD_DOWNLOAD = "thread_download";
    public static final String STATISTICS = "statistics";
    public static final String SPLASH = "splash";
    public static final String SYNC_SETTING = "sync_setting";
    public static final String FILEANALYSIS = "file_analysis";
    public static final String RECOMMEND = "recommend";
    public static final String MAIN_CUSTOMER = "main_customer";
    public static final String FAVORITE = "favorite";

    public static final String LABELFIlE = "label_file";
    public static final String LABEL = "label";
    public static final String Recycle_BIN = "recycle";

    public static final String SYNC_DATA = "sync_data";
    public static final String SYNC_HISTORY = "sync_history";
    public static final String SEARCH_FILE = "search";
    public static final String TABLE_SETTING = "setting";
    public static final String LOCAL_FILE = "local_file";

    public static final String FRAGMENT_INFO = "fragment_info";

    public static void initDB(Context context) {
        init(context, DEFAULT_DB_NAME);
    }

    public static void init(Context context, String dbName) {
        if (context == null) {
            throw new IllegalArgumentException("context can't be null");
        }
        enableQueryBuilderLog();
        mContext = context.getApplicationContext();
        DB_NAME = dbName;
    }

    public static DaoMaster getDaoMaster() {
        if (lock.get() > 0) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            lock.incrementAndGet();
        }

        if (daoMaster == null) {

            DaoMaster.OpenHelper helper = null;
            try {
                helper = new FeOpenHelper(mContext, DB_NAME, null);

                if (db != null && !db.isOpen()) {
                    db.close();
                }

                db = helper.getWritableDatabase();
                daoMaster = new DaoMaster(db);

            } catch (SQLiteDatabaseLockedException e) {
                e.printStackTrace();

                if (db != null) {
                    db.close();
                }

                if (helper != null) {
                    db = helper.getWritableDatabase();
                    daoMaster = new DaoMaster(db);
                }

            }
        }

        if (lock.get() > 0) {
            synchronized (lock) {
                lock.decrementAndGet();
                lock.notify();
            }
        }

        return daoMaster;
    }

    public static DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public static void closeDB() {
        if (daoSession != null) daoSession = null;
        if (daoMaster != null) daoMaster = null;
        if (db != null) db.close();
    }


    public static void enableQueryBuilderLog() {
        QueryBuilder.LOG_SQL = false;
        QueryBuilder.LOG_VALUES = false;
    }

    private static class FeOpenHelper extends DaoMaster.OpenHelper {

        public FeOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static ArrayList<String> needDropBasicTables() {
        ArrayList<String> tableList = new ArrayList<>();
        tableList.add(APKS);
        tableList.add(APPS);
        tableList.add(FILEANALYSIS);
        tableList.add(GCLOUD_ACCOUNT);
        tableList.add(GCLOUD_FILE);
        tableList.add(TABLE_MUSIC_PLAY);
        tableList.add(MAIN_CUSTOMER);
        tableList.add(NAVIGATION);
        tableList.add(RECOMMEND);
        tableList.add(ROOT_FILE);
        tableList.add(SEARCH_HISTORY);
        tableList.add(SETTING);
        tableList.add(SPLASH);
        tableList.add(STATISTICS);
        tableList.add(SYNC_SETTING);
        tableList.add(TASK_DOWNLOAD);
        tableList.add(THREAD_DOWNLOAD);
        tableList.add(WIFI_DEVICE);
        return tableList;
    }

}
