package com.daemonw.file.core.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.daemonw.file.FileConst;
import com.orhanobut.logger.Logger;

/**
 * Created by daemonw on 4/14/18.
 */

public class SdcardMountReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isUSBMounted = sp.getBoolean(FileConst.PREF_USB_MOUNTED, false);
        Uri rootUri = intent.getData();
        switch (action) {
            case Intent.ACTION_MEDIA_UNMOUNTED:
                if (rootUri != null) {
                    String path = rootUri.getLastPathSegment();
                    if (isUSBMounted) {
                        sp.edit().putString(FileConst.PREF_EXTERNAL_PATH, path).apply();
                    }
                    Logger.d("Media Mounted, Name = " + path);
                }
                break;
            case Intent.ACTION_MEDIA_MOUNTED:
                if (rootUri != null) {
                    String path = rootUri.getLastPathSegment();
                    if (!isUSBMounted) {
                        sp.edit().putString(FileConst.PREF_EXTERNAL_PATH, path).apply();
                    }
                    Logger.d("Media Unmounted, Name = " + path);
                }
                break;
            case Intent.ACTION_MEDIA_REMOVED:
                break;
            case Intent.ACTION_MEDIA_EJECT:
                break;
        }
    }
}
