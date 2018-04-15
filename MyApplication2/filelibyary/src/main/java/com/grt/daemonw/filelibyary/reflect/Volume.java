package com.grt.daemonw.filelibyary.reflect;

import android.content.Context;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.grt.daemonw.filelibyary.file.HybirdFile;
import com.grt.daemonw.filelibyary.utils.BuildUtils;

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

    public static final int MOUNT_INTERNAL = 0;
    public static final int MOUNT_EXTERNAL = 1;
    public static final int MOUNT_USB = 2;

    private Volume() {

    }


    public static Volume fromStorageVolume(Context context, StorageVolume volume) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Volume v = mirrorObj(context, volume);
        if (BuildUtils.thanMarshmallow()) {
            v.mountType = v.getVolumeTypeSinceMarshmallow(storageManager);
        } else {
            v.mountType = v.getVolumeType(storageManager);
        }
        return v;
    }

    private static Volume mirrorObj(Context context, StorageVolume volume) {
        Volume v = new Volume();
        v.storageVolume = volume;
        File file = (File) ReflectUtil.getPrivateField(volume, "mPath");
        v.mPath = file.getAbsolutePath();
        v.mStorageId = (int) ReflectUtil.getPrivateField(volume, "mStorageId");
        v.mState = (String) ReflectUtil.getPrivateField(volume, "mState");
        Method method = ReflectUtil.getPublicMethod(volume, "getDescription", Context.class);
        v.mDescription = (String) ReflectUtil.invokeMethod(method, volume, context);
        return v;
    }

    private int getVolumeType(StorageManager storageManager) {
        int type = Volume.MOUNT_INTERNAL;
        return type;
    }

    private int getVolumeTypeSinceMarshmallow(StorageManager storageManager) {
        String volumeId = (String) ReflectUtil.getPrivateField(storageVolume, "mId");
        int type = Volume.MOUNT_INTERNAL;
        Method volumeMethod = ReflectUtil.getPublicMethod(storageManager, "findVolumeById", String.class);
        if (volumeMethod == null) {
            Log.e(LOG_TAG, "can't find method findVolumeById in class StorageManager");
            return type;
        }
        Object volumeInfo = ReflectUtil.invokeMethod(volumeMethod, storageManager, volumeId);
        Object diskInfo = ReflectUtil.getPublicField(volumeInfo, "disk");
        if (diskInfo == null) {
            Log.e(LOG_TAG, "can't find filed \'disk\' in class VolumeInfo");
            return type;
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
        return type;
    }

    public String getState() {
        return (String) ReflectUtil.getPrivateField(storageVolume, "mState");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("mStorageId = ").append(mStorageId).append('\n')
                .append("mPath = ").append(mPath).append('\n')
                .append("mState = ").append(mState).append('\n')
                .append("mDescription = ").append(mDescription).append('\n');
        return sb.toString();
    }
}
