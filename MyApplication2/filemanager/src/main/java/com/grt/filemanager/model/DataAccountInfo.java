package com.grt.filemanager.model;

import com.grt.filemanager.util.StorageUtils;

import java.io.Serializable;

/**
 * Created by liwei on 2015/10/9
 */
public class DataAccountInfo implements Serializable {

    private int mDataId;
    private int mAccountId;
    private String mAccount;
    private int mRootName;
    private String mRootPath;
    private long mTotalSize;
    private long mLeftSize;

    public int getDataId() {
        return mDataId;
    }

    public void setDataId(int dataId) {
        this.mDataId = dataId;
    }

    public int getAccountId() {
        return mAccountId;
    }

    public void setAccountId(int accountId) {
        this.mAccountId = accountId;
    }

    public String getAccount() {
        return mAccount;
    }

    public void setAccount(String account) {
        this.mAccount = account;
    }

    public int getRootName() {
        return mRootName;
    }

    public void setRootName(int rootName) {
        this.mRootName = rootName;
    }

    public String getRootPath() {
        return mRootPath;
    }

    public void setRootPath(String rootPath) {
        this.mRootPath = rootPath;
    }

    public long getTotalSize() {
        return mTotalSize;
    }

    public void setTotalSize(long totalSize) {
        this.mTotalSize = totalSize;
    }

    public long getLeftSize() {
        return mLeftSize;
    }

    public void setLeftSize(long leftSize) {
        this.mLeftSize = leftSize;
    }

    @Override
    public String toString() {
        return "dataId=" + mDataId + " accountId=" + mAccountId +
                " account=" + mAccount + " rootPath=" + mRootPath +
                " totalSize=" + StorageUtils.getPrettyFileSize(mTotalSize) +
                " leftSize=" + StorageUtils.getPrettyFileSize(mLeftSize);
    }


}