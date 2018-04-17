package com.grt.daemonw.filelibrary.file;

import com.grt.daemonw.filelibrary.reflect.Volume;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public abstract class AbstractFile {
    public static final int TYPE_INTERNAL = Volume.MOUNT_INTERNAL;
    public static final int TYPE_USB = Volume.MOUNT_EXTERNAL;
    public static final int TYPE_EXTERNAL = Volume.MOUNT_USB;

    protected String mPath;
    protected int mType;

    public AbstractFile(String filePath) {
        this.mPath = filePath;
    }

    public abstract boolean delete();

    public abstract AbstractFile createNewFile(String fileName) throws IOException;

    public abstract AbstractFile mkDir(String folderName) throws IOException;

    public abstract String getName();

    public abstract String getParent();

    public abstract AbstractFile getParentFile();

    public abstract String getPath();

    public abstract OutputStream getOutStream() throws IOException;

    public abstract InputStream getInputStream() throws IOException;

    public abstract int getFileType();

    public abstract boolean isDirectory();

    public abstract ArrayList<AbstractFile> listFiles();

    public String getFilePath() {
        return mPath;
    }
}
