package com.daemonw.filelib.model;

import com.daemonw.filelib.reflect.Volume;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public abstract class Filer {
    public static final int TYPE_INTERNAL = Volume.MOUNT_INTERNAL;
    public static final int TYPE_EXTERNAL = Volume.MOUNT_EXTERNAL;
    public static final int TYPE_USB = Volume.MOUNT_USB;

    protected boolean mChecked;
    protected String mPath;
    protected int mType;


    public abstract boolean delete();

    public abstract Filer createNewFile(String fileName) throws IOException;

    public abstract Filer mkDir(String folderName) throws IOException;

    public abstract String getName();

    public abstract String getPath();

    public abstract Filer getParentFile();

    public abstract String getParentPath();

    public abstract OutputStream getOutStream() throws IOException;

    public abstract InputStream getInputStream() throws IOException;

    public abstract boolean isDirectory();

    public abstract ArrayList<Filer> listFiles();

    public abstract long lastModified();

    public abstract boolean hasChild(String fileName);

    public abstract boolean equals(Object o);

    public abstract long length();

    public abstract void fillWithZero() throws IOException;

    public int getType() {
        return mType;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }
}
