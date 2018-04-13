package com.grt.filemanager.orm.dao;

public class Image {

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
    //分辨率
    private String mResolution;

    public Image() {
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public String getFileName() {
        return mFileName;
    }

    public long getSize() {
        return mSize;
    }

    public boolean isIsFolder() {
        return mIsFolder;
    }

    public int getCount() {
        return mCount;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public long getLastModify() {
        return mLastModify;
    }

    public String getResolution() {
        return mResolution;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public void setSize(long size) {
        this.mSize = size;
    }

    public void setIsFolder(boolean isFolder) {
        this.mIsFolder = isFolder;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    public void setMimeType(String mimeType) {
        this.mMimeType = mimeType;
    }

    public void setLastModify(long lastModify) {
        this.mLastModify = lastModify;
    }

    public void setResolution(String resolution) {
        this.mResolution = resolution;
    }

}
