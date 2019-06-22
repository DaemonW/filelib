package com.daemonw.file;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.Logger;

public class App extends MultiDexApplication {
    private static Handler mHandler;
    private static App mApp;
    private static SharedPreferences mSetting;

    public App() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        initAppContext();
    }

    public void initAppContext() {
        mHandler = new Handler(getMainLooper());
        mSetting = PreferenceManager.getDefaultSharedPreferences(this);
        Logger.addLogAdapter(new DiskLogAdapter());
        //Logger.addLogAdapter(new AndroidLogAdapter());
        CrashHandler.getInstance().init(this, true);
    }

    public static App getApp() {
        return mApp;
    }

    public static Handler getAppHandler() {
        return mHandler;
    }

    public static SharedPreferences getSetting() {
        return mSetting;
    }
}
