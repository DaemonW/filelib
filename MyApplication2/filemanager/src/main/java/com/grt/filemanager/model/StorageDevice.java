package com.grt.filemanager.model;

import android.text.TextUtils;

import com.grt.filemanager.R;
import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.constant.DataConstant;
import com.grt.filemanager.constant.ResultConstant;
import com.grt.filemanager.mvp.storage.StorageManagerUtil;
import com.grt.filemanager.receiver.UsbReceiver;
import com.grt.filemanager.util.BuildUtils;
import com.grt.filemanager.util.FileUtils;
import com.grt.filemanager.util.LogToFile;
import com.grt.filemanager.util.PreferenceUtils;
import com.grt.filemanager.util.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StorageDevice extends BaseAccount {

    @Override
    public List<DataAccountInfo> getAccountList() {
        ArrayList<DataAccountInfo> list = new ArrayList<>();
        long totalSize;
        long leftSize;
        int dataId = DataConstant.STORAGE_DATA_ID;
        int accountId = 0;
        File file;
        String path;
        int rootName = 0;
        String account = null;

        StorageManagerUtil manger = StorageManagerUtil.getInstant();
        List<StorageVolume> volumeList = manger.getVolumeList();
        for (StorageVolume volume : volumeList) {
            path = volume.mPath;
            file = new File(path);
            totalSize = file.getTotalSpace();
//            if (totalSize <= 0) {
//                continue;
//            }

            if (volume.mState != null && (volume.mState.equals("unmounted") ||
                    volume.mState.equals("removed"))) {
                continue;
            }

            leftSize = file.getFreeSpace();
            if (volume.mPrimary || !volume.mRemovable) {
                rootName = R.string.mount_internal;
                manger.setPrimaryStoragePath(path);
            } else {
                String secondaryStorage = System.getenv("SECONDARY_STORAGE");
                String[] secondaryStorageArray;

                if (secondaryStorage != null) {
                    if (secondaryStorage.contains(":")) {
                        secondaryStorageArray = secondaryStorage.split(":");
                    } else {
                        secondaryStorageArray = new String[]{secondaryStorage};
                    }

                    boolean hit = false;
                    if (path.startsWith(secondaryStorageArray[0])) {
                        rootName = R.string.mount_external;
                        hit = true;
                        manger.setSecondStoragePath(path);
                    }

                    if (!hit) {
                        if (volume.mDescription != null &&
                                (volume.mDescription.toLowerCase().contains("usb")
                                        || volume.mDescription.startsWith("U") || volume.getmDsk() == DataConstant.USB_TYPE)) {
                            rootName = R.string.mount_usb;
                            account = FileUtils.getFileName(path);
                            manger.setUsbPath(path);
//                            LogToFile.e("asdfsdagfsadgsggas  hit", "hit asdf usbpath ="+path);
                        } else {
                            rootName = R.string.mount_external;
                            manger.setSecondStoragePath(path);
                        }
                    }

                } else {
                    if (volume.mDescription != null && volume.mDescription.toLowerCase().contains("usb")
                            || volume.mDescription.startsWith("U") || volume.getmDsk() == DataConstant.USB_TYPE) {
                        rootName = R.string.mount_usb;
                        account = FileUtils.getFileName(path);
                        manger.setUsbPath(path);
//                        LogToFile.e("asdfsdagfsadgsggas", "asdf usbpath ="+path);
                    } else {
                        String usbPath = manger.getUsbDevicePath();
                        if (!TextUtils.isEmpty(usbPath) && path.startsWith(usbPath)) {
                            rootName = R.string.mount_usb;
                            account = FileUtils.getFileName(path);
                            manger.setUsbPath(path);
//                            LogToFile.e("asdfsdagfsadgsggas three", "three asdf usbpath ="+path);
                        } else {
                            rootName = R.string.mount_external;
                            manger.setSecondStoragePath(path);
                        }
                    }

                }
            }

            if (rootName == R.string.mount_usb) {
                String filePath = path + "/" + TimeUtils.getCurrentSecondString();
                boolean result = false;
                try {
                    File javaFile = new File(filePath);
                    result = javaFile.createNewFile() && javaFile.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!result) {
                    FileInfo testFile = StorageData.createStorageObject(filePath);
                    result = testFile.create(false) == ResultConstant.SUCCESS && testFile.delete();
                }

                list.add(createDataRootInfo(rootName, dataId, result ? accountId++ : -1,
                        path, account, totalSize, leftSize));
                UsbReceiver.registerReceiver(context);
            } else if (rootName == R.string.mount_external){
                String filePath = path + "/" + TimeUtils.getCurrentSecondString();
                FileInfo testFile = StorageData.createStorageObject(filePath);
                boolean result = testFile.create(false) == ResultConstant.SUCCESS && testFile.delete();

                list.add(createDataRootInfo(rootName, dataId, result ? accountId++ : -1,
                        path, account, totalSize, leftSize));
            }else {
                list.add(createDataRootInfo(rootName, dataId, accountId++,
                        path, account, totalSize, leftSize));
            }
        }

//        if (BuildUtils.thanMarshmallow()) {
//            path = manger.getUsbDevicePath();
//            if (path != null) {
//                String name;
//                if (path.isEmpty()) {
//                    name = "";
//                    accountId = -1;
//                } else {
//                    name = FileUtils.getFileName(path);
//                }
//                list.add(createDataRootInfo(R.string.usb, dataId, accountId, path, name, -1, -1));
//            }
//        }

        return list;
    }

}
