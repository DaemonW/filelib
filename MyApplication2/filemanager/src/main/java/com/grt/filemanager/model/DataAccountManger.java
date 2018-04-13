package com.grt.filemanager.model;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.grt.filemanager.constant.DataConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by LDC on 2018/3/7.
 */

public class DataAccountManger {

    /**
     * @param dataId 数据ID
     * @param path   文件路径
     * @return 根据数据ID和文件路径返回缓存的对应设备信息
     */
    public static DataAccountInfo getAccountInfo(int dataId, String path) {
        ArrayMap<Integer, ArrayMap<Integer, DataAccountInfo>> accountMap = BaseAccount.getChildrenAccountMap();
        ArrayMap<Integer, DataAccountInfo> accountInfoArrayMap = accountMap.get(dataId);
        if (accountInfoArrayMap != null) {

            DataAccountInfo accountInfo;
            String rootPath;
            Set<Integer> keySet = accountInfoArrayMap.keySet();
            for (int key : keySet) {
                accountInfo = accountInfoArrayMap.get(key);
                rootPath = accountInfo.getRootPath();
                if (!rootPath.equals("/") && path.startsWith(rootPath)) {
                    return accountInfo;
                }
            }
        }

        return null;
    }

    public static DataModule getAccountInfo(int dataId) {
        ArrayMap<Integer, DataModule> accountMap = BaseAccount.getAccountMap();
        DataModule account = createBaseAccount(dataId);
        accountMap.put(dataId, account);

        List<DataAccountInfo> accountInfoList = account.getAccountList();
        if (accountInfoList != null) {
            int count = accountInfoList.size();
            DataAccountInfo accountInfo;
            ArrayMap<Integer, ArrayMap<Integer, DataAccountInfo>> childrenAccountMap =
                    BaseAccount.getChildrenAccountMap();
            ArrayMap<Integer, DataAccountInfo> dataMap = childrenAccountMap.get(dataId);
            if (dataMap == null) {
                dataMap = new ArrayMap<>();
                childrenAccountMap.put(dataId, dataMap);
            }

            for (int index = 0; index < count; index++) {
                accountInfo = accountInfoList.get(index);
                dataMap.put(accountInfo.getAccountId(), accountInfo);
            }
        }

        return account;
    }

    private static DataModule createBaseAccount(int dataId) {
        DataModule baseAccount;

        switch (dataId) {
            case DataConstant.STORAGE_DATA_ID:
                baseAccount = new StorageDevice();
                break;

            default:
                baseAccount = new StorageDevice();
                break;
        }

        return baseAccount;
    }

    /**
     * 根据文件路径返回内存磁盘设备信息
     *
     * @return 本地设备信息, 未找到则返回null
     */
    public static DataAccountInfo getMemoryAccount() {
        ArrayMap<Integer, DataAccountInfo> accountInfoArrayMap = BaseAccount.getChildrenAccountMap()
                .get(DataConstant.MEMORY_DATA_ID);
        return accountInfoArrayMap != null ? accountInfoArrayMap.valueAt(0) : null;
    }

    /**
     * 根据文件路径返回本地设备信息
     *
     * @param path 文件路径
     * @return 本地设备信息, 未找到则返回null
     */
    public static DataAccountInfo getLocalAccount(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        List<DataAccountInfo> accountList = DataAccountManger.getList(DataConstant.LOCAL_DEVICE);
        String rootPath;
        for (DataAccountInfo accountInfo : accountList) {
            rootPath = accountInfo.getRootPath();
            if (!rootPath.equals("/") && path.contains(rootPath)) {
                return accountInfo;
            }
        }

        return null;
    }

    /**
     * 通过数据源类型返回数据源特征接口列表.
     *
     * @param type 数据源类型.
     * @return 数据源特征接口列表.
     */
    public static List<DataAccountInfo> getList(int type) {
        ArrayList<DataAccountInfo> arrayList = new ArrayList<>();

        switch (type) {
            case DataConstant.ACCOUNT_DEVICE:
//                if (!Utils.isSpecial() && !Utils.isFreeVersion()){
//                    arrayList.addAll(getAccountInfo(DataConstant.STORE_ID).getAccountList());
////                arrayList.addAll(getAccountInfo(DataConstant.QUICK_GUIDE_ID).getAccountList());
//                }
                break;

            case DataConstant.LOCAL_DEVICE:
                arrayList.addAll(getAccountInfo(DataConstant.STORAGE_DATA_ID).getAccountList());
//                arrayList.addAll(getAccountInfo(DataConstant.MEMORY_DATA_ID).getAccountList());
                break;

            case DataConstant.ADVANCE_TOOLS_DEVICE:
//                arrayList.addAll(getAccountInfo(DataConstant.CAMERA_ID).getAccountList());
//                arrayList.addAll(getAccountInfo(DataConstant.RECORD_SOUND_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.SAFEBOX_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.LABEL_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.DOWNLOADER_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.FTP_SERVER_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.TRANSFER_LIST_ID).getAccountList());
                break;

            case DataConstant.NET_DEVICE:
                arrayList.addAll(getAccountInfo(DataConstant.MY_CLOUD_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.MIX_CLOUD_ID).getAccountList());
            case DataConstant.PUBLIC_NET_DEVICE:
                arrayList.addAll(getAccountInfo(DataConstant.FTP_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.DROPBOX_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.GOOGLE_DRIVE_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.ONE_DRIVE_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.SINA_STORAGE_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.JIANGUOYUN_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.ADD_NET_ID).getAccountList());
                break;

            case DataConstant.CLASS_DEVICE:
                arrayList.addAll(getAccountInfo(DataConstant.IMAGE_DATA_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.MUSIC_DATA_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.VIDEO_DATA_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.APP_DATA_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.ZIP_DATA_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.DOC_DATA_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.DOWNLOAD_ID).getAccountList());
                break;

            case DataConstant.SECRET_NET_DEVICE:
                arrayList.addAll(getAccountInfo(DataConstant.MY_CLOUD_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.DROPBOX_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.GOOGLE_DRIVE_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.ONE_DRIVE_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.SINA_STORAGE_ID).getAccountList());
                arrayList.addAll(getAccountInfo(DataConstant.JIANGUOYUN_ID).getAccountList());
                break;

            default:
                break;
        }

        return arrayList;
    }
}
