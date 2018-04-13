package com.grt.filemanager.model;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import com.grt.filemanager.apientrance.ApiPresenter;


public abstract class BaseAccount implements DataModule {

    protected static final Context context = ApiPresenter.getContext();

    private static ArrayMap<Integer, ArrayMap<Integer, DataAccountInfo>> childrenAccountMap = new ArrayMap<>();

    public static ArrayMap<Integer, ArrayMap<Integer, DataAccountInfo>> getChildrenAccountMap() {
        return childrenAccountMap;
    }

    private static ArrayMap<Integer, DataModule> accountMap = new ArrayMap<>();

    public static ArrayMap<Integer, DataModule> getAccountMap() {
        return accountMap;
    }

    protected DataAccountInfo createDataRootInfo(int rootName, int dataId, int accountId,
                                                 String rootPath, long totalSize, long leftSize) {
        DataAccountInfo dataAccountInfo = new DataAccountInfo();

        dataAccountInfo.setRootName(rootName);
        dataAccountInfo.setAccountId(accountId);
        dataAccountInfo.setRootPath(rootPath);
        dataAccountInfo.setTotalSize(totalSize);
        dataAccountInfo.setLeftSize(leftSize);
        dataAccountInfo.setDataId(dataId);

        return dataAccountInfo;
    }

    protected DataAccountInfo createDataRootInfo(int rootName, int dataId, int accountId,
                                                 String rootPath, String account,
                                                 long totalSize, long leftSize) {
        DataAccountInfo dataAccountInfo = new DataAccountInfo();

        dataAccountInfo.setAccount(account);
        dataAccountInfo.setRootName(rootName);
        dataAccountInfo.setAccountId(accountId);
        dataAccountInfo.setRootPath(rootPath);
        dataAccountInfo.setTotalSize(totalSize);
        dataAccountInfo.setLeftSize(leftSize);
        dataAccountInfo.setDataId(dataId);

        return dataAccountInfo;
    }

}
