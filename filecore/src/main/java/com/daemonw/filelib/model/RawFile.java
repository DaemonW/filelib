package com.daemonw.filelib.model;

import android.util.Log;

import com.daemonw.filelib.utils.RawFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class RawFile extends Filer {
    private File mFile;

    public RawFile(String filePath) {
        mFile = new File(filePath);
        mType = TYPE_INTERNAL;
        mPath = mFile.getAbsolutePath();
    }

    private RawFile(File file) {
        mFile = file;
        mType = TYPE_INTERNAL;
        mPath = mFile.getAbsolutePath();
    }

    @Override
    public boolean delete() {
        return rm(mFile);
    }

    @Override
    public Filer createNewFile(String fileName) throws IOException {
        String name = new File(fileName).getName();
        File newFile = new File(mFile, name);
        newFile.createNewFile();
        return new RawFile(mFile);
    }

    @Override
    public Filer mkDir(String folderName) throws IOException {
        String name = new File(folderName).getName();
        File newFolder = new File(mFile, name);
        boolean success = newFolder.mkdir();
        if (!success) {
            throw new IOException("create directory failed");
        }
        return new RawFile(mFile);
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public Filer getParentFile() {
        return new RawFile(mFile.getParentFile());
    }

    @Override
    public String getParentPath() {
        return getParentFile().mPath;
    }

    @Override
    public OutputStream getOutStream() throws IOException {
        return new FileOutputStream(mFile);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(mFile);
    }

    @Override
    public boolean isDirectory() {
        return mFile.isDirectory();
    }

    @Override
    public ArrayList<Filer> listFiles() {
        ArrayList<Filer> subFiles = new ArrayList<>();
        File[] sub = mFile.listFiles();
        if (sub == null || sub.length <= 0) {
            return subFiles;
        }
        for (File f : sub) {
            subFiles.add(new RawFile(f));
        }
        return subFiles;
    }

    @Override
    public long lastModified() {
        return mFile.lastModified();
    }

    @Override
    public boolean hasChild(String fileName) {
        String name = new File(fileName).getName();
        return new File(mFile, name).exists();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof RawFile)) {
            return false;
        }
        RawFile f = (RawFile) o;
        return f.mPath.equals(mPath);
    }

    @Override
    public long length() {
        return mFile.length();
    }

    @Override
    public boolean fillWithZero() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(mPath, "rw");
        return RawFileUtil.fillWithZero(raf);
    }


    public static boolean rm(File f) {
        if (f.isDirectory()) {
            File[] subFiles = f.listFiles();
            for (File subFile : subFiles) {
                rm(subFile);
            }
        }
        return f.delete();
    }
}
