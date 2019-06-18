package com.daemonw.file.core.model;

import android.net.Uri;

import com.daemonw.file.core.utils.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

class InternalFile extends Filer {
    private File mFile;

    InternalFile(String filePath) {
        mFile = new File(filePath);
        mPath = mFile.getAbsolutePath();
    }

    InternalFile(File file) {
        mFile = file;
        mPath = mFile.getAbsolutePath();
    }

    @Override
    public boolean delete() {
        return rm(mFile);
    }

    @Override
    public boolean createNewFile() throws IOException {
        return mFile.createNewFile();
    }

    @Override
    public boolean createChild(String name) throws IOException {
        if (!mFile.exists() || mFile.isDirectory()) {
            return false;
        }
        return new File(mFile, name).createNewFile();
    }

    @Override
    public boolean mkChild(String name) throws IOException {
        if (!mFile.exists() || mFile.isDirectory()) {
            return false;
        }
        return new File(mFile, name).mkdir();
    }

    @Override
    public boolean mkDir() throws IOException {
        return mFile.mkdir();
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
    public String getUri() {
        return Uri.fromFile(mFile).toString();
    }

    @Override
    public Filer getParentFile() {
        return new InternalFile(mFile.getParentFile());
    }

    @Override
    public String getParentPath() {
        return getParentFile().mPath;
    }

    @Override
    public FileOutputStream getOutStream() throws IOException {
        return new FileOutputStream(mFile);
    }

    @Override
    public FileInputStream getInputStream() throws IOException {
        return new FileInputStream(mFile);
    }

    @Override
    public boolean isDirectory() {
        return mFile.isDirectory();
    }

    @Override
    public Filer[] listFiles() {
        File[] sub = mFile.listFiles();
        if (sub == null || sub.length <= 0) {
            return null;
        }
        Filer[] subFiles = new Filer[sub.length];
        for (int i = 0; i < sub.length; i++) {
            subFiles[i] = new InternalFile(sub[i]);
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

        if (!(o instanceof InternalFile)) {
            return false;
        }
        InternalFile f = (InternalFile) o;
        return f.mPath.equals(mPath);
    }

    @Override
    public long length() {
        return mFile.length();
    }

    @Override
    public boolean renameTo(String fileName) {
        String name = new File(fileName).getName();
        return mFile.renameTo(new File(mFile.getParent(), name));
    }

    @Override
    public boolean exists() {
        return mFile.exists();
    }

    @Override
    public boolean erase() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(mPath, "rw");
        return fillWithZero(raf);
    }

    @Override
    public int getType() {
        return TYPE_INTERNAL;
    }

    private static boolean rm(File f) {
        if (f.isDirectory()) {
            File[] subFiles = f.listFiles();
            for (File subFile : subFiles) {
                rm(subFile);
            }
        }
        return f.delete();
    }

    private static boolean fillWithZero(RandomAccessFile file) {
        boolean success = true;
        try {
            byte[] buff = new byte[4096];
            long left = file.length();
            while (left >= buff.length) {
                file.write(buff);
                left -= buff.length;
            }
            file.write(buff, 0, (int) left);
            file.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            IOUtil.closeStream(file);
        }
        return success;
    }
}
