package com.grt.filemanager.model;

/**
 * @author Administrator
 * @version 2016/7/22 0022
 *          ${tags}
 */

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.grt.filemanager.util.BuildUtils;
import com.grt.filemanager.util.LogToFile;

import java.lang.reflect.Method;

/**
 * 存储设备及容量信息
 */
public class StorageVolume {
    public int mStorageId;
    public String mPath;
    public String mDescription;
    public boolean mPrimary;
    public boolean mRemovable;
    public boolean mEmulated;
    public int mMtpReserveSpace;
    public boolean mAllowMassStorage;
    public long mMaxFileSize;  //最大文件大小。(0表示无限制)
    public String mState;      //返回null
    public int mDsk;//磁盘类型 1:U盘, 2:外置sd卡

    public StorageVolume(Context context, Object reflectItem) {
        try {
            Method fmStorageId = reflectItem.getClass().getDeclaredMethod("getStorageId");
            fmStorageId.setAccessible(true);
            mStorageId = (Integer) fmStorageId.invoke(reflectItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method fmPath = reflectItem.getClass().getDeclaredMethod("getPath");
            fmPath.setAccessible(true);
            mPath = (String) fmPath.invoke(reflectItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (BuildUtils.thanNougat()){
                mDescription = ((android.os.storage.StorageVolume) reflectItem).getDescription(context);
            }else {
                mDescription = (String) reflectItem.getClass().getDeclaredMethod("getDescription").invoke(reflectItem, context);
            }
//            LogToFile.e("StorageVolume", "description = "+mDescription);
        } catch (Exception e) {
        }

        if (mDescription == null || TextUtils.isEmpty(mDescription)) {
            try {
                Method fmDescriptionId = reflectItem.getClass().getDeclaredMethod("getDescription");
                fmDescriptionId.setAccessible(true);
                mDescription = (String) fmDescriptionId.invoke(reflectItem, context);
            } catch (Exception e) {
            }

            try {
                Method fmDescriptionId = reflectItem.getClass().getDeclaredMethod("getDescriptionId");
                fmDescriptionId.setAccessible(true);
                int mDescriptionId = (Integer) fmDescriptionId.invoke(reflectItem);
                if (mDescriptionId != 0) {
                    mDescription = context.getResources().getString(mDescriptionId);
                }
            } catch (Exception e) {
            }
        }

        try {
            if (BuildUtils.thanNougat()){
                mPrimary = ((android.os.storage.StorageVolume) reflectItem).isPrimary();
            }else {
                Method fmPrimary = reflectItem.getClass().getDeclaredMethod("isPrimary");
                fmPrimary.setAccessible(true);
                mPrimary = (Boolean) fmPrimary.invoke(reflectItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (BuildUtils.thanNougat()){
                mRemovable = ((android.os.storage.StorageVolume) reflectItem).isRemovable();
            }else {
                Method fisRemovable = reflectItem.getClass().getDeclaredMethod("isRemovable");
                fisRemovable.setAccessible(true);
                mRemovable = (Boolean) fisRemovable.invoke(reflectItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (BuildUtils.thanNougat()){
                mEmulated = ((android.os.storage.StorageVolume) reflectItem).isEmulated();
            }else {
                Method fisEmulated = reflectItem.getClass().getDeclaredMethod("isEmulated");
                fisEmulated.setAccessible(true);
                mEmulated = (Boolean) fisEmulated.invoke(reflectItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method fmMtpReserveSpace = reflectItem.getClass().getDeclaredMethod("getMtpReserveSpace");
            fmMtpReserveSpace.setAccessible(true);
            mMtpReserveSpace = (Integer) fmMtpReserveSpace.invoke(reflectItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method fAllowMassStorage = reflectItem.getClass().getDeclaredMethod("allowMassStorage");
            fAllowMassStorage.setAccessible(true);
            mAllowMassStorage = (Boolean) fAllowMassStorage.invoke(reflectItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Method fMaxFileSize = reflectItem.getClass().getDeclaredMethod("getMaxFileSize");
            fMaxFileSize.setAccessible(true);
            mMaxFileSize = (Long) fMaxFileSize.invoke(reflectItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (BuildUtils.thanNougat()){
                mState = ((android.os.storage.StorageVolume) reflectItem).getState();
            }else {
                Method fState = reflectItem.getClass().getDeclaredMethod("getState");
                fState.setAccessible(true);
                mState = (String) fState.invoke(reflectItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isMounted() {
        return mState.equals(Environment.MEDIA_MOUNTED);
    }

    public String getDescription() {
        return mDescription;
    }

    /**
     * 获取存储设备的唯一标识
     */
    public String getUniqueFlag() {
        return String.valueOf(mStorageId);
    }

//        public boolean isUsbStorage(){
//            return android.R.string.storage_usb;
//        }


    @Override
    public String toString() {
        return "StorageVolume{" +
                "\nmStorageId=" + mStorageId +
                "\n, mPath='" + mPath + '\'' +
                "\n, mDescription=" + mDescription +
                "\n, mPrimary=" + mPrimary +
                "\n, mRemovable=" + mRemovable +
                "\n, mEmulated=" + mEmulated +
                "\n, mMtpReserveSpace=" + mMtpReserveSpace +
                "\n, mAllowMassStorage=" + mAllowMassStorage +
                "\n, mMaxFileSize=" + mMaxFileSize +
                "\n, mState='" + mState + '\'' +
                '}' + "\n";
    }

    public int getmDsk() {
        return mDsk;
    }

    public void setmDsk(int mDsk) {
        this.mDsk = mDsk;
    }
}
