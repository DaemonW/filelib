package com.grt.filemanager.orm.dao;

public class Music {

    //名称
    private String mFileName;
    //路径
    private String mFilePath;
    //时长
    private long mDuration;
    //歌手
    private String mSinger;
    //专辑
    private String mAlbum;
    //时间
    private long mLastModify;
    //大小
    private long mSize;
    //是否目录
    private boolean mIsFolder;
    //个数
    private int mCount;
    //mimeType
    private String mMimeType;

    public Music() {

    }

    public String getFileName() {
        return mFileName;
    }


    public long getDuration() {
        return mDuration;
    }

    public String getSinger() {
        return mSinger;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public long getLastModify() {
        return mLastModify;
    }


    public long getSize() {
        return mSize;
    }


    public void setFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public void setDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    public void setSinger(String mSinger) {
        this.mSinger = mSinger;
    }

    public void setAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    public void setLastModify(long lastModify) {
        this.mLastModify = lastModify;
    }


    public void setSize(long mSize) {
        this.mSize = mSize;
    }

    public void setFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public boolean getIsFolder() {
        return mIsFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.mIsFolder = isFolder;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setMimeType(String mimeType) {
        this.mMimeType = mimeType;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

}