package com.grt.filemanager.orm.dao;

public class Video {

    //名称
    private String mFileName;
    //路径
    private String mFilePath;
    //大小
    private long mSize;
    //是否目录
    private boolean mIsFolder;
    //个数
    private int mCount;
    //mimeType
    private String mMimeType;
    //时间
    private long mLastModify;

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        this.mSize = size;
    }

    public boolean getIsFolder() {
        return mIsFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.mIsFolder = isFolder;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        this.mMimeType = mimeType;
    }

    public long getLastModify() {
        return mLastModify;
    }

    public void setLastModify(long lastModify) {
        this.mLastModify = lastModify;
    }
}
