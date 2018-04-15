package com.grt.daemonw.filelibyary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.grt.daemonw.filelibyary.Constant;
import com.grt.daemonw.filelibyary.file.ExtFile;
import com.grt.daemonw.filelibyary.reflect.Volume;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageUtil {
    private final static String LOG_TAG = StorageUtil.class.getSimpleName();
    public static final int REQUEST_ASK_PERMISSION = 0;

    public static List<Volume> getVolumes(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> volumeList = getVolumeList(storageManager);
        List<Volume> volumes = new ArrayList<>();
        for (StorageVolume v : volumeList) {
            Volume vol = Volume.fromStorageVolume(context, v);
            if (vol.getState().equals(Environment.MEDIA_MOUNTED)) {
                volumes.add(vol);
            }
        }
        return volumes;
    }

    public static Volume getVolume(Context context, int mountType) {
        List<Volume> volumes = getVolumes(context);
        for (Volume v : volumes) {
            if (v.mountType == mountType) {
                return v;
            }
        }
        return null;
    }


    private static List<StorageVolume> getVolumeList(StorageManager storageManager) {
        List<StorageVolume> volumeList = new ArrayList<>();
        try {
            if (BuildUtils.thanNougat()) {
                volumeList = storageManager.getStorageVolumes();
            } else if (BuildUtils.thanLollipop()) {
                Method method = StorageManager.class.getMethod("getVolumeList");
                StorageVolume[] result = (StorageVolume[]) method.invoke(storageManager);
                if (result != null && result.length > 0) {
                    Collections.addAll(volumeList, result);
                }
            } else {
                //not available under SDK 21
                Log.e(LOG_TAG, "method getVolumeList() is not supported");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return volumeList;
    }


    public static boolean hasExtSdcardPermission(Activity context) {
        String extCardPath = PreferenceManager.getDefaultSharedPreferences(context).getString(Constant.PREF_EXT_URI, null);
        if (extCardPath == null) {
            return false;
        }

        if (!isPersistedUri(context, extCardPath)) {
            return false;
        }

        ExtFile file = new ExtFile(context, extCardPath);
        try {
            ExtFile subFile = file.createNewFile("test.txt");
            if (subFile == null) {
                return false;
            }
            subFile.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isPersistedUri(Activity context, String uri) {
        boolean isPersisted = false;
        List<UriPermission> perms = context.getContentResolver().getPersistedUriPermissions();
        for (UriPermission p : perms) {
            if (p.getUri().toString().equals(uri)) {
                isPersisted = true;
                break;
            }
        }
        return isPersisted;
    }

    public static void requestPermission(Activity context) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        context.startActivityForResult(intent, REQUEST_ASK_PERMISSION);
    }

    public static void handlePermissionRequest(Activity context, int requestCode, int resultCode, Intent resultData) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_ASK_PERMISSION) {
            Uri treeUri = resultData.getData();
            DocumentFile pickedDir = DocumentFile.fromTreeUri(context, treeUri);
            Log.d(LOG_TAG, "ext_sdcard_uri = " + pickedDir.getUri().toString());
            if (treeUri == null) {
                return;
            }
            context.getContentResolver().takePersistableUriPermission(treeUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION |
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(Constant.PREF_EXT_URI, treeUri.toString()).apply();
        }
    }
}
