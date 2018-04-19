package com.grt.daemonw.filelibrary.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public interface Filer {
    public static final int TYPE_LOCAL = 0;
    public static final int TYPE_FTP = 1;

    boolean delete();

    Filer createNewFile(String fileName) throws IOException;

    Filer mkDir(String folderName) throws IOException;

    String getName();

    String getPath();

    String getUri();

    Filer getParentFile();

    String getParentPath();

    String getParentUri();

    OutputStream getOutStream() throws IOException;

    InputStream getInputStream() throws IOException;

    int getFileType();

    boolean isDirectory();

    ArrayList<Filer> listFiles();
}
