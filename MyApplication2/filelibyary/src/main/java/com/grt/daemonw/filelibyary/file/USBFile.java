package com.grt.daemonw.filelibyary.file;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class USBFile  extends Filer{
    private Context mContext;

    public USBFile(Context context, String filePath){
        super(filePath);
        this.mContext=context;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public Filer createNewFile(String fileName) throws IOException {
        return null;
    }

    @Override
    public Filer mkDir(String folderName) throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getParent() {
        return null;
    }

    @Override
    public Filer getParentFile() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public OutputStream getOutStream() throws IOException {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public int getFileType() {
        return 0;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public ArrayList<Filer> listFiles() {
        return null;
    }
}
