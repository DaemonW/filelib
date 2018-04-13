package com.grt.daemonw.filelibyary.file;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class LocalFile extends HybirdFile {
    private File mFile;
    private Context mContext;

    public LocalFile(String filePath, Context context) {
        super(filePath);
        mFile = new File(filePath);
        mContext = context;
        mType=HybirdFile.TYPE_FILE;
    }

    @Override
    public boolean delete() {
        return mFile.delete();
    }

    @Override
    public boolean createNewFile() throws IOException {
        return mFile.createNewFile();
    }

    @Override
    public boolean mkDir() {
        return mFile.mkdir();
    }

    @Override
    public String getName() {
        return mFile.getName();
    }

    @Override
    public String getParent() {
        return null;
    }

    @Override
    public HybirdFile getParentFile() {
        File f = mFile.getParentFile();
        return new LocalFile(f.getAbsolutePath(), mContext);
    }

    @Override
    public String getPath() {
        return mFile.getPath();
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
    public ArrayList<? extends HybirdFile> listFiles() {
        ArrayList<LocalFile> files = new ArrayList<>();
        File[] subFiles = mFile.listFiles();
        if (subFiles == null) {
            return files;
        }
        for (File f : subFiles) {
            files.add(new LocalFile(f.getAbsolutePath(), mContext));
        }
        return files;
    }

    @Override
    public int getFileType() {
        return HybirdFile.TYPE_FILE;
    }
}
