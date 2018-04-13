package com.grt.filemanager.util;

import android.util.Log;

import com.grt.filemanager.BuildConfig;

public class LogUtils {

    public static boolean isDebug = BuildConfig.DEBUG;

    public static void i(String tag, String message) {
        if (isDebug) {
            Log.i(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (isDebug) {
            Log.e(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (isDebug) {
            Log.d(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (isDebug) {
            Log.v(tag, message);
        }
    }
}
