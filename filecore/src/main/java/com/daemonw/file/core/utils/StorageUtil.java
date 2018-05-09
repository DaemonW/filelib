package com.daemonw.file.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.daemonw.file.FileConst;
import com.daemonw.file.core.exception.PermException;
import com.daemonw.file.core.reflect.Volume;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageUtil {
    private final static String LOG_TAG = StorageUtil.class.getSimpleName();

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


    public static String getMountPath(Activity context, int mountPoint) throws PermException {
        List<Volume> volumes = StorageUtil.getVolumes(context);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String rootPath = null;
        for (Volume v : volumes) {
            if (v.mountType != mountPoint) {
                continue;
            }
            if (!StorageUtil.hasWritePermission(context, v.mountType)) {
                throw new PermException(getPermMessage(v.mountType), v.mountType);
            }
//            if (v.mountType == Volume.MOUNT_EXTERNAL) {
//                rootPath = v.mPath;
//                if (!new File(rootPath).canRead()) {
//                    rootPath = sp.getString(FileConst.PREF_EXTERNAL_URI, null);
//                }
//            } else if (v.mountType == Volume.MOUNT_USB) {
//                rootPath = v.mPath;
//                if (!new File(rootPath).canRead()) {
//                    rootPath = sp.getString(FileConst.PREF_USB_URI, null);
//                }
//            } else {
//                rootPath = v.mPath;
//            }
            rootPath = v.mPath;
        }
        return rootPath;
    }

    public static String getMountUri(Context context, int mountType) {
        String uri = null;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        switch (mountType) {
            case Volume.MOUNT_INTERNAL:
                break;
            case Volume.MOUNT_EXTERNAL:
                uri = sp.getString(FileConst.PREF_EXTERNAL_URI, null);
                break;
            case Volume.MOUNT_USB:
                uri = sp.getString(FileConst.PREF_USB_URI, null);
                break;
        }
        return uri;
    }

    private static String getPermMessage(int mountType) {
        String msg = FileConst.MSG_NO_PERM;
        if (mountType == Volume.MOUNT_EXTERNAL) {
            msg = FileConst.MSG_NO_PERM_ON_EXTERNAL;
        } else if (mountType == Volume.MOUNT_USB) {
            msg = FileConst.MSG_NO_PERM_ON_USB;
        }
        return msg;
    }


    public static DocumentFile findDocumentFile(Context context, String filePath, String rootPath, String rootUri) {
        if (filePath == null || rootPath == null || rootUri == null) {
            return null;
        }
        if (!filePath.startsWith(rootPath)) {
            return null;
        }
        DocumentFile rootFile = DocumentFile.fromTreeUri(context, Uri.parse(rootUri));
        if (filePath.equals(rootPath)) {
            return rootFile;
        }
        int startIndex = rootPath.length();
        if (!rootPath.endsWith("/")) {
            startIndex = startIndex + 1;
        }
        String relativePath = filePath.substring(startIndex);
        String[] pathSegments = relativePath.split("/");
        if (pathSegments == null || pathSegments.length == 0) {
            return rootFile;
        }
        DocumentFile file = rootFile;
        for (String name : pathSegments) {
            file = file.findFile(name);
            if (file == null) {
                break;
            }
        }
        return file;
    }

    public static boolean hasWritePermission(Activity context, int mountType) {
        if (mountType == Volume.MOUNT_INTERNAL) {
            return true;
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String mediaPath = null;
        if (mountType == Volume.MOUNT_EXTERNAL) {
            mediaPath = sp.getString(FileConst.PREF_EXTERNAL_URI, null);
        } else if (mountType == Volume.MOUNT_USB) {
            mediaPath = sp.getString(FileConst.PREF_USB_URI, null);
        }
        if (mediaPath == null) {
            return false;
        }

        if (!isPersistedUri(context, mediaPath)) {
            return false;
        }

        DocumentFile file = DocumentFile.fromTreeUri(context, Uri.parse(mediaPath));
        if (file.canWrite()) {
            return true;
        }
        try {
            DocumentFile subFile = file.createFile(MimeTypes.getMimeType(FileConst.DUMB_FILE), FileConst.DUMB_FILE);
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

    private static boolean isPersistedUri(Activity context, String uri) {
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
}
