package com.daemonw.filelib.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.daemonw.filelib.FileConst;
import com.daemonw.filelib.reflect.Volume;

public class PermissionUtil {
    private static final String LOG_TAG = PermissionUtil.class.getSimpleName();

    public static void requestPermission(Activity context, int mountType) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        int requestCode = mountType == Volume.MOUNT_EXTERNAL ? FileConst.REQUEST_GRANT_EXTERNAL_PERMISSION : FileConst.REQUEST_GRANT_USB_PERMISSION;
        context.startActivityForResult(intent, requestCode);
    }

    public static void handlePermissionRequest(Activity context, int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            Uri treeUri = resultData.getData();
            if (treeUri == null) {
                return;
            }
            DocumentFile pickedDir = DocumentFile.fromTreeUri(context, treeUri);
            Log.d(LOG_TAG, "external_storage_uri = " + pickedDir.getUri().toString());
            context.getContentResolver().takePersistableUriPermission(treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            if (requestCode == FileConst.REQUEST_GRANT_EXTERNAL_PERMISSION) {
                sp.edit().putString(FileConst.PREF_EXTERNAL_URI, treeUri.toString()).apply();
            } else if (requestCode == FileConst.REQUEST_GRANT_USB_PERMISSION) {
                sp.edit().putString(FileConst.PREF_USB_URI, treeUri.toString()).apply();
            }
        }
    }
}
