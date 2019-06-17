package com.daemonw.file.core.model;

import com.daemonw.file.core.reflect.Volume;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Filer {
    public static final int TYPE_INTERNAL = Volume.MOUNT_INTERNAL;
    public static final int TYPE_EXTERNAL = Volume.MOUNT_EXTERNAL;
    public static final int TYPE_USB = Volume.MOUNT_USB;

    protected boolean mChecked;
    protected String mPath;


    public abstract boolean delete();

    public abstract boolean createNewFile() throws IOException;

    public abstract boolean createChild(String name) throws IOException;

    public abstract boolean mkChild(String name) throws IOException;

    public abstract boolean mkDir() throws IOException;

    public abstract String getName();

    public abstract String getPath();

    public abstract String getUri();

    public abstract Filer getParentFile();

    public abstract String getParentPath();

    public abstract FileOutputStream getOutStream() throws IOException;

    public abstract FileInputStream getInputStream() throws IOException;

    public abstract boolean isDirectory();

    public abstract ArrayList<Filer> listFiles();

    public abstract long lastModified();

    public abstract boolean hasChild(String fileName);

    public abstract boolean equals(Object o);

    public abstract long length();

    public abstract boolean renameTo(String fileName);

    public abstract boolean exists();

    public abstract boolean erase() throws IOException;

    public abstract int getType();

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }
}
