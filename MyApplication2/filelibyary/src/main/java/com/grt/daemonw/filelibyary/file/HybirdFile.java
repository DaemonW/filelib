package com.grt.daemonw.filelibyary.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public abstract class HybirdFile {
    public static final int TYPE_FILE = 0;
    public static final int TYPE_USB = 1;
    public static final int TYPE_EXT = 2;

    protected String mPath;
    protected int mType;

    public HybirdFile(String filePath) {
        this.mPath = filePath;
    }

    public  abstract boolean delete();

    public abstract boolean createNewFile() throws IOException;

    public abstract boolean mkDir();

    public abstract String getName();

    public abstract String getParent();

    public abstract HybirdFile getParentFile();

    public abstract String getPath();

    public abstract OutputStream getOutStream() throws IOException;

    public abstract InputStream getInputStream() throws IOException;

    public abstract int getFileType();

    public abstract ArrayList<? extends HybirdFile> listFiles();

    public String getFilePath(){
        return mPath;
    }
}
