package com.grt.filemanager.model;

import com.grt.filemanager.constant.ArchiveConstant;
import com.grt.filemanager.constant.SettingConstant;
import com.grt.filemanager.util.BuildUtils;
import com.grt.filemanager.util.LocalFileHelper;
import com.grt.filemanager.util.LogToFile;
import com.grt.filemanager.util.PreferenceUtils;

import java.io.File;

/**
 * Created by LDC on 2018/3/8.
 */

public class StorageData extends LocalData{
    private static StorageDevice device;
    private static String TAG = "StorageData";
    /**
     * 数据源抽象基类构造函数.
     *
     * @param accountId
     */
    public StorageData(int accountId) {
        super(accountId);
    }

    public static FileInfo createStorageObject(String path) {
//        LogToFile.e(TAG, "createStorageObject BuildUtils.thanLollipop()== "+ BuildUtils.thanLollipop()+
//        "\n"+ "LocalFileHelper.inUsbStorage(path) == " + LocalFileHelper.inUsbStorage(path) +"\n"+
//        "PreferenceUtils.getPrefString(SettingConstant.USB_ROOT_URI) == " + PreferenceUtils.getPrefString(SettingConstant.USB_ROOT_URI, "")
//        +"\n" +"path==" +path);
        if (BuildUtils.thanLollipop() &&
                LocalFileHelper.inUsbStorage(path) &&
                !PreferenceUtils.getPrefString(SettingConstant.USB_ROOT_URI, "").isEmpty()) {

//            LogToFile.e(TAG, "new UsbObject path = " + path);
            return new UsbObject(path);
        } else {
            return initStorageObject(path);
        }
    }

    private static FileInfo initStorageObject(String path) {
        if (!ArchiveConstant.isInArchive(path)) {
            return new StorageObject(path);
        } else {
            return null;
//            return ArchiveFactory.getArchiveFile(path, "");
        }
    }

    @Override
    protected String getSubClassRootPath(int accountId) {
        try {
            if (device == null) {
                device = new StorageDevice();
            }

            return device.getAccountList().get(accountId).getRootPath();
        } catch (Exception e) {
            e.printStackTrace();
            return File.separator;
        }
    }

    @Override
    protected FileInfo getSubClassObject(String path, int accountId) {
        return createStorageObject(path);
    }
}
