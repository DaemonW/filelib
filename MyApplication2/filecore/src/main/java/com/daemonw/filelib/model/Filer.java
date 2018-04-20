package com.daemonw.filelib.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public interface Filer {
    public static final int TYPE_RAW = 0;
    public static final int TYPE_SAF = 1;

    boolean delete();

    Filer createNewFile(String fileName) throws IOException;

    Filer mkDir(String folderName) throws IOException;

    String getName();

    String getPath();

    Filer getParentFile();

    String getParentPath();

    OutputStream getOutStream() throws IOException;

    InputStream getInputStream() throws IOException;

    int getFileType();

    boolean isDirectory();

    ArrayList<Filer> listFiles();

    long lastModified();
}
