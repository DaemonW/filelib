package com.grt.daemonw.filelibrary.file;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class LocalFile extends Filer {
    private File mFile;
    private Context mContext;

    public LocalFile(Context context, String filePath) {
        super(filePath);
        mFile = new File(filePath);
        mContext = context;
        mType = Filer.TYPE_INTERNAL;
    }

    public LocalFile(Context context, File file) {
        super(file.getAbsolutePath());
        mFile = file;
        mContext = context;
        mType = Filer.TYPE_INTERNAL;
    }

    @Override
    public boolean delete() {
        return mFile.delete();
    }

    @Override
    public LocalFile createNewFile(String fileName) throws IOException {
        File file = new File(fileName);
        File newFile = new File(mPath + "/" + file.getName());
        boolean success = newFile.createNewFile();
        if (success) {
            return new LocalFile(mContext, newFile.getAbsolutePath());
        }
        return null;
    }

    @Override
    public Filer mkDir(String folderName) {
        File file = new File(folderName);
        File f = new File(mPath + "/" + file.getName());
        if (f.mkdir()) {
            return new LocalFile(mContext, f);
        }
        return null;
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
    public Filer getParentFile() {
        File f = mFile.getParentFile();
        return new LocalFile(mContext, f.getAbsolutePath());
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
    public ArrayList<Filer> listFiles() {
        ArrayList<Filer> files = new ArrayList<>();
        File[] subFiles = mFile.listFiles();
        if (subFiles == null) {
            return files;
        }
        for (File f : subFiles) {
            files.add(new LocalFile(mContext, f.getAbsolutePath()));
        }
        return files;
    }

    @Override
    public int getFileType() {
        return Filer.TYPE_INTERNAL;
    }

    @Override
    public boolean isDirectory() {
        return mFile.isDirectory();
    }

    @Override
    public String getFilePath() {
        return mFile.getAbsolutePath();
    }
}
