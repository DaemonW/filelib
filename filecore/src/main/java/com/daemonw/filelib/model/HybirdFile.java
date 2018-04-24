package com.daemonw.filelib.model;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by daemonw on 4/13/18.
 */

public class HybirdFile extends Filer {
    private Filer fileDelegate;
    private Activity mContext;

    public HybirdFile(Activity context, String filePath, int type) {
        mContext = context;
        switch (type) {
            case Filer.TYPE_INTERNAL:
                fileDelegate = new RawFile(filePath);
                break;
            case Filer.TYPE_EXTERNAL:
                fileDelegate = new ExternalFile(context, filePath);
                break;
            case Filer.TYPE_USB:
                fileDelegate = new UsbFile(context, filePath);
                break;
        }
    }

    @Override
    public Filer createNewFile(String fileName) throws IOException {
        return fileDelegate.createNewFile(fileName);
    }

    @Override
    public Filer mkDir(String folderName) throws IOException {
        return fileDelegate.mkDir(folderName);
    }

    @Override
    public String getPath() {
        return fileDelegate.getPath();
    }

    @Override
    public Filer getParentFile() {
        return fileDelegate.getParentFile();
    }

    @Override
    public String getParentPath() {
        return fileDelegate.getParentPath();
    }

    @Override
    public OutputStream getOutStream() throws IOException {
        return fileDelegate.getOutStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return fileDelegate.getInputStream();
    }

    @Override
    public boolean isDirectory() {
        return fileDelegate.isDirectory();
    }

    @Override
    public ArrayList<Filer> listFiles() {
        return fileDelegate.listFiles();
    }

    @Override
    public long lastModified() {
        return fileDelegate.lastModified();
    }

    @Override
    public boolean hasChild(String fileName) {
        return fileDelegate.hasChild(fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof HybirdFile)) {
            return false;
        }
        HybirdFile h = (HybirdFile) o;
        return fileDelegate.equals(h.fileDelegate);
    }

    @Override
    public boolean fillWithZero() throws IOException {
        return fileDelegate.fillWithZero();
    }

    @Override
    public String getName() {
        return fileDelegate.getName();
    }

    @Override
    public long length() {
        return fileDelegate.length();
    }

    @Override
    public boolean delete() {
        return fileDelegate.delete();
    }
}
