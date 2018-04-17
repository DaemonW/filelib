package com.grt.daemonw.filelibrary.reflect;

import android.content.Context;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.PreferenceManager;
import android.util.Log;

import com.grt.daemonw.filelibrary.Constant;
import com.grt.daemonw.filelibrary.file.AbstractFile;
import com.grt.daemonw.filelibrary.utils.BuildUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.lang.reflect.Method;

public class Volume {
    public static final String LOG_TAG = Volume.class.getSimpleName();

    private Object storageVolume;
    public int mStorageId;
    public String mPath;
    public int mountType;
    private String mState;
    public String mDescription;
    public boolean mIsPrimary;
    public boolean mIsRemovable;

    public static final int MOUNT_UNKNOWN = -1;
    public static final int MOUNT_INTERNAL = 0;
    public static final int MOUNT_EXTERNAL = 1;
    public static final int MOUNT_USB = 2;

    private Volume() {

    }


    public static Volume fromStorageVolume(Context context, StorageVolume volume) {
        Volume v = mirrorObj(context, volume);
        v.mountType = v.getVolumeType(context);
        return v;
    }

    private static Volume mirrorObj(Context context, StorageVolume volume) {
        Volume v = new Volume();
        v.storageVolume = volume;
        File file = (File) ReflectUtil.getPrivateField(volume, "mPath");
        v.mPath = file.getAbsolutePath();
        v.mStorageId = (int) ReflectUtil.getPrivateField(volume, "mStorageId");
        v.mState = (String) ReflectUtil.getPrivateField(volume, "mState");
        v.mIsRemovable = (boolean) ReflectUtil.getPrivateField(volume, "mRemovable");
        v.mIsPrimary = (boolean) ReflectUtil.getPrivateField(volume, "mPrimary");
        Method method = ReflectUtil.getPublicMethod(volume, "getDescription", Context.class);
        v.mDescription = (String) ReflectUtil.invokeMethod(method, volume, context);
        return v;
    }

    private int getVolumeType(Context context) {

        //is internal
        if (mIsPrimary && !mIsRemovable) {
            return MOUNT_INTERNAL;
        }
        //get type upon android 6.0
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        if (BuildUtils.thanMarshmallow()) {
            int type = getVolumeTypeSinceMarshmallow(storageManager);
            if (type != MOUNT_UNKNOWN) {
                return type;
            }
        }
        //get type under android 6.0
        //judge by path
        String extSdPath = PreferenceManager.getDefaultSharedPreferences(context).getString(Constant.PREF_EXTERNAL_PATH, null);
        Logger.e("extSd card = "+extSdPath);
        if (extSdPath != null) {
            if (mPath.contains(extSdPath)) {
                return MOUNT_EXTERNAL;
            }
        }
        //judge by label
        String label = mDescription.toUpperCase();
        if (label.contains("USB") || label.contains("OTG")) {
            return MOUNT_USB;
        }
        if (label.contains("SD") || label.contains("CARD")) {
            return MOUNT_EXTERNAL;
        }
        //judge by path
        String path = mPath.toUpperCase();
        if (path.contains("USB") || path.contains("OTG")) {
            return MOUNT_USB;
        }
        if (path.contains("SD") || path.contains("CARD")) {
            return MOUNT_EXTERNAL;
        }
        //juage by secondary_storage property
        String secondaryStorage = System.getenv("SECONDARY_STORAGE");
        Logger.e("secondary path = "+secondaryStorage);
        if (secondaryStorage != null) {
            if (secondaryStorage.equals(extSdPath)) {
                return MOUNT_EXTERNAL;
            }
            if (secondaryStorage.toUpperCase().contains("SD") || secondaryStorage.toUpperCase().contains("CARD")) {
                return MOUNT_EXTERNAL;
            }
        }
        return MOUNT_USB;
    }

    private int getVolumeTypeSinceMarshmallow(StorageManager storageManager) {
        String volumeId = (String) ReflectUtil.getPrivateField(storageVolume, "mId");
        Method volumeMethod = ReflectUtil.getPublicMethod(storageManager, "findVolumeById", String.class);
        if (volumeMethod == null) {
            Log.e(LOG_TAG, "can't find method findVolumeById in class StorageManager");
            return MOUNT_UNKNOWN;
        }
        Object volumeInfo = ReflectUtil.invokeMethod(volumeMethod, storageManager, volumeId);
        Object diskInfo = ReflectUtil.getPublicField(volumeInfo, "disk");
        if (diskInfo == null) {
            Log.e(LOG_TAG, "can't find filed \'disk\' in class VolumeInfo");
            return MOUNT_UNKNOWN;
        }

        Method isSdMethod = ReflectUtil.getPublicMethod(diskInfo, "isSd");
        boolean isSd = (boolean) ReflectUtil.invokeMethod(isSdMethod, diskInfo);
        if (isSd) {
            return Volume.MOUNT_EXTERNAL;
        }
        Method isUsbMethod = ReflectUtil.getPublicMethod(diskInfo, "isUsb");
        boolean isUsb = (boolean) ReflectUtil.invokeMethod(isUsbMethod, diskInfo);
        if (isUsb) {
            return Volume.MOUNT_USB;
        }
        return MOUNT_UNKNOWN;
    }

    public String getState() {
        return (String) ReflectUtil.getPrivateField(storageVolume, "mState");
    }

    public String toString0() {
        StringBuilder sb = new StringBuilder();
        sb.append("mStorageId = ").append(mStorageId).append('\n')
                .append("mPath = ").append(mPath).append('\n')
                .append("mState = ").append(mState).append('\n')
                .append("mDescription = ").append(mDescription).append('\n');
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("mPath = ").append(mPath).append('\n')
                .append("mDescription = ").append(mDescription).append('\n')
                .append("mountType = ").append(mountType);
        return sb.toString();
    }
}
