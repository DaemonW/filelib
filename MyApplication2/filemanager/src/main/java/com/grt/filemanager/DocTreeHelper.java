package com.grt.filemanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.grt.filemanager.util.BuildUtils;

/**
 * Created by LDC on 2018/3/19.
 */

public class DocTreeHelper {
    private static Context context;
    public static void startUsbGrantIntent(Activity activity) {
        if (BuildUtils.thanLollipop()) {
            try {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                activity.startActivityForResult(intent, ActivityResultHandler.USB_PERMISSION_GRANT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void startOpenDocTreeIntent(Activity activity) {
        if (BuildUtils.thanLollipop()) {
            try {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                activity.startActivityForResult(intent, ActivityResultHandler.EXTERNAL_SDCARD_GRANT_PERMISSION);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
