package com.grt.filemanager.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by LDC on 2018/3/8.
 */

public class PermissionUtils {
    public static final String[] WRITE_EXTERNAL_STORAGE_PER = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static boolean checkHasPermission(Activity mContext, String permission) {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
