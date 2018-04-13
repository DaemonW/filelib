package com.grt.filemanager.mvp.storage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.SettingConstant;
import com.grt.filemanager.model.MountInfo;
import com.grt.filemanager.model.StorageVolume;
import com.grt.filemanager.util.BuildUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LogToFile;
import com.grt.filemanager.util.LogUtils;
import com.grt.filemanager.util.PreferenceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by LDC on 2018/3/7.
 */

public class StorageManagerUtil {
    private static String TAG = "StorageManagerUtil";
    private String mSecondStoragePath;
    private StorageManager mStorageManager;
    private Context mContext;
    private static StorageManagerUtil manger;
    private static String mUsbPath;
    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private String mPrimaryStoragePath;

    public StorageManagerUtil(Context context) {
        mContext = context;
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
    }

    public static String checkUsbGrant() {
        String usbRootUri = PreferenceUtils.getPrefString(SettingConstant.USB_ROOT_URI, "");
        String name = StorageManagerUtil.getNameFromUri(usbRootUri);

        if (!TextUtils.isEmpty(name)) {
            return StorageManagerUtil.getUsbMountPoint(name);
        } else {
            return null;
        }
    }

    public static StorageManagerUtil getInstant() {
        if (manger == null) {
            manger = new StorageManagerUtil(ApiPresenter.getContext());
        }

        return manger;
    }

    public static String getNameFromUri(String uri) {
        try {
            String name = FileUtils.getFileName(uri);
            name = URLDecoder.decode(name, "utf-8");
            return name.endsWith(":") ? name.substring(0, name.length() - 1) : name;
        } catch (UnsupportedEncodingException e) {
            LogUtils.d("StorageManagerUtil-getNameFromUri", e.getMessage());
        }

        return null;
    }

    public static String getUsbMountPoint(String name) {
        if (name == null) {
            return null;
        }

        String result = MountInfo.getMountStr();
        if (!TextUtils.isEmpty(result)) {
            BufferedReader bufferedReader = new BufferedReader(new StringReader(result));

            try {
                String line;
                do {
                    line = bufferedReader.readLine();
                    if (line == null) {
                        return null;
                    }

                    if (line.contains(name)) {
                        String[] parts = line.split(" ");
                        return parts[1];
                    }
                } while (true);

            } catch (IOException e) {
                LogUtils.e(TAG + "getUsbMountPoint", e.getMessage());
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LogUtils.e(TAG + "getUsbMountPoint", e.getMessage());
                }
            }
        }

