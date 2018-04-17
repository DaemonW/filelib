package com.grt.daemonw.filelibrary.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.grt.daemonw.filelibrary.Constant;

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

        switch (action) {
            case Intent.ACTION_MEDIA_UNMOUNTED:
                Intent intent1 = intent;
                int a1 = 2;
                break;
            case Intent.ACTION_MEDIA_MOUNTED:
                Uri rootUri = intent.getData();
                if (rootUri != null) {
                    String path = rootUri.getLastPathSegment();
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constant.PREF_EXTERNAL_PATH, path).apply();
                }
                break;
            case Intent.ACTION_MEDIA_REMOVED:
                break;
            case Intent.ACTION_MEDIA_EJECT:
                break;
        }
    }
}
