package com.grt.filemanager.model;

import android.content.Context;


import com.grt.filemanager.apientrance.ApiPresenter;
import com.grt.filemanager.util.LocalFileHelper;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by liwei on 2016/4/27.
 */
public abstract class LocalObject implements FileInfo {

    protected static Context context = ApiPresenter.getContext();

    protected abstract String getFilePath();

    @Override
    public InputStream getInputStream() {
        return LocalFileHelper.getInputStream(getFilePath());
    }

    @Override
    public OutputStream getOutputStream() {
        return LocalFileHelper.getOutputStream(getFilePath());
    }

    @Override
    public boolean rename(String newPath) {
        return LocalFileHelper.rename(getFilePath(), newPath);
    }

    @Override
    public int create(boolean isFolder) {
        return LocalFileHelper.createFile(getFilePath(), isFolder);
    }

    @Override
    public boolean delete() {
        return LocalFileHelper.deleteFile(getFilePath());
    }

    @Override
    public boolean exists() {
        return LocalFileHelper.isExists(getFilePath());
    }

}
