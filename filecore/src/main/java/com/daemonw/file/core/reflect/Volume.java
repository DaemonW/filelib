package com.daemonw.file.core.reflect;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.PreferenceManager;

import com.daemonw.file.FileConst;
import com.daemonw.file.core.utils.BuildUtils;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.lang.reflect.Method;

public class Volume {
    public static final String LOG_TAG = Volume.class.getSimpleName();
    private static String INTERNAL_DESCRIPTION;
    private static String EXTERNAL_DESCRIPTION;
    private static String USB_DESCRIPTION;

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


    static {
        Resources resources = Resources.getSystem();
        int internalStrRes = resources.getIdentifier("storage_internal", "string", "android");
        if (internalStrRes != 0) {
            INTERNAL_DESCRIPTION = resources.getString(internalStrRes);
        }
        int externalStrRse = resources.getIdentifier("storage_sd_card", "string", "android");
        if (externalStrRse != 0) {
            EXTERNAL_DESCRIPTION = resources.getString(externalStrRse);
        } else {
            EXTERNAL_DESCRIPTION = "SD";
        }
        int usbStrRes = resources.getIdentifier("storage_usb_drive", "string", "android");
        if (usbStrRes != 0) {
            USB_DESCRIPTION = resources.getString(usbStrRes);
        } else {
            USB_DESCRIPTION = "USB";
        }
    }


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
        //judge by label
        String label = mDescription;
        if (label.contains(USB_DESCRIPTION) || label.contains("U")) {
            return MOUNT_USB;
        }
        if (label.contains(EXTERNAL_DESCRIPTION) || label.contains("SD")) {
            return MOUNT_EXTERNAL;
        }

        //judge by secondary_storage property
        String secondaryStorage = System.getenv("SECONDARY_STORAGE");
        if (secondaryStorage != null) {
            if (secondaryStorage.equals(mPath)) {
                return MOUNT_EXTERNAL;
            }
        }

        //judge by path
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String extSdPath = sp.getString(FileConst.PREF_EXTERNAL_PATH, null);
        if (extSdPath != null) {
            if (mPath.contains(extSdPath)) {
                return MOUNT_EXTERNAL;
            }
        }

        return MOUNT_USB;
    }

    private int getVolumeTypeSinceMarshmallow(StorageManager storageManager) {
        String volumeId = (String) ReflectUtil.getPrivateField(storageVolume, "mId");
        Method volumeMethod = ReflectUtil.getPublicMethod(storageManager, "findVolumeById", String.class);
        if (volumeMethod == null) {
            Logger.e(LOG_TAG, "can't find method \"findVolumeById\" in class StorageManager");
            return MOUNT_UNKNOWN;
        }
        Object volumeInfo = ReflectUtil.invokeMethod(volumeMethod, storageManager, volumeId);
        Object diskInfo = ReflectUtil.getPublicField(volumeInfo, "disk");
        if (diskInfo == null) {
            Logger.e(LOG_TAG, "can't find filed \"disk\" in class VolumeInfo");
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
        mState = (String) ReflectUtil.getPrivateField(storageVolume, "mState");
        return mState;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("mPath = ").append(mPath).append('\n')
                .append("mDescription = ").append(mDescription).append('\n')
                .append("mountType = ").append(mountType);
        return sb.toString();
    }
}
