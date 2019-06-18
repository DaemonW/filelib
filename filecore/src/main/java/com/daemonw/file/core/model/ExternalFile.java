package com.daemonw.file.core.model;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.daemonw.file.core.utils.IOUtil;
import com.daemonw.file.core.utils.RawFileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

abstract class ExternalFile extends Filer {
    private static final String EXTERNAL_STORAGE_URI = "content://com.android.externalstorage.documents/tree/";
    File mRawFile;
    private DocFile mSafFile;
    protected Context mContext;
    String mRootPath;
    String mRootUri;


    ExternalFile(Context context, String filePath, String rootPath, String rootUri) {
        mContext = context;
        mPath = filePath;
        mRawFile = new File(filePath);
        mRootPath = rootPath;
        mRootUri = rootUri;
    }

    ExternalFile(Context context, File file, String rootPath, String rootUri) {
        mContext = context;
        mPath = file.getAbsolutePath();
        mRawFile = file;
        mRootPath = rootPath;
        mRootUri = rootUri;
    }

    ExternalFile(Context context, String filePath, String rootPath, String rootUri, DocFile file) {
        mContext = context;
        mRawFile = new File(filePath);
        mPath = mRawFile.getAbsolutePath();
        mRootPath = rootPath;
        mRootUri = rootUri;
        mSafFile = file;
    }


    @Override
    public boolean delete() {
        if (canRawWrite()) {
            return mRawFile.delete();
        }

        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return false;
        }
        return file.delete();
    }

    @Override
    public boolean createNewFile() throws IOException {
        if (canRawWrite()) {
            return mRawFile.createNewFile();
        }
        DocFile file = getDocumentFile();
        DocFile parent = file.getParentFile();
        if (!parent.exists()) {
            return false;
        }
        DocFile newSafFile = parent.createFile(mRawFile.getName());
        if (newSafFile != null) {
            mSafFile = newSafFile;
            return true;
        }
        return false;
    }

    @Override
    public boolean mkChild(String name) throws IOException {
        if (!exists() || !isDirectory()) {
            return false;
        }
        if (canRawWrite()) {
            return new File(mRawFile, name).mkdir();
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return false;
        }
        DocFile newSafFile = file.createDirectory(name);
        return newSafFile != null;
    }

    @Override
    public boolean createChild(String name) throws IOException {
        if (!exists() || !isDirectory()) {
            return false;
        }
        if (canRawWrite()) {
            return new File(mRawFile, name).createNewFile();
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return false;
        }
        DocFile newSafFile = file.createFile(name);
        return newSafFile != null;
    }

    @Override
    public boolean mkDir() throws IOException {

        if (canRawWrite()) {
            return mRawFile.mkdir();
        }
        DocFile file = getDocumentFile();
        DocFile parent = file.getParentFile();
        if (!parent.exists()) {
            return false;
        }
        DocFile newSafFolder = parent.createDirectory(mRawFile.getName());
        return newSafFolder != null;
    }

    @Override
    public String getName() {
        return mRawFile.getName();
    }

    @Override
    public String getPath() {
        return mPath;
    }

    @Override
    public String getUri() {
        if (canRawRead()) {
            return Uri.fromFile(mRawFile).toString();
        }
        DocFile f = getDocumentFile();
        if (!f.exists()) {
            return null;
        }
        return f.getUri().toString();
    }

    @Override
    public String getParentPath() {
        return getParentFile().mPath;
    }

    @Override
    public FileOutputStream getOutStream() throws IOException {
        if (canRawWrite()) {
            return new FileOutputStream(mRawFile);
        }
        DocFile file = getDocumentFile();
        boolean exist = file.exists();
        if (!exist) {
            boolean success = createNewFile();
            if (!success) {
                throw new IOException();
            }
            file = mSafFile;
        }
        return (FileOutputStream) mContext.getContentResolver().openOutputStream(file.getUri());
    }

    @Override
    public FileInputStream getInputStream() throws IOException {
        if (canRawRead()) {
            return new FileInputStream(mRawFile);
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        return (FileInputStream) mContext.getContentResolver().openInputStream(file.getUri());
    }

    @Override
    public boolean isDirectory() {
        if (canRawRead()) {
            return mRawFile.isDirectory();
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return false;
        }
        return file.isDirectory();
    }

    @Override
    public long lastModified() {
        if (canRawRead()) {
            return mRawFile.lastModified();
        }

        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return 0;
        }
        return file.lastModified();
    }

    @Override
    public boolean hasChild(String fileName) {
        String name = new File(fileName).getName();
        if (canRawRead()) {
            return new File(mRawFile, name).exists();
        }
        DocFile file = new DocFile(mContext, mPath + "/" + name, mRootPath, mRootUri);
        return file.exists();
    }


    @Override
    public long length() {
        if (canRawRead()) {
            return mRawFile.length();
        }

        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return 0;
        }
        return file.length();
    }

    @Override
    public boolean renameTo(String fileName) {
        String name = new File(fileName).getName();
        if (canRawWrite()) {
            return mRawFile.renameTo(new File(mRawFile.getParent(), name));
        }
        DocFile file = getDocumentFile();
        if (!file.exists()) {
            return false;
        }

        boolean success = false;
        try {
            success = file.renameTo(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    public boolean exists() {
        if (canRawRead()) {
            return mRawFile.exists();
        }
        DocFile file = getDocumentFile();
        return file.exists();
    }


    DocFile getDocumentFile() {
        if (mSafFile != null) {
            return mSafFile;
        }
        DocFile file = new DocFile(mContext, mPath, mRootPath, mRootUri);
        mSafFile = file;
        return file;
    }


    private boolean canRawWrite() {
        return mRawFile.getParentFile().canWrite();
    }

    boolean canRawRead() {
        boolean canRead = mRawFile.getParentFile().canRead();
        if (canRead) {
            return true;
        }

        //特殊文件,不可读写
        if (getName().equals(".android_secure")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof Filer)) {
            return false;
        }

        Filer f = (Filer) o;
        return f.mPath.equals(mPath) && f.getType() == getType();
    }


    @Override
    public boolean erase() throws IOException {
        if (canRawWrite()) {
            RandomAccessFile raf = new RandomAccessFile(mPath, "rw");
            return RawFileUtil.fillWithZero(raf);
        } else {
            DocFile file = getDocumentFile();
            if (!file.exists()) {
                return false;
            }
            ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(file.getUri(), "rw");
            return fillWithZero(pfd, length());
        }
    }


    private static boolean fillWithZero(ParcelFileDescriptor file, long length) {
        boolean success = true;
        ParcelFileDescriptor.AutoCloseOutputStream out = new ParcelFileDescriptor.AutoCloseOutputStream(file);
        try {
            byte[] buff = new byte[4096];
            long left = length;
            while (left >= buff.length) {
                out.write(buff);
                left -= buff.length;
            }
            out.write(buff, 0, (int) left);
            out.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            IOUtil.closeStream(out);
        }
        return success;
    }


    private boolean isContentUri(String uriStr) {
        if (uriStr == null || uriStr.isEmpty()) {
            return false;
        }
        return uriStr.startsWith("content://");
    }

    private boolean isFileUri(String uriStr) {
        if (uriStr == null || uriStr.isEmpty()) {
            return false;
        }
        return uriStr.startsWith("file://");
    }

    private boolean isExternalStorageUri(String uriStr) {
        if (uriStr == null || uriStr.isEmpty()) {
            return false;
        }
        return uriStr.startsWith(EXTERNAL_STORAGE_URI);
    }
}
