package com.grt.filemanager.model;

import android.graphics.Bitmap;
import android.os.Bundle;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FileInfo {

    String getName();

    String getPath();

    long getLastModified();

    long getSize();

    String getMimeType();

    Bitmap getThumb(boolean isList);

    Bundle getSpecialInfo(String type);

    boolean isFolder();

    List<FileInfo> getList(boolean containHide);

    InputStream getInputStream();

    OutputStream getOutputStream();

    int create(boolean isFolder);

    boolean rename(String newPath);

    boolean delete();

    boolean exists();

}