        return null;
    }

    public String getSecondaryStorage() {
        if (mSecondStoragePath != null) {
            return mSecondStoragePath;
        }

        String secondaryStorage = System.getenv("SECONDARY_STORAGE");
        String[] secondaryStorageArray;
        String path;
        File file;
        List<StorageVolume> volumeList = getVolumeList();
        for (StorageVolume volume : volumeList) {

            path = volume.mPath;
            file = new File(path);
            if (volume.mPrimary || file.getTotalSpace() <= 0 ||
                    (volume.mState != null && (volume.mState.equals("unmounted") ||
                            volume.mState.equals("removed")))) {
                continue;
            }

            if (secondaryStorage != null) {
                if (secondaryStorage.contains(":")) {
                    secondaryStorageArray = secondaryStorage.split(":");
                } else {
                    secondaryStorageArray = new String[]{secondaryStorage};
                }
                if (path.startsWith(secondaryStorageArray[0])) {
                    mSecondStoragePath = path;
                    return path;
                }
            } else {
                if (volume.mDescription != null &&
                        !volume.mDescription.toLowerCase().contains("usb")
                        && !volume.mDescription.startsWith("U")) {
                    mSecondStoragePath = path;
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * 获取存储设备及容量信息
     */
    public List<StorageVolume> getVolumeList() {
        List<StorageVolume> volumeList = new ArrayList<>();
        try {
            if (mStorageManager == null) {
                if (mContext == null) {
                    mContext = ApiPresenter.getContext();
                }
                mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
            }

            if (BuildUtils.thanNougat()){//android 7.0以上
                List<android.os.storage.StorageVolume> list = mStorageManager.getStorageVolumes();
                for (Object item : list){
                    volumeList.add(new StorageVolume(mContext, item));
                }
            }else {
                Method method = mStorageManager.getClass().getMethod("getVolumeList");
                Object[] list = (Object[]) method.invoke(mStorageManager);
                for (Object item : list) {
                    volumeList.add(new StorageVolume(mContext, item));
                }
            }

            if (BuildUtils.thanMarshmallow()){//android 6.0 以上
                Object resutlt = mStorageManager.getClass().getMethod("getVolumes").invoke(mStorageManager);
                List<? extends Object> volumes = (List<? extends Object>) resutlt;
                for (int i = 0; i < volumes.size(); i++) {
                    Object o = volumes.get(i);
                    if (o != null){
                        String uuid = getFsUuid(o);
                        if (uuid != null){
                            for (StorageVolume volume : volumeList){
                                String ned = volume.mPath.substring(volume.mPath.lastIndexOf("/") + 1);
                                if (uuid.equals(ned)){
                                    Object ds = getDiskInfo(o);
                                    volume.setmDsk(getVolumeType(ds));
                                    break;
                                }else {
                                    continue;
                                }
                            }
                        }
                    }
//                LogToFile.e("StorageManager", "result = "+result);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return volumeList;
    }

    private Object getDiskInfo(Object volumeInfo) throws Exception {
        Class clazz = Class.forName("android.os.storage.VolumeInfo");
        return clazz.getMethod("getDisk").invoke(volumeInfo);
    }

    private String getFsUuid(Object volumeInfo) throws Exception{
       Class clazz =  Class.forName("android.os.storage.VolumeInfo");
        return (String) clazz.getMethod("getFsUuid").invoke(volumeInfo);
    }

    private int getVolumeType(Object diskInfo) throws Exception {
        if (diskInfo != null){
            Class clazz = Class.forName("android.os.storage.DiskInfo");
            boolean isDefault = (boolean) clazz.getMethod("isDefaultPrimary").invoke(diskInfo);
//            if(isDefault){
//                return 0;
//            }
            boolean isUsb = (boolean) clazz.getMethod("isUsb").invoke(diskInfo);
            if(isUsb){
                return 1;
            }
            boolean isExtSd = (boolean) clazz.getMethod("isSd").invoke(diskInfo);
            if(isExtSd){
                return 2;
            }
        }
        return -1;
    }

    /**
     * 返回null表示没有usb设备，返回空字符表示usb设备没有授权，否则返回usb设备挂载路径
     */
    public String getUsbDevicePath() {
        String path = null;
        String usbRootUri = PreferenceUtils.getPrefString(SettingConstant.USB_ROOT_URI, "");
        if (TextUtils.isEmpty(usbRootUri)) {
            if (BuildUtils.thanLollipop() && getUsbDevice() != null) {
                path = "";
            }
        } else {
            String name = getNameFromUri(usbRootUri);
            path = getUsbMountPoint(name);
            if (!TextUtils.isEmpty(path)) {
//                mUsbPath = path;//USB路径初始化时候已赋值,无需再次赋值
            } else {
                path = mUsbPath;
            }
        }

        return path;
    }

    /**
     * 返回null表示没有usb设备，返回空字符表示usb设备没有授权，否则返回usb设备挂载路径
     */
    public String getUsbDevicePathNew() {
        String path = null;
        String usbRootUri = PreferenceUtils.getPrefString(SettingConstant.USB_ROOT_URI, "");
//        LogToFile.e(TAG, "usbRootPath =" + TextUtils.isEmpty(usbRootUri));
        if (TextUtils.isEmpty(usbRootUri)) {
            if (BuildUtils.thanLollipop() && getUsbDevice() != null) {
                path = "";
            }
        } else {
            String name = getNameFromUri(usbRootUri);
            path = getUsbMountPoint(name);
//            LogToFile.e(TAG, "path =" + path);
            if (!PreferenceUtils.getPrefString(SettingConstant.MEDIA_MOUNTED_PATH, "").equals("")) {//外接设备已经移除返回null
                if (!TextUtils.isEmpty(path)) {
                    mUsbPath = path;
                } else {
                    path = mUsbPath;
                }
            }
        }

        return path;
    }

    @SuppressLint("NewApi")
    public UsbDevice getUsbDevice() {
        try {
            HashMap<String, UsbDevice> deviceHashMap = mUsbManager.getDeviceList();
            Set<String> keys = deviceHashMap.keySet();
            UsbDevice usbDevice;
//            LogToFile.e(TAG, "keys size=" + keys.size());
            for (String key : keys) {
                usbDevice = deviceHashMap.get(key);
//                LogToFile.e(TAG, "usbDevice config size =" + usbDevice.getConfigurationCount() + "///get(0).get=" + usbDevice.getConfiguration(0).getInterface(0).getInterfaceClass());
                if (usbDevice.getConfiguration(0).getInterface(0).getInterfaceClass() == 8) {
                    mUsbDevice = usbDevice;
                    return mUsbDevice;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param secondStoragePath 外置卡路径
     * @param name              USB设备路径名
     * @return 外置卡与USB设备是否是同一个设备
     */
    public boolean checkUsbMountPoint(String secondStoragePath, String name) {
        if (secondStoragePath == null || name == null) {
            return false;
        }

        String result = MountInfo.getMountStr();
        String usbMountsPoint = null;
        if (!TextUtils.isEmpty(result)) {
            BufferedReader bufferedReader = new BufferedReader(new StringReader(result));

            try {
                String line;
                do {
                    line = bufferedReader.readLine();
                    if (line == null) {
                        return false;
                    }

                    if (line.contains(name)) {
                        String[] parts = line.split(" ");

                        if (usbMountsPoint == null) {
                            usbMountsPoint = parts[1];
                        } else {
                            if (usbMountsPoint.equals(parts[0]) && secondStoragePath.equals(parts[1])) {
                                return true;
                            }
                        }

                    }
                } while (true);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public void setPrimaryStoragePath(String path) {
        this.mPrimaryStoragePath = path;
    }

    public void setSecondStoragePath(String path) {
        this.mSecondStoragePath = path;
    }

    public void setUsbPath(String usbPath) {
        mUsbPath = usbPath;
    }

    public String getUsbPath() {
        return mUsbPath;
    }

    public boolean hasUsbPermission(UsbDevice usbDevice) {
        try {
            return usbDevice != null && mUsbManager.hasPermission(usbDevice);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isMemoryPath(String path) {
        if (path.isEmpty()) {
            return false;
        }

        String sdcard = getPrimaryStorage();
        if (sdcard != null && !sdcard.isEmpty() && path.startsWith(sdcard)) {
            return false;
        }

        String secondPath = getSecondaryStorage();
        if (secondPath != null && !secondPath.isEmpty() && path.startsWith(secondPath)) {
            return false;
        }

        if (BuildUtils.thanLollipop()) {
            String usePath = getUsbDevicePath();
            if (usePath != null && !usePath.isEmpty() && path.startsWith(usePath)) {
                return false;
            }
        }

        return true;
    }

    public String getPrimaryStorage() {
        if (mPrimaryStoragePath == null) {
            List<StorageVolume> volumeList = getVolumeList();
            for (StorageVolume volume : volumeList) {
                if (volume.mPrimary || !volume.mRemovable) {
                    mPrimaryStoragePath = volume.mPath;
                    break;
                }
            }
        }

        if (mPrimaryStoragePath == null) {
            mPrimaryStoragePath = Environment.getExternalStorageDirectory().getPath();
        }
        return mPrimaryStoragePath;
    }
}
